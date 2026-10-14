package com.zxr.tools.impl;

import com.zxr.tools.AgentTool;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;

import java.util.concurrent.TimeUnit;

@Component
public class RedisAnalyzeTool implements AgentTool {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisAnalyzeTool(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getName() {
        return "redisAnalyze";
    }

    @Override
    public String getDescription() {
        return "分析 Redis Key 的类型、TTL、内存占用，给出优化建议";
    }

    @Override
    @Tool(name = "redis_analyze",description = "分析 Redis Key 的类型、TTL、内存占用，给出优化建议。输入是 Redis Key 名称")
    public String execute(String input) {
        long start = System.currentTimeMillis();

        // 输入就是一个 Key 名，去掉可能的空格
        String key = input.trim();
        try {
            // 1. 检查 Key 是否存在
            Boolean exists = redisTemplate.hasKey(key);
            if (exists == null || !exists) {
                long duration = System.currentTimeMillis() - start;
                return "Key 不存在：" + key;
            }

            // 2. 获取 Key 的类型
            DataType type = redisTemplate.type(key);

            // 3. 获取 TTL（秒）
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            // 4. 拼装结果
            StringBuilder sb = new StringBuilder();
            sb.append("【Redis Key 分析】\n");
            sb.append("Key: ").append(key).append("\n");
            sb.append("类型: ").append(type != null ? type.code() : "unknown").append("\n");

            if (ttl == null || ttl == -1) {
                sb.append("TTL: -1（永不过期）⚠️\n");
                sb.append("风险: 该 Key 没有设置过期时间，长期占用内存，建议设置合理 TTL\n");
            } else if (ttl == -2) {
                sb.append("TTL: 已过期\n");
            } else {
                sb.append("TTL: ").append(ttl).append(" 秒\n");
                if (ttl > 86400) {
                    sb.append("提示: TTL 超过 1 天，请确认是否符合业务预期\n");
                }
            }

            long duration = System.currentTimeMillis() - start;
            return sb.toString();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            return "Redis 分析失败：" + e.getMessage();
        }
    }
}