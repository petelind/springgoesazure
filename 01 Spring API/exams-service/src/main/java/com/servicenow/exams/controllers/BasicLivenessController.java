package com.servicenow.exams.controllers;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

import java.util.Map;

@RestController
@RequestMapping("/")
public class BasicLivenessController {

    @Value(value = "${spring.application.version}")
    private String version;

    @Value(value = "${spring.application.environment}")
    private String environment;

    @Operation(
           summary = "Basic liveness check",
                description = "Basic liveness check",
                responses = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Basic liveness check",
                                content = @Content(
                                        mediaType = "text/plain",
                                        examples = {
                                                @ExampleObject(
                                                        name = "Basic liveness check",
                                                        value = "v.1.0.0 Examinator is alive! Environment: dev"
                                                )
                                        }
                                )
                        )
                }
    )
    @GetMapping("/")
    public String basicAlive()
    {
        return "v." + version + " Examinator is alive! Environment: " + environment;
    }


}
