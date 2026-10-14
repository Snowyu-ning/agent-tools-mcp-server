package com.zxr.tools.impl;

import com.zxr.tools.AgentTool;
import com.zxr.tools.ToolResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;
import java.util.Map;

@Component
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
    @Tool(name = "sql_explain",description = "分析 SQL 语句的执行计划，给出索引优化建议。输入是一条完整的 SELECT 语句")
    public String execute(String input) {
        long start = System.currentTimeMillis();

        try {
            // 1. 拼出 EXPLAIN 语句
            String explainSql = "EXPLAIN " + input;

            // 2. 执行 SQL，拿到执行计划
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(explainSql);

            // 3. 解析结果，拼成字符串
            StringBuilder sb = new StringBuilder();
            sb.append("【SQL 执行计划分析】\n");
            sb.append("原始 SQL：").append(input).append("\n\n");

            for (Map<String, Object> row : rows) {
                sb.append("表：").append(row.get("table")).append("\n");
                sb.append("访问类型：").append(row.get("type")).append("\n");
                sb.append("可能用到的索引：").append(row.get("possible_keys")).append("\n");
                sb.append("实际用的索引：").append(row.get("key")).append("\n");
                sb.append("预估扫描行数：").append(row.get("rows")).append("\n");
                sb.append("额外信息：").append(row.get("Extra")).append("\n");
                sb.append("---\n");
            }

            long duration = System.currentTimeMillis() - start;
            return sb.toString();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            return "SQL 分析失败：" + e.getMessage();
        }
    }
}