package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // implements search operations
    List<Employee> findByName(String name);

    List<Employee> findBySurname(String surname);

    List<Employee> findByFiscalCode(String fiscalCode);

    List<Employee> findByUsername(String username);

    List<Employee> findBySupervisor(Employee supervisor);
}
