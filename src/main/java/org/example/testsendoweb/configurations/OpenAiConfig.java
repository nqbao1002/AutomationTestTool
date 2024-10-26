package org.example.testsendoweb.configurations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import okhttp3.*;
import org.example.testsendoweb.models.ApiEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Builder
@Configuration
public class OpenAiConfig {

    private static final String OPENAI_API_URL = "";
    private final String OPENAI_API_KEY;
    public OpenAiConfig(@Value("${open.api.key}") String OPENAI_API_KEY) {
        this.OPENAI_API_KEY = OPENAI_API_KEY;
    }
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 10;  // Retry after 10 seconds if rate limited
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    // Method to call the OpenAI API and generate dynamic data for API request body
    public String generateRequestBody(String content, ApiEndpoint apiEndpoint) throws IOException {
        int attempts = 0;
        boolean successful = false;
        String generatedText = null;
        System.out.println(OPENAI_API_KEY);
        // Retry logic for handling rate limiting (HTTP 429)
        while (attempts < MAX_RETRIES && !successful) {
            try {
                attempts++;

                // Prepare the request body
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", "gpt-4o-mini");

// Messages array to set up a proper conversation with the AI
                JSONArray messages = new JSONArray();
                messages.put(new JSONObject()
                        .put("role", "system")
                        .put("content", "You are an API test case generator. Your goal is to create test cases for all possible edge cases with correct formatting.")
                );
                String prompt = "Generate a detailed test case table for the following API endpoint. " +
                        "The table should include columns for Test Case ID | Test Case | Endpoint | Method | Token | Request Body | Status Code | Expected Result | " +
                        "Ensure that the response is provided in a plain table format, and avoid any introductory or explanatory text. " +
                        "API Endpoint details:\n" +
                        "Endpoint: " + apiEndpoint.getEndpoint() + "\n" +
                        "Method: " + apiEndpoint.getMethod() + "\n" +
                        "Request Objects: " + apiEndpoint.getRequestRefObject().values() + "\n" +
                        "Response Objects: " + apiEndpoint.getResponseRefObject().values() + "\n";
                messages.put(new JSONObject()
                        .put("role", "user")
                        .put("content",
                                prompt + content
                        )
                );


//                messages.put(new JSONObject()
//                        .put("role", "user")
//                        .put("content", "Base on this endpoint selected: " + apiEndpoint));


                requestBody.put("messages", messages);
                requestBody.put("max_tokens", 4000);
                requestBody.put("temperature", 0);
                requestBody.put("n", 1);
                requestBody.put("top_p", 1);
                requestBody.put("stop", new JSONArray().put("\n\n"));


                // Build the HTTP request
                Request request = new Request.Builder()
                        .url(OPENAI_API_URL)
                        .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                        .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                        .build();

                // Execute the request
                Response response = client.newCall(request).execute();

                // Handle non-200 responses
                if (response.code() == 429) {
                    System.out.println("Rate limit exceeded. Retrying after " + RETRY_DELAY + " seconds...");
                    TimeUnit.SECONDS.sleep(RETRY_DELAY);  // Delay before retrying
                } else if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // Parse and return the response if successful
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    generatedText = jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                            .trim();
                    successful = true;  // Exit retry loop

                    // Print the AI response in the console
                    System.out.println(generatedText);
                }

            } catch (IOException | InterruptedException e) {
                System.err.println("Error occurred: " + e.getMessage());
            }
        }

        if (!successful) {
            throw new IOException("Failed to generate request body after " + MAX_RETRIES + " attempts");
        }

        return generatedText;  // Return the generated AI response
    }

    public String generateRequestBodyRetry(String content, ApiEndpoint apiEndpoint) throws IOException {
        int attempts = 0;
        boolean successful = false;
        String generatedText = null;

        // Retry logic for handling rate limiting (HTTP 429)
        while (attempts < MAX_RETRIES && !successful) {
            try {
                attempts++;

                // Prepare the request body
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", "gpt-4o-mini");

                // Messages array to set up a proper conversation with the AI
                JSONArray messages = new JSONArray();
                messages.put(new JSONObject()
                        .put("role", "system")
                        .put("content", "You are an API test case generator. Your goal is to create new and diverse test cases for all possible edge cases with correct formatting.")
                );
                String prompt = String.format(
                        "Review the existing test case table for the API endpoint %s provided below. Analyze the current test cases and add more to improve coverage and completeness. Consider the following when adding new test cases:\n" +
                                "\n" +
                                "1. Edge cases that may not be covered\n" +
                                "2. Different combinations of valid and invalid input parameters\n" +
                                "3. Potential security vulnerabilities\n" +
                                "4. Performance-related scenarios\n" +
                                "5. Error handling for various situations\n" +
                                "6. Boundary value analysis\n" +
                                "7. Compatibility with different data types or formats\n" +
                                "8. Provide the response in a plain table format without any introductory or explanatory text or header table.\n" +
                                "Add new test cases directly to the table, maintaining the existing format and level of detail. Ensure that new test case IDs follow the established numbering convention.\n" +
                                "Existing test case table:\n" +
                                "%s",
                        apiEndpoint.getEndpoint(),
                        content
                );
                System.out.println(prompt);

                messages.put(new JSONObject()
                        .put("role", "user")
                        .put("content", prompt)
                );
                requestBody.put("messages", messages);

                requestBody.put("max_tokens", 4000);
                requestBody.put("temperature", 0);
                requestBody.put("n", 1);
                requestBody.put("top_p", 1);
                requestBody.put("stop", new JSONArray().put("\n\n"));

                // Build the HTTP request
                Request request = new Request.Builder()
                        .url(OPENAI_API_URL)
                        .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                        .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                        .build();

                // Execute the request
                Response response = client.newCall(request).execute();

                // Handle non-200 responses
                if (response.code() == 429) {
                    System.out.println("Rate limit exceeded. Retrying after " + RETRY_DELAY + " seconds...");
                    TimeUnit.SECONDS.sleep(RETRY_DELAY);  // Delay before retrying
                } else if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // Parse and return the response if successful
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    generatedText = jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                            .trim();
                    successful = true;  // Exit retry loop
                }

            } catch (IOException | InterruptedException e) {
                System.err.println("Error occurred: " + e.getMessage());
                if (attempts >= MAX_RETRIES) {
                    throw new IOException("Failed to generate request body after " + MAX_RETRIES + " attempts", e);
                }
            }

        }
        return generatedText;
    }


}