package com.rocklin.nexai.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.rocklin.nexai.common.enums.ChatHistoryMessageTypeEnum;
import com.rocklin.nexai.common.enums.CodeGenTypeEnum;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.request.AppQueryPageListRequest;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.core.AiCodeGeneratorFacade;
import com.rocklin.nexai.core.handler.StreamHandlerExecutor;
import com.rocklin.nexai.mapper.AppMapper;
import com.rocklin.nexai.model.entity.App;
import com.rocklin.nexai.service.AppService;
import com.rocklin.nexai.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.FileUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.rocklin.nexai.common.constants.Constants.*;

/**
 * @ClassName AppServiceImpl
 * @Description ai应用生成
 * @Author: rocklin
 * @Date 2025/8/5 20:49
 * @Version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {

    private final AppMapper appMapper;
    private final ChatHistoryService chatHistoryService;
    private final AiCodeGeneratorFacade aiCodeGeneratorFacade;
    private final StreamHandlerExecutor streamHandlerExecutor;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, Long userId) {
        //查询应用
        App app = appMapper.queryAppById(appId);
        Assert.notNull(app, ErrorCode.OPERATION_ERROR, "应用不存在");
        Assert.isTrue(app.getUserId().equals(userId), ErrorCode.OPERATION_ERROR, "无权限操作");
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(app.getCodeGenType());
        Assert.notNull(codeGenTypeEnum, ErrorCode.OPERATION_ERROR, "不支持的生成模式");
        //在调用 AI 前，先保存用户消息到数据库中
        chatHistoryService.createHistory(appId, message,
                ChatHistoryMessageTypeEnum.USER.getValue(), userId);
        //调用 AI 生成代码（流式）
        Flux<String> codeFlux = aiCodeGeneratorFacade
                .generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        //收集 AI 响应的内容，并且在完成后保存记录到对话历史
        return streamHandlerExecutor.doExecute(codeFlux, chatHistoryService, appId, userId, codeGenTypeEnum);
    }

    @Override
    public Long createApp(App app) {
        Long result = appMapper.insert(app);
        Assert.isTrue(result > 0, "创建应用失败");
        return app.getId();
    }

    @Override
    public String deployApp(App app) {
        String deployKey = app.getDeployKey();
        if(StrUtil.isBlank(deployKey)){
            deployKey = RandomUtil.randomString(6);
        }
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + app.getId();
        String sourceDirPath = CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        //检查源代码目录是否存在
        File sourceDir = new File(sourceDirPath);
        Assert.isTrue(sourceDir.exists() && sourceDir.isDirectory(),
                ErrorCode.INTERNAL_SERVER_ERROR, "应用代码路径不存在，请先生成应用");
        String deployDirPath = CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        //复制文件到部署目录
        try {
            FileUtil.copyDir(sourceDir, new File(deployDirPath));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "应用部署失败：" + e.getMessage());
        }
        //更新app信息
        app.setDeployKey(deployKey);
        app.setDeployedTime(LocalDateTime.now());
        Long result = appMapper.updateDeployAppInfo(app);
        Assert.isTrue(result > 0, ErrorCode.OPERATION_ERROR, "数据库异常，更新应用失败");
        return String.format("%s/%s", CODE_DEPLOY_HOST, deployKey);
    }

    @Override
    public App getAppById(Long id) {
        return appMapper.queryAppById(id);
    }

    @Override
    public void updateApp(App app) {
        Long result = appMapper.update(app);
        Assert.isTrue(result > 0, ErrorCode.OPERATION_ERROR, "数据库异常，更新应用失败");
    }

    @Override
    public void deleteApp(Long id) {
        Long result = appMapper.delete(id);
        Assert.isTrue(result > 0, ErrorCode.OPERATION_ERROR, "数据库异常，删除应用失败");
    }

    @Override
    public PageResponse<App> queryAppPageList(AppQueryPageListRequest req) {
        int offset = (req.getPageNum() - 1) * req.getPageSize();
        //总数据量，不是当前页数据量
        long total = appMapper.countTotal(req);
        //当前页数据
        List<App> list = appMapper.listCurUserAppPage(req,offset,req.getPageSize());
        return new PageResponse<>(list, total, req.getPageNum(), req.getPageSize());
    }
}
