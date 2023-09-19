package com.servicenow.exams.core;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@Document(collection = "edges")
public class Edge {
    @Id
    private String id;
    private String label;
    private String fromNodeId;
    private String toNodeId;
    // other fields and getters/setters
}