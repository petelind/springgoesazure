package com.servicenow.exams.exams;
import jakarta.persistence.*;

public record Question (
        Integer Id,
        String question,
        String answer) {
}
