package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookRepositoryTest {
    // Set automatically the attribute to the BookRepository instance
    @Autowired
    private BookRepository bookRepository;

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

    }

}