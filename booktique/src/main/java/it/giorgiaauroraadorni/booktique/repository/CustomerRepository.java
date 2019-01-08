package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /* implements search operations */

    List<Customer> findByName(String name);

    List<Customer> findBySurname(String surname);

    // the customer found will be just one because the fiscal code is a natural id, therefore unique
    Customer findByFiscalCode(String fiscalCode);

    // the customer found will be just one because the username is saved as unique
    Customer findByUsername(String username);
}
