package com.rocklin.nexai.controller;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.common.response.BaseResponse;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.model.vo.UserLoginVO;
import com.rocklin.nexai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.rocklin.nexai.common.constants.Constants.USER_ID;

/**
 * @ClassName UserController
 * @Description 用户相关模块
 * @Author: rocklin
 * @Date 2025/8/3 19:08
 * @Version 1.0
 */
@Tag(name = "用户", description = "用户相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     *注册
     */
    @Operation(summary = "注册", description = "注册用户")
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody @Validated UserRegisterRequest req) {
        Assert.notNull(req,ErrorCode.PARAMS_ERROR,"参数为空");
        Assert.isTrue(req.getUserPassword().equals(req.getCheckPassword()),
                ErrorCode.PARAMS_ERROR, "密码和校验密码不一致");
        userService.register(req);
        return BaseResponse.success();
    }

    /**
     * 登录
     */
    @Operation(summary = "登录", description = "用户登录")
    @PostMapping("/login")
    public BaseResponse<UserLoginResponse> login(@RequestBody @Validated UserLoginRequest req) {
        Assert.notNull(req,ErrorCode.PARAMS_ERROR,"参数为空");
        UserLoginResponse userLoginResponse =userService.login(req);
        return BaseResponse.success(userLoginResponse);
    }
    /**
     * 获取当前登录用户
     */
    @Operation(summary = "获取当前登录用户", description = "获取当前登录用户")
    @PostMapping("/getCurrentUser")
    public BaseResponse<UserLoginVO> getCurrentUser(@RequestAttribute(USER_ID) String userId) {
        Assert.notNull(userId, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        UserLoginResponse currentUser = userService.getCurrentUser(Long.valueOf(userId));
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(currentUser, userLoginVO);
        return BaseResponse.success(userLoginVO);
    }

    /**
     *登出
     */

    /**
     * 根据 id 获取包装类
     */

    /**
     * 管理员接口
     * 创建用户
     */

    /**
     * 管理员接口
     * 根据 id 获取用户
     */

    /**
     * 管理员接口
     * 删除用户
     */

    /**
     * 管理员接口
     * 更新用户
     */

    /**
     * 管理员接口
     * 分页获取用户封装列表
     */
}
