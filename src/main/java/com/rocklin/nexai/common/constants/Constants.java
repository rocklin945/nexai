package com.rocklin.nexai.common.constants;

/**
 * @ClassName Constants
 * @Description 通用常量
 * @Author: rocklin
 * @Date 2025/8/2 10:37
 * @Version 1.0
 */
public final class Constants {
    public static final String SUCCESS = "success";

    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN = "token";
    public static final String BEARER = "Bearer ";
    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";

    /**
     * JWT
     */
    public static final int TOKEN_START_INDEX = 7;

    public static final String CHUNK_DATA = "d";

    public static final int OFFSET = 0;
    public static final int MAX_CONTEXT_SIZE = 20;

    /**
     * 文件保存的根目录
     */
    public static final String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir")
            + "/tmp/code_output";
    /**
     * 精选应用
     */
    public static final Integer GOOD_APP = 1;

    /**
     * 网站预览相关
     */
    public static final String STATIC_PATH = "/static/";
    public static final String LOCATION = "Location";
    public static final String REDIRECT = "/";
    public static final String INDEX_HTML = "/index.html";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String HTML = ".html";
    public static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    public static final String CSS = ".css";
    public static final String CSS_CONTENT_TYPE = "text/css; charset=UTF-8";
    public static final String JS = ".js";
    public static final String JS_CONTENT_TYPE = "application/javascript; charset=UTF-8";
    public static final String PNG = ".png";
    public static final String PNG_CONTENT_TYPE = "image/png";
    public static final String JPG = ".jpg";
    public static final String JPG_CONTENT_TYPE = "image/jpeg";
    public static final String OCTET_STREAM = "application/octet-stream";
}
