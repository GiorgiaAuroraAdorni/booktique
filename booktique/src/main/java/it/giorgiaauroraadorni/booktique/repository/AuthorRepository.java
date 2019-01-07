package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByName(String name);

    List<Author> findBySurname(String surname);

    List<Author> findByFiscalCode(String fiscalCode);

    List<Author> findByEmail(String fiscalCode);
}
