package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.*;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

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
        // create a list of valid customers entities and save them in the customerRepository
        // addresses were persisted
        dummyCustomers = customerFactory.createValidEntities(2);
        dummyCustomers = customerRepository.saveAll(dummyCustomers);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct.
     */
    @Test
    public void testCreateCustomer() {
        for (int i = 0; i < dummyCustomers.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, customerRepository.count());
            assertNotNull(customerRepository.existsById(dummyCustomers.get(i).getId()));

            // check if the customers contain the createdAt and updatedAt annotation that are automatically populate,
            // and check if the customers id are correctly automatic generated
            assertNotNull(dummyCustomers.get(i).getCreatedAt());
            assertNotNull(dummyCustomers.get(i).getUpdatedAt());
            assertNotNull(dummyCustomers.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals("CGNNMO00T00L00" + i + "C", dummyCustomers.get(i).getFiscalCode());
            assertEquals("Nome" + i, dummyCustomers.get(i).getName());
            assertEquals("Cognome" + i, dummyCustomers.get(i).getSurname());
            assertEquals("CUserNo" + i, dummyCustomers.get(i).getUsername());
            assertEquals("Qwerty1234", dummyCustomers.get(i).getPassword());
            assertEquals(LocalDate.now().minusYears(20 + i), dummyCustomers.get(i).getDateOfBirth());
            assertEquals(dummyCustomers.get(i).getName() + dummyCustomers.get(i).getSurname() + "@customer-mail.com", dummyCustomers.get(i).getEmail());
            assertEquals("333111111" + i, dummyCustomers.get(i).getMobilePhone());
            assertEquals("IT10000000000", dummyCustomers.get(i).getVatNumber());
            assertTrue(dummyCustomers.get(i).getAddress().equalsByAttributesWithoutId(addressFactory.createValidEntity(i)));
        }
    }

    /**
     * Throws an exception when attempting to create a customer without mandatory attributes.
     */
    @Test
    public void testIllegalCreateCustomer() {
        Customer invalidCustomer = new Customer();

        assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.saveAndFlush(invalidCustomer);
        });
    }

    @Test
    public void testSave() {
        Customer customer = customerFactory.createValidEntity(2);

        assertDoesNotThrow(() -> customerRepository.save(customer));
    }

    /**
     * Creates a customer with the same FiscalCode of another and throws an exception when attempting to insert data
     * by violating the unique constraints on the properties that constitute a natural-id.
     */
    @Test
    public void testUniqueFiscalCodeIdentifier() {
        var duplicatedCustomer = customerFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedCustomer.setFiscalCode("CGNNMO00T00L000C");
            customerRepository.saveAndFlush(duplicatedCustomer);
        });
    }

    /**
     * Creates a customer with the same username of another and throws an exception when attempting to insert data
     * by violating the unique constraints.
     */
    @Test
    public void testUniqueCustomerUsername() {
        var duplicatedCustomer = customerFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedCustomer.setUsername("CUserNo0");
            customerRepository.saveAndFlush(duplicatedCustomer);
        });
    }

    /**
     * Creates a customer with the same email of another and throws an exception when attempting to insert data by
     * violating the unique constraints.
     */
    @Test
    public void testUniqueEmail() {
        var duplicatedCustomer = customerFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedCustomer.setEmail("Nome0Cognome0@customer-mail.com");
            customerRepository.saveAndFlush(duplicatedCustomer);
        });
    }

    /**
     * Creates a customer with the same mobile phone of another and throws an exception when attempting to insert data
     * by violating the unique constraints.
     */
    @Test
    public void testUniqueMobilePhone() {
        var duplicatedCustomer = customerFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedCustomer.setMobilePhone("3331111110");
            customerRepository.saveAndFlush(duplicatedCustomer);
        });
    }

    /**
     * Test the correct persist of customer address.
     */
    @Test
    public void testCustomerAddress() {
        // check if the addresses are set correctly
        for (Customer customer: dummyCustomers) {
            assertTrue(addressRepository.existsById(customer.getAddress().getId()));
        }
    }

    /**
     * Throws an exception when attempting to create an author with illegal fiscal code.
     */
    @Test
    public void testIllegalFiscalCode() {
        var invalidCustomer = customerFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setFiscalCode("ABCDEFGHIJKLMNOP");
            customerRepository.saveAndFlush(invalidCustomer);
        });
    }

    /*
     * Throws an exception when attempting to create a customer with illegal email.
     */
    @Test
    public void testIllegalEmail() {
        var invalidCustomer = customerFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setEmail("NomeCognome@mail@10.com");
            customerRepository.saveAndFlush(invalidCustomer);
        });
    }

    /**
     * Throws an exception when attempting to create a customer with illegal mobile phone.
     */
    @Test
    public void testIllegalMobilePhone() {
        var invalidCustomer = customerFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setMobilePhone("0039333123456");
            customerRepository.saveAndFlush(invalidCustomer);
        });
    }

    /**
     * Throws an exception when attempting to create a customer with illegal date of birth.
     */
    @Test
    public void testIllegalDateOfBirth() {
        var invalidCustomer = customerFactory.createValidEntity(2);

        assertThrows(DateTimeException.class, () -> {
            invalidCustomer.setDateOfBirth(LocalDate.of(1980, 13, 32));
            customerRepository.save(invalidCustomer);
        });
    }

    /**
     * Throws an exception when attempting to create or update a customer with illegal size for the username attribute.
     */
    @Test
    public void testIllegalUsernameSize() {
        var invalidCustomer = customerFactory.createValidEntity(2);

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
     * Throws an exception when attempting to create or update a customer with illegal size for the password attribute.
     */
    @Test
    public void testIllegalPasswordSize() {
        var invalidCustomer = customerFactory.createValidEntity(2);

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
     * Throws an exception when attempting to create or update a customer with illegal size for the vat number attribute.
     */
    @Test
    public void testIllegalVatNumberSize() {
        var invalidCustomer = customerFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setVatNumber("12345678901234567890");
            customerRepository.saveAndFlush(invalidCustomer);
        }, "Vat Number must be at least 11 digit characters long");
    }

    /**
     * Throws an exception when attempting to create or update a customer with illegal size for the name attribute.
     */
    @Test
    public void testIllegalNameSize() {
        var invalidCustomer = customerFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidCustomer.setName("PrimoNomeSecondoNomeTerzoNomeQuartoNome");
            customerRepository.saveAndFlush(invalidCustomer);
        });
    }

    /**
     * Throws an exception when attempting to create or update a customer with illegal size for the surname attribute.
     */
    @Test
    public void testIllegalSurnameSize() {
        var invalidCustomer = customerFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidCustomer.setSurname("PrimoCognomeSecondoCognomeTerzoCognome");
            customerRepository.saveAndFlush(invalidCustomer);
        });
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly.
     */
    @Test
    public void testUpdateCustomer() {
        // get a customer from the repository
        Customer savedCustomer = dummyCustomers.get(0);

        var address = savedCustomer.getAddress();
        // change some attributes and update the customer object
        // the address association isn't updated
        savedCustomer.setName("Nuovo Nome");
        savedCustomer.setSurname("Nuovo Cognome");
        savedCustomer.setUsername("NuovoCUserNo");
        savedCustomer.setPassword("NuovaPassword");
        savedCustomer.setDateOfBirth(LocalDate.now().minusYears(20));
        savedCustomer.setEmail("NuovoNomeNuovoCognome@customer-mail.com");
        savedCustomer.setMobilePhone("3331111100");
        savedCustomer.setVatNumber("IT10000000011");

        savedCustomer = customerRepository.save(savedCustomer);

        // clear the memory in order to get a new istance of the saved customer from the db
        customerRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Customer updatedCustomer = customerRepository.findById(savedCustomer.getId()).get();

        assertTrue(customerRepository.existsById(updatedCustomer.getId()));
        assertEquals("Nuovo Nome", updatedCustomer.getName());
        assertEquals("Nuovo Cognome", updatedCustomer.getSurname());
        assertEquals("NuovoCUserNo", updatedCustomer.getUsername());
        assertEquals("NuovaPassword", updatedCustomer.getPassword());

        assertEquals(LocalDate.now().minusYears(20), updatedCustomer.getDateOfBirth());
        assertEquals("NuovoNomeNuovoCognome@customer-mail.com", updatedCustomer.getEmail());
        assertEquals("3331111100", updatedCustomer.getMobilePhone());
        assertEquals("IT10000000011", updatedCustomer.getVatNumber());
    }

    /**
     * Throws an exception when attempting to update the immutable natural identifier fiscal code.
     */
    @Test
    public void testUpdateFiscalCode() {
        // get a customer from the repository, modify the fiscal code and update the customer object
        Customer savedCustomer = dummyCustomers.get(0);

        assertThrows(JpaSystemException.class, () -> {
            savedCustomer.setFiscalCode("CGNNMO00A00L000C");
            customerRepository.saveAndFlush(savedCustomer);
        }, "It's not possible to updated a customer fiscal code!");
    }

    /**
     * Update the address of an entry and check if the fields are changed correctly and that the customer was updated.
     */
    @Test
    public void testUpdateAddress() {
        // get a customer from the repository, modify the fiscal code and update the customer object
        Customer savedCustomer = dummyCustomers.get(0);
        Address savedAddress = savedCustomer.getAddress();

        // modified some prequel attribute and update the istances
        savedAddress.setStreetAddress("Largo Nomelargo 100");
        savedAddress.setRegion("Nuova Regione");

        savedAddress = addressRepository.save(savedAddress);

        // clear the memory in order to get a new istance of the saved customer and address from the db
        addressRepository.flush();
        entityManager.clear();

        // get the updated customer and hs address from teh repository
        Customer updatedCustomer = customerRepository.findById(savedCustomer.getId()).get();
        Address updatedAddress = updatedCustomer.getAddress();

        // check that the customer and the address exist
        assertTrue(customerRepository.existsById(updatedCustomer.getId()));
        assertTrue(addressRepository.existsById(updatedAddress.getId()));

        // check that the address attribute have been updated correctly
        assertTrue(updatedAddress.equalsByAttributes(savedAddress));
        assertEquals("Largo Nomelargo 100", updatedAddress.getStreetAddress());
        assertEquals("Nuova Regione", updatedAddress.getRegion());
    }

    /**
     * Delete an entry and check that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteCustomer() {
        // get a customer from the repository and delete it
        Customer savedCustomer = dummyCustomers.get(0);
        customerRepository.delete(savedCustomer);

        // check that the customer has been deleted correctly
        assertFalse(customerRepository.existsById(savedCustomer.getId()));
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllCustomers() {
        customerRepository.deleteAll();
        assertTrue(customerRepository.findAll().isEmpty());
    }

    /**
     * Throws an exception when attempting to delete the customer address.
     */
    @Test
    public void testDeleteCustomerAddress() {
        // get a customer and his address from the repository and delete the address object
        Customer savedCustomer = dummyCustomers.get(0);
        Address customerAddress = savedCustomer.getAddress();
        addressRepository.delete(customerAddress);

        // clear the memory in order to get a new istance of the saved customer and address from the db
        addressRepository.flush();
        entityManager.clear();

        // throws an exception when attempting to access to a customer object whose address has been deleted
        Customer updatedCustomer = customerRepository.findById(savedCustomer.getId()).get();

        assertThrows(AssertionFailedError.class,
                () -> assertFalse(addressRepository.existsById(customerAddress.getId())),
                "It's not possible to eliminate a customer address");
        assertTrue(customerRepository.existsById(updatedCustomer.getId()));
        assertNotNull(updatedCustomer.getAddress());
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
