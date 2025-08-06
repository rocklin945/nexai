package com.rocklin.nexai.service.impl;

import com.rocklin.nexai.common.enums.ChatHistoryMessageTypeEnum;
import com.rocklin.nexai.common.enums.CodeGenTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.core.AiCodeGeneratorFacade;
import com.rocklin.nexai.mapper.AppMapper;
import com.rocklin.nexai.model.entity.App;
import com.rocklin.nexai.service.AppService;
import com.rocklin.nexai.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @ClassName AppServiceImpl
 * @Description ai应用生成
 * @Author: rocklin
 * @Date 2025/8/5 20:49
 * @Version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {

    private final AppMapper appMapper;
    private final ChatHistoryService chatHistoryService;
    private final AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, Long userId) {
        //查询应用
        App app = appMapper.queryAppById(appId);
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        Assert.isTrue(app.getUserId().equals(userId), ErrorCode.OPERATION_ERROR, "无权限操作");
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(app.getCodeGenType());
        Assert.notNull(codeGenTypeEnum, ErrorCode.OPERATION_ERROR, "不支持的生成模式");
        //在调用 AI 前，先保存用户消息到数据库中
        chatHistoryService.createHistory(appId, message,
                ChatHistoryMessageTypeEnum.USER.getValue(), userId);
        //调用 AI 生成代码（流式）
        Flux<String> contentFlux = aiCodeGeneratorFacade
                .generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        //收集 AI 响应的内容，并且在完成后保存记录到对话历史
        StringBuilder aiResponseBuilder = new StringBuilder();
        return contentFlux.map(chunk -> {
            // 实时收集 AI 响应的内容
            aiResponseBuilder.append(chunk);
            return chunk;
        }).doOnComplete(() -> {
            // 流式返回完成后，保存 AI 消息到对话历史中
            String aiResponse = aiResponseBuilder.toString();
            chatHistoryService.createHistory(appId, aiResponse,
                    ChatHistoryMessageTypeEnum.AI.getValue(), userId);
        }).doOnError(error -> {
            // 如果 AI 回复失败，也需要保存记录到数据库中
            String errorMessage = "AI回复失败：" + error.getMessage();
            chatHistoryService.createHistory(appId, errorMessage,
                    ChatHistoryMessageTypeEnum.AI.getValue(), userId);
        });
    }

    @Override
    public Long createApp(App app) {
        Long result = appMapper.insert(app);
        Assert.isTrue(result > 0, "创建应用失败");
        return app.getId();
    }
}
