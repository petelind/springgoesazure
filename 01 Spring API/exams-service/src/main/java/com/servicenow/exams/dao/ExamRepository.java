package com.servicenow.exams.dao;
import com.servicenow.exams.core.Exam;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends NodeRepository<Exam, String> {
    // You can add custom query methods here if needed; everything else is out of the box!!!
}
