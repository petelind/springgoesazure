package com.servicenow.exams.controllers;

import com.servicenow.exams.dao.EdgeRepository;
import com.servicenow.exams.dao.QuestionsRepository;
import com.servicenow.exams.core.Exam;
import com.servicenow.exams.dao.ExamRepository;
import com.servicenow.exams.config.HistoryClient;
import com.servicenow.exams.core.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ExamController {

    private final ExamRepository examRepository;
    private final WebClient mathClient;
    private final HistoryClient historyClient;
    private final QuestionsRepository questionsRepository;
    private final EdgeRepository connectionsRepository;


    public ExamController(ExamRepository examRepository, QuestionsRepository questionsRepository,
                          WebClient webClient, HistoryClient historyClient, EdgeRepository connectionsRepository) {
        this.examRepository = examRepository;
        this.questionsRepository = questionsRepository;
        this.mathClient = webClient;
        this.historyClient = historyClient;
        this.connectionsRepository = connectionsRepository;
    }

    @GetMapping("/exams")
    public List<Exam> getAllExams() {
        return (List<Exam>) examRepository.findAll();
    }


    @PostMapping("/exams/math")
    public Mono<Exam> createMathExam() {
        // This is one way of accessing data in another service - via webClient (works for older Spring).
        return mathClient.post().uri("/random?amount=2")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(Question.class)
                .collectList()
                .map(questions -> {

                            questions = questionsRepository.saveAll(questions);
                            Exam exam = new Exam();
                            exam.setLabel("Exam");
                            exam.setQuestions(questions);
                            exam.setDescription("Math exam description");

                            return examRepository.save(exam);

                        }
                );
    }

    @PostMapping("/exams/history")
    public Mono<Exam> createHistoryExam()
    {
        return this.historyClient.getQuestions(2)
                .map(questions -> {
                    questions = questionsRepository.saveAll(questions);
                    Exam exam = new Exam();
                    exam.setLabel("Exam");
                    exam.setQuestions(questions);
                    exam.setDescription("Exam description");
                    return examRepository.save(exam);

                }
                );
    }

    @PostMapping("/exams/complete")
    public Mono<Exam> createAllSpanningExam(
            @RequestParam int size,
            @RequestParam String name,
            @RequestParam String description
    ) {
        try {
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

            // Transform the Mono<List<Question>> into an Iterable<Question>
            return allQuestions
                    .flatMapIterable(questions -> questions) // Flatten the list
                    .collectList() // Collect into a List<Question>
                    .flatMap(questions -> {
                        // Save the questions to the repository
                        List<Question> savedQuestions = questionsRepository.saveAll(questions);

                        // Create and save the exam
                        // TODO: move repositories and logic out of the domain model (Exam) into @service
                        Exam exam = new Exam(
                                "Exam",
                                "Complete Exam",
                                connectionsRepository,
                                questionsRepository);
                        exam.setQuestions(savedQuestions);
                        return Mono.just(examRepository.save(exam));

                    });
        } catch (Exception e) {
            log.error("Error creating exam", e);
            return Mono.error(e);
        }
    }


    @DeleteMapping("/exams/all")
    public void deleteAllExams() {
        examRepository.deleteAll();
    }
}

