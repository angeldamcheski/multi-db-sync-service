package com.example.multidbsyncservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MultiDbSyncServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiDbSyncServiceApplication.class, args);
    }

}
