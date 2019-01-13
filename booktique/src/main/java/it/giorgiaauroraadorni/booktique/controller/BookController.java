package it.giorgiaauroraadorni.booktique.controller;

import it.giorgiaauroraadorni.booktique.exception.ResourceNotFoundException;
import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/books")
    public Page<Book> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @PostMapping("/books")
    public Book createBook(@Valid @RequestBody Book book) {
        return bookRepository.save(book);
    }

    @PutMapping("/books/{bookId}")
    public Book updateBook(@PathVariable Long bookId, @Valid @RequestBody Book bookRequest) {
        return bookRepository.findById(bookId)
                .map(book -> {
                    book.setTitle(bookRequest.getTitle());
                    return bookRepository.save(book);
                }).orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        return bookRepository.findById(bookId)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));
    }
}
