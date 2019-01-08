package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Customer;
import it.giorgiaauroraadorni.booktique.model.Employee;
import it.giorgiaauroraadorni.booktique.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    /* implements search operations */

    List<Purchase> findByOrderDate(LocalDate orderDate);

    List<Purchase> findByCustomer(Customer customer);

    List<Purchase> findByEmployee(Employee employee);
}
