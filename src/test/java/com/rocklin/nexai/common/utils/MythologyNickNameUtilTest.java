package com.rocklin.nexai.common.utils;

import org.junit.jupiter.api.Test;

/**
 * @ClassName MythologyNickNameUtilTest
 * @Description 随机名称生成测试类
 * @Author: rocklin
 * @Date 2025/8/5 17:26
 * @Version 1.0
 */
public class MythologyNickNameUtilTest {
    @Test
    public void testGenerateNickname() {
        for (int i = 0; i < 10; i++) {
            System.out.println(MythologyNicknameUtil.generateNickname());
        }
    }
}
