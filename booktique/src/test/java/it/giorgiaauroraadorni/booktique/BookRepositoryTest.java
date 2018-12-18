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
    private BookRepository repository;

    @Test
    void repositoryLoads() {}

    @Test
    public void testBookCreate() {
//        Author author = new Author();
//
//        author.setFiscalCode("CRL ZNR 64P25 A638G");
//        author.setName("Carlos");
//        author.setSurname("Ruiz Zafón");

        Book book = new Book();
        book.setIsbn("978-84-08-04364-5");
        book.setTitle("La sombra del viento");
        book.setPublisher("Editorial Planeta");

        repository.save(book);

        assertTrue(repository.findAll().contains(book));
    }

}