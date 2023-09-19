package com.servicenow.exams.dao;

import com.servicenow.exams.core.Edge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeRepository extends MongoRepository<Edge, String> {
}