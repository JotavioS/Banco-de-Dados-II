package com.example.db2.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "books")
public class Book {
    @Id
    private String id;
    private String title;
    private String authorId;
    private LocalDate publicationDate; // Data de publicação do livro
    private String genre;              // Gênero do livro
    private String isbn;               // ISBN do livro
}
