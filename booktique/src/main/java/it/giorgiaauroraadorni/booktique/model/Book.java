package it.giorgiaauroraadorni.booktique.model;

import it.giorgiaauroraadorni.booktique.utility.EntityToDict;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book extends AuditModel implements Serializable, EntityToDict {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    // The ISBN is a sequence of 10/13 digits start with only 978.
    @NaturalId
    @Pattern(regexp = "^((978[\\--– ])?[0-9][0-9\\--– ]{10}[\\--– ][0-9xX])|((978)?[0-9]{9}[0-9Xx])$")
    private String isbn;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 100)
    private String subtitle;

    @ManyToMany(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Author> authors;

    @Column(length = 30, nullable = false)
    private String publisher;

    private Integer edition;

    @Column(length = 30)
    private String language;

    public enum Format {
        DIGITAL,
        PAPERBACK,
        HARDCOVER
    }

    @Enumerated(EnumType.STRING)
    private Format bookFormat;

    private LocalDate publicationDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Book prequel;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "prequel", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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

    private void setPrequel(Book prequel) {
        this.prequel = prequel;
    }

    public Book getSequel() {
        return sequel;
    }

    private void setSequel(Book sequel) {
        this.sequel = sequel;
    }

    /**
     * Set both sides of the association, adding a prequel to the book and setting the book as a sequel for the prequel
     * @param prequel
     */
    public void addPrequel(Book prequel) {
        if (prequel == null) {
            this.setPrequel(null);
        } else {
            this.setPrequel(prequel);
            prequel.setSequel(this);
        }
    }
    /**
     * Set both sides of the association, adding a sequel to the book and setting the book as a prequel for the sequel
     * @param sequel
     */
    public void addSequel(Book sequel) {
        if (sequel == null) {
            this.setSequel(null);
        } else {
            this.setSequel(sequel);
            sequel.setPrequel(this);
        }
    }

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributes(Object expectedObject) {
        Book book = (Book) expectedObject;
        return this.equalsByAttributesWithoutIdAndAssociations(expectedObject)
                && Objects.equals(getId(), book.getId());
    }

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributesWithoutIdAndAssociations(Object expectedObject) {
        if (this == expectedObject) return true;
        if (!(expectedObject instanceof Book)) return false;
        Book book = (Book) expectedObject;
        return Objects.equals(getIsbn(), book.getIsbn()) &&
                Objects.equals(getTitle(), book.getTitle()) &&
                getSubtitle().equals(book.getSubtitle()) &&
                Objects.equals(getPublisher(), book.getPublisher()) &&
                getEdition().equals(book.getEdition()) &&
                getLanguage().equals(book.getLanguage()) &&
                getBookFormat() == book.getBookFormat() &&
                getPublicationDate().isEqual(book.getPublicationDate());
        // ignore associations
    }

    /**
     * Called before every deletion to update the prequel and sequel attributes of the book that needs to be deleted.
     * The prequel sequel and the sequel prequel of the current book, which refer to the book that is being
     * deleted, are set to null.
     */
    @PreRemove
    public void preRemoveBook() {
        prequel = this.getPrequel();
        sequel = this.getSequel();
        if (sequel != null) {
            sequel.setPrequel(null);
        } else if (prequel != null) {
            prequel.setSequel(null);
        }
    }

    @Override
    public Map<String, Object> entityToDict(boolean optionalId) {
        Map<String, Object> dictionaryAttributes = new HashMap<>();

        var authorsToDict = this.getAuthors()
                .stream()
                .map((e) -> e.entityToDict(optionalId))
                .collect(Collectors.toSet());

        if (optionalId) {
            dictionaryAttributes.put("id", this.getId());
        }
        dictionaryAttributes.put("title", this.getTitle());
        dictionaryAttributes.put("isbn", this.getIsbn());
        dictionaryAttributes.put("authors", authorsToDict);
        if (this.getPrequel() != null) {
            dictionaryAttributes.put("prequel", this.getPrequel().entityToDict(optionalId));
        }
        if (this.getSequel() != null) {
            dictionaryAttributes.put("sequelId", this.getSequel().getId());
        }
        dictionaryAttributes.put("subtitle", this.getSubtitle());
        dictionaryAttributes.put("pubblicationDate", this.getPublicationDate());
        dictionaryAttributes.put("publisher", this.getPublisher());
        dictionaryAttributes.put("edition", this.getEdition());
        dictionaryAttributes.put("language", this.getLanguage());
        dictionaryAttributes.put("format", this.getBookFormat());

        return dictionaryAttributes;
    }
}
