package org.example.testsendoweb.controllers;

import org.example.testsendoweb.configurations.OpenAiConfig;

import lombok.AllArgsConstructor;
import org.example.testsendoweb.models.ApiEndpoint;
import org.example.testsendoweb.services.ApiFetcherService;
import org.example.testsendoweb.services.TestcaseGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;


@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class ApiTestingController {
    @Autowired
    private final OpenAiConfig aiConfig = OpenAiConfig.builder().build();
    private final ApiFetcherService apiFetcherService;
    private final TestcaseGenerationService testcaseGenerationService;

    @GetMapping("/fetchSwaggerData")
    public ResponseEntity<?> fetchSwaggerData(@RequestParam("url") String url) {
        try {
            List<ApiEndpoint> endpoints = apiFetcherService.swaggerApiFetcher(url);
            return ResponseEntity.ok(endpoints);
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch Swagger data: " + ex.getMessage() + "\n" + getStackTraceAsString(ex));
        } catch (Exception ex) {
            ex.printStackTrace(); // This will print the stack trace to your server logs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage() + "\n" + getStackTraceAsString(ex));
        }
    }

    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }


    @PostMapping("/generate")
    public ResponseEntity<?> generateTestCases(
            @RequestParam(value = "scriptText", required = false) String scriptText,
            @RequestBody ApiEndpoint selectedEndpoints
    ) {
        System.out.println("Select EndPoint: " + selectedEndpoints);


        StringBuilder testCaseResults = new StringBuilder();
        try {
            if(scriptText == null && selectedEndpoints == null) {
                scriptText = "";
                selectedEndpoints = new ApiEndpoint();
            }
            if (scriptText != null && !scriptText.trim().isEmpty() || selectedEndpoints != null && !selectedEndpoints.toString().isEmpty()) {
                try {
                    String result = testcaseGenerationService.retryGenerateRequestBodyForBoth(aiConfig, selectedEndpoints, scriptText, 100);
                    System.out.println("Test case first: " + result);
                    if(result != null) {
                        String content = aiConfig.generateRequestBodyRetry(result, selectedEndpoints);;
                        System.out.println("Test case second: " + content);
                        testCaseResults.append(content).append("\n");
                    }
                } catch (IOException ex) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to create test case for endpoint: " + selectedEndpoints.getEndpoint() + ", Error: " + ex.getMessage());
                }
                return ResponseEntity.ok(testCaseResults.toString());
            }

            // If neither scriptText, selectedEndpoints, nor apiUrl are provided
            else {
                return ResponseEntity.badRequest().body("No script text, endpoints, or API URL provided.");
            }


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

}