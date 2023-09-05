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

    @Operation(summary = "Basic liveness check", description = "Basic liveness check")
    @GetMapping("/")
    public String basicAlive()
    {
        return "v." + version + " MathDirect is alive! Environment: " + environment;
    }


}
