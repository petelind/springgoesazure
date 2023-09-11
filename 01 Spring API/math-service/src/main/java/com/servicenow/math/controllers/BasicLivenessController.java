package com.servicenow.math.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@Slf4j
@RequestMapping("/")
public class BasicLivenessController {

    @Value("${application.version}")
    private String version;

    @Value("${application.environment}")
    private String environment;

    @GetMapping("/")
    public String basicAlive()
    /**
     * This method is used to check if the service aliveness (not readiness) without the actuator endpoints.
     * @param version: The version of the service (e.g. 1.0.0); auto-populated from config.
     * @param environment: The environment the service is running in (e.g. dev, test, prod); auto-populated from config.
     * @return String which will contain the version and environment of the service running.
     * @throws IllegalArgumentException if the version is not a valid version or fails to parse it from config.
     *
     */
    {
        log.info("v." + version + " MathDirect is alive! Environment: " + environment);
        return "v." + version + " MathDirect is alive! Environment: " + environment;

    }


}
