package com.zxr.tools;

public interface AgentTool {

    /**
     * 工具名称
     */
    String getName();

    /**
     *工具描述，用于告诉LLM 这个工具是干什么的
     */
    String getDescription();

    /**
     *执行工具，传入用户输入，返回工具结果
     */
    String execute(String input);
}
