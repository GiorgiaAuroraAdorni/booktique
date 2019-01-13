package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.model.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static it.giorgiaauroraadorni.booktique.utility.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the BookRepository instance
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityFactory<Book> bookFactory;

    @Autowired
    private EntityFactory<Author> authorFactory;

    private List<Book> dummyBooks;

    @BeforeEach
    void createDummyBooks() {
        // create a list of valid books entities
        dummyBooks = bookFactory.createValidEntities(2);

        // add the first book as prequel for the second one
        dummyBooks.get(1).addPrequel(dummyBooks.get(0));

        // save the created entities in the bookRepository and persist addresses
        dummyBooks = bookRepository.saveAll(dummyBooks);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct.
     */
    @Test
    public void testCreateBook() {
        for (int i = 0; i < dummyBooks.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, bookRepository.count());
            assertTrue(bookRepository.existsById(dummyBooks.get(i).getId()));

            // check if the books contain the createdAt and updatedAt annotation that are automatically populate,
            // and check if the books id are correctly automatic generated
            assertNotNull(dummyBooks.get(i).getCreatedAt());
            assertNotNull(dummyBooks.get(i).getUpdatedAt());
            assertNotNull(dummyBooks.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertAttributesEquals(bookFactory.createValidEntity(i), dummyBooks.get(i), false);
            assertAssociationEquals(dummyBooks.get(i).getAuthors(), Set.of(authorFactory.createValidEntity(i)), false);
        }
    }

    /**
     * Throws an exception when attempting to create a book without mandatory attributes.
     */
    @Test
    public void testIllegalCreateBook() {
        Book invalidBook = new Book();

        assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.saveAndFlush(invalidBook);
        });
    }

    @Test
    public void testSave() {
        var book = bookFactory.createValidEntity(3);

        assertDoesNotThrow(() -> bookRepository.save(book));
    }

    /**
     * Creates a book with the same ISBN of another and throws an exception when attempting to insert data
     * by violating the unique constraints on the properties that constitute a natural-id.
     */
    @Test
    public void testUniqueBookIdentifier() {
        Book duplicatedBook = bookFactory.createValidEntity();

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedBook.setIsbn("978-00-00-00000-0");
            bookRepository.saveAndFlush(duplicatedBook);
        });
    }

    /**
     * Test the correct persist of books authors.
     */
    @Test
    public void testBookAuthors() {
        for (Book book: dummyBooks)
            for (Author a: book.getAuthors()) {
                assertTrue(authorRepository.existsById(a.getId()));
            }
    }

    /**
     * Test the correct persist and merge of books prequel and sequel.
     */
    @Test
    public void testBookPrequel() {
        // check if the books prequels are set correctly
        assertNull(bookRepository.findById(dummyBooks.get(0).getId()).get().getPrequel());
        assertNotNull(bookRepository.findById(dummyBooks.get(1).getId()).get().getPrequel());
        assertEquals(bookRepository.getOne(dummyBooks.get(0).getId()),
                bookRepository.getOne(dummyBooks.get(1).getId()).getPrequel());

        // check if the books sequels are set correctly
        assertNotNull(bookRepository.findById(dummyBooks.get(0).getId()).get().getSequel());
        assertNull(bookRepository.findById(dummyBooks.get(1).getId()).get().getSequel());
        assertEquals(bookRepository.getOne(dummyBooks.get(1).getId()),
                bookRepository.getOne(dummyBooks.get(0).getId()).getSequel());
    }

    /**
     * Throws an exception when attempting to create a book with illegal book format type.
     */
    @Test
    public void testIllegalBookFormat() {
        Book invalidBook = bookFactory.createValidEntity();

        assertThrows(IllegalArgumentException.class,
                () -> invalidBook.setBookFormat(Book.Format.valueOf("AUDIO_BOOK")));
    }

    /**
     * Throws an exception when attempting to create or update a book with illegal size for the title attribute.
     */
    @Test
    public void testIllegalTitleSize() {
        Book invalidBook = bookFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidBook.setTitle("The Persecution and Assassination of Jean-Paul Marat as Performed by the Inmates of" +
                    " the Asylum of Charenton Under the Direction of the Marquis de Sade");
            bookRepository.saveAndFlush(invalidBook);
        });
    }

    /**
     * Throws an exception when attempting to create or update a book with illegal size for the publisher attribute.
     */
    @Test
    public void testIllegalPublisherSize() {
        Book invalidBook = bookFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidBook.setPublisher("Marion Boyars Publishers Ltd; 5 Revised edition edizione (1 ottobre 1969)");
            bookRepository.saveAndFlush(invalidBook);
        });
    }

    /**
     * Throws an exception when attempting to create or update a book with illegal size for the subtitle attribute.
     */
    @Test
    public void testIllegalSubtitleSize() {
        Book invalidBook = bookFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidBook.setSubtitle("This extraordinary play, which swept Europe before coming to America, is based " +
                    "on two historical truths: the infamous Marquis de Sade was confined in the lunatic asylum of " +
                    "Charenton, where he staged plays; and the revolutionary Jean-Paul Marat was stabbed in a " +
                    "bathtub by Charlotte Corday at the height of the Terror during the French Revolution. But this " +
                    "play-within-a-play is not historical drama. Its thought is as modern as today's police states " +
                    "and The Bomb; its theatrical impact has everywhere been called a major innovation.");
            bookRepository.saveAndFlush(invalidBook);
        });
    }

    /**
     * Throws an exception when attempting to create or update a book with illegal size for the language attribute.
     */
    @Test
    public void testIllegalLanguageSize() {
        Book invalidBook = bookFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidBook.setLanguage("Indo-European Germanic West Germanic Anglo-Frisian Anglic English");
            bookRepository.saveAndFlush(invalidBook);
        });
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly.
     */
    @Test
    public void testUpdateBook() {
        // get a Book from the repository
        Book savedBook = dummyBooks.get(0);

        // change some attributes and update the Book object
        // the authors association isn't updated
        savedBook.setTitle("Nuovo Titolo");
        savedBook.setPublisher("Nuovo Editore");
        savedBook.setSubtitle("Nuovo Sottotitolo");
        savedBook.setBookFormat(Book.Format.DIGITAL);
        savedBook.setEdition(2);
        savedBook.setLanguage("Nuova Lingua");
        savedBook.setPublicationDate(savedBook.getPublicationDate().plusYears(1));

        savedBook = bookRepository.save(savedBook);

        // clear the memory in order to get a new istance of the saved book from the db
        bookRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Book updatedBook = bookRepository.findById(savedBook.getId()).get();

        assertTrue(bookRepository.existsById(updatedBook.getId()));
        assertAttributesEquals(savedBook, updatedBook, true);
    }

    /**
     * Throws an exception when attempting to update the immutable natural identifier isbn.
     */
    @Test
    public void testUpdateIsbn() {
        // get a book from the repository, modify the isbn and update the isbn object
        Book savedBook = dummyBooks.get(0);

        assertThrows(JpaSystemException.class, () -> {
            savedBook.setIsbn("978-00-00-00000-4");
            bookRepository.saveAndFlush(savedBook);
        }, "It's not possible to updated a book isbn!");
    }

    /**
     * Update the author of an entry and check if the book have been updated correctly.
     */
    @Test
    public void testUpdateBookAuthors() {
        // get a Book from the repository
        Book savedBook = dummyBooks.get(0);
        Set<Author> savedBookAuthors = savedBook.getAuthors();

        // add an author to the savedBookAuthors and update the object
        var author = authorFactory.createValidEntity(3);
        author = authorRepository.save(author);

        savedBookAuthors.add(author);
        savedBook.setAuthors(savedBookAuthors);

        // clear the memory in order to get new istances of the saved book and the authors from the db
        bookRepository.flush();
        entityManager.clear();

        Book updatedBook = bookRepository.findById(savedBook.getId()).get();
        Set<Author> updatedBookAuthors = updatedBook.getAuthors();

        // check that all the attributes have been updated correctly and contain the expected value
        assertTrue(bookRepository.existsById(updatedBook.getId()));
        for (Author a: updatedBookAuthors) {
            assertTrue(authorRepository.existsById(a.getId()));
        }
        assertAttributesEquals(savedBook, updatedBook, true);
        assertAssociationEquals(savedBookAuthors, updatedBookAuthors, true);
    }

    /**
     * Update the prequel of an entry and check if the fields are changed correctly and that the sequel was updated.
     */
    @Test
    public void testUpdateBookPrequel() {
        // get a Book from the repository
        Book sequel = dummyBooks.get(1);
        Book prequel = sequel.getPrequel();

        // modified some prequel attribute and update the book istance
        prequel.setSubtitle("Nuovo sottotitolo");
        prequel.setTitle("Nuovo titolo");
        prequel = bookRepository.save(prequel);

        // clear the memory in order to get new istances of the saved books from the db
        bookRepository.flush();
        entityManager.clear();

        // get the updated book from the repository
        Book updatedSequel = bookRepository.findById(sequel.getId()).get();
        Book updatedPrequel = updatedSequel.getPrequel();

        // check that all the books exist
        assertTrue(bookRepository.existsById(updatedSequel.getId()));
        assertTrue(bookRepository.existsById(updatedPrequel.getId()));

        // check that the sequel book attributes have been updated correctly and contain the expected value
        assertNotNull(updatedSequel.getPrequel());
        assertAttributesEquals(prequel, updatedSequel.getPrequel(), true);
        assertEquals("Nuovo sottotitolo", updatedSequel.getPrequel().getSubtitle());
        assertEquals("Nuovo titolo", updatedSequel.getPrequel().getTitle());

        // check that the prequel book attributes have been updated correctly and contain the expected value
        assertAttributesEquals(prequel, updatedPrequel, true);
        assertNotNull(updatedPrequel.getSequel());
        assertAttributesEquals(updatedSequel, updatedPrequel.getSequel(), true);
    }

    /**
     * Delete an entry and check that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteBook() {
        // get a Book from the repository and delete it
        Book savedBook = dummyBooks.get(0);
        bookRepository.delete(savedBook);

        // check that the book has been deleted correctly
        assertFalse(bookRepository.existsById(savedBook.getId()));
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllBooks() {
        bookRepository.deleteAll();
        assertTrue(bookRepository.findAll().isEmpty());
    }

    /**
     * Delete the prequel of an entry and check that was removed correctly and that the entry was updated.
     */
    @Test
    public void testDeleteBookPrequel() {
        // get a Book and his prequel from the repository
        Book prequel = dummyBooks.get(0);
        Book sequel = prequel.getSequel();

        // delete the book prequel and set null the sequel of the prequel
        bookRepository.delete(prequel);
        sequel.addPrequel(null);
        sequel = bookRepository.save(sequel);

        // clear the memory in order to get a new istance of the saved book from the db
        bookRepository.flush();
        entityManager.clear();

        // get the updated book from the repository
        Book updatedSequel = bookRepository.findById(sequel.getId()).get();

        // check the existence of the sequel, the inexistence of the prequel and that the sequel book attributes have
        // been updated correctly
        assertTrue(bookRepository.existsById(updatedSequel.getId()));
        assertNull(updatedSequel.getPrequel());
        assertAttributesEquals(sequel, updatedSequel, true);
    }

    /**
     * Delete the sequel of an entry and check that was removed correctly and that the entry was updated.
     */
    @Test
    public void testDeleteBookSequel() {
        // get a Book and his sequel from the repository
        Book sequel = dummyBooks.get(1);
        Book prequel = sequel.getPrequel();

        // delete the book sequel and set null the prequel of the sequel
        bookRepository.delete(sequel);
        prequel.addSequel(null);
        prequel = bookRepository.save(prequel);

        // clear the memory in order to get a new istance of the saved book from the db
        bookRepository.flush();
        entityManager.clear();

        // get the updated book from the repository
        Book updatedPrequel = bookRepository.findById(prequel.getId()).get();

        // check the existence of the prequel, the inexistence of the sequel and that the prequel book attributes
        // have been updated correctly
        assertTrue(bookRepository.existsById(updatedPrequel.getId()));
        assertNull(updatedPrequel.getSequel());
        assertAttributesEquals(prequel, updatedPrequel, true);
    }

    /**
     * Throws an exception when attempting to delete an author if he has written a book.
     */
    @Test
    public void testDeleteBookAuthor() {
        // get a pair book-authors from the repository and delete the authors object
        Book book = dummyBooks.get(1);
        Set<Author> authors = book.getAuthors();
        authorRepository.deleteAll();

        // throws an exception when attempting to delete an author of a book
        assertThrows(AssertionFailedError.class, () -> {
            assertTrue(authorRepository.findAll().isEmpty());
            assertNull(bookRepository.findById(book.getId()).get().getAuthors());
            assertNotEquals(authors, bookRepository.findById(book.getId()).get().getAuthors());
        }, "It's not possible to delete an author if he has written a book!");
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedBooks = bookRepository.findAll();
        var savedAuthors = authorRepository.findAll();

        // check if all the books are correctly added to the repository
        assertTrue(savedBooks.containsAll(dummyBooks), "findAll should fetch all dummy books");

        assertFalse(savedAuthors.isEmpty());
        for (Book b: dummyBooks) {
            assertTrue(savedAuthors.containsAll(b.getAuthors()), "findAll should fetch all dummy authors");
        }
    }

    @Test
    public void testFindById() {
        // check the correct reading of the book via findById
        var foundBook = bookRepository.findById(dummyBooks.get(0).getId());

        assertEquals(foundBook.get(), dummyBooks.get(0));
        assertEquals(foundBook.get().getId(), dummyBooks.get(0).getId());
    }

    @Test
    public void testFindByTitle() {
        // check the correct reading of all the books via findByTitle
        var foundBooks = bookRepository.findByTitle(dummyBooks.get(0).getTitle());

        assertTrue(foundBooks.contains(dummyBooks.get(0)));
        for (Book b: foundBooks) {
            assertEquals(b.getTitle(), dummyBooks.get(0).getTitle());
        }

        // try to search for books by a not existing title
        var notFoundBooks = bookRepository.findByTitle("Titolo Inesistente");

        assertTrue(notFoundBooks.isEmpty());
    }

    @Test
    public void testFindByIsbn() {
        // check the correct reading of all the books via findByIsbn
        var foundBook = bookRepository.findByIsbn(dummyBooks.get(0).getIsbn());

        assertEquals(foundBook, dummyBooks.get(0));
        assertEquals(foundBook.getIsbn(), dummyBooks.get(0).getIsbn());

        // try to search for book by a not existing isbn
        var notFoundBooks = bookRepository.findByIsbn("978-11-11-11111-1");

        assertNull(notFoundBooks);
    }

    @Test
    public void testFindByAuthorName() {
        // check the correct reading of all the books via findByAuthors_Name
        for (Author a: dummyBooks.get(0).getAuthors()) {
            var foundBooks = bookRepository.findByAuthors_Name(a.getName());
            assertTrue(foundBooks.contains(dummyBooks.get(0)));

            for (Book b: foundBooks) {
                for (Author ba : b.getAuthors()) {
                    assertEquals(a.getName(), ba.getName());
                }
            }
        }

        // try to search for books by a not existing author name
        var notFoundBooks = bookRepository.findByAuthors_Name("Nome inesistente");

        assertTrue(notFoundBooks.isEmpty());
    }
}
