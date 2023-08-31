package com.servicenow.exams.exams;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    private String questions;
    //You may map the questions as a list of questions, but it is not required for our purposes. For your reference:
    //@OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //private List<Question> questions;

}
