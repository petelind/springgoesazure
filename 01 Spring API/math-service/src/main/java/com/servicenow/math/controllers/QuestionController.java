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
    private final RandomQuestionGenerator randomQuestionsGenerator;
    private QuestionsRepository questionsRepository;
    public QuestionController(QuestionsRepository questionsRepository, RandomQuestionGenerator randomQuestionGenerator) {
        this.questionsRepository = questionsRepository;
        this.randomQuestionsGenerator = randomQuestionGenerator;
    }

    @GetMapping("/") // <2>
    public List<Question> getAllQuestions() {
        return questionsRepository.getQuestions();
    }

    @GetMapping("/{id}") // <3>
    public  Question getQuestionById(@PathVariable int id) {
        return questionsRepository.getQuestionById(id);
    }

    @PostMapping("/")
    public Question addQuestion(@Valid @RequestBody Question question) {
        questionsRepository.addQuestions(List.of(question));
        return question;
    }

    @PostMapping("/random")
    public List<Question> getRequiredNumberOfQuestions(@RequestParam int amount) {
        try {
            return questionsRepository.getRequiredNumberOfQuestions(amount);
        } catch (IllegalArgumentException e) {
            int currentQuestionCount = questionsRepository.getQuestions().size();
            var targetXQuestions = amount - currentQuestionCount;
            List<Question> newQuestions = randomQuestionsGenerator.generateXRandomQuestions(targetXQuestions);
            questionsRepository.addQuestions(newQuestions);
            return questionsRepository.getQuestions();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleException(IllegalArgumentException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        // Create a Map to store field errors
        Map<String, String> fieldErrors = new HashMap<>();

        // Iterate through field errors and populate the Map
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Create an HTTP response with the field errors and status code
        return new ResponseEntity<>(fieldErrors, HttpStatus.BAD_REQUEST);
    }

}
