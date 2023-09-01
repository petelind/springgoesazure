package com.servicenow.math.questions;

import com.servicenow.math.questions.Question;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class QuestionRepositoryGremlin {

    private final Client gremlinClient;

    public QuestionRepositoryGremlin(Client gremlinClient) {
        this.gremlinClient = gremlinClient;
    }

    public void createQuestion(Question question) throws ExecutionException, InterruptedException {
        String query = String.format("g.addV('Question').property('Id', %d).property('question', '%s').property('answer', '%s')",
                question.Id(), question.question(), question.answer());

        gremlinClient.submit(query).all().get();
    }

    public Question readQuestion(Integer id) throws ExecutionException, InterruptedException {
        String query = String.format("g.V().has('Question', 'Id', %d).valueMap().toList()", id);

        List<Result> results = gremlinClient.submit(query).all().get();

        if (!results.isEmpty()) {
            Result result = results.get(0);
            return new Question(
                    id,
                    (String) result.getVertex().property("question").value(),
                    (String) result.getVertex().property("answer").value()
            );
        }

        return null;
    }

    public void updateQuestion(Question question) throws ExecutionException, InterruptedException {
        String query = String.format("g.V().has('Question', 'Id', %d).property('question', '%s').property('answer', '%s')",
                question.Id(), question.question(), question.answer());

        gremlinClient.submit(query).all().get();
    }

    public void deleteQuestion(Integer id) throws ExecutionException, InterruptedException {
        String query = String.format("g.V().has('Question', 'Id', %d).drop()", id);

        gremlinClient.submit(query).all().get();
    }

    public List<Question> findAllQuestions() throws ExecutionException, InterruptedException {
        String query = "g.V().hasLabel('Question')";

        List<Result> results = gremlinClient.submit(query).all().get();
        // Todo: seems there is no getVertex() method in Result class anymore. Need alternate way to get vertex properties.
        return results.stream().map(result ->
                new Question(
                        (Integer) result.getVertex().property("Id").value(),
                        (String) result.getVertex().property("question").value(),
                        (String) result.getVertex().property("answer").value()
                )
        ).collect(Collectors.toList());
    }

    @ExceptionHandler(ExecutionException.class)
    public ErrorResponse handleException(ExecutionException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
