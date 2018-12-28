package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="authors")
public class Author extends Person {

    private Person author;

    @Size(min = 10, max = 280)
    private String biography;

    // The web site url matches with or without protocol.
    @Pattern(regexp = "^[http://www.|https://www.|www.][\\S]+$")
    private String webSiteURL;

    // Getters and Setters
    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getWebSiteURL() {
        return webSiteURL;
    }

    public void setWebSiteURL(String webSiteURL) {
        this.webSiteURL = webSiteURL;
    }
}