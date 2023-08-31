package com.servicenow.exams.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BasicLivenessController {

    @Value(value = "${spring.application.version}")
    private String version;

    @Value(value = "${spring.application.environment}")
    private String environment;

    @GetMapping("/")
    public String basicAlive()
    {
        return "v." + version + " Examinator is alive! Environment: " + environment;
    }


}
