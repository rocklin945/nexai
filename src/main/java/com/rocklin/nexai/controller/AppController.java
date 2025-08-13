package com.rocklin.nexai.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rocklin.nexai.common.annotation.AuthCheck;
import com.rocklin.nexai.common.annotation.SlidingWindowRateLimit;
import com.rocklin.nexai.common.enums.CodeGenTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.enums.UserRoleEnum;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.request.*;
import com.rocklin.nexai.common.response.BaseResponse;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.common.utils.WebScreenshotUtils;
import com.rocklin.nexai.model.entity.App;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.service.AppService;
import com.rocklin.nexai.service.ScreenshotService;
import com.rocklin.nexai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.rocklin.nexai.common.constants.Constants.CHUNK_DATA;
import static com.rocklin.nexai.common.constants.Constants.GOOD_APP;

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
    private final ScreenshotService screenshotService;

    /**
     * 创建应用
     */
    @Operation(summary = "创建应用", description = "创建应用")
    @PostMapping("/create")
    @SlidingWindowRateLimit()
    public BaseResponse<Long> createApp(@RequestBody @Validated AppCreateRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        // 获取当前登录用户id
        Long userId = userService.getCurrentUser().getUserId();
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
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam Long appId,
                                                       @RequestParam String message) {
        // 参数校验
        Assert.notNull(appId, ErrorCode.PARAMS_ERROR, "appId为空");
        Assert.notNull(message, ErrorCode.PARAMS_ERROR, "message为空");
        // 获取当前登录用户id
        Long userId = userService.getCurrentUser().getUserId();
        // 调用服务生成代码（SSE 流式返回）
        Flux<String> contentFlux = appService
                .chatToGenCode(appId, message, userId);
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

    /**
     * 应用部署
     */
    @Operation(summary = "应用部署", description = "应用部署")
    @PostMapping("/deploy")
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<String> deployApp(@RequestBody @Validated AppDeployRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        UserLoginResponse currentUser = userService.getCurrentUser();
        Assert.notNull(currentUser.getUserId(), ErrorCode.NOT_LOGIN_ERROR, "未登录");
        App app = appService.getAppById(req.getAppId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        Assert.isTrue(currentUser.getUserId().equals(app.getUserId()) ||
                        currentUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue()),
                ErrorCode.UNAUTHORIZED, "无权限部署");
        String deployUrl = appService.deployApp(app);
        // 返回部署 URL
        return BaseResponse.success(deployUrl);
    }

    /**
     * 截图保存封面
     */
    @Operation(summary = "截图保存封面", description = "截图保存封面")
    @PostMapping("/save/cover")
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Boolean> saveCover(@RequestBody @Validated AppSaveCoverRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        String coverUrl = screenshotService.generateAndUploadScreenshot(req.getUrl());
        Assert.notNull(coverUrl, ErrorCode.OPERATION_ERROR, "截图保存失败");
        App app = appService.getAppById(req.getId());
        app.setCover(coverUrl);
        appService.updateApp(app);
        return BaseResponse.success();
    }

    /**
     * 更新应用（用户只能更新自己的应用名称）
     */
    @Operation(summary = "更新应用", description = "更新应用")
    @PostMapping("/update")
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Boolean> updateApp(@RequestBody @Validated AppUpdateRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        Long userId = userService.getCurrentUser().getUserId();
        App app = appService.getAppById(req.getId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        Assert.isTrue(userId == app.getUserId(),
                ErrorCode.UNAUTHORIZED, "只能更新自己的应用名称");
        app.setAppName(req.getAppName());
        appService.updateApp(app);
        return BaseResponse.success();
    }

    /**
     * 删除应用（用户只能删除自己的应用）
     */
    @Operation(summary = "删除应用", description = "删除应用")
    @PostMapping("/delete")
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Boolean> deleteApp(@RequestBody @Validated AppDeleteRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        UserLoginResponse currentUser = userService.getCurrentUser();
        App app = appService.getAppById(req.getId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        Assert.isTrue(currentUser.getUserId().equals(app.getUserId()) ||
                        currentUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue()),
                ErrorCode.UNAUTHORIZED, "仅本人或管理员可删除");
        appService.deleteApp(req.getId());
        return BaseResponse.success();
    }

    /**
     * 根据appId获取应用详情
     */
    @Operation(summary = "根据appId获取应用详情", description = "根据appId获取应用详情")
    @PostMapping("/getById")
    @SlidingWindowRateLimit(windowInSeconds = 1, maxCount = 3)
    public BaseResponse<App> getAppById(@RequestBody @Validated AppGetByIdRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        UserLoginResponse currentUser = userService.getCurrentUser();
        Long userId = currentUser.getUserId();
        App app = appService.getAppById(req.getId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        boolean isGoodApp = app.getPriority() == 99;
        Assert.isTrue(isGoodApp ||
                        currentUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue()) ||
                        app.getUserId().equals(userId), ErrorCode.UNAUTHORIZED, "无权限访问");
        return BaseResponse.success(app);
    }

    /**
     * 分页获取当前用户创建的应用列表
     */
    @Operation(summary = "分页获取当前用户应用列表", description = "分页获取当前用户应用列表")
    @PostMapping("curUser/list/page")
    @SlidingWindowRateLimit(windowInSeconds = 1, maxCount = 3)
    public BaseResponse<PageResponse<App>> listCurUserAppPage(@RequestBody @Validated AppQueryPageListRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        Assert.isTrue(req.getPageSize() <= 20,
                ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        Long userId = userService.getCurrentUser().getUserId();
        req.setUserId(userId);
        return BaseResponse.success(appService.queryAppPageList(req));
    }

    /**
     * 分页获取精选的应用列表
     */
    @Operation(summary = "分页获取精选的应用列表", description = "分页获取精选的应用列表")
    @PostMapping("/good/list/page")
    @SlidingWindowRateLimit(windowInSeconds = 1, maxCount = 3)
    public BaseResponse<PageResponse<App>> listGoodAppByPage(@RequestBody @Validated AppQueryPageListRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        Assert.isTrue(req.getPageSize() <= 20,
                ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
        req.setPriority(GOOD_APP);
        return BaseResponse.success(appService.queryAppPageList(req));
    }

        /**
         * 管理员删除应用
         */
    @Operation(summary = "管理员删除应用", description = "管理员删除应用")
    @PostMapping("/admin/delete")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody @Validated DeleteRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        App app = appService.getAppById(req.getId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        appService.deleteApp(req.getId());
        return BaseResponse.success();
    }

    /**
     * 管理员更新应用
     */
    @Operation(summary = "管理员更新应用", description = "管理员更新应用")
    @PostMapping("/admin/update")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Boolean> updateAppByAdmin(@RequestBody @Validated AppAdminUpdateRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        App app = appService.getAppById(req.getId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        app.setAppName(StrUtil.isBlank(req.getAppName()) ? app.getAppName() : req.getAppName());
        app.setCover(StrUtil.isBlank(req.getCover()) ? app.getCover() : req.getCover());
        app.setPriority(req.getPriority() == null ? app.getPriority() : req.getPriority());
        appService.updateApp(app);
        return BaseResponse.success();
    }

    /**
     * 管理员分页获取应用列表
     */
    @Operation(summary = "管理员分页获取应用列表", description = "管理员分页获取应用列表")
    @PostMapping("/admin/list/page")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 1, maxCount = 3)
    public BaseResponse<PageResponse<App>> listAppPageByAdmin(@RequestBody @Validated AppQueryPageListRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        return BaseResponse.success(appService.queryAppPageList(req));
    }

    /**
     * 管理员根据 id 获取应用详情
     */
    @Operation(summary = "管理员根据id获取应用详情", description = "管理员根据id获取应用详情")
    @PostMapping("/admin/getAppById")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 1, maxCount = 3)
    public BaseResponse<App> getAppVOByIdByAdmin(@RequestBody @Validated AppGetByIdRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        App app = appService.getAppById(req.getId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        return BaseResponse.success(app);
    }
}
