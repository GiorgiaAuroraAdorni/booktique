package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Employee;
import it.giorgiaauroraadorni.booktique.model.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmployeeRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the EmployeeRepository instance
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityFactory<Employee> employeeFactory;

    @Autowired
    private EntityFactory<Address> addressFactory;

    private List<Employee> dummyEmployees;

    @BeforeEach
    void createDummyEmployees() {
        // create a list of valid employees entities
        dummyEmployees = employeeFactory.createValidEntities(2);

        // add the dummyEmployees.get(0) as supervisor for all the employees
        for (Employee e: dummyEmployees) {
            e.setSupervisor(dummyEmployees.get(0));
        }

        // save the created entities in the employeeRepository and persist addresses
        dummyEmployees = employeeRepository.saveAll(dummyEmployees);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateEmployee() {
       for (int i = 0; i < dummyEmployees.size(); i++) {
            assertNotEquals(0, employeeRepository.count());
            assertTrue(employeeRepository.existsById(dummyEmployees.get(i).getId()));

            // check if the employees contain the createdAt and updatedAt annotation that are automatically populate,
            // and check if the employees id are correctly automatic generated
            assertNotNull(dummyEmployees.get(i).getCreatedAt());
            assertNotNull(dummyEmployees.get(i).getUpdatedAt());
            assertNotNull(dummyEmployees.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals("CGNNMO00T00L00" + i + "E", dummyEmployees.get(i).getFiscalCode());
            assertEquals("Nome" + i, dummyEmployees.get(i).getName());
            assertEquals("Cognome" + i, dummyEmployees.get(i).getSurname());
            assertEquals(LocalDate.now().minusYears(30 + i), dummyEmployees.get(i).getDateOfBirth());
            assertEquals("Nome" + i + "Cognome" + i + "@employee-mail.com", dummyEmployees.get(i).getEmail());
            assertEquals("333000000" + i, dummyEmployees.get(i).getMobilePhone());
            assertEquals("EUserNo" + i, dummyEmployees.get(i).getUsername());
            assertEquals("Qwerty1234", dummyEmployees.get(i).getPassword());
            assertEquals(dummyEmployees.get(0), dummyEmployees.get(i).getSupervisor());
            assertEquals(LocalDate.now().minusYears(5).plusMonths(i), dummyEmployees.get(i).getHireDate());
            assertTrue(dummyEmployees.get(i).getAddress().equalsByAttributesWithoutId(addressFactory.createValidEntity(i)));
        }
    }

    /**
     * Throws an exception when attempting to create an employee without mandatory attributes
     */
    @Test
    public void testIllegalCreateEmployee() {
        Employee invalidEmployee = new Employee();

        assertThrows(DataIntegrityViolationException.class, () -> {
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    @Test
    public void testSave() {
        Employee employee = employeeFactory.createValidEntity(2);
        employee.setSupervisor(dummyEmployees.get(0));

        assertDoesNotThrow(() -> employeeRepository.save(employee));
    }

    /**
     * Creates a customer with the same FiscalCode of another and throws an exception when attempting to insert data
     * by violating the unique constraints on the properties that constitute a natural-id.
     */
    @Test
    public void testUniqueFiscalCodeIdentifier() {
        var duplicatedEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedEmployee.setFiscalCode("CGNNMO00T00L000E");
            employeeRepository.saveAndFlush(duplicatedEmployee);
        });
    }

    /**
     * Creates an employee with the same username of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints.
     */
    @Test
    public void testUniqueEmployeeUsername() {
        var duplicatedEmployee = employeeFactory.createValidEntity(2);

        // save the employee in the repository
        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedEmployee.setUsername("EUserNo0");
            employeeRepository.saveAndFlush(duplicatedEmployee);
        });
    }

    /**
     * Creates an employee with the same email of another and throws an exception when attempting to insert data by
     * violating the unique constraints.
     */
    @Test
    public void testUniqueEmail() {
        var duplicatedEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedEmployee.setEmail("Nome0Cognome0@employee-mail.com");
            employeeRepository.saveAndFlush(duplicatedEmployee);
        });
    }

    /**
     * Creates an employee with the same mobile phone of another and throws an exception when attempting to insert data
     * by violating the unique constraints.
     */
    @Test
    public void testUniqueMobilePhone() {
        var duplicatedEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedEmployee.setMobilePhone("3330000000");
            employeeRepository.saveAndFlush(duplicatedEmployee);
        });
    }

    /**
     * Test the correct persistence of employee addresses.
     */
    @Test
    public void testEmployeeAddress() {
        for (Employee e: dummyEmployees) {
            assertTrue(addressRepository.existsById(e.getAddress().getId()));
        }
    }

    @Test
    public void testEmployeeSupervisor() {
        for (Employee e: dummyEmployees) {
            assertTrue(employeeRepository.existsById(e.getSupervisor().getId()));
        }
    }

    /**
     * Throws an exception when attempting to create an employee with illegal fiscal code.
     */
    @Test
    public void testIllegalFiscalCode() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidEmployee.setFiscalCode("ABCDEFGHIJKLMNOP");
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    /*
     * Throws an exception when attempting to create an employee with illegal email.
     */
    @Test
    public void testIllegalEmail() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidEmployee.setEmail("NomeCognome@mail@10.com");
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    /**
     * Throws an exception when attempting to create an employee with illegal mobile phone.
     */
    @Test
    public void testIllegalMobilePhone() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidEmployee.setMobilePhone("0039333123456");
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    /**
     * Throws an exception when attempting to create an employee with illegal date of birth.
     */
    @Test
    public void testIllegalDateOfBirth() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DateTimeException.class, () -> {
            invalidEmployee.setDateOfBirth(LocalDate.of(1980, 13, 32));
            employeeRepository.save(invalidEmployee);
        });
    }

    /**
     * Throws an exception when attempting to create or update a employee with illegal size for the username attribute.
     */
    @Test
    public void testIllegalUsernameSize() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidEmployee.setUsername("ChristieCarlsonClark15gennaio1983");
            employeeRepository.saveAndFlush(invalidEmployee);
        }, "Username cannot be longer than 32 characters");

        assertThrows(JpaSystemException.class, () -> {
            invalidEmployee.setUsername("User");
            employeeRepository.saveAndFlush(invalidEmployee);
        }, "Username must be at least 5 characters long");
    }

    /**
     * Throws an exception when attempting to create or update an employee  with illegal size for the password attribute.
     */
    @Test
    public void testIllegalPasswordSize() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidEmployee.setPassword("-X2LPM4r`2.SJn)nGxW3Dt}4$C+z??\"d7np=fHWDTB`y2ye:w2>\\5Kf,}\\Ks?*NBq7FG./Qp" +
                    "(>uxFtfs~U(A!tLHSGk>a5bhue^2wq#~3K9mc2[P(J:]c&hez(Jm&F?j2");
            employeeRepository.saveAndFlush(invalidEmployee);
        }, "Password cannot be longer than 128 characters");

        assertThrows(JpaSystemException.class, () -> {
            invalidEmployee.setPassword("Qwerty12");
            employeeRepository.saveAndFlush(invalidEmployee);
        }, "Password must be at least 8 characters long");
    }

    /**
     * Throws an exception when attempting to create or update an employee with illegal size for the name attribute.
     */
    @Test
    public void testIllegalNameSize() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidEmployee.setName("PrimoNomeSecondoNomeTerzoNomeQuartoNome");
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    /**
     * Throws an exception when attempting to create or update an employee with illegal size for the surname attribute.
     */
    @Test
    public void testIllegalSurnameSize() {
        var invalidEmployee = employeeFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidEmployee.setSurname("PrimoCognomeSecondoCognomeTerzoCognome");
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateEmployee() {
        // get a employees from the repository
        Employee savedEmployee = dummyEmployees.get(0);

        // change some attributes and update the employee object
        // the address association isn't updated
        // the supervisor association isn't updated
        savedEmployee.setName("Nuovo Nome");
        savedEmployee.setSurname("Nuovo Cognome");
        savedEmployee.setUsername("NuovoEUserNo");
        savedEmployee.setPassword("NuovaPassword");
        savedEmployee.setDateOfBirth(LocalDate.now().minusYears(35));
        savedEmployee.setEmail("NuovoNomeNuovoCognome@employee-mail.com");
        savedEmployee.setMobilePhone("3331111100");
        savedEmployee.setHireDate(LocalDate.now());

        savedEmployee = employeeRepository.save(savedEmployee);

        // clear the memory in order to get a new istance of the saved employee from the db
        employeeRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Employee updatedEmployee = employeeRepository.findById(savedEmployee.getId()).get();

        assertTrue(employeeRepository.existsById(updatedEmployee.getId()));
        assertEquals("Nuovo Nome", updatedEmployee.getName());
        assertEquals("Nuovo Cognome", updatedEmployee.getSurname());
        assertEquals("NuovoEUserNo", updatedEmployee.getUsername());
        assertEquals("NuovaPassword", updatedEmployee.getPassword());
        assertEquals(LocalDate.now().minusYears(35), updatedEmployee.getDateOfBirth());
        assertEquals("NuovoNomeNuovoCognome@employee-mail.com", updatedEmployee.getEmail());
        assertEquals("3331111100", updatedEmployee.getMobilePhone());
        assertEquals(LocalDate.now(), updatedEmployee.getHireDate());
    }

    /**
     * Throws an exception when attempting to update the immutable natural identifier fiscal code.
     */
    @Test
    public void testUpdateFiscalCode() {
        // get an employee from the repository, modify the fiscal code and update the employee object
        Employee savedEmployee = dummyEmployees.get(0);

        assertThrows(JpaSystemException.class, () -> {
            savedEmployee.setFiscalCode("CGNNMO00A00L000E");
            employeeRepository.saveAndFlush(savedEmployee);
        }, "It's not possible to updated an employee fiscal code!");
    }

    /**
     * Update the address of an entry and check if the fields are changed correctly and that the employee was updated.
     */
    @Test
    public void testUpdateAddress() {
        // get an employee from the repository, modify the address and update the employee object
        Employee savedEmployee = dummyEmployees.get(0);
        Address savedAddress = savedEmployee.getAddress();

        // modified some address attributes and update the istances
        savedAddress.setStreetAddress("Largo Nomelargo 100");
        savedAddress.setRegion("Nuova Regione");

        savedAddress = addressRepository.save(savedAddress);

        // clear the memory in order to get a new istance of the saved employee and address from the db
        addressRepository.flush();
        entityManager.clear();

        // get the updated employee and address from the repository
        Employee updatedEmployee = employeeRepository.findById(savedEmployee.getId()).get();
        Address updatedAddress = updatedEmployee.getAddress();

        // check that the employee and the address exist
        assertTrue(employeeRepository.existsById(updatedEmployee.getId()));
        assertTrue(addressRepository.existsById(updatedAddress.getId()));

        // check that the address attribute have been updated correctly
        assertTrue(updatedAddress.equalsByAttributes(savedAddress));
        assertEquals("Largo Nomelargo 100", updatedAddress.getStreetAddress());
        assertEquals("Nuova Regione", updatedAddress.getRegion());
    }

    /**
     * Update the supervisor of an entry and check if the fields are changed correctly and that the employee was updated.
     */
    @Test
    public void testUpdateSupervisor() {
        // get an employee from the repository, modify the supervisor and update the employee object
        Employee savedEmployee = dummyEmployees.get(0);
        Employee savedSupervisor = savedEmployee.getSupervisor();

        // modified some supervisor attributes and update the istances
        savedSupervisor.setUsername("SupervisorNo1");
        savedSupervisor.setName("Nome Supervisor");

        savedSupervisor = employeeRepository.save(savedSupervisor);

        // clear the memory in order to get a new istance of the saved employee and supervisor from the db
        addressRepository.flush();
        entityManager.clear();

        // get the updated employee and supervisor from the repository
        Employee updatedEmployee = employeeRepository.findById(savedEmployee.getId()).get();
        Employee updatesSupervisor = updatedEmployee.getSupervisor();

        // check that the employee and the supervisor exist
        assertTrue(employeeRepository.existsById(updatedEmployee.getId()));
        assertTrue(employeeRepository.existsById(updatesSupervisor.getId()));

        // check that the supervisor attribute have been updated correctly
        assertTrue(updatesSupervisor.equalsByAttributes(savedSupervisor));
        assertEquals("SupervisorNo1", updatesSupervisor.getUsername());
        assertEquals("Nome Supervisor", updatesSupervisor.getName());
    }

    /**
     * Delete an entry and check that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteEmployee() {
        // get a employee from the repository and delete it
        Employee savedEmployee = dummyEmployees.get(1);
        employeeRepository.delete(savedEmployee);

        // check that the employee has been deleted correctly
        assertFalse(employeeRepository.existsById(savedEmployee.getId()));
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllEmployees() {
        // in order to remove all the entities from the repository is necessary to set the self-association in such
        // a way that every employee is the supervisor of himself
        for (Employee e: dummyEmployees) {
            e.setSupervisor(e);
        }
        employeeRepository.deleteAll();
        assertTrue(employeeRepository.findAll().isEmpty());
    }

    /**
     * Throws an exception when attempting to delete a supervisor, since it isn't allowed to delete a supervisor if
     * he has submitted.
     * The elimination of a supervisor is allowed only if the subordinate employees are first re-allocated.
     */
    @Test
    public void testDeleteEmployeeSupervisor() {
        // get a a pair employee-supervisor from the repository and delete the supervisor object
        Employee employee = dummyEmployees.get(1);
        Employee supervisor = employee.getSupervisor();
        employeeRepository.delete(supervisor);

        // clear the memory in order to get a new istance of the saved employee and supervisor from the db
        addressRepository.flush();
        entityManager.clear();

        // throws an exception when attempting to delete a supervisor that has subordinate employees
        assertThrows(AssertionFailedError.class,
                () -> assertFalse(employeeRepository.existsById(supervisor.getId())),
                        "It's not possible to eliminate a supervisor if his subordinates have not been first relocated");

        // reallocate subordinate employees and update the employee object
        for (Employee e: dummyEmployees) {
            e.setSupervisor(employee);
        }
        employeeRepository.delete(supervisor);
        employeeRepository.save(employee);

        // clear the memory in order to get a new istance of the saved employee and supervisor from the db
        addressRepository.flush();
        entityManager.clear();

        // check that the supervisor has been deleted correctly and the subordinate updated
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).get();
        Employee updatedSupervisor = updatedEmployee.getSupervisor();

        assertTrue(employeeRepository.existsById(updatedEmployee.getId()));
        assertFalse(employeeRepository.existsById(supervisor.getId()));
        assertTrue(employeeRepository.existsById(updatedSupervisor.getId()));
    }

    /**
     * Delete the employee address and check if the employee was updated correctly.
     */
    @Test
    public void testDeleteEmployeeAddress() {
        // get a employee and his address from the repository
        Employee savedEmployee = employeeRepository.findById(dummyEmployees.get(0).getId()).get();
        Address employeeAddress = savedEmployee.getAddress();

        // delete the address object
        addressRepository.delete(employeeAddress);

        // check that the address has been deleted correctly
        assertEquals(addressRepository.findById(employeeAddress.getId()), Optional.empty());

        // throws an exception when attempting to access to an employee object whose address has been deleted
        assertThrows(AssertionFailedError.class, () -> {
            assertNull(employeeRepository.findById(savedEmployee.getId()).get().getAddress());
            assertNotEquals(employeeAddress, employeeRepository.findById(savedEmployee.getId()).get().getAddress());
        }, "It's not possible to eliminate an address if his employee haven't been first updated");

        // update the employee setting null the supplier address
        savedEmployee.setAddress(null);
        employeeRepository.save(savedEmployee);

        Employee employeeAfterAddressDel = employeeRepository.findById(savedEmployee.getId()).get();

        // check that the employee has been updated correctly
        assertNull(employeeAfterAddressDel.getAddress());
        assertNotEquals(employeeAddress, employeeAfterAddressDel.getAddress());
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedEmployees = employeeRepository.findAll();
        var savedAddresses = addressRepository.findAll();

        // check if all the employees are correctly added to the repository
        assertTrue(savedEmployees.containsAll(dummyEmployees), "findAll should fetch all dummy employees");
        assertFalse(savedAddresses.isEmpty());
        for (Employee e: dummyEmployees) {
            assertTrue(savedAddresses.contains(e.getAddress()), "findAll should fetch all dummy addresses");
        }
    }

    @Test
    public void testFindById() {
        // check the correct reading of the employee via findById
        var foundEmployee = employeeRepository.findById(dummyEmployees.get(0).getId());

        assertEquals(foundEmployee.get(), dummyEmployees.get(0));
        assertEquals(foundEmployee.get().getId(), dummyEmployees.get(0).getId());
    }

    @Test
    public void testFindByName() {
        // check the correct reading of all the employees via findByName
        var foundEmployees = employeeRepository.findByName(dummyEmployees.get(0).getName());

        assertTrue(foundEmployees.contains(dummyEmployees.get(0)));
        for (Employee a: foundEmployees) {
            assertEquals(a.getName(), dummyEmployees.get(0).getName());
        }

        // try to search for employees by a not existing name
        var notFoundEmployees = employeeRepository.findByName("Nome Inesistente");

        assertTrue(notFoundEmployees.isEmpty());
    }

    @Test
    public void testFindBySurname() {
        // check the correct reading of all the employees via findBySurname
        var foundEmployees = employeeRepository.findBySurname(dummyEmployees.get(0).getSurname());

        assertTrue(foundEmployees.contains(dummyEmployees.get(0)));
        for (Employee a: foundEmployees) {
            assertEquals(a.getSurname(), dummyEmployees.get(0).getSurname());
        }

        // try to search for employees by a not existing surname
        var notFoundEmployees = employeeRepository.findBySurname("Cognome Inesistente");

        assertTrue(notFoundEmployees.isEmpty());
    }

    @Test
    public void testFindByFiscalCode() {
        // check the correct reading of the employee via findByFiscalCode
        var foundEmployee = employeeRepository.findByFiscalCode(dummyEmployees.get(0).getFiscalCode());

        assertEquals(foundEmployee, dummyEmployees.get(0));
        assertEquals(foundEmployee.getFiscalCode(), dummyEmployees.get(0).getFiscalCode());

        // try to search for an employee by a not existing fiscal code
        var notFoundEmployee = employeeRepository.findByFiscalCode("AAAAAA00A00A000A");

        assertNull(notFoundEmployee);
    }

    @Test
    public void testFindByUsername() {
        // check the correct reading of the employee via findByUsername
        var foundEmployee = employeeRepository.findByUsername(dummyEmployees.get(0).getUsername());

        assertEquals(foundEmployee, dummyEmployees.get(0));
        assertEquals(foundEmployee.getUsername(), dummyEmployees.get(0).getUsername());

        // try to search for an employee by a not existing username
        var notFoundEmployee = employeeRepository.findByUsername("User Inesistente");

        assertNull(notFoundEmployee);
    }

    @Test
    public void testFindBySupervisor() {
        // check the correct reading of all the employees via findBySupervisor
        var foundEmployees = employeeRepository.findBySupervisor(dummyEmployees.get(1).getSupervisor());

        assertTrue(foundEmployees.contains(dummyEmployees.get(1)));
        for (Employee e: foundEmployees) {
            assertEquals(e.getSupervisor(), dummyEmployees.get(1).getSupervisor());
        }

        // try to search for employees by a not existing supervisor
        var notFoundEmployees = employeeRepository.findBySupervisor(dummyEmployees.get(1));

        assertTrue(notFoundEmployees.isEmpty());
    }
}
