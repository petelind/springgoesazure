package com.servicenow.exams.dao;

import com.servicenow.exams.core.Node;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NodeRepository<T extends Node, String> extends MongoRepository<T, String> { // TODO: set both from & to; otherwise wouldnt work!
    @Query(value = "{ $graphLookup: { from: 'edges', startWith: '$id', connectFromField: 'fromNodeId', connectToField: 'toNodeId', as: 'connectedNodes' } }")
    List<T> findConnectedNodes(String nodeId);
}