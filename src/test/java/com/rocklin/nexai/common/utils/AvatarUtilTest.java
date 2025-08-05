package com.rocklin.nexai.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AvatarUtilTest {

    @Test
    public void testGenerateRandomAvatarUrl_NotNullAndCorrectPrefix() {
        String seed = "testUser";
        String avatarUrl = AvatarUtil.generateRandomAvatarUrl(seed);

        // 断言返回的URL不为空
        assertNotNull(avatarUrl, "生成的头像 URL 不能为空");

        // 判断是否以合法前缀开头
        boolean hasValidPrefix = avatarUrl.startsWith("https://api.dicebear.com/9.x/adventurer-neutral/svg?seed=") ||
                                 avatarUrl.startsWith("https://api.dicebear.com/9.x/thumbs/svg?seed=");
        assertTrue(hasValidPrefix, "URL 前缀不合法：" + avatarUrl);

        // 判断 seed 是否包含在末尾
        assertTrue(avatarUrl.endsWith(seed), "URL 中应包含 seed：" + seed);
    }

    @Test
    public void testMultipleGenerations_ProduceDifferentPrefixes() {
        String seed = "user123";
        boolean hasThumbs = false;
        boolean hasAdventurer = false;

        // 尝试生成多次，看是否能覆盖两个随机值
        for (int i = 0; i < 20; i++) {
            String url = AvatarUtil.generateRandomAvatarUrl(seed);
            System.out.println("url = " + url);
            if (url.contains("thumbs")) {
                hasThumbs = true;
            }
            if (url.contains("adventurer-neutral")) {
                hasAdventurer = true;
            }
        }

        assertTrue(hasThumbs, "应至少生成过 thumbs 风格头像");
        assertTrue(hasAdventurer, "应至少生成过 adventurer-neutral 风格头像");
    }
}
