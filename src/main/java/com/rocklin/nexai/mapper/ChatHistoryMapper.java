package com.rocklin.nexai.mapper;

import com.rocklin.nexai.model.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatHistoryMapper {
    Long insert(ChatHistory chatHistory);

    List<ChatHistory> selectListByAppId(@Param("appId") long appId, @Param("offset") int offset, @Param("limit") int limit);
}
