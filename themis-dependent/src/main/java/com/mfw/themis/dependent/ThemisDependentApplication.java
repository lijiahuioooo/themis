package com.mfw.themis.dependent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ThemisDependentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThemisDependentApplication.class, args);
    }

}
