package com.tadiwa.financialanalytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "Check if the service is alive", description = "Returns a status message to indicate the service is running.")
    @GetMapping
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("I'm alive");
    }
}
