package com.acars.acarsforwarder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AcarsForwarderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcarsForwarderApplication.class, args);
    }

}
