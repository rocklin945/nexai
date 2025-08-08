package com.rocklin.nexai.controller;

import com.rocklin.nexai.common.annotation.AuthCheck;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.enums.UserRoleEnum;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.request.ChatHistoryQueryRequest;
import com.rocklin.nexai.common.response.BaseResponse;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.model.entity.App;
import com.rocklin.nexai.model.entity.ChatHistory;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.service.AppService;
import com.rocklin.nexai.service.ChatHistoryService;
import com.rocklin.nexai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ChatHistoryController
 * @Description 聊天记录控制器
 * @Author: rocklin
 * @Date 2025/8/7 20:55
 * @Version 1.0
 */
@Tag(name = "对话记录", description = "对话记录相关接口")
@RestController
@RequestMapping("/chatHistory")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;
    private final UserService userService;
    private final AppService appService;

    /**
     *分页查询某个应用的对话历史(根据最后一条记录的创建时间进行游标查询)
     */
    @Operation(summary = "分页查询应用的对话历史", description ="分页查询应用的对话历史")
    @PostMapping("/historyPageList")
    public BaseResponse<PageResponse<ChatHistory>> listAppChatHistory(@RequestBody @Validated ChatHistoryQueryRequest req){
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        Assert.isTrue(req.getPageSize()>0 && req.getPageSize()<=50, ErrorCode.PARAMS_ERROR,
                "页面大小必须在1-50之间");
        UserLoginResponse currentUser = userService.getCurrentUser();
        Assert.notNull(currentUser, ErrorCode.NOT_LOGIN_ERROR, "未登录");
        Long userId = currentUser.getUserId();
        App app = appService.getAppById(req.getAppId());
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        boolean isGoodApp = app.getPriority() == 99;
        boolean isAdmin = currentUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue());
        Assert.isTrue(isGoodApp || isAdmin || app.getUserId().equals(userId),
                ErrorCode.OPERATION_ERROR, "无权限");
        if(!isAdmin && !isGoodApp){
            Assert.notNull(req.getUserId(), ErrorCode.PARAMS_ERROR, "用户id不能为空");
            req.setUserId(userId);
        }
        PageResponse<ChatHistory> pageResponse =chatHistoryService.listAppChatHistoryByPage(req);
        return BaseResponse.success(pageResponse);
    }

    /**
     * 管理员分页查询所有应用的对话历史
     */
    @Operation(summary = "管理员分页查询所有应用的对话历史", description ="管理员分页查询所有应用的对话历史")
    @PostMapping("/admin/historyPageList")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    public BaseResponse<PageResponse<ChatHistory>> listAllAppChatHistory(@RequestBody @Validated ChatHistoryQueryRequest req){
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        PageResponse<ChatHistory> pageResponse =chatHistoryService.listAppChatHistoryByAdmin(req);
        return BaseResponse.success(pageResponse);
    }
}
