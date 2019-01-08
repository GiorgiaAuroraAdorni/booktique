package it.giorgiaauroraadorni.booktique.model;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AuthorTestFactory implements EntityTestFactory<Author> {

    @Override
    public Author createValidEntity(int idx) {
        var author = new Author();

        // mandatory attribute (inherits from person)
        author.setFiscalCode("CGNNMO00T00L00" + idx + "S");
        author.setName("Nome" + idx);
        author.setSurname("Cognome" + idx);

        // other attributes
        author.setDateOfBirth(LocalDate.now().minusYears(35 + idx));
        author.setEmail(author.getName() + author.getSurname() + "@mail.com");
        author.setMobilePhone("333333333" + idx);
        author.setWebSiteURL("https://www." + author.getName() + author.getSurname() + ".org");
        author.setBiography(author.getName() + " is a friendly government politician and has a post-graduate degree " +
                "in philosophy, politics and economics. To lear more about " + author.getName() + " " +
                author.getSurname() + " by visit the website " + author.getWebSiteURL() + " !");

        return author;
    }

    @Override
    public void updateValidEntity(Author entity) {

    }
}
