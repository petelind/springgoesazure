package com.servicenow.math.controllers;

import com.servicenow.math.questions.Question;
import com.servicenow.math.questions.QuestionsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions") // <1>
public class QuestionController {
    private QuestionsRepository questionsRepository;
    public QuestionController(QuestionsRepository questionsRepository) {
        this.questionsRepository = questionsRepository;
    }

    @GetMapping("/") // <2>
    public List<Question> getAllQuestions() {
        return questionsRepository.getQuestions();
    }

    @GetMapping("/{id}") // <3>
    public  Question getQuestionById(@PathVariable int id) {
        return questionsRepository.getQuestionById(id);
    }

    @PostMapping("/random")
    public List<Question> getRequiredNumberOfQuestions(@RequestParam int amount) {
        return questionsRepository.getRequiredNumberOfQuestions(amount);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleException(IllegalArgumentException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }


}
