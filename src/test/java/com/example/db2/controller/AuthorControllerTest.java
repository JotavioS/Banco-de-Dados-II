package com.example.db2.controller;

import com.example.db2.model.Author;
import com.example.db2.repository.AuthorRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        authorRepository.deleteAll();  // Limpa o banco de dados antes de cada teste

        // Cria um autor de teste
        testAuthor = new Author();
        testAuthor.setName("Jane Doe");
        testAuthor.setBirthDate(LocalDate.of(1980, 1, 1));
        testAuthor.setNationality("American");
        testAuthor = authorRepository.save(testAuthor);
    }

    @Test
    public void testGetAllAuthors() {
        given()
            .when()
            .get("/api/authors")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(1))
            .body("[0].name", equalTo("Jane Doe"))
            .body("[0].birthDate", equalTo("1980-01-01"))
            .body("[0].nationality", equalTo("American"));
    }

    @Test
    public void testCreateAuthor() {
        Map<String, Object> newAuthor = new HashMap<>();
        newAuthor.put("name", "John Smith");
        newAuthor.put("birthDate", "1990-05-15");
        newAuthor.put("nationality", "British");

        given()
            .contentType(ContentType.JSON)
            .body(newAuthor)
            .when()
            .post("/api/authors")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", notNullValue())
            .body("name", equalTo("John Smith"))
            .body("birthDate", equalTo("1990-05-15"))
            .body("nationality", equalTo("British"));
    }

    @Test
    public void testGetAuthorById() {
        given()
            .when()
            .get("/api/authors/{id}", testAuthor.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(testAuthor.getId()))
            .body("name", equalTo("Jane Doe"))
            .body("birthDate", equalTo("1980-01-01"))
            .body("nationality", equalTo("American"));
    }

    @Test
    public void testUpdateAuthor() {
        Map<String, Object> updatedAuthor = new HashMap<>();
        updatedAuthor.put("name", "Jane Smith");
        updatedAuthor.put("birthDate", "1985-07-20");
        updatedAuthor.put("nationality", "Canadian");

        given()
            .contentType(ContentType.JSON)
            .body(updatedAuthor)
            .when()
            .put("/api/authors/{id}", testAuthor.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(testAuthor.getId()))
            .body("name", equalTo("Jane Smith"))
            .body("birthDate", equalTo("1985-07-20"))
            .body("nationality", equalTo("Canadian"));
    }

    @Test
    public void testDeleteAuthor() {
        given()
            .when()
            .delete("/api/authors/{id}", testAuthor.getId())
            .then()
            .statusCode(HttpStatus.OK.value());

        // Verifica se o autor foi realmente deletado
        given()
            .when()
            .get("/api/authors/{id}", testAuthor.getId())
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testCreateAuthorWithoutName() {
        Map<String, Object> newAuthor = new HashMap<>();
        newAuthor.put("birthDate", "1995-08-25");
        newAuthor.put("nationality", "French");

        given()
            .contentType(ContentType.JSON)
            .body(newAuthor)
            .when()
            .post("/api/authors")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(equalTo("Erro: O nome do autor é obrigatório."));
    }

    @Test
    public void testUpdateNonExistentAuthor() {
        Map<String, Object> updatedAuthor = new HashMap<>();
        updatedAuthor.put("name", "Non Existent");
        updatedAuthor.put("birthDate", "1995-08-25");
        updatedAuthor.put("nationality", "French");

        given()
            .contentType(ContentType.JSON)
            .body(updatedAuthor)
            .when()
            .put("/api/authors/{id}", "non-existent-id")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body(equalTo("Erro: autor não encontrado."));
    }
}