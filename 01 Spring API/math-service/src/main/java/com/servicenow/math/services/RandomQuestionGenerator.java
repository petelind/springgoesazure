package com.servicenow.math.services;

import com.servicenow.math.questions.Question;

import java.util.ArrayList;
import java.util.List;

public class RandomQuestionGenerator {
    /**
     * Generates a random question like "How much is 1+1?" with the answer "2"
     *
     * @return random Question
     */

    public Question generateRandomQuestion() {
        /**
         * Picks A and B randomly between 1 and 100,
         * and returns a Question with the question "How much is A+B?" and the answer "A+B"
         * @return random Question
         */
        // get random numbers between 1 and 100
        int a = (int) (Math.random() * 100 + 1);
        int b = (int) (Math.random() * 100 + 1);
        // create question
        String question = "How much is " + a + "+" + b + "?";
        String answer = Integer.toString(a + b);
        Integer id = (int) (Math.random() * 100 + 1);
        return new Question(id, question, answer);
    }

    public List<Question> generateXRandomQuestions(Integer x) {
        /**
         * Generates x random questions
         * @param Integer x number of questions to generate
         * @return random List<Question>
         */
        List<Question> questions = new ArrayList<Question>();
        for (int i = 0; i < x; i++) {
            questions.add(generateRandomQuestion());
        }
        return questions;

    }
}