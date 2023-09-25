package com.servicenow.math.questions;

import com.servicenow.math.questions.Question;
import com.servicenow.math.service.GremlinConverter;
import lombok.RequiredArgsConstructor;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryGremlin {

    private final Client gremlinClient;
    private final GremlinConverter gremlinConverter;

    public void createQuestion(Question question) throws ExecutionException, InterruptedException {
/*        String query = String.format("g.addV('Question').property('Id', %d).property('question', '%s').property('answer', '%s')",
                question.Id(), question.question(), question.answer());

        gremlinClient.submit(query).all().get();
        */

    }

    public Question readQuestion(Integer id) throws ExecutionException, InterruptedException {
        // The way it should be:
        /*        GraphTraversalSource g = traversal().withRemote(gremlinConnection);
        List<Vertex> results = g.V().has("Question", "Id", id).toList();

        List<Question> questions = results.stream().map(result ->
                new Question(
                        (Integer) result.property("Id").value(),
                        (String) result.property("question").value(),
                        (String) result.property("answer").value()
                )
        ).collect(Collectors.toList());
        return questions.get(0); */
        // but it would not work: https://github.com/Azure/azure-cosmos-dotnet-v2/issues/439

        // so we have to use the old way:
        String query = String.format("g.V().has('Question', 'Id', %d)", id);
        List<Result> results = gremlinClient.submit(query).all().get();


        if (!results.isEmpty()) {
            Result result = results.get(0);
            return new Question(
                    id,
                    (String) result.getVertex().property("question").value(),
                    (String) result.getVertex().property("answer").value()
            );
        }

        /*for (Result result : results) {
            LinkedHashMap<String, Object> properties = result.get(LinkedHashMap.class); // Get the properties map


            Integer Id = (Integer) properties.get("Id");
            String question = (String) properties.get("question");
            String answer = (String) properties.get("answer");
            return new Question(Id, question, answer);
        }*/

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

        final Collection<Result> results = gremlinClient.submit(query)
                .all()
                .thenApply(gremlinConverter::convert)
                .get();

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
