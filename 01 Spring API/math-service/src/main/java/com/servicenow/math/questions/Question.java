package com.servicenow.math.questions;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public record Question(
        @Id
        Integer Id,
        @NotEmpty(message = "Question must not be empty")
        @Size(min = 3, max = 255, message = "Question must be between 3 and 255 characters")
        String question,
        @NotEmpty(message = "Answer must not be empty")
        @Size(min = 1, max = 255, message = "Answer must be between 1 and 255 characters")
        String answer) {
}