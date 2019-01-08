package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // implements search operations
    List<Customer> findByName(String name);

    List<Customer> findBySurname(String surname);

    List<Customer> findByFiscalCode(String fiscalCode);

    List<Customer> findByUsername(String username);
}
