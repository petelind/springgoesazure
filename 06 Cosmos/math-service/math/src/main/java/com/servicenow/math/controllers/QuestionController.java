package com.servicenow.math.controllers;

import com.servicenow.math.questions.Question;
import com.servicenow.math.questions.QuestionRepositoryGremlin;
import com.servicenow.math.questions.QuestionsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/questions") // <1>
public class QuestionController {
    private QuestionRepositoryGremlin questionsRepository;

    public QuestionController(QuestionRepositoryGremlin questionsRepository) {
        this.questionsRepository = questionsRepository;
    }

    @GetMapping("/") // <2>
    public List<Question> getAllQuestions() throws ExecutionException, InterruptedException {

            return questionsRepository.findAllQuestions();
    }

    @GetMapping("/{id}") // <3>
    public  Question getQuestionById(@PathVariable int id) throws ExecutionException, InterruptedException {
        return questionsRepository.readQuestion(id);
    }

    @PostMapping("/random")
    public List<Question> getRequiredNumberOfQuestions(@RequestParam int amount) throws ExecutionException, InterruptedException {
        return questionsRepository.findAllQuestions();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleException(IllegalArgumentException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }


}
