package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.model.EntityTestFactory;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthorRepositoryTest {
    // Set automatically the attribute to the authorRepository instance
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityTestFactory<Author> authorFactory;

    private List<Author> dummyAuthors = new ArrayList<>();

    @BeforeEach
    void createDummyEntities() {

        dummyAuthors = (authorFactory.createValidEntities(3));

        dummyAuthors = authorRepository.saveAll(dummyAuthors);
    }

    // Test CRUD operations

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
        //List<Author> dummyAuthors = new ArrayList<>();

        for (int i = 0; i < dummyAuthors.size(); i++) {

            // check if the authors id are correctly automatic generated
            assertNotNull(authorRepository.getOne(dummyAuthors.get(i).getId()));
            assertNotNull(authorRepository.getOne(dummyAuthors.get(i).getId()));

            var savedAuthor = authorRepository.getOne(dummyAuthors.get(i).getId());

            // check if the authors contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedAuthor.getCreatedAt());
            assertNotNull(savedAuthor.getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedAuthor.getFiscalCode(), dummyAuthors.get(i).getFiscalCode());
            assertEquals(savedAuthor.getName(), dummyAuthors.get(i).getName());
            assertEquals(savedAuthor.getSurname(), dummyAuthors.get(i).getSurname());
            assertEquals(savedAuthor.getDateOfBirth(), dummyAuthors.get(i).getDateOfBirth());
            assertEquals(savedAuthor.getEmail(), dummyAuthors.get(i).getEmail());
            assertEquals(savedAuthor.getMobilePhone(), dummyAuthors.get(i).getMobilePhone());
            assertEquals(savedAuthor.getWebSiteURL(), dummyAuthors.get(i).getWebSiteURL());
            assertEquals(savedAuthor.getBiography(), dummyAuthors.get(i).getBiography());
            assertEquals(savedAuthor.getId(), dummyAuthors.get(i).getId());
        }
    }

    /**
     * Creates an author with the same FiscalCode of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints on the properties that
     * constitute a natural-id
     */
    @Test
    public void testUniqueAuthorIdentifier() {
        var duplicatedAuthor = authorFactory.createValidEntity(0);

        // set manually a new id in order to insert a new record and not for update an existing record
        //duplicatedAuthor.setId(9999l);
//        duplicatedAuthor.setFiscalCode("CGNNMO00T00L000");
//        duplicatedAuthor.setName("John");
//        duplicatedAuthor.setSurname("Cook");

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

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setFiscalCode("ABCDEFGHIJKLMNOP");
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

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setWebSiteURL("KimmyTurner.com");
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

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setEmail("KimmyTurner@mail@10.com");
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

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setMobilePhone("0039333123456");
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

    // Test search operations

    @Test
    public void testFindById() {
        // check the correct reading of the author via findById
        var foundAuthor = authorRepository.findById(dummyAuthors.get(0).getId());

        assertEquals(foundAuthor.get(), dummyAuthors.get(0));
        assertEquals(foundAuthor.get().getId(), dummyAuthors.get(0).getId());

        // try to search for authors by an not existing id
        var notFoundAuthor = authorRepository.findById(999L);

        assertTrue(notFoundAuthor.isEmpty());
    }

    @Test
    public void testFindByName() {
        // check the correct reading of all the authors via findByName
        var foundAuthors = authorRepository.findByName(dummyAuthors.get(0).getName());

        assertTrue(foundAuthors.contains(dummyAuthors.get(0)));
        for (Author a: foundAuthors) {
            assertEquals(a.getName(), dummyAuthors.get(0).getName());
        }

        // try to search for authors by an not existing name
        var notFoundAuthor = authorRepository.findByName("Autore Inesistente");

        assertTrue(notFoundAuthor.isEmpty());
    }

    @Test
    public void testFindBySurname() {
        // check the correct reading of all the authors via findBySurname
        var foundAuthors = authorRepository.findBySurname(dummyAuthors.get(0).getSurname());

        assertTrue(foundAuthors.contains(dummyAuthors.get(0)));
        for (Author a: foundAuthors) {
            assertEquals(a.getSurname(), dummyAuthors.get(0).getSurname());
        }

        // try to search for authors by an not existing surname
        var notFoundAuthor = authorRepository.findBySurname("Cognome Inesistente");

        assertTrue(notFoundAuthor.isEmpty());
    }

    @Test
    public void findByFiscalCode() {
        // check the correct reading of  the author via findByFiscalCode
        // the author found will be just one because the fiscal code is a natural id, therefore unique
        var foundAuthor = authorRepository.findByFiscalCode(dummyAuthors.get(0).getFiscalCode());

        assertTrue(foundAuthor.size() == 1);
        assertTrue(foundAuthor.contains(dummyAuthors.get(0)));
        assertEquals(foundAuthor.get(0).getFiscalCode(), dummyAuthors.get(0).getFiscalCode());

        // try to search for authors by an not existing fiscal code
        var notFoundAuthor = authorRepository.findByFiscalCode("AAAAAA00A00A000A");

        assertTrue(notFoundAuthor.isEmpty());
    }

    @Test
    public void findByEmail() {
        // check the correct reading of all the authors via findByEmail
        // the author found will be just one because the email is saved as unique
        var foundAuthor = authorRepository.findByEmail(dummyAuthors.get(2).getEmail());

        assertTrue(foundAuthor.size() == 1);
        assertTrue(foundAuthor.contains(dummyAuthors.get(2)));
        assertEquals(foundAuthor.get(0).getEmail(), dummyAuthors.get(2).getEmail());

        // try to search for authors by an not existing email
        var notFoundAuthor = authorRepository.findByEmail("emailinesistente@mail.com");

        assertTrue(notFoundAuthor.isEmpty());
    }
}
