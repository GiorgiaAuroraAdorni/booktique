package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookTestFactory implements EntityTestFactory<Book> {
    @Autowired
    private EntityTestFactory<Author> authorFactory;

    @Override
    public Book createValidEntity(int idx) {
        var book = new Book();
        //var author = authorFactory.createValidEntity();

        // mandatory attribute
        book.setIsbn("978-00-00-00000-" + idx);
        book.setTitle("Titolo");
        book.setPublisher("Editore");

        // other attributes
//        Set<Author> authors = new HashSet<>();
//        authors.add(author);
//        book.setAuthors(authors);
        book.setBookFormat(Book.Format.hardcover);
        book.setEdition(1);
        book.setLanguage("Lingua");
        book.setPublicationDate(LocalDate.of(1999, 1, 1));

        // the bidirectional self-association between prequel and sequel isn't created, so the attribute is initially null
        // the association with the authors isn't created, so the attribute is initially null

        return book;
    }

    @Override
    public void updateValidEntity(Book entity) {

    }
}
