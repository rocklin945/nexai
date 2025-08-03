package com.rocklin.nexai.mapper;

import com.rocklin.nexai.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName UserMapper
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/3 19:21
 * @Version 1.0
 */
@Mapper
public interface UserMapper {
    User query(@Param("userAccount") String userAccount);

    Long insert(User user);
}
