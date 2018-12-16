package it.giorgiaauroraadorni.booktique.model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
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
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    // The ISBN is a sequence of 10/13 digits start with only 978.
    @NaturalId
    @Pattern(regexp = "/((978[\\--– ])?[0-9][0-9\\--– ]{10}[\\--– ][0-9xX])|((978)?[0-9]{9}[0-9Xx])/")
    private String isbn;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3, max = 100)
    private String subtitle;

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<Author> authors;

    @NotBlank
    @Size(min = 3, max = 100)
    private String publisher;

    private Integer edition;

    @Size(min = 3, max = 30)
    private String language;

    @Enumerated(EnumType.STRING)
    private Format bookFormat;

    private LocalDate publicationDate;

    @OneToOne
    private Book prequel;

    @OneToOne(mappedBy = "prequel")
    private Book sequel;

    //Getters and Setters
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

    public Integer getEdition() {
        return edition;
    }

    public void setEdition(Integer edition) {
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
