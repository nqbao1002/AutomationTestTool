package org.example.testsendoweb.models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiEndpoint {

    private String endpoint;
    private String method;
    private String tag;

    private Map<String, ObjectNode> requestRefObject = new HashMap<>();
    private Map<String, ObjectNode> responseRefObject = new HashMap<>();

    //    private Map<String, ObjectNode> refObjectsDefinitions = new HashMap<>();

    public void addRequestRefObject(String key, ObjectNode value) {
        this.requestRefObject.put(key, value);
    }

    public void addResponseRefObject(String key, ObjectNode value) {
        this.responseRefObject.put(key, value);
    }

}
