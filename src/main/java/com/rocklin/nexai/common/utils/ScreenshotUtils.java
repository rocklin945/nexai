package com.rocklin.nexai.common.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.rocklin.nexai.common.constants.Constants.*;

@Slf4j
public class ScreenshotUtils {
    private static final String MICROLINK_API = "https://api.microlink.io?url=";

    /**
     * 保存网页截图到本地
     * @param webUrl 要截图的网址
     * @return 本地压缩图片路径 + 根目录
     */
    public static String saveWebPageScreenshot(String webUrl) {
        if (StrUtil.isBlank(webUrl)) {
            log.error("网页截图失败，url为空");
            return null;
        }

        // 创建本地存储路径
        String rootPath = SCREENSHOT_SAVE_PATH + UUID.randomUUID().toString().substring(0, 8);
        FileUtil.mkdir(rootPath);

            try {
                String apiUrl = MICROLINK_API + webUrl
                        + "&screenshot=true"
                        + "&waitUntil=networkidle2"
                        + "&waitForSelector=img"
                        + "&viewport.width=1600"
                        + "&viewport.height=900"
                        + "&waitForTimeout=8000";
                String jsonResponse = httpGet(apiUrl);

                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                if (!"success".equals(jsonObject.get("status").getAsString())) {
                    log.error("Microlink API 返回失败：{}", jsonResponse);
                    return null;
                }

                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonObject screenshot = null;

                // 优先使用 screenshot 字段
                if (data.has("screenshot") && !data.get("screenshot").isJsonNull()) {
                    screenshot = data.getAsJsonObject("screenshot");
                } else if (data.has("image") && !data.get("image").isJsonNull()) { // fallback 到 image
                    screenshot = data.getAsJsonObject("image");
                }

                if (screenshot == null || !screenshot.has("url") || screenshot.get("url").isJsonNull()) {
                    log.error("截图 URL 不存在，返回内容：{}", jsonResponse);
                    return null;
                }

                String imageUrlOrBase64 = screenshot.get("url").getAsString();
                log.info("获取截图 URL/Base64 成功");

                String localImagePath = rootPath + "/" + RandomUtil.randomNumbers(5) + PNG;

                // 判断是否 Base64
                if (imageUrlOrBase64.startsWith("data:image")) {
                    String base64Data = imageUrlOrBase64.substring(imageUrlOrBase64.indexOf(",") + 1);
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    try (FileOutputStream out = new FileOutputStream(localImagePath)) {
                        out.write(imageBytes);
                    }
                } else {
                    downloadFile(imageUrlOrBase64, localImagePath);
                }

                log.info("截图保存成功：{}", localImagePath);

                // 压缩图片
                String compressedImagePath = rootPath + "/" + RandomUtil.randomNumbers(5) + SCREENSHOT_COMPRESSED_SUFFIX;
                compressImage(localImagePath, compressedImagePath);
                log.info("压缩图片保存成功：{}", compressedImagePath);

                // 删除原图
                FileUtil.del(localImagePath);

                return compressedImagePath + COMMA + rootPath;

            } catch (Exception e) {
                log.warn("截图失败：{}", e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        log.error("网页截图最终失败：{}", webUrl);
        FileUtil.del(rootPath);
        return null;
    }

    // GET 请求
    private static String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(30000); // 30秒连接超时
        connection.setReadTimeout(60000);    // 60秒读取超时

        try (InputStream in = connection.getInputStream()) {
            return new String(in.readAllBytes());
        } finally {
            connection.disconnect();
        }
    }

    // 下载文件
    private static void downloadFile(String fileUrl, String localPath) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(localPath)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            conn.disconnect();
        }
    }

    // 压缩图片
    private static void compressImage(String originImagePath, String compressedImagePath) {
        final float COMPRESSION_QUALITY = 0.3f; // 压缩质量30%
        try {
            ImgUtil.compress(FileUtil.file(originImagePath),
                    FileUtil.file(compressedImagePath),
                    COMPRESSION_QUALITY);
        } catch (Exception e) {
            log.error("压缩图片失败：{} -> {}", originImagePath, compressedImagePath, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "压缩图片失败");
        }
    }
}
