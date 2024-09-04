package com.example.db2.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String authorId;
    private LocalDate publicationDate;
    private String genre;
    private String isbn;
}
