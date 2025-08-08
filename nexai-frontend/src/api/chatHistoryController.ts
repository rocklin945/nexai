// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 分页查询应用的对话历史 分页查询应用的对话历史 POST /chatHistory/historyPageList */
export async function listAppChatHistory(
  body: API.ChatHistoryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResponseChatHistory>(
    "/chatHistory/historyPageList",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/**管理员分页查询对话记录 */
export async function listAppChatHistoryByAdmin(
  body: API.ChatHistoryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResponseChatHistory>(
    "/chatHistory/admin/historyPageList",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}
