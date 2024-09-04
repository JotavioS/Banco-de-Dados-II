package com.example.db2.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.db2.model.Book;
import java.util.List;
import java.util.Optional;


public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByAuthorId(String authorId);

    Optional<Book> findByTitle(String title);
}