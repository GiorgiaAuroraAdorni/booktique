package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.repository.AuthorRepository;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookRepositoryTest {
    // Set automatically the attribute to the BookRepository instance
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void repositoryLoads() {}

    @Test
    public void testSimpleBookCreate() {
        // test only the mandatory parameter
        Book book = new Book();
        book.setIsbn("978-84-08-04364-5");
        book.setTitle("La sombra del viento");
        book.setPublisher("Editorial Planeta");

        // save the book in the repository
        bookRepository.save(book);

        // check if the book is added correctly to the repository
        List<Book> savedBook = bookRepository.findAll();
        assertTrue(savedBook.contains(book));

        // check if the book id is correctly automatic generated
        assertNotNull(book.getId());

        // check if book contains also the annotation that will automatically populate createdAt and updatedAt
        assertNotNull(book.getCreatedAt());
        assertNotNull(book.getUpdatedAt());
    }

    @Test
    public void testCompleteBookCreate() {
        // create an author
        Author author = new Author();
        Set<Author> authors = new HashSet<Author>();
        authors.add(author);

        author.setFiscalCode("CRLZNR64P25A638G");
        author.setName("Carlos");
        author.setSurname("Ruiz Zafón");
        author.setWebSiteURL("http://www.carlosruizzafon.com");

        // save the author in the repository
        authorRepository.save(author);

        // add also the author to the book attributes
        Book book = new Book();
        book.setIsbn("9788408081180");
        book.setTitle("El juego del ángel");
        book.setPublisher("Editorial Planeta");
        book.setAuthors(authors);
        book.setBookFormat(Book.Format.hardcover);
        book.setEdition(1);
        book.setLanguage("español");
        book.setPublicationDate(LocalDate.of(2008, 4, 17));

        // save the book in the repository
        bookRepository.save(book);

        // check if the book is added correctly to the repository
        List<Book> savedBook = bookRepository.findAll();
        assertTrue(savedBook.contains(book));
        assertNotNull(book.getAuthors());
        assertTrue(book.getAuthors().contains(author));
        assertNotNull(author.getId());
    }

    @Test
    public void testPrequel() {
        // create the prequel book
        Book book1 = new Book();
        book1.setIsbn("88-7782-702-5");
        book1.setTitle("Harry Potter e la pietra filosofale");
        book1.setPublisher("Salani Editore");
        book1.setPublicationDate(LocalDate.of(1998, 5, 1));

        // create the sequel book
        Book book2 = new Book();
        book2.setIsbn("88-7782-703-3");
        book2.setTitle("Harry Potter e la camera dei segreti");
        book2.setPublisher("Salani Editore");
        book2.setPublicationDate(LocalDate.of(1999, 1, 1));
        // set the book prequel
        book2.setPrequel(book1);

        // save the books in the repository
        bookRepository.save(book1);
        bookRepository.save(book2);

        // check if the books are added correctly to the repository
        List<Book> savedBook = bookRepository.findAll();
        assertTrue(savedBook.contains(book1));
        assertTrue(savedBook.contains(book2));

        // check if prequel and sequel are correctly setted
        assertNotNull(book1.getSequel());
        assertNotNull(book2.getPrequel());
    }
}