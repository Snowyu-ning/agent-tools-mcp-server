package com.zxr.tools;

import lombok.Data;

@Data
public class ToolResult {

    /**
     * 工具是否执行成功
     */
    private boolean success;

    /**
     * 工具返回的内容（成功时是结果，失败时是错误信息）
     */
    private String output;

    /**
     * 工具执行耗时（毫秒）
     */
    private long durationMs;

    public static ToolResult success(String output,long durationMs){
        ToolResult result = new ToolResult();
        result.setSuccess(true);
        result.setOutput(output);
        result.setDurationMs(durationMs);
        return result;
    }

    public static ToolResult fail(String errorMsg,long durationMs){
        ToolResult result = new ToolResult();
        result.setSuccess(false);
        result.setOutput(errorMsg);
        result.setDurationMs(durationMs);
        return result;
    }
}
