package com.example.db2.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AuthorRequest {
    private String name;
    private LocalDate birthDate;
    private String nationality;
}
