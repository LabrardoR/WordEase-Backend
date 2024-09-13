package com.head.wordeasebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.head.wordeasebackend.mapper")
@EnableScheduling
public class WordEaseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordEaseBackendApplication.class, args);
    }

}
