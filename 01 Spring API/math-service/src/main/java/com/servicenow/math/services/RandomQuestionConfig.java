package com.servicenow.math.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RandomQuestionConfig {

    @Bean
    public RandomQuestionGenerator randomQuestionGenerator() {
        return new RandomQuestionGenerator();
    }

}
