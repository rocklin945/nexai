package com.rocklin.nexai.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String testUrl = "http://localhost:5173/api/static/multi_file_101500889851756680/";
        String webPageScreenshot = WebScreenshotUtils.saveWebPageScreenshot(testUrl);
        Assertions.assertNotNull(webPageScreenshot);
    }
}
