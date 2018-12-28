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
                "in philosophy, politics and economics. \\n She is currently single. Her most recent romance was with" +
                " a sous chef called Walter Roland Campbell.\\n Julie has one child with Walter: Montgomery aged 4.");
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

    @Test
    public void testCreateBook() {
        /*
         * Insert many entries in the repository and check if these are readable and the attributes are correct
         */
        List<Author> savedAuthor = new ArrayList<>();

        for (int i = 0; i < dummyAuthors.size(); i++) {
            // check if the authors id are correctly automatic generated
            assertNotNull(authorRepository.getOne(dummyAuthors.get(i).getId()));
            savedAuthor.add(authorRepository.getOne(dummyAuthors.get(i).getId()));

            // check if the authors contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedAuthor.get(i).getCreatedAt());
            assertNotNull(savedAuthor.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedAuthor.get(i).getFiscalCode(), dummyAuthors.get(i).getFiscalCode());
            assertEquals(savedAuthor.get(i).getName(), dummyAuthors.get(i).getName());
            assertEquals(savedAuthor.get(i).getSurname(), dummyAuthors.get(i).getSurname());
            assertEquals(savedAuthor.get(i).getBirthDate(), dummyAuthors.get(i).getBirthDate());
            assertEquals(savedAuthor.get(i).getEmail(), dummyAuthors.get(i).getEmail());
            assertEquals(savedAuthor.get(i).getMobilePhone(), dummyAuthors.get(i).getMobilePhone());
            assertEquals(savedAuthor.get(i).getWebSiteURL(), dummyAuthors.get(i).getWebSiteURL());
            assertEquals(savedAuthor.get(i).getBiography(), dummyAuthors.get(i).getBiography());
        }
    }
}
