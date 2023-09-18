package com.servicenow.exams.controllers;

import com.servicenow.exams.exams.Exam;
import com.servicenow.exams.exams.ExamRepository;
import com.servicenow.exams.exams.HistoryClient;
import com.servicenow.exams.exams.Question;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class ExamController {

    private final ExamRepository examRepository;
    private final WebClient mathClient;
    private final HistoryClient historyClient;


    public ExamController(ExamRepository examRepository, WebClient webClient, HistoryClient historyClient) {
        this.examRepository = examRepository;
        this.mathClient = webClient;
        this.historyClient = historyClient;
    }

    @GetMapping("/exams")
    public List<Exam> getAllExams() {
        return (List<Exam>) examRepository.findAll();
    }


    @PostMapping("/exams/math")
    @Transactional
    // Todo: fix both controllers to accept all required parameters and make them handle exceptions
    public Mono<Exam> createMathExam() {
        // This is one way of accessing data in another service - via webClient (works for older Spring).
        return mathClient.post().uri("/random?amount=2")
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
                            exam.setDescription("Math exam description");

                            return examRepository.save(exam);

                        }
                );
    }

    @PostMapping("/exams/history")
    @Transactional
    public Mono<Exam> createHistoryExam()
    {
        return this.historyClient.getQuestions(2)
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

    @PostMapping("/exams/complete")
    @Transactional
    public Mono<Exam> createAllSpanningExam(
            @RequestParam int size,
            @RequestParam String name,
            @RequestParam String description
    ) {
        // Get questions from both services
        Mono<List<Question>> mathQuestions = this.mathClient.post().uri("/random?amount=" + size)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(Question.class)
                .collectList();

        Mono<List<Question>> historyQuestions = this.historyClient.getQuestions(size);

        // Combine them into one list - there is no readymade method for this, so we have to use zipWith
        Mono<List<Question>> allQuestions = mathQuestions.zipWith(historyQuestions)
                .map(tuple -> {
                    List<Question> math = tuple.getT1();
                    List<Question> history = tuple.getT2();
                    math.addAll(history);
                    return math; // its not math anymore, its combined list. you can combine with history with the same result
                });

        // If you dont understand whats happening here, here is what you need:
        // https://www.udemy.com/course/functional-programming-and-reactive-programming-in-java/
        return allQuestions.flatMap(allQuestionsList -> {
            // Now, let's get the IDs out of each question and create a string of IDs
            String questionIds = allQuestionsList.stream()
                    .map(Question::Id) // Use Id
                    .map(String::valueOf) // Convert to string
                    .collect(Collectors.joining(","));

            // try to return the exam as POJO - see what gonna happen
            Exam exam = new Exam();
            exam.setName(name);
            exam.setQuestions(questionIds);
            exam.setDescription(description);
            // even if you will force it to return exam, it will fail the thread, because it is not reactive

            // Save the exam to the repository and return it as a Mono, because the context is still reactive
            return Mono.just(examRepository.save(exam));
            // Bottom line: as soon as you entered the reactive context - you're stuck in it
        });
    }

    @DeleteMapping("/exams/all")
    public void deleteAllExams() {
        examRepository.deleteAll();
    }
}

