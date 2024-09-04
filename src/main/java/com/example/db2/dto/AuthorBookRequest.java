package com.example.db2.dto;

import com.example.db2.model.Book;

import lombok.Data;

import java.util.List;
import java.time.LocalDate;

@Data
public class AuthorBookRequest {
    private String id;
    private String name;
    private LocalDate birthDate;
    private String nationality;
    private List<Book> books;

    public AuthorBookRequest(String id, String name, LocalDate birthDate, String nationality, List<Book> books) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.books = books;
    }
}
