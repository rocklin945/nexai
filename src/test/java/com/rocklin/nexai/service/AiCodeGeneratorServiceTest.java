package com.rocklin.nexai.service;

import com.rocklin.nexai.core.result.HtmlCodeResult;
import com.rocklin.nexai.core.result.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(0L,"做个rocklin的博客，不超过 20 行");
        System.out.println(result.getHtmlCode());
        System.out.println(result.getDescription());
        Assertions.assertNotNull(result);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(0L,"做个博客网站，不超过 50 行");
        System.out.println(result.getHtmlCode());
        System.out.println(result.getCssCode());
        System.out.println(result.getJsCode());
        System.out.println(result.getDescription());
        Assertions.assertNotNull(result);
    }

    @Test
    void testChatMemory() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(0L,"做个个人博客网站，总代码量不超过 20 行");
        Assertions.assertNotNull(result);
        result = aiCodeGeneratorService.generateHtmlCode(0L,"不要生成网站，告诉我你刚刚做了什么？");
        Assertions.assertNotNull(result);
        result = aiCodeGeneratorService.generateHtmlCode(0L,"做个登录页面，总代码量不超过 20 行");
        Assertions.assertNotNull(result);
        result = aiCodeGeneratorService.generateHtmlCode(0L,"不要生成网站，告诉我你刚刚做了什么？");
        Assertions.assertNotNull(result);
    }
}
