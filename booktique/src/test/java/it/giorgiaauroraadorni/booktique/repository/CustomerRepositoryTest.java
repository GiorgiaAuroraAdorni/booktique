package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Customer;
import it.giorgiaauroraadorni.booktique.model.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerRepositoryTest {
    // Set automatically the attribute to the customerRepository instance
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityFactory<Customer> customerFactory;

    @Autowired
    private EntityFactory<Address> addressFactory;

    private List<Customer> dummyCustomers;

    @BeforeEach
    void createDummyCustomers() {
        // create a list of valid customers entities
        dummyCustomers = customerFactory.createValidEntities(2);

        // save the created entities in the customerRepository and persist addresses
        dummyCustomers = customerRepository.saveAll(dummyCustomers);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateCustomer() {
        List<Customer> savedCustomers = new ArrayList<>();

        for (int i = 0; i < dummyCustomers.size(); i++) {
            // check if the customers id are correctly automatic generated
            assertNotNull(customerRepository.getOne(dummyCustomers.get(i).getId()));
            savedCustomers.add(customerRepository.getOne(dummyCustomers.get(i).getId()));

            // check if the customers contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedCustomers.get(i).getCreatedAt());
            assertNotNull(savedCustomers.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedCustomers.get(i).getFiscalCode(), dummyCustomers.get(i).getFiscalCode());
            assertEquals(savedCustomers.get(i).getName(), dummyCustomers.get(i).getName());
            assertEquals(savedCustomers.get(i).getSurname(), dummyCustomers.get(i).getSurname());
            assertEquals(savedCustomers.get(i).getDateOfBirth(), dummyCustomers.get(i).getDateOfBirth());
            assertEquals(savedCustomers.get(i).getEmail(), dummyCustomers.get(i).getEmail());
            assertEquals(savedCustomers.get(i).getMobilePhone(), dummyCustomers.get(i).getMobilePhone());
            assertEquals(savedCustomers.get(i).getUsername(), dummyCustomers.get(i).getUsername());
            assertEquals(savedCustomers.get(i).getPassword(), dummyCustomers.get(i).getPassword());
            assertEquals(savedCustomers.get(i).getAddress(), dummyCustomers.get(i).getAddress());
            assertEquals(savedCustomers.get(i).getVatNumber(), dummyCustomers.get(i).getVatNumber());
            assertEquals(savedCustomers.get(i).getId(), dummyCustomers.get(i).getId());
        }
    }

    @Test
    public void testCustomerAddress() {
        // check if the addresses are set correctly
        for (int i = 0; i < dummyCustomers.size(); i++) {
            assertNotNull(customerRepository.findById(dummyCustomers.get(i).getId()).get().getAddress());
        }
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateCustomer() {
        // get a customer from the repository
        Customer savedCustomer = customerRepository.findById(dummyCustomers.get(0).getId()).get();

        var address = savedCustomer.getAddress();
        // change some attributes
        Address newAddress = addressFactory.createValidEntity(1);
        savedCustomer.setAddress(newAddress);
        savedCustomer.setName("Terry");
        savedCustomer.setUsername("TerryMitchell83");
        savedCustomer.setPassword("W422g31C38rRtCtM");

        // update the Customer object
        savedCustomer = customerRepository.save(savedCustomer);
        Customer updatedCustomer = customerRepository.findById(savedCustomer.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedCustomer);
        assertNotNull(updatedCustomer.getAddress());
        assertEquals(savedCustomer, updatedCustomer);
        assertEquals("Terry", updatedCustomer.getName());
        assertEquals("TerryMitchell83", updatedCustomer.getUsername());
        assertEquals("W422g31C38rRtCtM", updatedCustomer.getPassword());
        assertEquals(newAddress, updatedCustomer.getAddress());
        assertNotEquals(address, updatedCustomer.getAddress());
        assertTrue(addressRepository.findAll().contains(updatedCustomer.getAddress()));
    }

    /**
     * Creates a customer with the same username of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints.
     */
    @Test
    public void testUniqueCustomerUsernameIdentifier() {
        Customer savedCustomer = customerRepository.findById(dummyCustomers.get(0).getId()).get();
        var duplicatedUser = customerFactory.createValidEntity();

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedUser.setUsername(savedCustomer.getUsername());
            customerRepository.saveAndFlush(duplicatedUser);
        });
    }

    /**
     * Throws an exception when attempting to create a customer without mandatory attributes
     */
    @Test
    public void testIllegalCreateCustomer() {
        Customer invalidCustomer = new Customer();

        assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.saveAndFlush(invalidCustomer);
        });
    }

    /**
     * Throws an exception when attempting to create or update a customer with illegal size for the username attribute
     */
    @Test
    public void testIllegalUsernameSize() {
        var invalidCustomer = customerFactory.createValidEntity();

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidCustomer.setUsername("ChristieCarlsonClark15gennaio1983");
            customerRepository.saveAndFlush(invalidCustomer);
        }, "Username cannot be longer than 32 characters");

        assertThrows(JpaSystemException.class, () -> {
            invalidCustomer.setUsername("User");
            customerRepository.saveAndFlush(invalidCustomer);
        }, "Username must be at least 5 characters long");
    }

    /**
     * Throws an exception when attempting to create or update a customer with illegal size for the password attribute
     */
    @Test
    public void testIllegalPasswordSize() {
        var invalidCustomer = customerFactory.createValidEntity();

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidCustomer.setPassword("-X2LPM4r`2.SJn)nGxW3Dt}4$C+z??\"d7np=fHWDTB`y2ye:w2>\\5Kf,}\\Ks?*NBq7FG./Qp" +
                    "(>uxFtfs~U(A!tLHSGk>a5bhue^2wq#~3K9mc2[P(J:]c&hez(Jm&F?j2");
            customerRepository.saveAndFlush(invalidCustomer);
        }, "Password cannot be longer than 128 characters");

        assertThrows(JpaSystemException.class, () -> {
            invalidCustomer.setPassword("Qwerty12");
            customerRepository.saveAndFlush(invalidCustomer);
        }, "Password must be at least 8 characters long");
    }

    /**
     * Throws an exception when attempting to create or update a customer with illegal size for the vat number attribute
     */
    @Test
    public void testIllegalVatNumberSize() {
        var invalidCustomer = customerFactory.createValidEntity();

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setVatNumber("12345678901234567890");
            customerRepository.saveAndFlush(invalidCustomer);
        }, "Vat Number must be at least 11 digit characters long");
    }

    /**
     * Delete an entry and check if the customer was removed correctly
     */
    @Test
    public void testDeleteCustomer() {
        // get a customer from the repository
        Customer savedCustomer = customerRepository.findById(dummyCustomers.get(0).getId()).get();

        // delete the customer object
        customerRepository.delete(savedCustomer);

        // check that the customer has been deleted correctly
        assertEquals(customerRepository.findById(dummyCustomers.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        customerRepository.deleteAll();
        assertTrue(customerRepository.findAll().isEmpty());
    }

    /**
     * Throws an exception when attempting to delete the customer address
     */
    @Test
    public void testDeleteCustomerAddress() {
        // get a customer and his address from the repository
        Customer savedCustomer = customerRepository.findById(dummyCustomers.get(0).getId()).get();
        Address customerAddress = savedCustomer.getAddress();

        // delete the address object
        addressRepository.delete(customerAddress);

        // check that the address has been deleted correctly
        assertEquals(addressRepository.findById(customerAddress.getId()), Optional.empty());

        // throws an exception when attempting to access to a customer object whose address has been deleted
        assertThrows(AssertionFailedError.class, () -> {
            assertNull(customerRepository.findById(savedCustomer.getId()).get().getAddress());
            assertNotEquals(customerAddress, customerRepository.findById(savedCustomer.getId()).get().getAddress());
        }, "It's not possible to eliminate a customer address");
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedCustomers = customerRepository.findAll();
        var savedAddresses = addressRepository.findAll();

        // check if all the customers are correctly added to the repository
        assertTrue(savedCustomers.containsAll(dummyCustomers), "findAll should fetch all dummy customers");
        assertFalse(savedAddresses.isEmpty());
        for (Customer c : dummyCustomers) {
            assertTrue(savedAddresses.contains(c.getAddress()), "findAll should fetch all dummy addresses");
        }
    }

    @Test
    public void testFindById() {
        // check the correct reading of the customer via findById
        var foundCustomer = customerRepository.findById(dummyCustomers.get(0).getId());

        assertEquals(foundCustomer.get(), dummyCustomers.get(0));
        assertEquals(foundCustomer.get().getId(), dummyCustomers.get(0).getId());

        // try to search for an customer by a not existing id
        var notFoundCustomer = customerRepository.findById(999L);

        assertTrue(notFoundCustomer.isEmpty());
    }

    @Test
    public void testFindByName() {
        // check the correct reading of all the customers via findByName
        var foundCustomers = customerRepository.findByName(dummyCustomers.get(0).getName());

        assertTrue(foundCustomers.contains(dummyCustomers.get(0)));
        for (Customer c: foundCustomers) {
            assertEquals(c.getName(), dummyCustomers.get(0).getName());
        }

        // try to search for customers by a not existing name
        var notFoundCustomers = customerRepository.findByName("Nome Inesistente");

        assertTrue(notFoundCustomers.isEmpty());
    }

    @Test
    public void testFindBySurname() {
        // check the correct reading of all the customers via findBySurname
        var foundCustomers = customerRepository.findBySurname(dummyCustomers.get(0).getSurname());

        assertTrue(foundCustomers.contains(dummyCustomers.get(0)));
        for (Customer c: foundCustomers) {
            assertEquals(c.getSurname(), dummyCustomers.get(0).getSurname());
        }

        // try to search for customers by a not existing surname
        var notFoundCustomers = customerRepository.findBySurname("Cognome Inesistente");

        assertTrue(notFoundCustomers.isEmpty());
    }

    @Test
    public void testFindByFiscalCode() {
        // check the correct reading of the customer via findByFiscalCode
        var foundCustomer = customerRepository.findByFiscalCode(dummyCustomers.get(0).getFiscalCode());

        assertEquals(foundCustomer, dummyCustomers.get(0));
        assertEquals(foundCustomer.getFiscalCode(), dummyCustomers.get(0).getFiscalCode());

        // try to search for an customer by a not existing fiscal code
        var notFoundCustomer = customerRepository.findByFiscalCode("AAAAAA00A00A000A");

        assertNull(notFoundCustomer);
    }

    @Test
    public void testFindByUsername() {
        // check the correct reading of the customer via findByUsername
        var foundCustomer = customerRepository.findByUsername(dummyCustomers.get(0).getUsername());

        assertEquals(foundCustomer, dummyCustomers.get(0));
        assertEquals(foundCustomer.getUsername(), dummyCustomers.get(0).getUsername());

        // try to search for an customer by a not existing username
        var notFoundCustomer = customerRepository.findByUsername("User Inesistente");

        assertNull(notFoundCustomer);
    }
}
