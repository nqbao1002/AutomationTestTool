package org.example.testsendoweb.requests;

import org.example.testsendoweb.models.ApiEndpoint;

import java.util.List;

public class ApiTestRequest {
    private String apiUrl;
    private String scriptText;
    private List<ApiEndpoint> selectedEndpoints;

    // Getters and setters

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getScriptText() {
        return scriptText;
    }

    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }

    public List<ApiEndpoint> getSelectedEndpoints() {
        return selectedEndpoints;
    }

    public void setSelectedEndpoints(List<ApiEndpoint> selectedEndpoints) {
        this.selectedEndpoints = selectedEndpoints;
    }
}
