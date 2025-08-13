package com.rocklin.nexai.core.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.rocklin.nexai.common.utils.FolderFindUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * 文件目录读取工具
 * 使用 Hutool 简化文件操作
 */
@Slf4j
@Component
public class FileDirReadTool extends BaseTool {

    @Tool("读取目录结构，获取指定目录下的所有文件和子目录信息")
    public String readDir(@P("目录的路径，为空则读取整个项目结构")
                          String dirPath,
                          @P("appId") String appId) {
        try {
            String folder = FolderFindUtil.findFolder(appId);
            File targetDir = new File(folder);
            StringBuilder structure = new StringBuilder();
            structure.append("项目目录结构:\n");
            // 使用 Hutool 递归获取所有文件
            List<File> allFiles = FileUtil.loopFiles(targetDir);
            // 按路径深度和名称排序显示
            allFiles.stream().sorted((f1, f2) -> {
                int depth1 = getRelativeDepth(targetDir, f1);
                int depth2 = getRelativeDepth(targetDir, f2);
                if (depth1 != depth2) {
                    return Integer.compare(depth1, depth2);
                }
                return f1.getPath().compareTo(f2.getPath());
            }).forEach(file -> {
                int depth = getRelativeDepth(targetDir, file);
                String indent = "  ".repeat(depth);
                structure.append(indent).append(file.getName()).append("\n");
            });
            return structure.toString();
        } catch (Exception e) {
            String errorMessage = "读取目录结构失败: " + dirPath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }
    }

    /**
     * 计算文件相对于根目录的深度
     */
    private int getRelativeDepth(File root, File file) {
        Path rootPath = root.toPath();
        Path filePath = file.toPath();
        return rootPath.relativize(filePath).getNameCount() - 1;
    }

    @Override
    public String getToolName() {
        return "readDir";
    }

    @Override
    public String getDisplayName() {
        return "读取目录";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String dirPath = arguments.getStr("dirPath");
        if (StrUtil.isEmpty(dirPath)) {
            dirPath = "根目录";
        }
        return String.format("[工具调用] %s %s", getDisplayName(), dirPath);
    }
} 