package com.rocklin.nexai.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nexai API")
                        .description("Nexai项目API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("rocklin")
                                .url("https://github.com/rocklin945/nexai")));
    }
}