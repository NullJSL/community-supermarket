package com.community.supermarket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.community.supermarket.mapper")
@EnableScheduling
public class CommunityMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityMarketApplication.class, args);
    }
}
