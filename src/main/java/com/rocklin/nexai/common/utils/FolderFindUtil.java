package com.rocklin.nexai.common.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.rocklin.nexai.common.constants.Constants.*;

/**
 * @ClassName FolderFindUtil
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/12 17:44
 * @Version 1.0
 */
public class FolderFindUtil {
    public static String findFolder(String appId) {
        long longAppId = Long.parseLong(appId);
        String projectDirName1 = HTML_FILE_PREFIX + longAppId;
        String projectDirName2 = MULTI_FILE_FILE_PREFIX + longAppId;
        // 根目录
        Path root = Paths.get(CODE_OUTPUT_ROOT_DIR);

        // 拼出两个目录路径
        Path dir1 = root.resolve(projectDirName1);
        Path dir2 = root.resolve(projectDirName2);

        File targetDir;
        // 优先找dir1
        File folder1 = dir1.toFile();
        if (folder1.exists() && folder1.isDirectory()) {
            // 找到projectDirName1，进行处理
            System.out.println("在目录 " + dir1 + " 找到了文件夹");
            targetDir=folder1;
        } else {
            // 没找到，去找projectDirName2
            File folder2 = dir2.toFile();
            if (folder2.exists() && folder2.isDirectory()) {
                System.out.println("在目录 " + dir2 + " 找到了文件夹");
                targetDir=folder2;
            } else {
                System.out.println("两个目录都不存在");
                // 处理目录都不存在的情况
                return CODE_OUTPUT_ROOT_DIR+File.separator+MULTI_FILE_FILE_PREFIX + longAppId;
            }
        }
        return targetDir.getAbsolutePath();
    }
}
