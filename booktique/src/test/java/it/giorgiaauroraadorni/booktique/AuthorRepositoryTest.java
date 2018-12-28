package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.repository.AuthorRepository;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthorRepositoryTest {
    // Set automatically the attribute to the authorRepository instance
    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> dummyAuthors;

    @BeforeEach
    void createDummyAuthor() {
        /*
         * Create a list of authors entities that will be use in the test
         */
        dummyAuthors = IntStream
                .range(0, 3)
                .mapToObj(i -> new Author())
                .collect(Collectors.toList());

        // Create an author with only the mandatory parameter (inherited from person)
        dummyAuthors.get(0).setFiscalCode("ABCDEF12G24H567I");
        dummyAuthors.get(0).setName("John");
        dummyAuthors.get(0).setSurname("Cook");

        // Create an author with all the person attributes

        dummyAuthors.get(1).setFiscalCode("LMNOPQ89R10S111T");
        dummyAuthors.get(1).setName("Nathalie");
        dummyAuthors.get(1).setSurname("Russel");
        dummyAuthors.get(1).setBirthDate(LocalDate.of(1900, 1, 1));
        dummyAuthors.get(1).setEmail("NathalieRussel@mail.com");
        dummyAuthors.get(1).setMobilePhone("+39373973933");

        // Create an author with many attributes
        dummyAuthors.get(2).setFiscalCode("SMTJLU80T52F205H");
        dummyAuthors.get(2).setName("Julie");
        dummyAuthors.get(2).setSurname("Smith");
        dummyAuthors.get(2).setBirthDate(LocalDate.of(1980, 12, 12));
        dummyAuthors.get(2).setEmail("JulieSmith@mail.com");
        dummyAuthors.get(2).setMobilePhone("326330961");
        dummyAuthors.get(2).setWebSiteURL("https://www.JulieSmith.org");
        dummyAuthors.get(2).setBiography("Julie is a friendly government politician. She has a post-graduate degree " +
                "in  philosophy, politics and economics. \n She is currently single. Her most recent romance was with" +
                " a sous chef called Walter Roland Campbell, who was the same age as her. They broke up because " +
                "Walter wanted a quieter life than Julie could provide.\n Julie has one child with ex-boyfriend " +
                "Walter: Montgomery aged 4.\n Julie's best friend is a government politician called Josiah O'Doherty." +
                "They have a very fiery friendship.");
        // save the authors in the repository
        authorRepository.saveAll(dummyAuthors);
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedAuthor = authorRepository.findAll();

        // check if all the authors are correctly added to the repository
        assertTrue(savedAuthor.containsAll(dummyAuthors), "findAll should fetch all dummy authors");
    }
}
