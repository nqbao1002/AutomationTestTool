//package org.example.testsendoweb.models;
//
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//import java.util.Set;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Entity
//@Table(name = "endPoint")
//public class EndPointEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long endPointID;
//    String endPointName;
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "responseBody_ID", referencedColumnName = "responseBodyId")
//    private ResponseBody responseBody;
//    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "requestBody_ID", referencedColumnName = "requestBodyID")
//    private RequestBody requestBody;
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "parameter_ID", referencedColumnName = "paramID")
//    private ParameterEntity parameter;
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "method_ID", referencedColumnName = "methodID")
//    private MethodEntity methodEntity;
//
//}
