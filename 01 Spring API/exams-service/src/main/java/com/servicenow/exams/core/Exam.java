package com.servicenow.exams.core;

import com.servicenow.exams.dao.EdgeRepository;
import com.servicenow.exams.dao.QuestionsRepository;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.or;


@Data
@NoArgsConstructor
public class Exam extends Node {
    private String name;
    private String description;
    private QuestionsRepository questionsRepository;
    private EdgeRepository connectionsRepository;
    private List<Question> questions = new ArrayList<>();

    public Exam(String name, String description, EdgeRepository connectionsRepository, QuestionsRepository questionsRepository) {
        this.name = name;
        this.description = description;
        this.connectionsRepository = connectionsRepository;
        this.questionsRepository = questionsRepository;
    }

    public void setQuestions(List<Question> questions) {
        // Instead of storing question Ids, we now create Edges between the Exam and the Questions provided.
        for (Question question : questions) {
            this.createEdgeTo(question, connectionsRepository);
        }
        this.questionsRepository.saveAll(questions);
    }

    public List<Question> getQuestions() {
        if (this.questions.isEmpty()) {
            this.questions = questionsRepository.findConnectedNodes(this.getId());
        }
        return this.questions;
    }

}
