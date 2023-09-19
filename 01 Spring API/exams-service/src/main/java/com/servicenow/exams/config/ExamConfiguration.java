package com.servicenow.exams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ExamConfiguration {

    @Bean
    public WebClient webClient(@org.springframework.beans.factory.annotation.Value("${services.math.url}") String mathUrl) {
        // pay attention - its not lombok @Value, its spring @Value!
        return WebClient.builder().baseUrl(mathUrl + "/questions/").build();
    }

    @Bean
    public HistoryClient historyClient(@org.springframework.beans.factory.annotation.Value("${services.history.url}") String historyUrl) {
        WebClient historyClient = WebClient.builder().baseUrl(historyUrl + "/questions/").build();
        return HttpServiceProxyFactory
                .builder(WebClientAdapter
                        .forClient(historyClient))
                .build().createClient(HistoryClient.class);

    }


}
