package com.servicenow.exams.exams;
import org.springframework.data.repository.CrudRepository;

public interface ExamRepository extends CrudRepository<Exam, Integer> {
    // You can add custom query methods here if needed; everything else is out of the box!!!
}
