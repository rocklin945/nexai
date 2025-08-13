package com.rocklin.nexai.core;

import cn.hutool.json.JSONUtil;
import com.rocklin.nexai.common.enums.CodeGenTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.factory.AiCodeGeneratorServiceFactory;
import com.rocklin.nexai.core.message.AiResponseMessage;
import com.rocklin.nexai.core.message.ToolExecutedMessage;
import com.rocklin.nexai.core.message.ToolRequestMessage;
import com.rocklin.nexai.core.result.HtmlCodeResult;
import com.rocklin.nexai.core.result.MultiFileCodeResult;
import com.rocklin.nexai.core.saver.CodeFileSaverExecutor;
import com.rocklin.nexai.service.AiCodeGeneratorService;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * @ClassName AiCodeGeneratorFacade
 * @Description Ai代码生成器门面类
 * @Author: rocklin
 * @Date 2025/8/6 8:56
 * @Version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiCodeGeneratorFacade {

    private final AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    /**
     * 生成代码并保存到文件
     */
    public File generateAndSaveCode(String userMessage,
                                    CodeGenTypeEnum codeGenTypeEnum,
                                    Long appId) {
        //根据appId获取相应的AI服务实例
        AiCodeGeneratorService aiCodeGeneratorService
                = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(String.valueOf(appId),userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(String.valueOf(appId),userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, errorMessage);
            }
        };
    }

    /**
     *生成代码(流式)
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage,
                                                  CodeGenTypeEnum codeGenTypeEnum,
                                                  Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        // 根据 appId 获取相应的 AI 服务实例
        AiCodeGeneratorService aiCodeGeneratorService
                = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateHtmlCodeStream(String.valueOf(appId),userMessage);
                yield processTokenStream(tokenStream);
            }
            case MULTI_FILE -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateMultiFileCodeStream(String.valueOf(appId),userMessage);
                yield processTokenStream(tokenStream);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.OPERATION_ERROR, errorMessage);
            }
        };
    }

    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
                    /**
                     * 订阅 TokenStream 中的 部分回复事件。
                     * 每当 AI 模型生成一部分文本时，构造一个 AiResponseMessage 对象封装这段文本。
                     * 然后将该对象序列化成 JSON 字符串，通过 sink.next() 推送到 Flux 流里。
                     */
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    /**
                     * 监听模型请求调用工具的事件（如写文件、调用API等）。
                     * 把工具调用请求封装为 ToolRequestMessage 并转成 JSON，发送给流。
                     * index表示这是第几个工具调用请求
                     */
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        log.info("toolRequestMessage: " + toolRequestMessage);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    /**
                     * 监听工具执行完成事件。
                     * 把工具执行结果封装为 ToolExecutedMessage 并转成 JSON，发送给流。
                     */
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        log.info("toolExecutedMessage: " + toolExecutedMessage);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    //监听模型回复完成事件,触发流的结束信号
                    .onCompleteResponse((ChatResponse response) -> {
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }
}
