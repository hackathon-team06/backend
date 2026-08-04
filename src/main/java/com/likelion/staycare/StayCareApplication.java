package com.likelion.staycare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing
public class StayCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(StayCareApplication.class, args);
    }
}
