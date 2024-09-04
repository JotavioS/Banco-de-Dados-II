package com.example.db2.repository;

import com.example.db2.model.Author;
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
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    public void setUp() {
        // Limpa o repositório antes de cada teste para garantir um estado consistente
        authorRepository.deleteAll();

        // Cria um autor de teste para ser usado em diferentes cenários de teste
        testAuthor = new Author();
        testAuthor.setName("Jane Doe");
        testAuthor.setBirthDate(LocalDate.of(1980, 1, 1));
        testAuthor.setNationality("American");
        testAuthor = authorRepository.save(testAuthor);  // Salva o autor de teste no repositório
    }

    @AfterEach
    public void tearDown() {
        // Limpa o repositório após cada teste para evitar interferência entre testes
        authorRepository.deleteAll();
    }

    @Test
    public void testSaveAuthor() {
        // Teste para verificar se um autor pode ser salvo corretamente no repositório
        Author author = new Author();
        author.setName("John Smith");
        author.setBirthDate(LocalDate.of(1990, 5, 15));
        author.setNationality("British");

        Author savedAuthor = authorRepository.save(author);

        // Verifica se o autor salvo tem um ID não nulo e se os campos são os esperados
        assertThat(savedAuthor.getId()).isNotNull();
        assertThat(savedAuthor.getName()).isEqualTo("John Smith");
        assertThat(savedAuthor.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(savedAuthor.getNationality()).isEqualTo("British");
    }

    @Test
    public void testFindAllAuthors() {
        // Teste para verificar se todos os autores podem ser recuperados
        List<Author> authors = authorRepository.findAll();
        
        // Verifica se a lista de autores não está vazia e contém o autor de teste
        assertThat(authors).isNotEmpty();
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getName()).isEqualTo("Jane Doe");
        assertThat(authors.get(0).getBirthDate()).isEqualTo(LocalDate.of(1980, 1, 1));
        assertThat(authors.get(0).getNationality()).isEqualTo("American");
    }

    @Test
    public void testFindAuthorById() {
        // Teste para verificar se um autor pode ser encontrado pelo seu ID
        Optional<Author> foundAuthor = authorRepository.findById(testAuthor.getId());
        
        // Verifica se o autor foi encontrado e se os campos estão corretos
        assertThat(foundAuthor).isPresent();
        assertThat(foundAuthor.get().getName()).isEqualTo("Jane Doe");
        assertThat(foundAuthor.get().getBirthDate()).isEqualTo(LocalDate.of(1980, 1, 1));
        assertThat(foundAuthor.get().getNationality()).isEqualTo("American");
    }

    @Test
    public void testUpdateAuthor() {
        // Teste para verificar se um autor pode ser atualizado corretamente
        Optional<Author> optionalAuthor = authorRepository.findById(testAuthor.getId());
        
        assertThat(optionalAuthor).isPresent();

        Author authorToUpdate = optionalAuthor.get();
        authorToUpdate.setName("Jane Smith");
        authorToUpdate.setBirthDate(LocalDate.of(1985, 7, 20));
        authorToUpdate.setNationality("Canadian");
        Author updatedAuthor = authorRepository.save(authorToUpdate);

        // Verifica se os campos do autor foram atualizados corretamente
        assertThat(updatedAuthor.getName()).isEqualTo("Jane Smith");
        assertThat(updatedAuthor.getBirthDate()).isEqualTo(LocalDate.of(1985, 7, 20));
        assertThat(updatedAuthor.getNationality()).isEqualTo("Canadian");
    }

    @Test
    public void testDeleteAuthor() {
        // Teste para verificar se um autor pode ser deletado corretamente
        Optional<Author> foundAuthor = authorRepository.findById(testAuthor.getId());
        assertThat(foundAuthor).isPresent();

        authorRepository.delete(foundAuthor.get());
        Optional<Author> deletedAuthor = authorRepository.findById(testAuthor.getId());
        
        // Verifica se o autor não está mais presente no repositório
        assertThat(deletedAuthor).isNotPresent();
    }
}
