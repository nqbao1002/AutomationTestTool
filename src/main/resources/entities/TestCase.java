package entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@Setter
@Getter
public class TestCase {
    private int id;
    private String testCaseName;
    private String endPoint;
    private String method;
    private String token;
    private String body;
    private int statusCode;
    private String expectedResult;
}
