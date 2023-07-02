package com.study.boardproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan //thymeleaf conf 오류해결
@SpringBootApplication
public class BoardProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardProjectApplication.class, args);
    }

}
