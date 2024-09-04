package com.example.db2.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.db2.model.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {
}