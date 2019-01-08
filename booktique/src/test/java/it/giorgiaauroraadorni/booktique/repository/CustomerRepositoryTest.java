package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Customer;
import it.giorgiaauroraadorni.booktique.repository.AddressRepository;
import it.giorgiaauroraadorni.booktique.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerRepositoryTest {
    // Set automatically the attribute to the customerRepository instance
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    private List<Customer> dummyCustomers;
    private List<Address> dummyAddresses;

    /**
     * Create a list of addresses entities that will be use in the test
     */
    private void createDummyAddress() {
        dummyAddresses = IntStream
                .range(0, 3)
                .mapToObj(i -> new Address())
                .collect(Collectors.toList());

        // create an address with only the mandatory parameter
        dummyAddresses.get(0).setStreetAddress("Via Vinicio 59");
        dummyAddresses.get(0).setCity("Montecassiano");
        dummyAddresses.get(0).setProvince("MC");
        dummyAddresses.get(0).setPostalCode("04017");
        dummyAddresses.get(0).setCountry("Italia");

        // create an address with all the possible attributes
        dummyAddresses.get(1).setStreetAddress("Via Tancredi 96");
        dummyAddresses.get(1).setCity("Fonteblanda");
        dummyAddresses.get(1).setProvince("GR");
        dummyAddresses.get(1).setRegion("Toscana");
        dummyAddresses.get(1).setPostalCode("32349");
        dummyAddresses.get(1).setCountry("Italia");
        dummyAddresses.get(1).setBuilding("Appartamento 62 De Santis del friuli");

        // create an address with all the possible attributes
        dummyAddresses.get(2).setStreetAddress("Via Leone 1");
        dummyAddresses.get(2).setCity("Milano");
        dummyAddresses.get(2).setProvince("MI");
        dummyAddresses.get(2).setPostalCode("41845");
        dummyAddresses.get(2).setCountry("Italia");
        dummyAddresses.get(2).setBuilding("Piano 8");

        // save the addresses in the repository
        dummyAddresses = addressRepository.saveAll(dummyAddresses);
    }

    /**
     * Create a list of customers entities that will be use in the test
     */
    private void createDummyCustomer() {
        dummyCustomers = IntStream
                .range(0, 3)
                .mapToObj(i -> new Customer())
                .collect(Collectors.toList());

        // create a customer with only the mandatory parameter (inherited from person)
        dummyCustomers.get(0).setFiscalCode("MTCKLN83C13G224W");
        dummyCustomers.get(0).setName("Kaitlin");
        dummyCustomers.get(0).setSurname("Mitchell");
        Address address = dummyAddresses.get(0);
        dummyCustomers.get(0).setAddress(address);
        dummyCustomers.get(0).setUsername("KaitlinMitchell83");
        dummyCustomers.get(0).setPassword("W422g31C38nLkCtM");

        // create a customer with all the person attributes
        dummyCustomers.get(1).setFiscalCode("DVSMGN49A01C933C");
        dummyCustomers.get(1).setName("Morgan");
        dummyCustomers.get(1).setSurname("Davison");
        dummyCustomers.get(1).setAddress(dummyAddresses.get(1));
        dummyCustomers.get(1).setUsername("MorganDavison49");
        dummyCustomers.get(1).setPassword("C339c10A94nGmSvD");
        dummyCustomers.get(1).setDateOfBirth(LocalDate.of(1949, 1, 1));
        dummyCustomers.get(1).setEmail("MorganDavidson@mail.com");
        dummyCustomers.get(1).setMobilePhone("+393733733730");

        // create a customer with many attributes
        dummyCustomers.get(2).setFiscalCode("FRSTVS80T12A271K");
        dummyCustomers.get(2).setName("Travis");
        dummyCustomers.get(2).setSurname("Frost");
        dummyCustomers.get(2).setAddress(dummyAddresses.get(2));
        dummyCustomers.get(2).setDateOfBirth(LocalDate.of(1980, 12, 12));
        dummyCustomers.get(2).setEmail("TravisFrost@mail.com");
        dummyCustomers.get(2).setMobilePhone("3263261001");
        dummyCustomers.get(2).setUsername("TravisFrost80");
        dummyCustomers.get(2).setPassword("K172a21T08sVtSrF");
        dummyCustomers.get(2).setVatNumber("12345678901");

        // save the customers in the repository
        dummyCustomers = customerRepository.saveAll(dummyCustomers);
    }

    @BeforeEach
    void createDummyEntities() {
        createDummyAddress();
        createDummyCustomer();
    }

    // Test CRUD operations

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedCustomers = customerRepository.findAll();
        var savedAddresses = addressRepository.findAll();

        // check if all the customers are correctly added to the repository
        assertTrue(savedCustomers.containsAll(dummyCustomers), "findAll should fetch all dummy customers");
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy addresses");
    }

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

        // change some attributes
        savedCustomer.setName("Terry");
        savedCustomer.setFiscalCode("MTCTRR83C13G224W");
        savedCustomer.setUsername("TerryMitchell83");
        savedCustomer.setPassword("W422g31C38rRtCtM");
        savedCustomer.setAddress(dummyAddresses.get(1));

        // update the Customer object
        customerRepository.save(savedCustomer);
        Customer updatedCustomer = customerRepository.findById(savedCustomer.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedCustomer);
        assertEquals(savedCustomer, updatedCustomer);
        assertEquals("Terry", updatedCustomer.getName());
        assertEquals("MTCTRR83C13G224W", updatedCustomer.getFiscalCode());
        assertEquals("TerryMitchell83", updatedCustomer.getUsername());
        assertEquals("W422g31C38rRtCtM", updatedCustomer.getPassword());
        assertEquals(addressRepository.getOne(dummyAddresses.get(1).getId()), updatedCustomer.getAddress());
    }

    /**
     * Creates a customer with the same username of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints.
     */
    @Test
    public void testUniqueCustomerUsernameIdentifier() {
        Customer duplicatedUser = new Customer();

        // set manually a new id in order to insert a new record and not for update an existing record
        duplicatedUser.setId(9999l);
        duplicatedUser.setFiscalCode("MTCKLN83C18G224W");
        duplicatedUser.setName("Kaitlin Christie");
        duplicatedUser.setSurname("Mitchell");
        duplicatedUser.setAddress(dummyAddresses.get(0));
        duplicatedUser.setPassword("W422g31C38nLkCtM");

        // save the customer in the repository
        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedUser.setUsername("KaitlinMitchell83");
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
     * Throws an exception when attempting to create or update a customer with illegal size for the attributes
     */
    @Test
    public void testIllegalSizeAttributes() {
        Customer invalidCustomer = new Customer();

        invalidCustomer.setFiscalCode("CRLCHR83C13G224W");
        invalidCustomer.setName("Christie");
        invalidCustomer.setSurname("Carlson");
        invalidCustomer.setAddress(addressRepository.getOne(dummyAddresses.get(0).getId()));
        invalidCustomer.setUsername("ChristieCarlson83");
        invalidCustomer.setPassword("W422g31C38rHcLrC");

        customerRepository.save(invalidCustomer);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidCustomer.setUsername("ChristieCarlsonClark15gennaio1983");
            customerRepository.saveAndFlush(invalidCustomer);
        });

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setUsername("Chri");
            customerRepository.saveAndFlush(invalidCustomer);
        });

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setPassword("-X2LPM4r`2.SJn)nGxW3Dt}4$C+z??\"d7np=fHWDTB`y2ye:w2>\\5Kf,}\\Ks?*NBq7FG./Qp" +
                    "(>uxFtfs~U(A!tLHSGk>a5bhue^2wq#~3K9mc2[P(J:]c&hez(Jm&F?j2");
            customerRepository.saveAndFlush(invalidCustomer);
        });

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setPassword("Chris83");
            customerRepository.saveAndFlush(invalidCustomer);
        });

        assertThrows(ConstraintViolationException.class, () -> {
            invalidCustomer.setVatNumber("12345678901234567890");
            customerRepository.saveAndFlush(invalidCustomer);
        });
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

    // Test search operations

    @Test
    public void testFindById() {
        // check the correct reading of the customer via findById
        var foundCustomer = customerRepository.findById(dummyCustomers.get(0).getId());

        assertEquals(foundCustomer.get(), dummyCustomers.get(0));
        assertEquals(foundCustomer.get().getId(), dummyCustomers.get(0).getId());

        // try to search for an customer by an not existing id
        var notFoundCustomer = customerRepository.findById(999L);

        assertTrue(notFoundCustomer.isEmpty());
    }

    @Test
    public void testFindByName() {
        // check the correct reading of all the customers via findByName
        var foundCustomers = customerRepository.findByName(dummyCustomers.get(0).getName());

        assertTrue(foundCustomers.contains(dummyCustomers.get(0)));
        for (Customer a: foundCustomers) {
            assertEquals(a.getName(), dummyCustomers.get(0).getName());
        }

        // try to search for customers by an not existing name
        var notFoundCustomers = customerRepository.findByName("Nome Inesistente");

        assertTrue(notFoundCustomers.isEmpty());
    }

    @Test
    public void testFindBySurname() {
        // check the correct reading of all the customers via findBySurname
        var foundCustomers = customerRepository.findBySurname(dummyCustomers.get(0).getSurname());

        assertTrue(foundCustomers.contains(dummyCustomers.get(0)));
        for (Customer a: foundCustomers) {
            assertEquals(a.getSurname(), dummyCustomers.get(0).getSurname());
        }

        // try to search for customers by an not existing surname
        var notFoundCustomers = customerRepository.findBySurname("Cognome Inesistente");

        assertTrue(notFoundCustomers.isEmpty());
    }

    @Test
    public void testFindByFiscalCode() {
        // check the correct reading of the customer via findByFiscalCode
        // the employee found will be just one because the fiscal code is a natural id, therefore unique
        var foundCustomer = customerRepository.findByFiscalCode(dummyCustomers.get(0).getFiscalCode());

        assertEquals(foundCustomer.get(0), dummyCustomers.get(0));
        assertEquals(foundCustomer.get(0).getFiscalCode(), dummyCustomers.get(0).getFiscalCode());

        // try to search for an customer by an not existing fiscal code
        var notFoundCustomer = customerRepository.findByFiscalCode("AAAAAA00A00A000A");

        assertTrue(notFoundCustomer.isEmpty());
    }

    @Test
    public void testFindByUsername() {
        // check the correct reading of the customer via findByUsername
        // the employee found will be just one because the username is unique
        var foundCustomer = customerRepository.findByUsername(dummyCustomers.get(0).getUsername());

        assertEquals(foundCustomer.get(0), dummyCustomers.get(0));
        assertEquals(foundCustomer.get(0).getUsername(), dummyCustomers.get(0).getUsername());

        // try to search for an customer by an not existing username
        var notFoundCustomer = customerRepository.findByUsername("User Inesistente");

        assertTrue(notFoundCustomer.isEmpty());
    }
}
