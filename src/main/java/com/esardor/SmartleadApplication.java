package com.esardor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartleadApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartleadApplication.class, args);
    }

}
