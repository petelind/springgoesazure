package com.servicenow.math.controllers;

import com.servicenow.math.questions.Question;
import com.servicenow.math.questions.QuestionsRepository;
import com.servicenow.math.services.RandomQuestionGenerator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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


}
