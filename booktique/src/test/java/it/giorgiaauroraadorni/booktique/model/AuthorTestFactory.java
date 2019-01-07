package it.giorgiaauroraadorni.booktique.model;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthorTestFactory implements EntityTestFactory<Author> {

    @Override
    public Author createValidEntity(int idx) {
        var author = new Author();

        // creates authors with only the mandatory parameter (inherits from person)
        author.setFiscalCode("CGNNMO00T00L00" + idx + "S");
        author.setName("Nome " + idx);
        author.setSurname("Cognome " +idx);

        return author;
    }

    @Override
    public void updateValidEntity(Author entity) {

    }
}