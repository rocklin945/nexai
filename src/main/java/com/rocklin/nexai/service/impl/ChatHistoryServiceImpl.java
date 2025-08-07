package com.rocklin.nexai.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.rocklin.nexai.common.enums.ChatHistoryMessageTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.request.ChatHistoryQueryRequest;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.mapper.ChatHistoryMapper;
import com.rocklin.nexai.model.entity.ChatHistory;
import com.rocklin.nexai.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.rocklin.nexai.common.constants.Constants.OFFSET;

/**
 * @ClassName ChatHistoryServiceImpl
 * @Description 聊天记录服务实现类
 * @Author: rocklin
 * @Date 2025/8/6 8:35
 * @Version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryMapper chatHistoryMapper;

    @Override
    public Long createHistory(Long appId, String message, String messageType, Long userId) {
        Assert.notNull(ChatHistoryMessageTypeEnum.getEnumByValue(messageType)
                , ErrorCode.PARAMS_ERROR, "不支持的消息类型");
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .build();
        Long result = chatHistoryMapper.insert(chatHistory);
        Assert.isTrue(result > 0, ErrorCode.OPERATION_ERROR, "创建聊天记录失败");
        return chatHistory.getId();
    }

    /**
     * 加载聊天记录到内存
     */
    @Override
    public Integer loadChatHistoryToMemory(long appId, MessageWindowChatMemory chatMemory,
                                           int maxContextSize) {
        try {
            // 查询指定 appId 的所有聊天记录
            List<ChatHistory> chatHistoryList = chatHistoryMapper
                    .selectListByAppId(appId, OFFSET, maxContextSize);
            if(CollUtil.isEmpty(chatHistoryList)){
                return 0;
            }
            return historyToMemory(appId, chatMemory, chatHistoryList);
        }catch (Exception e){
            log.error("加载历史对话失败，appId: {}, error: {}", appId, e.getMessage(), e);
            // 加载失败不影响系统运行，只是没有历史上下文
            return 0;
        }
    }

    @Override
    public PageResponse<ChatHistory> listAppChatHistoryByPage(ChatHistoryQueryRequest req) {
        List<ChatHistory> records = chatHistoryMapper.selectAppChatHistoryByCursor(req);
        PageResponse<ChatHistory> pageResponse = new PageResponse<>();
        pageResponse.setList(records);
        pageResponse.setPageSize(req.getPageSize());
        return pageResponse;
    }

    private static int historyToMemory(long appId, MessageWindowChatMemory chatMemory, List<ChatHistory> chatHistoryList) {
        // 按照时间顺序将消息添加到记忆中
        int loadedCount = 0;
        // 先清理历史缓存，防止重复加载
        chatMemory.clear();
        for (ChatHistory history : chatHistoryList) {
            if (ChatHistoryMessageTypeEnum.USER.getValue().equals(history.getMessageType())) {
                chatMemory.add(UserMessage.from(history.getMessage()));
            } else if (ChatHistoryMessageTypeEnum.AI.getValue().equals(history.getMessageType())) {
                chatMemory.add(AiMessage.from(history.getMessage()));
            }
            loadedCount++;
        }
        log.info("成功为 appId: {} 加载 {} 条历史消息", appId, loadedCount);
        return loadedCount;
    }
}
