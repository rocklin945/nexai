package com.rocklin.nexai.service;

import com.rocklin.nexai.common.request.ChatHistoryQueryRequest;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.model.entity.ChatHistory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

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

    PageResponse<ChatHistory> listAppChatHistoryByPage(ChatHistoryQueryRequest req);
}
