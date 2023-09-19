package com.servicenow.exams.dao;

import com.servicenow.exams.core.Question;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionsRepository extends NodeRepository<Question, String> {
}
