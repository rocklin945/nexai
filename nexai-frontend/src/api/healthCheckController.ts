// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 健康检查 检查服务是否正常运行 GET /health/check */
export async function check(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>("/health/check", {
    method: "GET",
    ...(options || {}),
  });
}

/** 异常测试接口 异常测试接口 GET /health/test-error */
export async function error(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>("/health/test-error", {
    method: "GET",
    ...(options || {}),
  });
}
