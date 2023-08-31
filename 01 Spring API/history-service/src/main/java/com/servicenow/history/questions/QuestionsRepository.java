package com.servicenow.history.questions;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuestionsRepository {
    private final List<Question> questions = List.of(
            new Question(1, "Day India got indepence?", "August 15, 1947"),
            new Question(2, "Emperor with a Historical Popularity Index of 82.44?", "Ashoka")
    );

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
}
