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

    @BeforeEach
    void createDummyAuthor() {
        /*
        * Create a list of authors entities that will be use in the test
        */
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
        authorRepository.saveAll(dummyAuthors);
    }

    @BeforeEach
    void createDummyBook() {
        /*
        * Create a list of books entities that will be use in the test
        */
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
        authors.add(dummyAuthors.get(0));
        authors.add(dummyAuthors.get(1));
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
        bookRepository.saveAll(dummyBooks);
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedAuthor = authorRepository.findAll();
        var savedBook = bookRepository.findAll();

        // check if all the authors are correctly added to the repository
        assertTrue(savedAuthor.containsAll(dummyAuthors), "findAll should fetch all dummy authors");

        // check if all the books are correctly added to the repository
        assertTrue(savedBook.containsAll(dummyBooks), "findAll should fetch all dummy books");
    }

    @Test
    public void testCreateBook() {
        /*
         * Insert many entries in the repository and check if these are readable and the attributes are correct
         */

        List<Book> savedBooks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            // check if the books id is correctly automatic generated
            assertNotNull(dummyBooks.get(i).getId());

            // check if the books contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(dummyBooks.get(i).getCreatedAt());
            assertNotNull(dummyBooks.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            savedBooks.add(bookRepository.getOne(dummyBooks.get(i).getId()));

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
        }
    }

    @Test
    public void testBookAuthors() {
        // check if the authors are set correctly
        assertNotNull(dummyBooks.get(1).getAuthors());
        assertNotNull(dummyBooks.get(2).getAuthors());
        assertNull(dummyBooks.get(0).getAuthors());
        assertNull(dummyBooks.get(3).getAuthors());
    }

    @Test
    public void testBookPrequel() {
        // check if the books prequels are set correctly
        assertNotNull(dummyBooks.get(3).getPrequel());
        assertNull(dummyBooks.get(0).getPrequel());
        assertNull(dummyBooks.get(1).getPrequel());
        assertNull(dummyBooks.get(2).getPrequel());

        // check if the books sequels are set correctly
        assertNotNull(dummyBooks.get(2).getSequel());
        assertNull(dummyBooks.get(0).getSequel());
        assertNull(dummyBooks.get(1).getSequel());
        assertNull(dummyBooks.get(3).getSequel());
    }

    @Test
    public void testUniqueAuthorIdentifier() {
        /*
         * Creates an author with the same FiscalCode of another and throws an exception when attempting to insert data
         * by violating an integrity constraint, in particular, the unique constraints on the properties that
         * constitute a natural-id
         */

        Author duplicatedAuthor = new Author();
        // set manually a new id because when i try to insert a second record it results in update of existing record
        duplicatedAuthor.setId(9999l);
        duplicatedAuthor.setFiscalCode("ABCDEF12G24H567I");
        duplicatedAuthor.setName("John");
        duplicatedAuthor.setSurname("Cook");

        // add and save the author in the repository
        dummyAuthors.add(duplicatedAuthor);
        assertThrows(DataIntegrityViolationException.class, () -> {
            authorRepository.saveAll(dummyAuthors);
            authorRepository.flush();
        });
    }

    @Test
    public void testUniqueBookIdentifier() {
        /*
         * Creates a book with the same ISBN of another and throws an exception when attempting to insert data
         * by violating an integrity constraint, in particular, the unique constraints on the properties that
         * constitute a natural-id
         */

        Book duplicatedBook = new Book();
        // set manually a new id because when i try to insert a second record it results in update of existing record
        duplicatedBook.setId(9999l);
        duplicatedBook.setIsbn("978-84-08-04364-5");
        duplicatedBook.setTitle("Mountain Of Dreams");
        duplicatedBook.setPublisher("Adventure Publications");

        // save the book in the repository
        dummyBooks.add(duplicatedBook);
        assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.saveAll(dummyBooks);
            bookRepository.flush();
        });
    }

    @Test
    public void testIllegalBookFormat() {
        /*
         * Throws an exception when attempting to create a book with illegal format type
         */
        Book wrongBook = new Book();
        // set manually a new id because when i try to insert a second record it results in update of existing record
        wrongBook.setTitle("The Secret Of Book");
        wrongBook.setIsbn("9781234567897");
        wrongBook.setPublisher("GoldWrite Publishing");
        assertThrows(IllegalArgumentException.class, () -> wrongBook.setBookFormat(Book.Format.valueOf("AudioBook")));

        // save the book in the repository
        dummyBooks.add(wrongBook);
        bookRepository.saveAll(dummyBooks);
        bookRepository.flush();
    }

    @Test
    public void testUpdateBook() {
        /*
         * Update one entry partially, edit different attributes and check if the fields are changed correctly
         */
        // get a Book from the repository
        Book savedBook = bookRepository.findById(dummyBooks.get(0).getId()).get();
        // change author name
        dummyAuthors.get(0).setName("Tom");
        Set<Author> authors = new HashSet<>();
        authors.add(dummyAuthors.get(0));
        savedBook.setAuthors(authors);

        // update the Author object
        authorRepository.saveAll(dummyAuthors);

        // add a subtitle
        savedBook.setSubtitle("The Secret Of The Dreams");

        // update the Book object
        bookRepository.save(savedBook);

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(bookRepository.findById(savedBook.getId()));
        assertEquals(savedBook, bookRepository.findById(dummyBooks.get(0).getId()).get());
        assertEquals("Tom", authorRepository.findById(dummyAuthors.get(0).getId()).get().getName());
        assertEquals(authors, bookRepository.findById(savedBook.getId()).get().getAuthors());
        assertEquals("The Secret Of The Dreams", bookRepository.findById(savedBook.getId()).get().getSubtitle());
    }

}
