package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;

@Entity
@Table(name="author")
public class Author extends Person {

    private Person author;

    // Getters and Setters
    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }
}