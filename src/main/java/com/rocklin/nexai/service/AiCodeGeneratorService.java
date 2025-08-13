package com.rocklin.nexai.service;

import com.rocklin.nexai.core.result.HtmlCodeResult;
import com.rocklin.nexai.core.result.MultiFileCodeResult;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.service.*;

/**
 * @ClassName AiCodeGeneratorService
 * @Description AI代码生成服务接口
 * @Author: rocklin
 * @Date 2025/8/6 9:14
 * @Version 1.0
 */
public interface AiCodeGeneratorService {
    /**
     * 非流式生成 HTML 代码
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(@MemoryId String appId, @UserMessage String userMessage);

    /**
     * 非流式生成多文件代码
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(@MemoryId String appId, @UserMessage String userMessage);

    /**
     * 流式生成 HTML 代码
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    TokenStream generateHtmlCodeStream(@MemoryId String appId, @UserMessage String userMessage);

    /**
     * 流式生成多文件代码
     *
     * @param userMessage 用户提示词
     * @return AI 的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    TokenStream generateMultiFileCodeStream(@MemoryId String appId, @UserMessage String userMessage);
}
