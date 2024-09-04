package com.example.db2.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.db2.dto.AuthorBookRequest;
import com.example.db2.dto.AuthorRequest;
import com.example.db2.model.Author;
import com.example.db2.model.Book;
import com.example.db2.repository.AuthorRepository;
import com.example.db2.repository.BookRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Autores", description = "Endpoints para operações CRUD de autores")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    @Operation(summary = "Listar todos os autores", description = "Retorna uma lista de todos os autores")
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autor criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para criação do Autor")
    })
    @Operation(summary = "Criar um novo autor", description = "Adiciona um novo autor ao banco de dados")
    @PostMapping
    public ResponseEntity<?> createAuthor(@RequestBody AuthorRequest authorRequest) {
        if (authorRequest.getName() == null || authorRequest.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Erro: O nome do autor é obrigatório.");
        }

        Author author = new Author();
        author.setName(authorRequest.getName());
        author.setBirthDate(authorRequest.getBirthDate());
        author.setNationality(authorRequest.getNationality());

        Author savedAuthor = authorRepository.save(author);
        return ResponseEntity.ok(savedAuthor);
    }

    @Operation(summary = "Obter autor por ID com seus livros", description = "Retorna os detalhes de um autor específico e seus livros com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autor e seus livros encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado com o ID fornecido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorBookRequest> getAuthorById(@PathVariable String id) {
        Optional<Author> authorOptional = authorRepository.findById(id);
        if (authorOptional.isPresent()) {
            Author author = authorOptional.get();
            List<Book> books = bookRepository.findByAuthorId(author.getId());
            AuthorBookRequest authorDTO = new AuthorBookRequest(
                    author.getId(),
                    author.getName(),
                    author.getBirthDate(),
                    author.getNationality(),
                    books);
            return ResponseEntity.ok(authorDTO);
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o autor não for encontrado
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um autor existente", description = "Atualiza os detalhes de um autor específico")
    public ResponseEntity<?> updateAuthor(@PathVariable String id, @RequestBody AuthorRequest authorRequest) {
        Optional<Author> existingAuthor = authorRepository.findById(id);
        if (!existingAuthor.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: autor não encontrado.");
        }

        if (authorRequest.getName() == null || authorRequest.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Erro: O nome do autor é obrigatório.");
        }

        Author author = existingAuthor.get();
        author.setName(authorRequest.getName());
        author.setBirthDate(authorRequest.getBirthDate());
        author.setNationality(authorRequest.getNationality());

        Author savedAuthor = authorRepository.save(author);
        return ResponseEntity.ok(savedAuthor);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um autor", description = "Remove um autor específico do banco de dados e seus livros associados")
    public ResponseEntity<Void> deleteAuthor(@PathVariable String id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            // Remove todos os livros associados ao autor
            List<Book> books = bookRepository.findByAuthorId(id);
            if (!books.isEmpty()) {
                bookRepository.deleteAll(books);
            }

            // Remove o autor
            authorRepository.delete(author.get());
            return ResponseEntity.ok().build(); // Retorna 200 OK após deletar
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o autor não for encontrado
        }
    }
}
