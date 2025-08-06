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
}
