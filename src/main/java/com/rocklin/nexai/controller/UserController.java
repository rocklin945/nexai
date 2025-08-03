package com.rocklin.nexai.controller;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.common.response.BaseResponse;
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
 * @ClassName UserController
 * @Description TODO
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
        Assert.isTrue(req!=null,ErrorCode.PARAMS_ERROR,"参数为空");
        Assert.isTrue(req.getUserPassword().equals(req.getCheckPassword()),
                ErrorCode.PARAMS_ERROR, "密码和校验密码不一致");
        userService.register(req);
        return BaseResponse.success();
    }

    /**
     * 登录
     */

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
