package com.servicenow.exams.core;

import com.servicenow.exams.dao.EdgeRepository;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "nodes")
public class Node {
    @Id
    private String id;
    private String label;
    // other fields and getters/setters

    // TODO: Think - whats the best strategy to populate these fields?
    public List<String> incomingEdges = new ArrayList<>();
    public List<String> outgoingEdges = new ArrayList<>();

    // Method to create an edge to another Node and update outgoingEdges
    public void createEdgeTo(Node toNode, EdgeRepository edgeRepository) {
        // Create the edge
        Edge edge = new Edge();
        edge.setFromNodeId(this.getId());
        edge.setToNodeId(toNode.getId());

        // Save the edge
        edgeRepository.save(edge);

        // Update the local outgoingEdges list
        this.outgoingEdges.add(edge.getId());
    }
}

