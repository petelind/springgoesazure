package com.servicenow.exams.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Bean
    public OpenAPI springOpenAPI() {
        Info apiInfo = new Info()
                .title("ExamDirect API")
                .description("Get new exam questions and answers")
                .version("v0.0.1");

        OpenAPI openAPI = new OpenAPI()
                .info(apiInfo);

        return openAPI;
    }
}
