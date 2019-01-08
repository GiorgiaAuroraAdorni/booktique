package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    /* implements search operations */

    List<Employee> findByName(String name);

    List<Employee> findBySurname(String surname);

    // the employee found will be just one because the fiscal code is a natural id, therefore unique
    Employee findByFiscalCode(String fiscalCode);

    // the employee found will be just one because the username is saved as unique
    Employee findByUsername(String username);

    List<Employee> findBySupervisor(Employee supervisor);
}
