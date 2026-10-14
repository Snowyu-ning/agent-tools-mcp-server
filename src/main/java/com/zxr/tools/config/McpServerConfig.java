package com.zxr.tools.config;

import com.zxr.tools.impl.CodeAnalyzeTool;
import com.zxr.tools.impl.RedisAnalyzeTool;
import com.zxr.tools.impl.SqlExplainTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(SqlExplainTool sqlTool,
                                                     RedisAnalyzeTool redisTool,
                                                     CodeAnalyzeTool codeTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(sqlTool, redisTool, codeTool)
                .build();
    }
}