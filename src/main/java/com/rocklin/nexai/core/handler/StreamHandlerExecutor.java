package com.rocklin.nexai.core.handler;

import com.rocklin.nexai.common.enums.CodeGenTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 流处理器执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamHandlerExecutor {

    private final JsonMessageStreamHandler jsonMessageStreamHandler;

    /**
     * 创建流处理器并处理聊天历史记录
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param userId          登录用户ID
     * @param codeGenType        代码生成类型
     * @return 处理后的流
     */
    public Flux<String> doExecute(Flux<String> originFlux,
                                  ChatHistoryService chatHistoryService,
                                  Long appId, Long userId, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML, MULTI_FILE ->
                    jsonMessageStreamHandler.handle(originFlux, chatHistoryService,
                            appId, userId, codeGenType);
            default -> throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "不支持的代码生成类型");
        };
    }
}
