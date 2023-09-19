package com.servicenow.exams.dao;

import com.servicenow.exams.core.Question;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionsRepository extends NodeRepository<Question, String> {
    @Cacheable(value = "questions")
    @Override
    List<Question> findAll();

    @CacheEvict(value = "questions", allEntries = true)
    @Override
    <S extends Question> List<S> saveAll(Iterable<S> entities);
}
