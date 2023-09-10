package com.servicenow.math.questions;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public record Question(

        Integer Id,
        String question,
        String answer) {
}