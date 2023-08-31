package com.servicenow.exams.exams;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface HistoryClient {

    @PostExchange("/random")
    Mono<List<Question>> getQuestions(@RequestParam("amount") int amount);
}
