package com.servicenow.math.questions;

import lombok.Data;
import lombok.NoArgsConstructor;


public record Question(
        Integer Id,
        String question,
        String answer) {

}