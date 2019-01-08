package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    /* implements search operations */

    List<Author> findByName(String name);

    List<Author> findBySurname(String surname);

    // the author found will be just one (or nobody) because the fiscal code is a natural id, therefore unique
    Author findByFiscalCode(String fiscalCode);

    // the author found will be just one (or nobody) because the email is saved as unique
    Author findByEmail(String email);
}
