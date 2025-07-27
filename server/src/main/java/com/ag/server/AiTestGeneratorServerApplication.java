package com.attila.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AiTestGeneratorServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiTestGeneratorServerApplication.class, args);
    }
}
