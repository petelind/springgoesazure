package com.servicenow.math.questions;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionsRepository  {
    private List<Question> questions = new ArrayList<>();

    public List<Question> getQuestions() {
        return questions;
    }


    public List<Question> getRequiredNumberOfQuestions(int amount) {
        if (amount > questions.size()) {
            throw new IllegalArgumentException("Not enough questions in repository! Only got " + questions.size() + " questions.");
        }
        return questions.subList(0, amount);
    }

    public Question getQuestionById(int id) throws IllegalArgumentException {
        return questions.stream().filter(question -> question.Id().equals(id)).findFirst().orElseThrow(
                () -> new IllegalArgumentException("No question with id " + id + " found!")
        );
    }

    public void addQuestions(List<Question> newQuestions) {
        questions.addAll(newQuestions);
    }
}
