package com.rocklin.nexai.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rocklin.nexai.common.enums.CodeGenTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.request.AppCreateRequest;
import com.rocklin.nexai.common.request.ChatToGenCodeRequest;
import com.rocklin.nexai.common.response.BaseResponse;
import com.rocklin.nexai.model.entity.App;
import com.rocklin.nexai.service.AppService;
import com.rocklin.nexai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.rocklin.nexai.common.constants.Constants.CHUNK_DATA;

/**
 * @ClassName AppController
 * @Description 应用生成接口
 * @Author: rocklin
 * @Date 2025/8/5 20:47
 * @Version 1.0
 */
@Tag(name = "应用生成", description = "应用生成相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class AppController {

    private final AppService appService;
    private final UserService userService;

    /**
     * 创建应用
     */
    @Operation(summary = "创建应用", description = "创建应用")
    @PostMapping("/create")
    public BaseResponse<Long> createApp(@RequestBody @Validated AppCreateRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        // 获取当前登录用户id
        Long userId= userService.getCurrentUser().getUserId();
        String initPrompt = req.getInitPrompt();
        // 创建应用
        App app = new App();
        app.setUserId(userId);
        app.setInitPrompt(initPrompt);
        // 应用名称暂时为 initPrompt 前 12 位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        // 暂时设置为多文件生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        Long appId = appService.createApp(app);
        return BaseResponse.success(appId);
    }

    /**
     * 对话生成代码
     */
    @Operation(summary = "对话生成代码", description = "对话生成代码")
    @PostMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestBody @Validated ChatToGenCodeRequest req) {
        // 参数校验
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        Assert.isTrue(req.getAppId()!= null && req.getAppId() > 0,
                ErrorCode.PARAMS_ERROR, "应用id错误");
        // 获取当前登录用户id
        Long userId= userService.getCurrentUser().getUserId();
        // 调用服务生成代码（SSE 流式返回）
        Flux<String> contentFlux = appService.chatToGenCode(req.getAppId(), req.getMessage(), userId);
        return contentFlux
                .map(chunk -> {
                    Map<String, String> wrapper = Map.of(CHUNK_DATA, chunk);
                    String jsonData = JSONUtil.toJsonStr(wrapper);
                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 发送结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }
}
