package com.rocklin.nexai.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;

/**
 * @ClassName ChatHistoryService
 * @Description 聊天记录服务
 * @Author: rocklin
 * @Date 2025/8/6 8:35
 * @Version 1.0
 */
public interface ChatHistoryService {
    Long createHistory(Long appId, String message, String messageType, Long userId);

    Integer loadChatHistoryToMemory(long appId, MessageWindowChatMemory chatMemory, int maxContextSize);
}
