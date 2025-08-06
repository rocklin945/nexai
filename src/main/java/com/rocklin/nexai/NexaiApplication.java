package com.rocklin.nexai;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.rocklin.nexai.mapper")
public class NexaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexaiApplication.class, args);
    }

}
