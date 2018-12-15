package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book extends AuditModel implements Serializable {
    public enum Format {
        digital,
        paperback,
        hardcover
    }

    @Id
    private Long id;

    // FIXME: check ISBN
    //@Id
    @NotBlank
    @Size(min = 13, max = 13)
    private String isbn;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3, max = 100)
    private String subtitle;

    // FIXME
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Author> authors;

    @NotBlank
    @Size(min = 3, max = 100)
    private String publisher;

    // FIXME
    private int edition;

    @Size(min = 3, max = 30)
    private String language;

    @Enumerated(EnumType.STRING)
    private Format bookFormat;

    // FIXME
    private LocalDate publicationDate;

    @OneToOne
    private Book prequel;

    @OneToOne(mappedBy = "prequel")
    private Book sequel;

    //Getters and Setters
/*
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}
            , fetch = FetchType.EAGER)
    @JoinTable(table = @Table(name = "book_author"),
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")})

    public Set<Author> getAuthors() {
        return authors;
    }
    */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Format getBookFormat() {
        return bookFormat;
    }

    public void setBookFormat(Format bookFormat) {
        this.bookFormat = bookFormat;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Book getPrequel() {
        return prequel;
    }

    public void setPrequel(Book prequel) {
        this.prequel = prequel;
    }

    public Book getSequel() {
        return sequel;
    }

    public void setSequel(Book sequel) {
        this.sequel = sequel;
    }
}
