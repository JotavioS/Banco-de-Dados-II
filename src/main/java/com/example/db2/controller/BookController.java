package com.example.db2.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.db2.model.Book;
import com.example.db2.dto.BookRequest;
import com.example.db2.model.Author;
import com.example.db2.repository.BookRepository;
import com.example.db2.repository.AuthorRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Livros", description = "Endpoints para operações CRUD de livros")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping
    @Operation(summary = "Listar todos os livros", description = "Retorna uma lista de todos os livros")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    @Operation(summary = "Criar um novo livro", description = "Adiciona um novo livro ao banco de dados. O ID do autor e o nome do livro devem ser fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para criação do livro")
    })
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookRequest bookRequest) {
        // Verifica se o ID do autor foi fornecido
        if (bookRequest.getAuthorId() == null) {
            return ResponseEntity.badRequest().body("Erro: O ID do autor é obrigatório.");
        }

        // Verifica se o nome do livro foi fornecido
        if (bookRequest.getTitle() == null || bookRequest.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Erro: O nome do livro é obrigatório.");
        }

        Optional<Author> author = authorRepository.findById(bookRequest.getAuthorId());
        if (author.isPresent()) {
            Book book = new Book();
            book.setTitle(bookRequest.getTitle());
            book.setAuthorId(author.get().getId());
            book.setPublicationDate(bookRequest.getPublicationDate()); // Novo atributo
            book.setGenre(bookRequest.getGenre()); // Novo atributo
            book.setIsbn(bookRequest.getIsbn()); // Novo atributo
            Book savedBook = bookRepository.save(book);
            return ResponseEntity.ok(savedBook);
        } else {
            return ResponseEntity.badRequest().body("Erro: Autor não encontrado.");
        }
    }

    @Operation(summary = "Obter livro por ID", description = "Retorna os detalhes de um livro específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado com o ID fornecido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se não encontrado
        }
    }

    @Operation(summary = "Atualizar um livro existente", description = "Atualiza as informações de um livro existente com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para atualização do livro")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable String id, @RequestBody BookRequest bookRequest) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (!existingBook.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Livro não encontrado.");
        }

        // Verifica se o nome do livro foi fornecido
        if (bookRequest.getTitle() == null || bookRequest.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Erro: O nome do livro é obrigatório.");
        }

        // Verifica se o ID do autor foi fornecido
        if (bookRequest.getAuthorId() == null) {
            return ResponseEntity.badRequest().body("Erro: O ID do autor é obrigatório.");
        }

        Optional<Author> author = authorRepository.findById(bookRequest.getAuthorId());
        if (!author.isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Autor não encontrado.");
        }

        Book book = existingBook.get();
        book.setTitle(bookRequest.getTitle());
        book.setAuthorId(author.get().getId());
        book.setPublicationDate(bookRequest.getPublicationDate()); // Novo atributo
        book.setGenre(bookRequest.getGenre()); // Novo atributo
        book.setIsbn(bookRequest.getIsbn()); // Novo atributo
        Book updatedBook = bookRepository.save(book);
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "Deletar um livro", description = "Remove um livro do banco de dados com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            return ResponseEntity.ok().build(); // Retorna 200 OK após deletar
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o livro não for encontrado
        }
    }
}
