package com.servicenow.history.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BasicLivenessController {

    @Value("${application.version}")
    private String version;

    @Value("${application.environment}")
    private String environment;

    @Value("${application.name}")
    private String name;

    @Operation(summary = "Basic liveness check",
            description = "Allows you instantly identify which version of app on which environment is running")
    @GetMapping("/")
    public String basicAlive()
    {
        return "v." + version + " " + name + " is alive! Environment: " + environment;
    }


}
