package com.rocklin.nexai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rocklin.nexai.mapper")
public class NexaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexaiApplication.class, args);
    }

}
