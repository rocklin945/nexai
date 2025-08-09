package com.rocklin.nexai.controller;

import com.rocklin.nexai.common.annotation.AuthCheck;
import com.rocklin.nexai.common.annotation.SlidingWindowRateLimit;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.enums.UserRoleEnum;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.request.*;
import com.rocklin.nexai.common.response.BaseResponse;
import com.rocklin.nexai.common.utils.AvatarUtil;
import com.rocklin.nexai.common.utils.EncryptPasswordUtil;
import com.rocklin.nexai.common.utils.MythologyNicknameUtil;
import com.rocklin.nexai.model.entity.User;
import com.rocklin.nexai.model.vo.UserGoodAppVo;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.model.vo.UserLoginVO;
import com.rocklin.nexai.service.UserService;
import com.rocklin.nexai.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private final EncryptPasswordUtil encryptPasswordUtil;

    /**
     * 注册
     */
    @Operation(summary = "注册", description = "注册用户")
    @PostMapping("/register")
    @SlidingWindowRateLimit()
    public BaseResponse<Long> register(@RequestBody @Validated UserRegisterRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
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
    @SlidingWindowRateLimit()
    public BaseResponse<UserLoginResponse> login(@RequestBody @Validated UserLoginRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        UserLoginResponse userLoginResponse = userService.login(req);
        return BaseResponse.success(userLoginResponse);
    }

    /**
     * 获取当前登录用户
     */
    @Operation(summary = "获取当前登录用户", description = "获取当前登录用户")
    @PostMapping("/getCurrentUser")
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<UserLoginVO> getCurrentUser(@RequestAttribute(USER_ID) String userId) {
        Assert.notNull(userId, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        UserLoginResponse currentUser = userService.getCurrentUser(Long.valueOf(userId));
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(currentUser, userLoginVO);
        return BaseResponse.success(userLoginVO);
    }

    /**
     * 登出
     */
    @Operation(summary = "登出", description = "用户登出")
    @PostMapping("/logout")
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Void> logout(@RequestAttribute(USER_ID) String userId) {
        Assert.notNull(userId, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        userService.logout(Long.valueOf(userId));
        return BaseResponse.success();
    }

    /**
     * 管理员接口
     * 创建用户
     */
    @Operation(summary = "创建用户", description = "管理员接口，创建用户")
    @PostMapping("/create")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit()
    public BaseResponse<Long> createUser(@RequestBody @Validated UserCreateRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "参数为空");
        User user = new User();
        BeanUtils.copyProperties(req, user);
        user.setUserName(MythologyNicknameUtil.generateNickname());
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = encryptPasswordUtil.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        user.setUserAvatar(AvatarUtil.generateRandomAvatarUrl(req.getUserAccount()));
        return BaseResponse.success(userService.createUser(user));
    }

    /**
     * 管理员接口
     * 根据 id 获取用户
     */
    @Operation(summary = "根据 id 获取用户", description = "管理员接口，根据 id 获取用户")
    @GetMapping("/getById")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<User> getById(@RequestParam Long id) {
        Assert.notNull(id, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        return BaseResponse.success(userService.getUserById(id));
    }

    /**
     * 管理员接口
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "管理员接口，删除用户")
    @PostMapping("/delete")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Boolean> deleteUser(@RequestParam Long id) {
        Assert.notNull(id, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        User userById = userService.getUserById(id);
        Assert.notNull(userById, ErrorCode.OPERATION_ERROR, "用户不存在");
        userService.deleteUser(id);
        return BaseResponse.success();
    }

    /**
     * 管理员接口
     * 更新用户
     */
    @Operation(summary = "更新用户", description = "管理员接口，更新用户")
    @PostMapping("/update")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<Boolean> updateUser(@RequestBody @Validated UpdateUserRequest req) {
        Assert.notNull(req, ErrorCode.PARAMS_ERROR, "用户更新数据不能为空");
        User userById = userService.getUserById(req.getId());
        Assert.notNull(userById, ErrorCode.OPERATION_ERROR, "用户不存在");
        userService.updateUser(req);
        return BaseResponse.success();
    }

    /**
     * 管理员接口
     * 分页获取用户封装列表
     */
    @Operation(summary = "分页获取用户列表", description = "管理员接口，分页获取用户列表")
    @PostMapping("/list/page")
    @AuthCheck(enableRole = UserRoleEnum.ADMIN)
    @SlidingWindowRateLimit(windowInSeconds = 10, maxCount = 3)
    public BaseResponse<PageResponse<UserLoginVO>> listUserByPage(@RequestBody @Validated UserPageQueryRequest req) {
        return BaseResponse.success(userService.listUserByPageWithFilter(req));
    }

    /**
     * 获取精选作品的用户信息
     */
    @Operation(summary = "获取精选作品用户信息", description = "获取精选作品用户信息")
    @PostMapping("/getGoodAppUserInfo")
    @SlidingWindowRateLimit(windowInSeconds = 1, maxCount = 2)
    public BaseResponse<List<UserGoodAppVo>> getGoodAppUserInfo(@RequestBody List<Long> ids) {
        Assert.notNull(ids, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        List<User> userByIdList = userService.batchGetUserById(ids);
        List<UserGoodAppVo> userGoodAppVoList = userByIdList.stream().map(user -> {
            UserGoodAppVo userGoodAppVo = new UserGoodAppVo();
            userGoodAppVo.setId(user.getId());
            userGoodAppVo.setUserName(user.getUserName());
            userGoodAppVo.setUserAvatar(user.getUserAvatar());
            return userGoodAppVo;
        }).collect(Collectors.toList());
        return BaseResponse.success(userGoodAppVoList);
    }
}
