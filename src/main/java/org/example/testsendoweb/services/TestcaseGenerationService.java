package org.example.testsendoweb.services;

import org.example.testsendoweb.configurations.OpenAiConfig;
import org.example.testsendoweb.models.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TestcaseGenerationService {

    public String retryGenerateRequestBodyForBoth(OpenAiConfig aiConfig, ApiEndpoint endpointUrl, String textScript, int maxAttempts) throws IOException {
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                return aiConfig.generateRequestBody(textScript, endpointUrl);
            } catch (IOException e) {
                attempts++;
                if (attempts >= maxAttempts) {
                    throw new IOException("Failed to generate request body after " + maxAttempts + " attempts", e);
                }
                try {
                    Thread.sleep(2000 * attempts); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    throw new IOException("Interrupted during retry", ie);
                }
            }
        }
        return null;
    }
}
