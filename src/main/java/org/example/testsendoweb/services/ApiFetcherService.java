package org.example.testsendoweb.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.testsendoweb.models.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ApiFetcherService {
    public static List<String> extractRefs(String jsonString) {
        List<String> refs = new ArrayList<>();

        // Pattern to match $ref values
        Pattern refPattern = Pattern.compile("\"\\$ref\"\\s*:\\s*\"([^\"]+)\"");
        Matcher refMatcher = refPattern.matcher(jsonString);

        // Loop through all found $refs
        while (refMatcher.find()) {
            String fullRef = refMatcher.group(1);
            String shortRef = fullRef.substring(fullRef.lastIndexOf('/') + 1);
            refs.add(shortRef);
        }

        return refs;
    }


    public List<ApiEndpoint> swaggerApiFetcher(String urlSwagger) throws IOException {
        List<ApiEndpoint> apiEndpoints = new ArrayList<>();
        String responseBody = fetchJsonFromUrl(urlSwagger);
        if (responseBody == null || responseBody.isEmpty()) {
            throw new IOException("Failed to fetch JSON from URL: " + urlSwagger);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseBody);
        JsonNode paths = jsonNode.get("paths");
        JsonNode definitions = getDefinitions(jsonNode);
        if (paths == null || !paths.isObject()) {
            throw new IOException("No paths found in the Swagger JSON");
        }
        paths.fields().forEachRemaining(entry -> {
            ApiEndpoint apiEndpoint = new ApiEndpoint();
            JsonNode operations = entry.getValue();
            apiEndpoint.setEndpoint(entry.getKey());
            operations.fields().forEachRemaining(operation -> {
                apiEndpoint.setMethod(operation.getKey());
                JsonNode operationDetails = operation.getValue();

                JsonNode tagsNode = operationDetails.get("tags");
                apiEndpoint.setTag(tagsNode.toString().replaceAll("[\\[\\]\\\\/\"']", ""));

                System.out.println("tag"+tagsNode);
                // Process request body
                JsonNode parameters = operationDetails.get("parameters");
                if( parameters != null && !parameters.isNull()) {
                    processReferences(parameters, definitions, apiEndpoint, new LinkedList<>(), false);
                }
                JsonNode requestBody = operationDetails.get("requestBody");
                if (requestBody != null) {
                    processReferences(requestBody, definitions, apiEndpoint, new LinkedList<>(), false);
                }

                // Process responses
                JsonNode responses = operationDetails.get("responses");
                if (responses != null) {
                    processReferences(responses, definitions, apiEndpoint, new LinkedList<>(), true);
                }
            });
            apiEndpoints.add(apiEndpoint);
        });
        return apiEndpoints;
    }

    private void processReferences(JsonNode node, JsonNode definitions, ApiEndpoint apiEndpoint, List<String> processedRefs, boolean isInResponse) {
        String nodeString = node.toString();
        List<String> refs = extractRefs(nodeString);

        refs.forEach(ref -> {
//            if (ref.equals("HttpStatusCode") || processedRefs.contains(ref) || ref.equals("ErrorType")){
//                return;
//            }

            processedRefs.add(ref);

            JsonNode refNode = definitions.get(ref);

            if (refNode != null && refNode.isObject()) {
                ObjectNode refObject = (ObjectNode) refNode;

                // Use the passed isInResponse flag instead of calling isInResponseBlock
                if (isInResponse) {
                    System.out.println("Response: " + ref);
                    apiEndpoint.addResponseRefObject(ref, refObject);
                } else {
                    System.out.println("Request: " + ref);
                    apiEndpoint.addRequestRefObject(ref, refObject);
                }
                processReferences(refObject, definitions, apiEndpoint, processedRefs, isInResponse);
            }
        });
    }

    private JsonNode getDefinitions(JsonNode jsonNode) {
        JsonNode definitions = jsonNode.get("definitions");
        if (definitions == null || definitions.isEmpty()) {
            JsonNode components = jsonNode.get("components");
            if (components != null && components.has("schemas")) {
                definitions = components.get("schemas");
            }
        }
        return definitions != null ? definitions : new ObjectMapper().createObjectNode();
    }

    private String fetchJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            throw new IOException("GET request failed. Response Code: " + responseCode);
        }
    }
}
