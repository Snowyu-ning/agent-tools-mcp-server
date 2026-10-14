package com.zxr.tools.impl;

import com.zxr.tools.AgentTool;
import com.zxr.tools.ToolResult;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;

@Component
public class CodeAnalyzeTool implements AgentTool {

    @Override
    public String getName() {
        return "codeAnalyze";
    }

    @Override
    public String getDescription() {
        return "分析代码规范、潜在 Bug 和性能问题";
    }

    @Override
    @Tool(name = "code_analyze",description = "分析代码的潜在 Bug、性能问题、代码规范。输入是代码片段")
    public String execute(String input) {
        long start = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("【代码分析结果】\n");
        sb.append("输入代码：").append(input).append("\n\n");

        // 根据输入特征给出不同的分析（还是假数据，但和输入相关）
        if (input.contains("for") || input.contains("while")) {
            sb.append("检测到循环结构：\n");
            sb.append("1. 检查循环内是否有数据库调用或IO操作\n");
            sb.append("2. 循环边界是否明确，是否存在死循环风险\n");
        }
        if (input.contains("select") || input.contains("sql")) {
            sb.append("检测到SQL相关代码：\n");
            sb.append("1. 是否存在字符串拼接SQL（注入风险）\n");
            sb.append("2. 是否使用了参数化查询\n");
        }
        if (input.contains("null")) {
            sb.append("检测到空值判断：\n");
            sb.append("1. 建议用 Objects.requireNonNull 或 Optional 替代\n");
        }
        if (input.contains("catch")) {
            sb.append("检测到异常处理：\n");
            sb.append("1. 避免空 catch 吞异常\n");
            sb.append("2. 建议记录日志或包装后抛出\n");
        }
        if (input.contains("get(0)") || input.contains("findFirst")) {
            sb.append("检测到集合取值：\n");
            sb.append("1. 建议先判空或使用 findFirst()\n");
        }

        sb.append("\n建议结合具体上下文进一步分析。\n");

        long duration = System.currentTimeMillis() - start;
        return sb.toString();
    }
}