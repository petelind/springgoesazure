package com.servicenow.exams;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class ExamsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamsServiceApplication.class, args);
    }

}
