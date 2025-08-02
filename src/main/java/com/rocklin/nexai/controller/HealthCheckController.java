package com.rocklin.nexai.controller;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HealthCheckController
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/2 9:58
 * @Version 1.0
 */
@Tag(name = "健康检查", description = "健康检查相关接口")
@RestController
@RequestMapping("/health")
public class HealthCheckController {
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    @GetMapping("/check")
    public BaseResponse<String> check() {
        return BaseResponse.success("服务正常");
    }

    @Operation(summary = "异常测试接口", description = "异常测试接口")
    @GetMapping("/test-error")
    public BaseResponse<String> error() {
        throw new RuntimeException("测试异常");
    }
}
