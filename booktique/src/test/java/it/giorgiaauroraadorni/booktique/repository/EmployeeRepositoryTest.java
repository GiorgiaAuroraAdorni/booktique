package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Employee;
import it.giorgiaauroraadorni.booktique.model.EntityTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmployeeRepositoryTest {

    // Set automatically the attribute to the EmployeeRepository instance
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityTestFactory<Employee> employeeFactory;

    @Autowired
    private EntityTestFactory<Address> addressFactory;

    private List<Employee> dummyEmployees;

    @BeforeEach
    void createDummyEmployees() {
        // create a list of valid employees entities
        dummyEmployees = employeeFactory.createValidEntities(2);

        // add the same address and the same supervisor to all the employees
        for (Employee e: dummyEmployees) {
            e.setSupervisor(dummyEmployees.get(0));
        }

        // save the created entities in the employeeRepository and persist addresses
        dummyEmployees = employeeRepository.saveAll(dummyEmployees);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateEmployee() {
        List<Employee> savedEmployees = new ArrayList<>();

        for (int i = 0; i < dummyEmployees.size(); i++) {
            // check if the employees id are correctly automatic generated
            assertNotNull(employeeRepository.getOne(dummyEmployees.get(i).getId()));
            savedEmployees.add(employeeRepository.getOne(dummyEmployees.get(i).getId()));

            // check if the employees contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedEmployees.get(i).getCreatedAt());
            assertNotNull(savedEmployees.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedEmployees.get(i).getFiscalCode(), dummyEmployees.get(i).getFiscalCode());
            assertEquals(savedEmployees.get(i).getName(), dummyEmployees.get(i).getName());
            assertEquals(savedEmployees.get(i).getSurname(), dummyEmployees.get(i).getSurname());
            assertEquals(savedEmployees.get(i).getDateOfBirth(), dummyEmployees.get(i).getDateOfBirth());
            assertEquals(savedEmployees.get(i).getEmail(), dummyEmployees.get(i).getEmail());
            assertEquals(savedEmployees.get(i).getMobilePhone(), dummyEmployees.get(i).getMobilePhone());
            assertEquals(savedEmployees.get(i).getUsername(), dummyEmployees.get(i).getUsername());
            assertEquals(savedEmployees.get(i).getPassword(), dummyEmployees.get(i).getPassword());
            assertEquals(savedEmployees.get(i).getAddress(), dummyEmployees.get(i).getAddress());
            assertEquals(savedEmployees.get(i).getSupervisor(), dummyEmployees.get(i).getSupervisor());
            assertEquals(savedEmployees.get(i).getHireDate(), dummyEmployees.get(i).getHireDate());
            assertEquals(savedEmployees.get(i).getId(), dummyEmployees.get(i).getId());
        }
    }

    @Test
    public void testEmployeeAddress() {
        // check if the addresses are set correctly
        for (Employee e: dummyEmployees) {
            assertNotNull(employeeRepository.findById(e.getId()).get().getAddress());
        }
    }

    @Test
    public void testEmployeeSupervisor() {
        // check if the supervisors are set correctly
        for (int i = 0; i < dummyEmployees.size(); i++) {
            assertNotNull(employeeRepository.findById(dummyEmployees.get(0).getId()).get().getSupervisor());
        }
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateEmployee() {
        // get a employees from the repository
        Employee savedEmployee = employeeRepository.findById(dummyEmployees.get(0).getId()).get();

        // change some attributes
        Address newAddress = addressFactory.createValidEntity(1);
        savedEmployee.setAddress(newAddress);
        savedEmployee.setName("Terry");
        savedEmployee.setUsername("TerryMitchell83");
        savedEmployee.setPassword("W422g31C38rRtCtM");
        savedEmployee.setSupervisor(dummyEmployees.get(1));

        // update the employee object
        savedEmployee = employeeRepository.save(savedEmployee);
        Employee updatedEmployee = employeeRepository.findById(savedEmployee.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedEmployee);
        assertNotNull(updatedEmployee.getAddress());
        assertNotNull(updatedEmployee.getSupervisor());
        assertEquals(savedEmployee, updatedEmployee);
        assertEquals("Terry", updatedEmployee.getName());
        assertEquals("TerryMitchell83", updatedEmployee.getUsername());
        assertEquals("W422g31C38rRtCtM", updatedEmployee.getPassword());
        assertEquals(newAddress, updatedEmployee.getAddress());
        assertNotNull(addressRepository.getOne(newAddress.getId()));
        assertEquals(dummyEmployees.get(1), updatedEmployee.getSupervisor());
    }

    /**
     * Creates an employee with the same username of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints.
     */
    @Test
    public void testUniqueEmployeeUsernameIdentifier() {
        Employee duplicatedEmployee = employeeFactory.createValidEntity();

        // save the employee in the repository
        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedEmployee.setUsername("UserNo1");
            employeeRepository.saveAndFlush(duplicatedEmployee);
        });
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

    /**
     * Throws an exception when attempting to create or update a employee with illegal size for the username attribute
     */
    @Test
    public void testIllegalUsernameSize() {
        var invalidEmployee = employeeFactory.createValidEntity();

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
     * Throws an exception when attempting to create or update an employee  with illegal size for the password attribute
     */
    @Test
    public void testIllegalPasswordSize() {
        var invalidEmployee = employeeFactory.createValidEntity();

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
     * Delete an entry and check if the employee was removed correctly
     */
    @Test
    public void testDeleteEmployee() {
        // get a employee from the repository
        Employee savedEmployee = employeeRepository.findById(dummyEmployees.get(1).getId()).get();

        // delete the employee object
        employeeRepository.delete(savedEmployee);

        // check that the employee has been deleted correctly
        assertEquals(employeeRepository.findById(dummyEmployees.get(1).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        employeeRepository.deleteAll();
        employeeRepository.flush();
        assertTrue(employeeRepository.findAll().isEmpty());
    }

    /**
     * Delete the supervisor and check if the employee was updated correctly.
     * Throws an exception when attempting to access an employee object whose supervisor has been deleted, since it
     * isn't allowed to delete a supervisor if he has submitted.
     * The elimination of a supervisor is allowed only if the subordinates are first re-allocated.
     */
    @Test
    public void testDeleteEmployeeSupervisor() {
        // get a a pair employee-supervisor from the repository
        Employee employee = employeeRepository.findById(dummyEmployees.get(1).getId()).get();
        Employee supervisor = employee.getSupervisor();

        // delete the supervisor object
        employeeRepository.delete(supervisor);

        // check that the supervisor has been deleted correctly
        assertEquals(employeeRepository.findById(supervisor.getId()), Optional.empty());

        // throws an exception when attempting to access an employee object whose supervisor has been deleted
        assertThrows(AssertionFailedError.class, () -> {
                    assertNull(employeeRepository.findById(employee.getId()).get().getSupervisor());
                    assertNotEquals(supervisor, employeeRepository.findById(employee.getId()).get().getSupervisor());
                }, "It's not possible to eliminate a supervisor if his subordinates have not been first relocated");

        // reallocation of the subordinates
        employee.setSupervisor(null);
        employeeRepository.save(employee);

        Employee employeeAfterSupervisorDel = employeeRepository.findById(employee.getId()).get();

        // check if the supervisor field is correctly update
        assertNull(employeeAfterSupervisorDel.getSupervisor());
        assertNotEquals(supervisor, employeeAfterSupervisorDel.getSupervisor());
    }

    /**
     * Delete the employee address and check if the employee was updated correctly
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

        // try to search for an employee by a not existing id
        var notFoundEmployee = employeeRepository.findById(999L);

        assertTrue(notFoundEmployee.isEmpty());
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
