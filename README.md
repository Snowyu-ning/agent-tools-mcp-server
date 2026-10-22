# Agent Tools MCP Server
[![M8ven Score](https://m8ven.ai/badge/mcp/snowyu-ning-agent-tools-mcp-server-zlh8q1?v=b36b591b92b46177b408b9ca4686b702)](https://m8ven.ai/mcp/snowyu-ning-agent-tools-mcp-server-zlh8q1?s=readme)

> 基于 MCP（Model Context Protocol）协议的工具服务，为 Agent 应用提供 SQL 分析、Redis 分析、代码分析三类标准化工具。

[![Java](https://img.shields.io/badge/Java-21-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-green)]()
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.2-blue)]()
[![MCP](https://img.shields.io/badge/MCP-SSE-purple)]()

---

## 项目简介

本项目是 [Agent Workflow Platform](https://github.com/Snowyu-ning/agent-workflow-platform) 的**工具服务端**。

通过 MCP（Model Context Protocol）协议，把工具能力从 Agent 主项目中独立出来，实现：

- **解耦**：工具与 Agent 从代码级解耦，通过标准协议通信
- **独立部署**：工具可以单独发版，不影响 Agent 主服务
- **可复用**：任何支持 MCP 协议的 Agent 系统都能复用这些工具
- **标准化**：基于 JSON-RPC 2.0，工具定义（name / description / 参数 schema）标准化

---

## 提供的工具

| 工具名 | 能力 | 底层实现 |
|--------|------|----------|
| `sql_explain` | 分析 SQL 执行计划，给出索引优化建议 | MySQL EXPLAIN |
| `redis_analyze` | 分析 Redis Key 类型、TTL、命名规范 | RedisTemplate |
| `code_analyze` | 分析代码潜在 Bug、性能问题、安全风险 | 规则 + 语义分析 |

---

## 系统架构

```
                 Agent Workflow Platform
                 （MCP Client）
                        │
                        │ SSE 协议
                        │ JSON-RPC 2.0
                        ▼
        ┌───────────────────────────────┐
        │   agent-tools-mcp-server       │
        │   （本项目，MCP Server）        │
        │                                │
        │   ├── sql_explain              │
        │   ├── redis_analyze            │
        │   └── code_analyze             │
        └───────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
      MySQL           Redis          业务逻辑
```

---

## 快速开始

### 依赖环境

- JDK 21+
- MySQL 8.0（端口 3306）
- Redis 7.0（端口 6379）

### 1. 启动依赖服务

```bash
docker run -d --name agent-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=xxxx \
  -e MYSQL_DATABASE=agent_db mysql:8.0

docker run -d --name agent-redis -p 6379:6379 redis:7.0
```

### 2. 启动 MCP Server

```bash
mvn spring-boot:run
```

服务监听 `8081`，SSE 端点：`http://localhost:8081/sse`

### 3. 验证服务

浏览器访问：

```
http://localhost:8081/sse
```

看到类似以下响应，说明服务正常：

```
event: endpoint
data: /mcp/message?sessionId=xxxxx
```

### 4. 用 MCP Inspector 调试

```bash
npx @modelcontextprotocol/inspector
```

在 Inspector 界面：
- Transport Type 选 `SSE`
- URL 填 `http://localhost:8081/sse`
- 点 Connect
- 在 Tools 标签页可以看到三个工具

---

## 技术栈

- **Java 21**
- **Spring Boot 3.5.8**
- **Spring AI 1.1.2**（MCP Server starter）
- **MCP Java SDK**（SSE 传输）
- **MySQL / Redis**（工具底层依赖）

---

## 核心配置

**`application.yml`**：

```yaml
server:
  port: 8081

spring:
  ai:
    mcp:
      server:
        name: agent-tools-mcp-server
        version: 1.0.0
        stdio: false           # SSE 模式
        type: SYNC
        sse-endpoint: /sse

  datasource:
    url: jdbc:mysql://127.0.0.1:3306/agent_db?...
    username: root
    password: xxxx

  data:
    redis:
      host: 127.0.0.1
      port: 6379
```

---

## 项目结构

```
agent-tools-mcp-server/
├── src/main/java/com/zxr/tools/
│   ├── AgentTool.java         工具接口
│   ├── ToolResult.java         结果封装
│   ├── impl/
│   │   ├── SqlExplainTool.java
│   │   ├── RedisAnalyzeTool.java
│   │   └── CodeAnalyzeTool.java
│   └── config/
│       └── McpServerConfig.java  MCP Server 配置
└── pom.xml
```

---

## 相关项目

- **Agent Workflow Platform**（主项目）：https://github.com/Snowyu-ning/agent-workflow-platform

---

## 后续规划

- [ ] 增加更多工具：Docker 沙箱执行、文档检索、代码搜索
- [ ] 支持 Streamable HTTP 传输（替代 SSE）
- [ ] 工具级权限控制：按租户隔离工具访问
- [ ] 工具调用审计：记录每次调用的输入、输出、耗时
