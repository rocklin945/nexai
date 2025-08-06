// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 管理员删除应用 管理员删除应用 POST /app/admin/delete */
export async function deleteAppByAdmin(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/app/admin/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 管理员根据id获取应用详情 管理员根据id获取应用详情 POST /app/admin/getAppById */
export async function getAppVoByIdByAdmin(
  body: API.AppGetByIdRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseApp>("/app/admin/getAppById", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 管理员分页获取应用列表 管理员分页获取应用列表 POST /app/admin/list/page */
export async function listAppPageByAdmin(
  body: API.AppQueryPageListRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResponseApp>("/app/admin/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 管理员更新应用 管理员更新应用 POST /app/admin/update */
export async function updateAppByAdmin(
  body: API.AppAdminUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/app/admin/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 对话生成代码 对话生成代码 POST /app/chat/gen/code */
export async function chatToGenCode(
  body: API.ChatToGenCodeRequest,
  options?: { [key: string]: any }
) {
  return request<API.ServerSentEventString[]>("/app/chat/gen/code", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 创建应用 创建应用 POST /app/create */
export async function createApp(
  body: API.AppCreateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>("/app/create", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页获取当前用户应用列表 分页获取当前用户应用列表 POST /app/curUser/list/page */
export async function listCurUserAppPage(
  body: API.AppQueryPageListRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResponseApp>("/app/curUser/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除应用 删除应用 POST /app/delete */
export async function deleteApp(
  body: API.AppDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/app/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 应用部署 应用部署 POST /app/deploy */
export async function deployApp(
  body: API.AppDeployRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseString>("/app/deploy", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据appId获取应用详情 根据appId获取应用详情 POST /app/getById */
export async function getAppById(
  body: API.AppGetByIdRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseApp>("/app/getById", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页获取精选的应用列表 分页获取精选的应用列表 POST /app/good/list/page */
export async function listGoodAppByPage(
  body: API.AppQueryPageListRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResponseApp>("/app/good/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新应用 更新应用 POST /app/update */
export async function updateApp(
  body: API.AppUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/app/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
