package it.giorgiaauroraadorni.booktique.model;

import it.giorgiaauroraadorni.booktique.utility.EntityToDict;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "authors")
public class Author extends Person implements EntityToDict {

    @Column(length = 280)
    private String biography;

    // The web site url matches with or without protocol.
    @Pattern(regexp = "^[http://www.|https://www.|www.][\\S]+$")
    private String webSiteURL;

    // Getters and Setters
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

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributes(Object expectedObject, boolean optionalId) {
        if (this == expectedObject) return true;
        if (!(expectedObject instanceof Author)) return false;
        if (optionalId) {
            if(!(super.equalsByAttributes(expectedObject))) return false;
        } else {
            if(!(super.equalsByAttributesWithoutId(expectedObject))) return false;
        }
        Author author = (Author) expectedObject;
        return getBiography().equals(author.getBiography()) &&
                getWebSiteURL().equals(author.getWebSiteURL());
    }

    @Override
    public Map<String, Object> entityToDict(boolean optionalId) {
        Map<String, Object> dictionaryAttributes = new HashMap<>();

        if (optionalId) {
            dictionaryAttributes.put("id", this.getId());
        }
        dictionaryAttributes.put("fiscalCode", this.getFiscalCode());
        dictionaryAttributes.put("name", this.getName());
        dictionaryAttributes.put("surname", this.getSurname());
        dictionaryAttributes.put("dateOfBirth", this.getDateOfBirth().toString());
        dictionaryAttributes.put("email", this.getEmail());
        dictionaryAttributes.put("mobilePhone", this.getMobilePhone());
        dictionaryAttributes.put("webSiteURL", this.getWebSiteURL());
        dictionaryAttributes.put("biography", this.getBiography());

        return dictionaryAttributes;
    }
}
