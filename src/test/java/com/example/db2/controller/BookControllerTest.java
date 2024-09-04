package com.example.db2.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.example.db2.model.Author;
import com.example.db2.model.Book;
import com.example.db2.repository.AuthorRepository;
import com.example.db2.repository.BookRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        bookRepository.deleteAll(); // Limpa o banco de dados antes de cada teste
        authorRepository.deleteAll();

        // Cria um autor de teste válido
        testAuthor = new Author();
        testAuthor.setName("Test Author");
        testAuthor = authorRepository.save(testAuthor);
    }

    @Test
    public void testGetAllBooks() {
        // Primeiro, insira um livro no banco de dados para testar a listagem
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthorId(testAuthor.getId());
        book.setPublicationDate(LocalDate.parse("2024-01-01")); // Conversão para LocalDate
        book.setGenre("Fiction"); // Novo atributo
        book.setIsbn("1234567890"); // Novo atributo
        bookRepository.save(book);

        given()
                .when()
                .get("/api/books")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("[0].title", equalTo("Test Book"))
                .body("[0].authorId", equalTo(testAuthor.getId()))
                .body("[0].publicationDate", equalTo("2024-01-01")) // Certifique-se de que o formato é compatível
                .body("[0].genre", equalTo("Fiction")) // Novo atributo
                .body("[0].isbn", equalTo("1234567890")); // Novo atributo
    }

    @Test
    public void testCreateBook() {
        Map<String, Object> book = new HashMap<>();
        book.put("title", "New Book");
        book.put("authorId", testAuthor.getId());
        book.put("publicationDate", "2024-01-01"); // String para LocalDate na requisição
        book.put("genre", "Non-Fiction"); // Novo atributo
        book.put("isbn", "0987654321"); // Novo atributo

        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when()
                .post("/api/books")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("title", equalTo("New Book"))
                .body("authorId", equalTo(testAuthor.getId()))
                .body("publicationDate", equalTo("2024-01-01")) // Certifique-se de que o formato é compatível
                .body("genre", equalTo("Non-Fiction")) // Novo atributo
                .body("isbn", equalTo("0987654321")); // Novo atributo
    }

    @Test
    public void testGetBookById() {
        // Insere um livro e depois o recupera usando o ID
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthorId(testAuthor.getId());
        book.setPublicationDate(LocalDate.parse("2024-01-01")); // Conversão para LocalDate
        book.setGenre("Fiction"); // Novo atributo
        book.setIsbn("1234567890"); // Novo atributo
        Book savedBook = bookRepository.save(book);

        given()
                .when()
                .get("/api/books/{id}", savedBook.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(savedBook.getId()))
                .body("title", equalTo("Test Book"))
                .body("authorId", equalTo(testAuthor.getId()))
                .body("publicationDate", equalTo("2024-01-01")) // Certifique-se de que o formato é compatível
                .body("genre", equalTo("Fiction")) // Novo atributo
                .body("isbn", equalTo("1234567890")); // Novo atributo
    }

    @Test
    public void testUpdateBook() {
        // Insere um livro e depois o atualiza
        Book book = new Book();
        book.setTitle("Old Title");
        book.setAuthorId(testAuthor.getId());
        book.setPublicationDate(LocalDate.parse("2023-01-01")); // Conversão para LocalDate
        book.setGenre("Old Genre"); // Novo atributo
        book.setIsbn("1111111111"); // Novo atributo
        Book savedBook = bookRepository.save(book);

        Map<String, Object> updatedBook = new HashMap<>();
        updatedBook.put("title", "Updated Title");
        updatedBook.put("authorId", testAuthor.getId());
        updatedBook.put("publicationDate", "2024-01-01"); // String para LocalDate na requisição
        updatedBook.put("genre", "Updated Genre"); // Novo atributo
        updatedBook.put("isbn", "2222222222"); // Novo atributo

        given()
                .contentType(ContentType.JSON)
                .body(updatedBook)
                .when()
                .put("/api/books/{id}", savedBook.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", equalTo("Updated Title"))
                .body("authorId", equalTo(testAuthor.getId()))
                .body("publicationDate", equalTo("2024-01-01")) // Certifique-se de que o formato é compatível
                .body("genre", equalTo("Updated Genre")) // Novo atributo
                .body("isbn", equalTo("2222222222")); // Novo atributo
    }

    @Test
    public void testDeleteBook() {
        // Insere um livro e depois o deleta
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthorId(testAuthor.getId());
        book.setPublicationDate(LocalDate.parse("2024-01-01")); // Conversão para LocalDate
        book.setGenre("Fiction"); // Novo atributo
        book.setIsbn("1234567890"); // Novo atributo
        Book savedBook = bookRepository.save(book);

        given()
                .when()
                .delete("/api/books/{id}", savedBook.getId())
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verifica se o livro foi realmente deletado
        given()
                .when()
                .get("/api/books/{id}", savedBook.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
