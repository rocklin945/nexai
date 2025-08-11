package com.rocklin.nexai.common.factory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rocklin.nexai.core.tools.FileWriteTool;
import com.rocklin.nexai.service.AiCodeGeneratorService;
import com.rocklin.nexai.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.rocklin.nexai.common.constants.Constants.MAX_CONTEXT_SIZE;

/**
 * @ClassName AiCodeGeneratorServiceFactory
 * @Description AiCodeGeneratorService工厂类
 * @Author: rocklin
 * @Date 2025/8/6 9:15
 * @Version 1.0
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class AiCodeGeneratorServiceFactory {

    private final ChatModel chatModel;
    private final StreamingChatModel streamingChatModel;
    private final RedisChatMemoryStore redisChatMemoryStore;
    private final ChatHistoryService chatHistoryService;

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<Long, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，appId: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 创建新的 AI 服务实例
     *
     * @param appId
     * @return
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId) {
        log.info("为 appId: {} 创建新的 AI 服务实例", appId);
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(MAX_CONTEXT_SIZE)
                .build();
        // 从数据库中加载对话历史到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, MAX_CONTEXT_SIZE);
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .tools(new FileWriteTool())
                // 处理工具调用幻觉问题
                .hallucinatedToolNameStrategy(toolExecutionRequest ->
                        ToolExecutionResultMessage.from(toolExecutionRequest,
                                "Error: there is no tool called " + toolExecutionRequest.name())
                )
                .build();
    }

    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId) {
        return serviceCache.get(appId,this::createAiCodeGeneratorService);
    }

    /**
     * 创建 AI 代码生成器服务
     * 单元测试使用
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L);
    }
}
