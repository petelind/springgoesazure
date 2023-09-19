package com.servicenow.exams.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Component
public class DownstreamHealthCheck implements HealthIndicator {

    private final RestTemplate restTemplate;
    private final String mathServiceUrl;
    private final String historyServiceUrl;

    public DownstreamHealthCheck(RestTemplate restTemplate,
                                 @Value("${services.math.url}") String mathServiceUrl,
                                 @Value("${services.history.url}") String historyServiceUrl) {
        this.restTemplate = restTemplate;
        this.mathServiceUrl = mathServiceUrl;
        this.historyServiceUrl = historyServiceUrl;
    }

    @Override
    public Health health() {
        try {
            ResponseEntity<Void> mathResponse = restTemplate.getForEntity(mathServiceUrl, Void.class);
            ResponseEntity<Void> historyResponse = restTemplate.getForEntity(historyServiceUrl, Void.class);

            HttpStatus mathStatus = (HttpStatus) mathResponse.getStatusCode();
            HttpStatus historyStatus = (HttpStatus) historyResponse.getStatusCode();

            if (mathStatus == HttpStatus.OK && historyStatus == HttpStatus.OK) {
                return Health.up().withDetail("message", "Both services are healthy").build();
            } else {
                return Health.down().withDetail("message", "One or both services are not healthy").build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("message", "One or both services are not available").build();
        }
    }
}
