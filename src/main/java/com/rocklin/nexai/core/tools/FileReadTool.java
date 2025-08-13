package com.rocklin.nexai.core.tools;

import cn.hutool.json.JSONObject;
import com.rocklin.nexai.common.utils.FolderFindUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件读取工具
 * 支持 AI 通过工具调用的方式读取文件内容
 */
@Slf4j
@Component
public class FileReadTool extends BaseTool {

    @Tool("读取指定路径的文件内容")
    public String readFile(
            @P("文件的路径")
            String filePath,
            @ToolMemoryId @P("应用 ID")
            String appId
    ) {
        try {
            String folder = FolderFindUtil.findFolder(appId);
            Path path = Paths.get(folder+ File.separator +filePath);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return "错误：文件不存在或不是文件 - " + filePath;
            }
            return Files.readString(path);
        } catch (IOException e) {
            String errorMessage = "读取文件失败: " + filePath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    @Override
    public String getToolName() {
        return "readFile";
    }

    @Override
    public String getDisplayName() {
        return "读取文件";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String filePath = arguments.getStr("filePath");
        return String.format("[工具调用] %s %s", getDisplayName(), filePath);
    }
} 