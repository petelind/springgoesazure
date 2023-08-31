package com.servicenow.exams.controllers;

import com.servicenow.exams.exams.Exam;
import com.servicenow.exams.exams.ExamRepository;
import com.servicenow.exams.exams.HistoryClient;
import com.servicenow.exams.exams.Question;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class ExamController {

    private final ExamRepository examRepository;
    private final WebClient webClient;
    private final HistoryClient historyClient;

    @Autowired
    public ExamController(ExamRepository examRepository, WebClient webClient, HistoryClient historyClient) {
        this.examRepository = examRepository;
        this.webClient = webClient;
        this.historyClient = historyClient;
    }

    @GetMapping("/exams/all")
    public List<Exam> getAllExams() {
        return (List<Exam>) examRepository.findAll();
    }

    @PostMapping("/exams/history")
    @Transactional
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    // Todo: fix both controllers to accept all required parameters and make them handle exceptions
    public void createHistoryExam() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet.");
    }



    @PostMapping("/exams/math")
    @Transactional
    // Todo: fix both controllers to accept all required parameters and make them handle exceptions
    public Mono<Exam> createMathExam() {
        // This is one way of accessing data in another service - via webClient (works for older Spring).
        return webClient.post().uri("/random?amount=2")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(Question.class)
                .collectList()
                .map(questions -> {
                    questions.forEach(System.out::println);
                    String questionIds = questions.stream()
                            .map(Question::Id)
                            .map(String::valueOf) // Convert to string
                            .collect(Collectors.joining(","));
                            Exam exam = new Exam();
                            exam.setName("Exam");
                            exam.setQuestions(questionIds);
                            exam.setDescription("Exam description");

                            return examRepository.save(exam);

                        }
                );
    }

    @DeleteMapping("/exams/all")
    public void deleteAllExams() {
        examRepository.deleteAll();
    }
}

