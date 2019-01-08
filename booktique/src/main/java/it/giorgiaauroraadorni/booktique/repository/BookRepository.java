package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Author;
import it.giorgiaauroraadorni.booktique.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // implements search operations
    List<Book> findByTitle(String title);

    // the book found will be just one because the isbn is a natural id, therefore unique
    Book findByIsbn(String isbn);

    List<Book> findByAuthorsIn(Set<Author> authors);
}