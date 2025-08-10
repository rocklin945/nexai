package com.rocklin.nexai.mapper;

import com.rocklin.nexai.common.request.AppQueryPageListRequest;
import com.rocklin.nexai.model.entity.App;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppMapper {
    Long insert(App app);

    App queryAppById(Long appId);

    Long update(App app);

    Long delete(Long id);

    long countTotal(@Param("req") AppQueryPageListRequest req);

    List<App> listCurUserAppPage(@Param("req") AppQueryPageListRequest req,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);

    Long updateDeployAppInfo(App app);
}
