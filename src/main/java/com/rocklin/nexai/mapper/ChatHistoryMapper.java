package com.rocklin.nexai.mapper;

import com.rocklin.nexai.common.request.ChatHistoryQueryRequest;
import com.rocklin.nexai.model.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatHistoryMapper {
    Long insert(ChatHistory chatHistory);

    List<ChatHistory> selectListByAppId(@Param("appId") long appId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    List<ChatHistory> selectAppChatHistoryByCursor(@Param("req") ChatHistoryQueryRequest req);

    long countTotal(@Param("req") ChatHistoryQueryRequest req);

    List<ChatHistory> selectAppChatHistoryByAdmin(@Param("offset") int offset,
                                                  @Param("req") ChatHistoryQueryRequest req);

    Long deleteChatHistoryByAppId(Long id);

    Long deleteChatHistoryById(Long id);
}
