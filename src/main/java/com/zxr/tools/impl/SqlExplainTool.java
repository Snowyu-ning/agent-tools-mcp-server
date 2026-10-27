package com.zxr.tools.impl;

import com.zxr.tools.AgentTool;
import com.zxr.tools.ToolResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SqlExplainTool implements AgentTool {

    private final JdbcTemplate jdbcTemplate;

    public SqlExplainTool(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getName() {
        return "sqlExplain";
    }

    @Override
    public String getDescription() {
        return "分析 SQL 语句的执行计划，给出索引优化建议";
    }

    @Override
    @Tool(name = "sql_explain", description = "分析 SQL 语句的执行计划，给出索引优化建议。输入必须是一条完整的 SELECT 语句")
    public String execute(String input) {
        long start = System.currentTimeMillis();

        // 1. 空值检查
        if (input == null || input.trim().isEmpty()) {
            return "【安全拦截】SQL 不能为空。";
        }

        String trimmedInput = input.trim();

        // 2. 拒绝多语句
        if (trimmedInput.contains(";")) {
            return "【安全拦截】不允许多条语句拼接。";
        }

        // 3. 带边界的分词校验 (防 SELECTED 这种绕过)
        if (!trimmedInput.matches("(?i)^SELECT\\b.*")) {
            return "【安全拦截】仅支持 SELECT 查询语句。";
        }

        try {
            jdbcTemplate.setQueryTimeout(3);
            String explainSql = "EXPLAIN " + trimmedInput;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(explainSql);

            // 解析结果，拼成字符串
            StringBuilder sb = new StringBuilder();
            sb.append("【SQL 执行计划分析】\n");
            sb.append("原始 SQL：").append(trimmedInput).append("\n\n");

            for (Map<String, Object> row : rows) {
                sb.append("表：").append(row.get("table")).append("\n");
                sb.append("访问类型：").append(row.get("type")).append("\n");
                sb.append("可能用到的索引：").append(row.get("possible_keys")).append("\n");
                sb.append("实际用的索引：").append(row.get("key")).append("\n");
                sb.append("预估扫描行数：").append(row.get("rows")).append("\n");
                sb.append("额外信息：").append(row.get("Extra")).append("\n");
                sb.append("---\n");
            }

            // 用日志记录耗时
            long duration = System.currentTimeMillis() - start;
            log.info("【MCP Tool】sql_explain 执行完成，耗时: {} ms", duration);

            return sb.toString();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("SQL分析执行失败，耗时: {} ms, 错误信息: ", duration, e);
            return "SQL 分析失败：请检查 SQL 语法或表结构是否正确。";
        }
    }
}