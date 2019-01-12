package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.model.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthorRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the authorRepository instance
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityFactory<Author> authorFactory;

    private List<Author> dummyAuthors;

    @BeforeEach
    void createDummyAuthors() {
        // create a list of valid authors entities and save them in the authorRepository
        dummyAuthors = authorFactory.createValidEntities(2);
        dummyAuthors = authorRepository.saveAll(dummyAuthors);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct.
     */
    @Test
    public void testCreateBook() {
        for (int i = 0; i < dummyAuthors.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, authorRepository.count());
            assertNotNull(authorRepository.existsById(dummyAuthors.get(i).getId()));

            // check if the authors contain the createdAt and updatedAt annotation that are automatically populate,
            // and check if the books id are correctly automatic generated
            assertNotNull(dummyAuthors.get(i).getCreatedAt());
            assertNotNull(dummyAuthors.get(i).getUpdatedAt());
            assertNotNull(dummyAuthors.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals("CGNNMO00T00L00" + i + "A", dummyAuthors.get(i).getFiscalCode());
            assertEquals("Nome" + i, dummyAuthors.get(i).getName());
            assertEquals("Cognome" + i, dummyAuthors.get(i).getSurname());
            assertEquals(LocalDate.now().minusYears(35 + i), dummyAuthors.get(i).getDateOfBirth());
            assertEquals("Nome" + i + "Cognome" + i + "@author-mail.com", dummyAuthors.get(i).getEmail());
            assertEquals("333333333" + i, dummyAuthors.get(i).getMobilePhone());
            assertEquals("https://www." + "Nome" + i + "Cognome" + i + ".org", dummyAuthors.get(i).getWebSiteURL());
            assertEquals("Nome" + i + " is a friendly government politician and has a post-graduate degree " +
                    "in philosophy, politics and economics. To learn more about " + "Nome" + i + " " +
                    "Cognome" + i + " visit the website " + "https://www." + "Nome" + i + "Cognome" + i + ".org !", dummyAuthors.get(i).getBiography());
        }
    }

    /**
     * Throws an exception when attempting to create an author without mandatory attributes.
     */
    @Test
    public void testIllegalCreateAuthor() {
        Author invalidAuthor = new Author();

        assertThrows(DataIntegrityViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    @Test
    public void testSave() {
        var author = authorFactory.createValidEntity(2);

        assertDoesNotThrow(() -> authorRepository.save(author));
    }

    /**
     * Creates an author with the same FiscalCode of another and throws an exception when attempting to insert data
     * by the unique constraints on the properties that constitute a natural-id.
     */
    @Test
    public void testUniqueFiscalCodeIdentifier() {
        var duplicatedAuthor = authorFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedAuthor.setFiscalCode("CGNNMO00T00L000A");
            authorRepository.saveAndFlush(duplicatedAuthor);
        });
    }

    /**
     * Creates an author with the same email of another and throws an exception when attempting to insert data by
     * violating the unique constraints.
     */
    @Test
    public void testUniqueEmail() {
        var duplicatedAuthor = authorFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedAuthor.setEmail("Nome0Cognome0@author-mail.com");
            authorRepository.saveAndFlush(duplicatedAuthor);
        });
    }

    /**
     * Creates an author with the same mobile phone of another and throws an exception when attempting to insert data
     * by violating the unique constraints.
     */
    @Test
    public void testUniqueMobilePhone() {
        var duplicatedAuthor = authorFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedAuthor.setMobilePhone("3333333330");
            authorRepository.saveAndFlush(duplicatedAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal fiscal code.
     */
    @Test
    public void testIllegalFiscalCode() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setFiscalCode("ABCDEFGHIJKLMNOP");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal website URL.
     */
    @Test
    public void testIllegalWebSiteURL() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setWebSiteURL("NomeCognome.com");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /*
     * Throws an exception when attempting to create an author with illegal email.
     */
    @Test
    public void testIllegalEmail() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setEmail("NomeCognome@mail@10.com");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal mobile phone.
     */
    @Test
    public void testIllegalMobilePhone() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidAuthor.setMobilePhone("0039333123456");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create an author with illegal date of birth.
     */
    @Test
    public void testIllegalDateOfBirth() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(DateTimeException.class, () -> {
            invalidAuthor.setDateOfBirth(LocalDate.of(1980, 13, 32));
            authorRepository.save(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create or update an author with illegal size for the name attribute.
     */
    @Test
    public void testIllegalNameSize() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAuthor.setName("PrimoNomeSecondoNomeTerzoNomeQuartoNome");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create or update an author with illegal size for the surname attribute.
     */
    @Test
    public void testIllegalSurnameSize() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAuthor.setSurname("PrimoCognomeSecondoCognomeTerzoCognome");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Throws an exception when attempting to create or update an author with illegal size for the biography attribute.
     */
    @Test
    public void testIllegalBiographySize() {
        Author invalidAuthor = authorFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAuthor.setBiography("Julie is a friendly government politician. She has a post-graduate degree in " +
                    "philosophy, politics and economics. \n She is currently single. Her most recent romance was with a " +
                    "sous chef called Walter Roland Campbell, who was the same age as her. They broke up because Walter " +
                    "wanted a quieter life than Julie could provide.\n Julie has one child with ex-boyfriend Walter: " +
                    "Montgomery aged 4.\n Julie's best friend is a government politician called Josiah O'Doherty. They " +
                    "have a very fiery friendship.");
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly.
     */
    @Test
    public void testUpdateAuthor() {
        // get an author from the repository
        Author savedAuthor = dummyAuthors.get(0);

        // change some attributes and update the Author object
        savedAuthor.setName("Nuovo Nome");
        savedAuthor.setSurname("Nuovo Cognome");
        savedAuthor.setDateOfBirth(LocalDate.now().minusYears(35));
        savedAuthor.setEmail("NuovoNomeNuovoCognome@author-mail.com");
        savedAuthor.setMobilePhone("3333333300");
        savedAuthor.setWebSiteURL("https://www.NuovoNomeNuovoCognome.org");
        savedAuthor.setBiography("To learn more about Nuovo Nome visit the website " + savedAuthor.getWebSiteURL() + " !");

        savedAuthor = authorRepository.save(savedAuthor);

        // clear the memory in order to get a new istance of the saved author from the db
        authorRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Author updatedAuthor = authorRepository.findById(savedAuthor.getId()).get();

        assertTrue(authorRepository.existsById(updatedAuthor.getId()));
        assertEquals("Nuovo Nome", updatedAuthor.getName());
        assertEquals("Nuovo Cognome", updatedAuthor.getSurname());
        assertEquals(LocalDate.now().minusYears(35), updatedAuthor.getDateOfBirth());
        assertEquals("NuovoNomeNuovoCognome@author-mail.com", updatedAuthor.getEmail());
        assertEquals("3333333300", updatedAuthor.getMobilePhone());
        assertEquals("https://www.NuovoNomeNuovoCognome.org", updatedAuthor.getWebSiteURL());
        assertEquals("To learn more about Nuovo Nome visit the website " + savedAuthor.getWebSiteURL() + " !",
                updatedAuthor.getBiography());
    }

    /**
     * Throws an exception when attempting to update the immutable natural identifier fiscal code.
     */
    @Test
    public void testUpdateFiscalCode() {
        // get an author from the repository, modify the fiscal code and update the author object
        Author savedAuthor = dummyAuthors.get(0);

        assertThrows(JpaSystemException.class, () -> {
            savedAuthor.setFiscalCode("CGNNMO00A00L000A");
            authorRepository.saveAndFlush(savedAuthor);
        }, "It's not possible to updated an author fiscal code!");
    }

    /**
     * Delete an entry and check that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAuthor() {
        // get an author from the repository and delete it
        Author savedAuthor = dummyAuthors.get(0);
        authorRepository.delete(savedAuthor);

        // check that the author has been deleted correctly
        assertFalse(authorRepository.existsById(savedAuthor.getId()));
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllAuthors() {
        authorRepository.deleteAll();
        assertTrue(authorRepository.findAll().isEmpty());
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedAuthors = authorRepository.findAll();

        // check if all the authors are correctly added to the repository
        assertTrue(savedAuthors.containsAll(dummyAuthors), "findAll should fetch all dummy authors");
    }

    @Test
    public void testFindById() {
        // check the correct reading of the author via findById
        var foundAuthor = authorRepository.findById(dummyAuthors.get(0).getId());

        assertEquals(foundAuthor.get(), dummyAuthors.get(0));
        assertEquals(foundAuthor.get().getId(), dummyAuthors.get(0).getId());
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
        var notFoundAuthor = authorRepository.findByName("Nome Inesistente");

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
        var foundAuthor = authorRepository.findByFiscalCode(dummyAuthors.get(0).getFiscalCode());

        assertEquals(foundAuthor, dummyAuthors.get(0));
        assertEquals(foundAuthor.getFiscalCode(), dummyAuthors.get(0).getFiscalCode());

        // try to search for authors by an not existing fiscal code
        var notFoundAuthor = authorRepository.findByFiscalCode("AAAAAA00A00A000A");

        assertNull(notFoundAuthor);
    }

    @Test
    public void findByEmail() {
        // check the correct reading of all the authors via findByEmail
        var foundAuthor = authorRepository.findByEmail(dummyAuthors.get(0).getEmail());

        assertEquals(foundAuthor, dummyAuthors.get(0));
        assertEquals(foundAuthor.getEmail(), dummyAuthors.get(0).getEmail());

        // try to search for authors by an not existing email
        var notFoundAuthor = authorRepository.findByEmail("emailinesistente@mail.com");

        assertNull(notFoundAuthor);
    }
}
