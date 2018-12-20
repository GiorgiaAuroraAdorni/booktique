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
        assertTrue(bookRepository.findAll().contains(book));

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
        book.setPublicationDate(LocalDate.of(2008, 04, 17));

        // save the book in the repository
        bookRepository.save(book);

        // check if the book is added correctly to the repository
        assertTrue(bookRepository.findAll().contains(book));
        assertNotNull(book.getAuthors());
        assertTrue(book.getAuthors().contains(author));
        assertNotNull(author.getId());
    }

}