package com.governance.evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.governance.evaluation.repository")
@EntityScan(basePackages = "com.governance.evaluation.entity")
public class EvaluationPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvaluationPlatformApplication.class, args);
    }
}