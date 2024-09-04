package com.example.db2.repository;

import com.example.db2.model.Author;
import com.example.db2.model.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Book testBook;
    private Author testAuthor;

    @BeforeEach
    public void setUp() {
        // Limpa os repositórios antes de cada teste
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        // Cria um autor de teste
        testAuthor = new Author();
        testAuthor.setName("Test Author");
        testAuthor = authorRepository.save(testAuthor);

        // Cria um livro de teste com o autor válido
        testBook = new Book();
        testBook.setTitle("Sample Book");
        testBook.setAuthorId(testAuthor.getId());
        testBook.setPublicationDate(LocalDate.of(2024, 1, 1)); // Definindo uma data
        testBook.setGenre("Fiction"); // Novo campo
        testBook.setIsbn("1234567890"); // Novo campo
        testBook = bookRepository.save(testBook);
    }

    @AfterEach
    public void tearDown() {
        // Limpa os repositórios após cada teste
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void testSaveBook() {
        Book book = new Book();
        book.setTitle("Another Book");
        book.setAuthorId(testAuthor.getId());
        book.setPublicationDate(LocalDate.of(2024, 2, 1)); // Definindo uma data
        book.setGenre("Non-Fiction"); // Novo campo
        book.setIsbn("0987654321"); // Novo campo

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Another Book");
        assertThat(savedBook.getAuthorId()).isEqualTo(testAuthor.getId());
        assertThat(savedBook.getPublicationDate()).isEqualTo(LocalDate.of(2024, 2, 1)); // Verificando a data
        assertThat(savedBook.getGenre()).isEqualTo("Non-Fiction"); // Verificando o campo genre
        assertThat(savedBook.getIsbn()).isEqualTo("0987654321"); // Verificando o campo isbn
    }

    @Test
    public void testFindAllBooks() {
        List<Book> books = bookRepository.findAll();

        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Sample Book");
        assertThat(books.get(0).getPublicationDate()).isEqualTo(LocalDate.of(2024, 1, 1)); // Verificando a data
        assertThat(books.get(0).getGenre()).isEqualTo("Fiction"); // Verificando o campo genre
        assertThat(books.get(0).getIsbn()).isEqualTo("1234567890"); // Verificando o campo isbn
    }

    @Test
    public void testFindBookById() {
        Optional<Book> foundBook = bookRepository.findById(testBook.getId());

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Sample Book");
        assertThat(foundBook.get().getAuthorId()).isEqualTo(testAuthor.getId());
        assertThat(foundBook.get().getPublicationDate()).isEqualTo(LocalDate.of(2024, 1, 1)); // Verificando a data
        assertThat(foundBook.get().getGenre()).isEqualTo("Fiction"); // Verificando o campo genre
        assertThat(foundBook.get().getIsbn()).isEqualTo("1234567890"); // Verificando o campo isbn
    }

    @Test
    public void testUpdateBook() {
        Optional<Book> optionalBook = bookRepository.findById(testBook.getId());

        assertThat(optionalBook).isPresent();

        Book bookToUpdate = optionalBook.get();
        bookToUpdate.setTitle("Updated Title");
        bookToUpdate.setPublicationDate(LocalDate.of(2024, 3, 1)); // Atualizando a data
        bookToUpdate.setGenre("Updated Genre"); // Atualizando o campo genre
        bookToUpdate.setIsbn("2222222222"); // Atualizando o campo isbn
        Book updatedBook = bookRepository.save(bookToUpdate);

        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getAuthorId()).isEqualTo(testAuthor.getId());
        assertThat(updatedBook.getPublicationDate()).isEqualTo(LocalDate.of(2024, 3, 1)); // Verificando a data
        assertThat(updatedBook.getGenre()).isEqualTo("Updated Genre"); // Verificando o campo genre
        assertThat(updatedBook.getIsbn()).isEqualTo("2222222222"); // Verificando o campo isbn
    }

    @Test
    public void testDeleteBook() {
        Optional<Book> foundBook = bookRepository.findById(testBook.getId());
        assertThat(foundBook).isPresent();

        bookRepository.delete(foundBook.get());
        Optional<Book> deletedBook = bookRepository.findById(testBook.getId());
        assertThat(deletedBook).isNotPresent();
    }
}
