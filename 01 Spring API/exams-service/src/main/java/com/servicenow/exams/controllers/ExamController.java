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
    public Exam createAllSpanningExam(@RequestParam int size, @RequestParam String name, @RequestParam String description) {
        // get questions from both services
        Mono<List<Question>> mathQuestions = this.mathClient.post().uri("/random?amount=" + size)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(Question.class)
                .collectList();

        Mono<List<Question>> historyQuestions = this.historyClient.getQuestions(size);

        // combine them into one list
        Mono<List<Question>> allQuestions = mathQuestions.zipWith(historyQuestions)
                .map(tuple -> {
                    List<Question> math = tuple.getT1();
                    List<Question> history = tuple.getT2();
                    math.addAll(history);
                    return math;
                });

        // TODO: Map the list of questions to a list of question ids. Keep in mind - those are Monos, not Lists!
        String questionIds = "1, 2, 3, 4";

        Exam exam = new Exam();
        exam.setName(name);
        exam.setQuestions(questionIds);
        exam.setDescription(description);
        return this.examRepository.save(exam);

    }



    @DeleteMapping("/exams/all")
    public void deleteAllExams() {
        examRepository.deleteAll();
    }
}

