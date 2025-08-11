package com.rocklin.nexai.core;

import cn.hutool.json.JSONUtil;
import com.rocklin.nexai.common.enums.CodeGenTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.factory.AiCodeGeneratorServiceFactory;
import com.rocklin.nexai.core.message.AiResponseMessage;
import com.rocklin.nexai.core.message.ToolExecutedMessage;
import com.rocklin.nexai.core.message.ToolRequestMessage;
import com.rocklin.nexai.core.parser.CodeParserExecutor;
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
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, errorMessage);
            }
        };
    }

    /**
     *生成代码并保存(流式)
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
                TokenStream tokenStream = aiCodeGeneratorService.generateHtmlCodeStream(appId, userMessage);
                yield processCodeStream(tokenStream, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateMultiFileCodeStream(appId, userMessage);
                yield processCodeStream(tokenStream, CodeGenTypeEnum.MULTI_FILE, appId);
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
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
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

    /**
     * 流式代码处理方法
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        // 字符串拼接器，用于当流式返回所有的代码之后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            // 流式返回完成后，保存代码
            try {
                String completeCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                // 使用执行器保存代码
                File saveDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType, appId);
                log.info("保存成功，目录为：{}", saveDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败: {}", e.getMessage());
            }
        });
    }
}
