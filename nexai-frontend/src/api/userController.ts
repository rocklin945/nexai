// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 创建用户 管理员接口，创建用户 POST /user/create */
export async function createUser(
  body: API.UserCreateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>("/user/create", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除用户 管理员接口，删除用户 POST /user/delete */
export async function deleteUser(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteUserParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 根据 id 获取用户 管理员接口，根据 id 获取用户 GET /user/getById */
export async function getById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUser>("/user/getById", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取当前登录用户 获取当前登录用户 POST /user/getCurrentUser */
export async function getCurrentUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserLoginVO>("/user/getCurrentUser", {
    method: "POST",
    ...(options || {}),
  });
}

/** 分页获取用户列表 管理员接口，分页获取用户列表 POST /user/list/page */
export async function listUserByPage(
  body: API.UserPageQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResponseUserLoginVO>("/user/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 登录 用户登录 POST /user/login */
export async function login(
  body: API.UserLoginRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserLoginResponse>("/user/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 登出 用户登出 POST /user/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.BaseResponseVoid>("/user/logout", {
    method: "POST",
    ...(options || {}),
  });
}

/** 注册 注册用户 POST /user/register */
export async function register(
  body: API.UserRegisterRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>("/user/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新用户 管理员接口，更新用户 POST /user/update */
export async function updateUser(
  body: API.UpdateUserRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
