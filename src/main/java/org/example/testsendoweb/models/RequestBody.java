//package org.example.testsendoweb.models;
//
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
//public class RequestBody {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long reID;
//    String content;
//    @ManyToMany
//    @JoinTable(
//            name = "REQUESTBODY_ENUM",   // Name of the join table
//            joinColumns = @JoinColumn(name = "requestBodyID"), // Foreign key to RequestBody
//            inverseJoinColumns = @JoinColumn(name = "enumID")  // Foreign key to Enum
//    )
//    Set<EnumEntity> enums;
//    @OneToOne(mappedBy = "requestBody")
//    private EndPointEntity endpoint;
//
//}
