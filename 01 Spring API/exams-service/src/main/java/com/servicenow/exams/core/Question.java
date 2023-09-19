package com.servicenow.exams.core;
import com.servicenow.exams.core.Node;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
public class Question extends Node {
        public String question;
        public String answer; }