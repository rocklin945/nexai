package com.rocklin.nexai.service;

import com.rocklin.nexai.common.request.AppQueryPageListRequest;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.model.entity.App;
import reactor.core.publisher.Flux;

/**
 * @ClassName AppService
 * @Description 应用生成服务
 * @Author: rocklin
 * @Date 2025/8/5 20:49
 * @Version 1.0
 */
public interface AppService {
    Flux<String> chatToGenCode(Long appId, String message, Long userId);

    Long createApp(App app);

    String deployApp(App app);

    App getAppById(Long id);

    void updateApp(App app);

    void deleteApp(Long id);

    PageResponse<App> queryAppPageList(AppQueryPageListRequest req);

    void appCancelDeploy(App app);
}
