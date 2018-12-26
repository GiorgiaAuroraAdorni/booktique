package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.repository.AuthorRepository;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        /* Create a list of authors entities that will be use in the test */
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
        /* Create a list of books entities that will be use in the test */
        dummyBooks = IntStream
                .range(0, 4)
                .mapToObj(i -> new Book())
                .collect(Collectors.toList());

        // Create a book with only the mandatory parameter
        dummyBooks.get(0).setIsbn("978-84-08-04364-5");
        dummyBooks.get(0).setTitle("Mountain Of Dreams");
        dummyBooks.get(0).setPublisher("Adventure Publications");

        // Create a book with two authors
        Set<Author> authors = new HashSet<Author>();
        authors.add(dummyAuthors.get(0));
        authors.add(dummyAuthors.get(1));
        dummyBooks.get(1).setIsbn("9788408081180");
        dummyBooks.get(1).setTitle("Witches And Rebels");
        dummyBooks.get(1).setPublisher("Lincoln Publishing");
        dummyBooks.get(1).setAuthors(authors);

        // Create a book with many attributes
        dummyBooks.get(2).setIsbn("88-7782-702-5");
        dummyBooks.get(2).setTitle("Young In The West");
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
        for (int i = 0; i < 4; i++) {
            // check if the books id is correctly automatic generated
            assertNotNull(dummyBooks.get(i).getId());

            // check if books contain also the annotation that will automatically populate createdAt and updatedAt
            assertNotNull(dummyBooks.get(i).getCreatedAt());
            assertNotNull(dummyBooks.get(i).getUpdatedAt());
        }
    }

    @Test
    public void testBookAuthors() {
        // check if
        assertNotNull(dummyBooks.get(1).getAuthors());
        assertNotNull(dummyBooks.get(2).getAuthors());
        assertNull(dummyBooks.get(0).getAuthors());
        assertNull(dummyBooks.get(3).getAuthors());
    }

    @Test
    public void testBookPrequel() {
        // check if books prequels are correctly setted
        assertNotNull(dummyBooks.get(3).getPrequel());
        assertNull(dummyBooks.get(0).getPrequel());
        assertNull(dummyBooks.get(1).getPrequel());
        assertNull(dummyBooks.get(2).getPrequel());

        // check if books sequels are correctly setted
        assertNotNull(dummyBooks.get(2).getSequel());
        assertNull(dummyBooks.get(0).getSequel());
        assertNull(dummyBooks.get(1).getSequel());
        assertNull(dummyBooks.get(3).getSequel());
    }
}