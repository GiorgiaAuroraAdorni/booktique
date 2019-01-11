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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookRepositoryTest {
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

        // save the created entities in the bookRepository
        dummyBooks = bookRepository.saveAll(dummyBooks);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateBook() {
        List<Book> savedBooks = new ArrayList<>();

        for (int i = 0; i < dummyBooks.size(); i++) {
            // check if the books id are correctly automatic generated
            assertNotNull(bookRepository.getOne(dummyBooks.get(i).getId()));
            savedBooks.add(bookRepository.getOne(dummyBooks.get(i).getId()));

            // check if the books contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedBooks.get(i).getCreatedAt());
            assertNotNull(savedBooks.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedBooks.get(i).getIsbn(), dummyBooks.get(i).getIsbn());
            assertEquals(savedBooks.get(i).getTitle(), dummyBooks.get(i).getTitle());
            assertEquals(savedBooks.get(i).getPublisher(), dummyBooks.get(i).getPublisher());
            assertEquals(savedBooks.get(i).getAuthors(), dummyBooks.get(i).getAuthors());
            assertEquals(savedBooks.get(i).getBookFormat(), dummyBooks.get(i).getBookFormat());
            assertEquals(savedBooks.get(i).getEdition(), dummyBooks.get(i).getEdition());
            assertEquals(savedBooks.get(i).getLanguage(), dummyBooks.get(i).getLanguage());
            assertEquals(savedBooks.get(i).getPublicationDate(), dummyBooks.get(i).getPublicationDate());
            assertEquals(savedBooks.get(i).getPrequel(), dummyBooks.get(i).getPrequel());
            assertEquals(savedBooks.get(i).getSequel(), dummyBooks.get(i).getSequel());
            assertEquals(savedBooks.get(i).getSubtitle(), dummyBooks.get(i).getSubtitle());
            assertEquals(savedBooks.get(i).getId(), dummyBooks.get(i).getId());
        }
    }

    @Test
    public void testBookAuthors() {
        // check if the authors are set correctly
        for (int i = 0; i < dummyBooks.size(); i++) {
            assertNotNull(bookRepository.findById(dummyBooks.get(i).getId()).get().getAuthors());
        }
    }

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
     * Creates a book with the same ISBN of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints on the properties that
     * constitute a natural-id
     */
    @Test
    public void testUniqueBookIdentifier() {
        Book duplicatedBook = bookFactory.createValidEntity();

        // set manually a new id in order to insert a new record and not for update an existing record
        duplicatedBook.setIsbn("978-00-00-00000-0");

        // save the book in the repository
        assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.saveAndFlush(duplicatedBook);
        });
    }

    /**
     * Throws an exception when attempting to create a book with illegal format type
     */
    @Test
    public void testIllegalBookFormat() {
        Book invalidBook = bookFactory.createValidEntity();

        assertThrows(IllegalArgumentException.class, () -> invalidBook.setBookFormat(Book.Format.valueOf("AudioBook")));
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateBook() {
        // get a Book from the repository
        Book savedBook = bookRepository.findById(dummyBooks.get(0).getId()).get();
        // change some attributes and update the Book object
        // the authors association isn't updated
        savedBook.setTitle("Nuovo Titolo");
        savedBook.setPublisher("Nuovo Editore");
        savedBook.setSubtitle("Nuovo Sottotitolo");
        savedBook.setBookFormat(Book.Format.DIGITAL);
        savedBook.setEdition(2);
        savedBook.setLanguage("Nuova Lingua");
        savedBook.setPublicationDate(savedBook.getPublicationDate().plusYears(1));


        Set<Author> newAuthors = new HashSet<>();
        newAuthors.add(author);

        savedBook.setAuthors(newAuthors);
        savedBook.setSubtitle("The Secret Of The Dreams");

        // update the Book object
        savedBook = bookRepository.save(savedBook);
        Book updatedBook = bookRepository.findById(savedBook.getId()).get();
        Set<Author> updatedBookAuthors = updatedBook.getAuthors();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedBook);
        assertNotNull(updatedBook.getAuthors());
        assertEquals(savedBook, updatedBook);
        assertEquals(newAuthors, updatedBook.getAuthors());
        assertEquals("The Secret Of The Dreams", updatedBook.getSubtitle());
        assertNotEquals(initialAuthors, updatedBookAuthors);
        var storedAuthors = authorRepository.findAll();
        assertNotNull(storedAuthors.containsAll(updatedBookAuthors));
    }

    /**
     * Update the author of an entry and check if the book have been updated correctly
     */
    @Test
    public void testUpdateBookAuthors() {
        // get a Book from the repository
        Book savedBook = bookRepository.findById(dummyBooks.get(0).getId()).get();
        Set<Author> savedBookAuthors = savedBook.getAuthors();

        // change author attributes
        for (Author a: savedBookAuthors) {
            a.setName("Tom");
            a.setSurname("Cook");
        }

        // update the book object
        savedBook.setAuthors(savedBookAuthors);
        savedBook = bookRepository.save(savedBook);

        Book updatedBook = bookRepository.findById(savedBook.getId()).get();
        Set<Author> updatedBookAuthors = updatedBook.getAuthors();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedBook);
        assertEquals(savedBook, updatedBook);
        assertEquals(savedBookAuthors, updatedBookAuthors);
        for (Author a: updatedBookAuthors) {
            assertEquals("Tom", a.getName());
            assertEquals("Cook", a.getSurname());
        }
        var storedAuthors = authorRepository.findAll();
        assertNotNull(storedAuthors.containsAll(updatedBookAuthors));
    }

    /**
     * Update one entry with prequel or sequel attribute and check if the fields are changed correctly
     */
    @Test
    public void testUpdateBookPrequel() {
        // get a Book from the repository
        Book sequelBook = bookRepository.findById(dummyBooks.get(1).getId()).get();
        Book initialPrequelBook = sequelBook.getPrequel();

        // change the book prequel and set null the sequel of the initial prequel
        Book newPrequelBook = bookFactory.createValidEntity();
        sequelBook.addPrequel(newPrequelBook);
        initialPrequelBook.addSequel(null);

        // update the book object
        newPrequelBook = bookRepository.save(newPrequelBook);
        sequelBook = bookRepository.save(sequelBook);

        Book updatedSequelBook = bookRepository.findById(sequelBook.getId()).get();
        Book updatedPrequelBook = updatedSequelBook.getPrequel();

        // check that all book exist
        assertNotNull(updatedSequelBook);
        assertNotNull(initialPrequelBook);
        assertNotNull(newPrequelBook);
        assertNotNull(updatedPrequelBook);

        // check that the sequel book attributes have been updated correctly and contain the expected value
        assertEquals(sequelBook, updatedSequelBook);
        assertEquals(newPrequelBook, updatedPrequelBook);
        assertNotEquals(initialPrequelBook, updatedPrequelBook);

        // check that the initial prequel book attributes have been updated correctly and contain the expected value
        assertNull(initialPrequelBook.getSequel());
        assertNotEquals(updatedSequelBook, initialPrequelBook.getSequel());

        // check that the new prequel book attributes have been updated correctly and contain the expected value
        assertNotNull(updatedPrequelBook.getSequel());
        assertEquals(updatedPrequelBook, newPrequelBook);
    }

    /**
     * Throws an exception when attempting to create a book without mandatory attributes
     */
    @Test
    public void testIllegalCreateBook() {
        Book invalidBook = new Book();

        assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.saveAndFlush(invalidBook);
        });
    }

    /**
     * Throws an exception when attempting to create or update a book with illegal size for the attributes
     */
    @Test
    public void testIllegalSizeAttributes() {
        Book invalidBook = new Book();

        invalidBook.setIsbn("9780714503615");
        invalidBook.setTitle("Marat/Sade");
        invalidBook.setPublisher("Marion Boyars Publishers Ltd");

        bookRepository.save(invalidBook);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidBook.setTitle("The Persecution and Assassination of Jean-Paul Marat as Performed by the Inmates of" +
                    " the Asylum of Charenton Under the Direction of the Marquis de Sade");
            bookRepository.saveAndFlush(invalidBook);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidBook.setPublisher("Marion Boyars Publishers Ltd; 5 Revised edition edizione (1 ottobre 1969)");
            bookRepository.saveAndFlush(invalidBook);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidBook.setSubtitle("This extraordinary play, which swept Europe before coming to America, is based " +
                    "on two historical truths: the infamous Marquis de Sade was confined in the lunatic asylum of " +
                    "Charenton, where he staged plays; and the revolutionary Jean-Paul Marat was stabbed in a " +
                    "bathtub by Charlotte Corday at the height of the Terror during the French Revolution. But this " +
                    "play-within-a-play is not historical drama. Its thought is as modern as today's police states " +
                    "and The Bomb; its theatrical impact has everywhere been called a major innovation.");
            bookRepository.saveAndFlush(invalidBook);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidBook.setLanguage("Indo-European Germanic West Germanic Anglo-Frisian Anglic English");
            bookRepository.saveAndFlush(invalidBook);
        });
    }

    /**
     * Delete an entry and check if the book was removed correctly
     */
    @Test
    public void testDeleteBook() {
        // get a Book from the repository
        Book savedBook = bookRepository.findById(dummyBooks.get(0).getId()).get();

        // delete the Book object
        bookRepository.delete(savedBook);

        // check that the book has been deleted correctly
        assertEquals(bookRepository.findById(dummyBooks.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        bookRepository.deleteAll();
        assertTrue(bookRepository.findAll().isEmpty());
    }

    /**
     * Delete an entry and check if the book was removed correctly
     */
    @Test
    public void testDeleteBookPrequel() {
        // get a Book from the repository
        Book bookPrequel = bookRepository.findById(dummyBooks.get(0).getId()).get();
        Book bookSequel = bookPrequel.getSequel();

        // delete the Book object
        bookRepository.delete(bookPrequel);

        Book bookSequelAfterDel = bookRepository.findById(bookSequel.getId()).get();

        // check that the book has been deleted correctly
        assertEquals(bookRepository.findById(dummyBooks.get(0).getId()), Optional.empty());
        // check if the prequel field is correctly update
        assertNotNull(bookSequelAfterDel);
        assertNull(bookSequelAfterDel.getPrequel());
        assertNotEquals(bookPrequel, bookSequelAfterDel.getPrequel());
    }

    /**
     * Delete an entry and check if the book was removed correctly
     */
    @Test
    public void testDeleteBookSequel() {
        // get a Book from the repository
        Book bookSequel = bookRepository.findById(dummyBooks.get(1).getId()).get();
        Book bookPrequel = bookSequel.getPrequel();

        // delete the Book object
        bookRepository.delete(bookSequel);

        Book bookPrequelAfterDel = bookRepository.findById(bookPrequel.getId()).get();

        // check that the book has been deleted correctly
        assertEquals(bookRepository.findById(dummyBooks.get(1).getId()), Optional.empty());
        // check if the sequel field is correctly update
        assertNotNull(bookPrequelAfterDel);
        assertNull(bookPrequelAfterDel.getSequel());
        assertNotEquals(bookSequel, bookPrequelAfterDel.getSequel());
    }

    /**
     * Throws an exception when attempting to delete an author if he has written a book.
     */
    @Test
    public void testDeleteBookAuthor() {
        // get a a pair book-authors from the repository
        Book book = bookRepository.findById(dummyBooks.get(1).getId()).get();
        Set<Author> authors = book.getAuthors();

        // delete all the authors object
        authorRepository.deleteAll();

        // throws an exception when attempting to delete an author of a book
        assertThrows(AssertionFailedError.class, () -> {
            assertTrue(authorRepository.findAll().isEmpty());
            assertNull(bookRepository.findById(book.getId()).get().getAuthors());
            assertNotEquals(authors, bookRepository.findById(book.getId()).get().getAuthors());
        }, "It's not possible to delete an author if he has written a book");
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

        // try to search for an book by a not existing id
        var notFoundBook = bookRepository.findById(999L);

        assertTrue(notFoundBook.isEmpty());
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
