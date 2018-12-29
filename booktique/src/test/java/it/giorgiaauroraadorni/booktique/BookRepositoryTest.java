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
class BookRepositoryTest {
    // Set automatically the attribute to the BookRepository instance
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> dummyAuthors;
    private List<Book> dummyBooks;

    /**
     * Create a list of authors entities that will be use in the test
     */
    private void createDummyAuthor() {
        dummyAuthors = IntStream
                .range(0, 2)
                .mapToObj(i -> new Author())
                .collect(Collectors.toList());

        dummyAuthors.get(0).setFiscalCode("ABCDEF12G24H567I");
        dummyAuthors.get(0).setName("John");
        dummyAuthors.get(0).setSurname("Cook");

        dummyAuthors.get(1).setFiscalCode("LMNOPQ89R10S111T");
        dummyAuthors.get(1).setName("Nathalie");
        dummyAuthors.get(1).setSurname("Russel");

        // save the authors in the repository
        dummyAuthors = authorRepository.saveAll(dummyAuthors);
    }

    /**
     * Create a list of books entities that will be use in the test
     */
    private void createDummyBook() {
        dummyBooks = IntStream
                .range(0, 4)
                .mapToObj(i -> new Book())
                .collect(Collectors.toList());

        // Create a book with only the mandatory parameter
        dummyBooks.get(0).setIsbn("978-84-08-04364-5");
        dummyBooks.get(0).setTitle("Mountain Of Dreams");
        dummyBooks.get(0).setPublisher("Adventure Publications");

        // Create a book with two authors
        Set<Author> authors = new HashSet<>();
        authors.add(authorRepository.getOne(dummyAuthors.get(0).getId()));
        authors.add(authorRepository.getOne(dummyAuthors.get(1).getId()));
        dummyBooks.get(1).setIsbn("9788408081180");
        dummyBooks.get(1).setTitle("Witches And Rebels");
        dummyBooks.get(1).setPublisher("Lincoln Publishing");
        dummyBooks.get(1).setAuthors(authors);

        // Create a book with many attributes
        dummyBooks.get(2).setIsbn("88-7782-702-5");
        dummyBooks.get(2).setTitle("Young In The West");
        dummyBooks.get(2).setSubtitle("The A - Z Guide");
        dummyBooks.get(2).setPublisher("Lyon Publishing");
        dummyBooks.get(2).setAuthors(authors);
        dummyBooks.get(2).setBookFormat(Book.Format.hardcover);
        dummyBooks.get(2).setEdition(1);
        dummyBooks.get(2).setLanguage("english");
        dummyBooks.get(2).setPublicationDate(LocalDate.of(1999, 1, 1));

        // Create a book with a prequel
        dummyBooks.get(3).setIsbn("88-7782-702-6");
        dummyBooks.get(3).setTitle("After Young In The West");
        dummyBooks.get(3).setPublisher("Lyon Publishing");
        dummyBooks.get(3).setBookFormat(Book.Format.hardcover);
        dummyBooks.get(3).setEdition(1);
        dummyBooks.get(3).setLanguage("english");
        dummyBooks.get(3).setPublicationDate(LocalDate.of(2000, 1, 1));
        dummyBooks.get(3).addPrequel(dummyBooks.get(2));

        // save the books in the repository
        dummyBooks = bookRepository.saveAll(dummyBooks);
    }

    @BeforeEach
    void createDummyEntities() {
        createDummyAuthor();
        createDummyBook();
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedAuthors = authorRepository.findAll();
        var savedBooks = bookRepository.findAll();

        // check if all the authors are correctly added to the repository
        assertTrue(savedAuthors.containsAll(dummyAuthors), "findAll should fetch all dummy authors");

        // check if all the books are correctly added to the repository
        assertTrue(savedBooks.containsAll(dummyBooks), "findAll should fetch all dummy books");
    }

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
        assertNull(bookRepository.findById(dummyBooks.get(0).getId()).get().getAuthors());
        assertNotNull(bookRepository.findById(dummyBooks.get(1).getId()).get().getAuthors());
        assertNotNull(bookRepository.findById(dummyBooks.get(2).getId()).get().getAuthors());
        assertNull(bookRepository.findById(dummyBooks.get(3).getId()).get().getAuthors());
    }

    @Test
    public void testBookPrequel() {
        // check if the books prequels are set correctly
        assertNull(bookRepository.findById(dummyBooks.get(0).getId()).get().getPrequel());
        assertNull(bookRepository.findById(dummyBooks.get(1).getId()).get().getPrequel());
        assertNull(bookRepository.findById(dummyBooks.get(2).getId()).get().getPrequel());
        assertNotNull(bookRepository.findById(dummyBooks.get(3).getId()).get().getPrequel());
        assertEquals(bookRepository.getOne(dummyBooks.get(2).getId()),
                bookRepository.getOne(dummyBooks.get(3).getId()).getPrequel());

        // check if the books sequels are set correctly
        assertNull(bookRepository.findById(dummyBooks.get(0).getId()).get().getSequel());
        assertNull(bookRepository.findById(dummyBooks.get(1).getId()).get().getSequel());
        assertNotNull(bookRepository.findById(dummyBooks.get(2).getId()).get().getSequel());
        assertNull(bookRepository.findById(dummyBooks.get(3).getId()).get().getSequel());
        assertEquals(bookRepository.getOne(dummyBooks.get(3).getId()),
                bookRepository.getOne(dummyBooks.get(2).getId()).getSequel());
    }

    /**
     * Creates a book with the same ISBN of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints on the properties that
     * constitute a natural-id
     */
    @Test
    public void testUniqueBookIdentifier() {
        Book duplicatedBook = new Book();

        // set manually a new id in order to insert a new record and not for update an existing record
        duplicatedBook.setId(9999l);
        duplicatedBook.setIsbn("978-84-08-04364-5");
        duplicatedBook.setTitle("Mountain Of Dreams");
        duplicatedBook.setPublisher("Adventure Publications");

        // save the book in the repository
        assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.save(duplicatedBook);
            bookRepository.flush();
        });
    }

    /**
     * Throws an exception when attempting to create a book with illegal format type
     */
    @Test
    public void testIllegalBookFormat() {
        Book wrongBook = new Book();

        // set manually a new id in order to insert a new record and not for update an existing record
        wrongBook.setTitle("The Secret Of Book");
        wrongBook.setIsbn("9781234567897");
        wrongBook.setPublisher("GoldWrite Publishing");
        assertThrows(IllegalArgumentException.class, () -> wrongBook.setBookFormat(Book.Format.valueOf("AudioBook")));

        // save the book in the repository
        bookRepository.save(wrongBook);
        bookRepository.flush();
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateBook() {
        // get a Book from the repository
        Book savedBook = bookRepository.findById(dummyBooks.get(0).getId()).get();

        // change author name
        Author author = authorRepository.getOne(dummyAuthors.get(0).getId());
        author.setName("Tom");

        // update the Author object
        authorRepository.save(author);

        // add author to book
        Set<Author> authors = new HashSet<>();
        savedBook.setAuthors(authors);

        // add a subtitle
        savedBook.setSubtitle("The Secret Of The Dreams");

        // update the Book object
        bookRepository.save(savedBook);
        Book updatedBook = bookRepository.findById(savedBook.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedBook);
        assertEquals(savedBook, updatedBook);
        assertEquals(authors, updatedBook.getAuthors());
        for (Author a: updatedBook.getAuthors()) {
            assertEquals("Tom", a.getName());
        }
        assertEquals("The Secret Of The Dreams", updatedBook.getSubtitle());
    }

    /**
     * Update one entry with prequel or sequel attribute and check if the fields are changed correctly
     */
    @Test
    public void testUpdateBookPrequel() {
        // get a Book from the repository
        Book sequelBook = bookRepository.findById(dummyBooks.get(3).getId()).get();
        Book initialPrequelBook = sequelBook.getPrequel();

        // change the book prequel and set null the sequel of the initial prequel
        sequelBook.addPrequel(bookRepository.findById(dummyBooks.get(1).getId()).get());
        initialPrequelBook.addSequel(null);

        // update the book object
        bookRepository.save(sequelBook);

        Book updatedSequelBook = bookRepository.findById(sequelBook.getId()).get();
        Book newPrequelBook = sequelBook.getPrequel();

        // check that all book exist
        assertNotNull(updatedSequelBook);
        assertNotNull(initialPrequelBook);
        assertNotNull(newPrequelBook);

        // check that the sequel book attributes have been updated correctly and contain the expected value
        assertNull(updatedSequelBook.getSequel());
        assertEquals(sequelBook, updatedSequelBook);
        assertEquals(newPrequelBook, updatedSequelBook.getPrequel());
        assertNotEquals(initialPrequelBook, updatedSequelBook.getPrequel());

        // check that the initial prequel book attributes have been updated correctly and contain the expected value
        assertNull(initialPrequelBook.getSequel());
        assertNotEquals(updatedSequelBook, initialPrequelBook.getSequel());

        // check that the new prequel book attributes have been updated correctly and contain the expected value
        assertEquals(updatedSequelBook, newPrequelBook.getSequel());
    }

    /**
     * Update one entry deleting the author attribute and check if the fields are changed correctly
     */
    @Test
    public void testUpdateBookAuthor() {
        // FIXME: WIP: dont work
    }

    /**
     * Throws an exception when attempting to create a book without mandatory attributes
     */
    @Test
    public void testIllegalCreateBook() {
        Book wrongbook = new Book();

        assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.save(wrongbook);
            bookRepository.flush();
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
        Book bookPrequel = bookRepository.findById(dummyBooks.get(2).getId()).get();
        Book bookSequel = bookPrequel.getSequel();

        // delete the Book object
        bookRepository.delete(bookPrequel);
        // update the book prequel
        bookSequel.addPrequel(null);
        bookRepository.save(bookSequel);

        Book bookSequelAfterDel = bookRepository.findById(bookSequel.getId()).get();

        // check that the book has been deleted correctly
        assertEquals(bookRepository.findById(dummyBooks.get(2).getId()), Optional.empty());
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
        Book bookSequel = bookRepository.findById(dummyBooks.get(3).getId()).get();
        Book bookPrequel = bookSequel.getPrequel();

        // delete the Book object
        bookRepository.delete(bookSequel);
        // update the book sequel
        bookPrequel.addSequel(null);
        bookRepository.save(bookPrequel);

        Book bookPrequelAfterDel = bookRepository.findById(bookPrequel.getId()).get();

        // check that the book has been deleted correctly
        assertEquals(bookRepository.findById(dummyBooks.get(3).getId()), Optional.empty());
        // check if the sequel field is correctly update
        assertNotNull(bookPrequelAfterDel);
        assertNull(bookPrequelAfterDel.getSequel());
        assertNotEquals(bookSequel, bookPrequelAfterDel.getSequel());
    }

    /* Allow update or insert of null attributes */

}
