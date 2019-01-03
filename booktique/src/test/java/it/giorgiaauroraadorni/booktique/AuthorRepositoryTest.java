package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
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

    /**
     * Create a list of authors entities that will be use in the test
     */
    @BeforeEach
    void createDummyAuthor() {
        dummyAuthors = IntStream
                .range(0, 3)
                .mapToObj(i -> new Author())
                .collect(Collectors.toList());

        // create an author with only the mandatory parameter (inherited from person)
        dummyAuthors.get(0).setFiscalCode("ABCDEF12G24H567I");
        dummyAuthors.get(0).setName("John");
        dummyAuthors.get(0).setSurname("Cook");

        // create an author with all the person attributes
        dummyAuthors.get(1).setFiscalCode("LMNOPQ89R10S111T");
        dummyAuthors.get(1).setName("Nathalie");
        dummyAuthors.get(1).setSurname("Russel");
        dummyAuthors.get(1).setDateOfBirth(LocalDate.of(1900, 1, 1));
        dummyAuthors.get(1).setEmail("NathalieRussel@mail.com");
        dummyAuthors.get(1).setMobilePhone("+393739739330");

        // create an author with many attributes
        dummyAuthors.get(2).setFiscalCode("SMTJLU80T52F205H");
        dummyAuthors.get(2).setName("Julie");
        dummyAuthors.get(2).setSurname("Smith");
        dummyAuthors.get(2).setDateOfBirth(LocalDate.of(1980, 12, 12));
        dummyAuthors.get(2).setEmail("JulieSmith@mail.com");
        dummyAuthors.get(2).setMobilePhone("3263309610");
        dummyAuthors.get(2).setWebSiteURL("https://www.JulieSmith.org");
        dummyAuthors.get(2).setBiography("Julie is a friendly government politician. She has a post-graduate degree " +
                "in philosophy, politics and economics. \\n She is currently single. Her most recent romance was with" +
                " a sous chef called Walter Roland Campbell.\\n Julie has one child with Walter: Montgomery aged 4.");

        // save the authors in the repository
        dummyAuthors = authorRepository.saveAll(dummyAuthors);
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedAuthors = authorRepository.findAll();

        // check if all the authors are correctly added to the repository
        assertTrue(savedAuthors.containsAll(dummyAuthors), "findAll should fetch all dummy authors");
    }

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateBook() {
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
            assertEquals(savedAuthor.get(i).getDateOfBirth(), dummyAuthors.get(i).getDateOfBirth());
            assertEquals(savedAuthor.get(i).getEmail(), dummyAuthors.get(i).getEmail());
            assertEquals(savedAuthor.get(i).getMobilePhone(), dummyAuthors.get(i).getMobilePhone());
            assertEquals(savedAuthor.get(i).getWebSiteURL(), dummyAuthors.get(i).getWebSiteURL());
            assertEquals(savedAuthor.get(i).getBiography(), dummyAuthors.get(i).getBiography());
            assertEquals(savedAuthor.get(i).getId(), dummyAuthors.get(i).getId());
        }
    }

    /**
     * Creates an author with the same FiscalCode of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints on the properties that
     * constitute a natural-id
     */
    @Test
    public void testUniqueAuthorIdentifier() {
        Author duplicatedAuthor = new Author();

        // set manually a new id in order to insert a new record and not for update an existing record
        duplicatedAuthor.setId(9999l);
        duplicatedAuthor.setFiscalCode("ABCDEF12G24H567I");
        duplicatedAuthor.setName("John");
        duplicatedAuthor.setSurname("Cook");

        // save the author in the repository
        assertThrows(DataIntegrityViolationException.class, () -> {
            authorRepository.saveAndFlush(duplicatedAuthor);
        });
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateAuthor() {
        // get an author from the repository
        Author savedAuthor = authorRepository.findById(dummyAuthors.get(2).getId()).get();

        // change some attributes
        savedAuthor.setName("Tom");
        savedAuthor.setWebSiteURL("http://www.TomSmith.org");
        savedAuthor.setEmail("TomSmith@mail.com");
        savedAuthor.setBiography(null);

        // update the Author object
        authorRepository.save(savedAuthor);
        Author updatedAuthor = authorRepository.findById(savedAuthor.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedAuthor);
        assertEquals(savedAuthor, updatedAuthor);
        assertEquals("Tom", updatedAuthor.getName());
        assertEquals("http://www.TomSmith.org", updatedAuthor.getWebSiteURL());
        assertEquals("TomSmith@mail.com", updatedAuthor.getEmail());
        assertNull(updatedAuthor.getBiography());
    }

    /**
     * Throws an exception when attempting to create an author without mandatory attributes
     */
    @Test
    public void testIllegalCreateAuthor() {
        Author invalidAuthor = new Author();

        assertThrows(DataIntegrityViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal fiscal code format type
     */
    @Test
    public void testIllegalFiscalCodeFormat() {
        Author invalidAuthor = new Author();

        invalidAuthor.setName("Kimmy");
        invalidAuthor.setSurname("Turner");
        invalidAuthor.setFiscalCode("ABCDEFGHIJKLMNOP");
        assertThrows(ConstraintViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal website URL format type
     */
    @Test
    public void testIllegalWebSiteURLFormat() {
        Author invalidAuthor = new Author();

        invalidAuthor.setName("Kimmy");
        invalidAuthor.setSurname("Turner");
        invalidAuthor.setFiscalCode("TRNKMM90T04Z000A");
        invalidAuthor.setWebSiteURL("KimmyTurner.com");
        assertThrows(ConstraintViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /*
     * Throws an exception when attempting to create an author with illegal email format type
     */
    @Test
    public void testIllegalEmailFormat() {
        Author invalidAuthor = new Author();

        invalidAuthor.setName("Kimmy");
        invalidAuthor.setSurname("Turner");
        invalidAuthor.setFiscalCode("TRNKMM90T04Z000A");
        invalidAuthor.setEmail("KimmyTurner@mail@10.com");
        assertThrows(ConstraintViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal mobile phone format type
     */
    @Test
    public void testIllegalMobilePhoneFormat() {
        Author invalidAuthor = new Author();

        invalidAuthor.setName("Kimmy");
        invalidAuthor.setSurname("Turner");
        invalidAuthor.setFiscalCode("TRNKMM90T04Z000A");
        invalidAuthor.setMobilePhone("0039333123456");
        assertThrows(ConstraintViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal date of birth format type
     */
    @Test
    public void testIllegalDateOfBirthFormat() {
        Author invalidAuthor = new Author();

        invalidAuthor.setName("Kimmy");
        invalidAuthor.setSurname("Turner");
        invalidAuthor.setFiscalCode("TRNKMM90T04Z000A");
        assertThrows(DateTimeException.class, () -> {
            invalidAuthor.setDateOfBirth(LocalDate.of(1980, 13, 32));
            authorRepository.save(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create or update an author with illegal size for the attributes
     */
    @Test
    public void testIllegalSizeAttributes() {
        Author invalidAuthor = new Author();

        invalidAuthor.setName("Kimmy");
        invalidAuthor.setSurname("Turner");
        invalidAuthor.setFiscalCode("TRNKMM90T04Z000A");
        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAuthor.setBiography("Julie is a friendly government politician. She has a post-graduate degree in " +
                    "philosophy, politics and economics. \n She is currently single. Her most recent romance was with a " +
                    "sous chef called Walter Roland Campbell, who was the same age as her. They broke up because Walter " +
                    "wanted a quieter life than Julie could provide.\n Julie has one child with ex-boyfriend Walter: " +
                    "Montgomery aged 4.\n Julie's best friend is a government politician called Josiah O'Doherty. They " +
                    "have a very fiery friendship.");
            authorRepository.saveAndFlush(invalidAuthor);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidAuthor.setName("KimmyAntoinetteEvangelineJustine");
            authorRepository.saveAndFlush(invalidAuthor);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidAuthor.setSurname("TurnerDonaldsonPenningtonNicholson");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Delete an entry and check if it was removed correctly
     */
    @Test
    public void testDeleteAuthor() {
        // get an author from the repository
        Author savedAuthor = authorRepository.findById(dummyAuthors.get(0).getId()).get();

        // delete the author object
        authorRepository.delete(savedAuthor);

        // check that the author has been deleted correctly
        assertEquals(authorRepository.findById(dummyAuthors.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        authorRepository.deleteAll();
        assertTrue(authorRepository.findAll().isEmpty());
    }
}
