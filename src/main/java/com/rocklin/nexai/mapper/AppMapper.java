package com.rocklin.nexai.mapper;

import com.rocklin.nexai.model.entity.App;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppMapper {
    Long insert(App app);

    App queryAppById(Long appId);
}
