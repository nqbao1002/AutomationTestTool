package entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiEndpoint {
    private String endpoint;
    private String method;
    private String schema;
    private String schemaBody;



}
