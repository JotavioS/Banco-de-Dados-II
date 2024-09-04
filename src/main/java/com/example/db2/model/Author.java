package com.example.db2.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "authors")
public class Author {
    @Id
    private String id;
    private String name;
    private LocalDate birthDate;
    private String nationality;
}