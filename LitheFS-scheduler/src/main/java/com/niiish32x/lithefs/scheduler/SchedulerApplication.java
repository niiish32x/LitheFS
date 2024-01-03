package com.niiish32x.lithefs.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class SchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
