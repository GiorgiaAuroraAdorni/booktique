package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static it.giorgiaauroraadorni.booktique.utility.Assertions.assertAssociationEquals;
import static it.giorgiaauroraadorni.booktique.utility.Assertions.assertAttributesEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PurchaseRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the purchaseRepository instance
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityFactory<Purchase> purchaseFactory;

    @Autowired
    private EntityFactory<Item> itemFactory;

    @Autowired
    private EntityFactory<Employee> employeeFactory;

    @Autowired
    private EntityFactory<Customer> customerFactory;

    @Autowired
    private EntityFactory<Payment> paymentFactory;

    private List<Purchase> dummyPurchases;

    @BeforeEach
    void createDummyEntities() {
        // create a list of valid purchases entities and save the created entities in the purchaseRepository
        dummyPurchases = purchaseFactory.createValidEntities(2);
        dummyPurchases = purchaseRepository.saveAll(dummyPurchases);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct.
     */
    @Test
    public void testCreatePurchase() {
        for (int i = 0; i < dummyPurchases.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, purchaseRepository.count());
            assertTrue(purchaseRepository.existsById(dummyPurchases.get(i).getId()));

            // check if the purchases contain the createdAt and updatedAt annotation that are automatically populate,
            // check if the purchases id are correctly automatic generated
            assertNotNull(dummyPurchases.get(i).getCreatedAt());
            assertNotNull(dummyPurchases.get(i).getUpdatedAt());
            assertNotNull(dummyPurchases.get(i).getUpdatedAt());

            // check if the purchases contain the amount
            assertNotNull(dummyPurchases.get(i).getAmount());
            assertNotEquals(0, dummyPurchases.get(i).getAmount());

            // check that all the attributes have been created correctly and contain the expected value
            assertAttributesEquals(purchaseFactory.createValidEntity(i), dummyPurchases.get(i), false);
            assertAssociationEquals(Set.of(itemFactory.createValidEntity(i)), dummyPurchases.get(i).getItems(), false);
        }
    }

    /**
     * Throws an exception when attempting to create a purchase without mandatory attributes.
     */
    @Test
    public void testIllegalCreatePurchase() {
        Purchase invalidPurchase = new Purchase();

        assertThrows(DataIntegrityViolationException.class, () -> {
            purchaseRepository.saveAndFlush(invalidPurchase);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase without items.
     */
    @Test
    public void testCreationDeniesWithoutItems() {
        Purchase invalidPurchase = new Purchase();

        var employee = employeeFactory.createValidEntity(2);
        var customer = customerFactory.createValidEntity(2);
        var payment = paymentFactory.createValidEntity(2);

        invalidPurchase.setCustomer(customer);
        invalidPurchase.setEmployee(employee);
        invalidPurchase.setOrderDate(LocalDate.of(2019, 10, 10));
        invalidPurchase.setPaymentDetails(payment);

        assertThrows(DataIntegrityViolationException.class, () -> {
            purchaseRepository.saveAndFlush(invalidPurchase);
        });
    }

    @Test
    public void testSave() {
        var purchase = purchaseFactory.createValidEntity(3);

        assertDoesNotThrow(() -> purchaseRepository.save(purchase));
    }

    /**
     * Throws an exception when attempting to create a purchase with the same payment details violating unique constraint.
     */
    @Test
    public void testUniquePaymentDetails() {
        Purchase duplicatedPurchase = purchaseFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedPurchase.setPaymentDetails(dummyPurchases.get(0).getPaymentDetails());
            purchaseRepository.saveAndFlush(duplicatedPurchase);
        });
    }

    /**
     * Test the correct persistence of customer.
     */
    @Test
    public void testPurchaseCustomer() {
        for (Purchase purchase: dummyPurchases) {
            assertTrue(customerRepository.existsById(purchase.getCustomer().getId()));
        }
    }

    /**
     * Test the correct persistence of employee.
     */
    @Test
    public void testPurchaseEmployee() {
        for (Purchase purchase: dummyPurchases) {
            assertTrue(employeeRepository.existsById(purchase.getEmployee().getId()));
        }
    }

    /**
     * Test the correct persistence of payment details.
     */
    @Test
    public void testPurchasePaymentDetails() {
        for (Purchase purchase: dummyPurchases) {
            assertTrue(paymentRepository.existsById(purchase.getPaymentDetails().getId()));
        }
    }

    /**
     * Test the correct persistence of items.
     */
    @Test
    public void testPurchaseItems() {
        for (Purchase purchase: dummyPurchases)
            for (Item i: purchase.getItems()) {
                assertTrue(itemRepository.existsById(i.getId()));
            }

    }

    /**
     * Throws an exception when attempting to create a purchase with illegal status.
     */
    @Test
    public void testIllegalPurchaseStatusFormat() {
        Purchase invalidPurchase = purchaseFactory.createValidEntity(2);

        assertThrows(IllegalArgumentException.class,
                () -> invalidPurchase.setStatus(Purchase.Status.valueOf("UNKNOWN")));
    }

    /**
     * Throws an exception when attempting to add an invalid shipping date to the purchase, in particular that
     * precedes the order date.
     */
    @Test
    public void testIllegalShippingDate() {
        Purchase invalidPurchase = purchaseFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidPurchase.setShippingDate(invalidPurchase.getOrderDate().minusDays(1));
            purchaseRepository.saveAndFlush(invalidPurchase);
        });
    }

    /**
     * Update one entry editing different attributes and check if the fields are changed correctly.
     */
    @Test
    public void testUpdatePurchase() {
        // get a purchase from the repository, change some attributes and update the purchase object
        Purchase savedPurchase = dummyPurchases.get(0);

        savedPurchase.setOrderDate(LocalDate.now());
        savedPurchase.setStatus(Purchase.Status.PAYMENT_REVIEW);
        savedPurchase.setShippingDate(LocalDate.now());

        savedPurchase = purchaseRepository.save(savedPurchase);

        // clear the memory in order to get a new instance of the saved purchase from the db
        purchaseRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Purchase updatedPurchase = purchaseRepository.findById(savedPurchase.getId()).get();

        assertTrue(purchaseRepository.existsById(updatedPurchase.getId()));
        assertAttributesEquals(savedPurchase, updatedPurchase, true);
    }

    /**
     * Update the customer of an entry editing different attributes and check if the fields are changed correctly and
     * that the purchase was updated.
     */
    @Test
    public void testUpdatePurchaseCustomer() {
        // get a purchase from the repository, modify his customer and update the purchase object
        Purchase savedPurchase = dummyPurchases.get(0);
        Customer savedCustomer = savedPurchase.getCustomer();

        savedCustomer.setName("Nuovo Nome");
        savedCustomer.setSurname("Nuovo Cognome");
        savedCustomer.setUsername("NuovoCUserNo");
        savedCustomer.setPassword("NuovaPassword");
        savedCustomer.setDateOfBirth(LocalDate.now().minusYears(20));
        savedCustomer.setEmail("NuovoNomeNuovoCognome@customer-mail.com");
        savedCustomer.setMobilePhone("3331111100");
        savedCustomer.setVatNumber("IT10000000011");

        savedCustomer = customerRepository.save(savedCustomer);

        // clear the memory in order to get new istances of the saved purchase and customer from the db
        purchaseRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Purchase updatedPurchase = purchaseRepository.findById(savedPurchase.getId()).get();
        Customer updatedCustomer = updatedPurchase.getCustomer();

        assertTrue(purchaseRepository.existsById(updatedPurchase.getId()));
        assertTrue(customerRepository.existsById(updatedCustomer.getId()));
        assertAttributesEquals(savedPurchase, updatedPurchase, true);
    }

    /**
     * Update the employee of an entry editing different attributes and check if the fields are changed correctly and
     * that the purchase was updated.
     */
    @Test
    public void testUpdatePurchaseEmployee() {
        // get a purchase from the repository, modify his employee and update the purchase object
        Purchase savedPurchase = dummyPurchases.get(0);
        Employee savedEmployee = savedPurchase.getEmployee();

        savedEmployee.setName("Nuovo Nome");
        savedEmployee.setSurname("Nuovo Cognome");
        savedEmployee.setUsername("NuovoEUserNo");
        savedEmployee.setPassword("NuovaPassword");
        savedEmployee.setDateOfBirth(LocalDate.now().minusYears(35));
        savedEmployee.setEmail("NuovoNomeNuovoCognome@employee-mail.com");
        savedEmployee.setMobilePhone("3331111100");
        savedEmployee.setHireDate(LocalDate.now());

        savedEmployee = employeeRepository.save(savedEmployee);

        // clear the memory in order to get new istances of the saved purchase and employee from the db
        purchaseRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Purchase updatedPurchase = purchaseRepository.findById(savedPurchase.getId()).get();
        Employee updatedEmployee = updatedPurchase.getEmployee();

        assertTrue(purchaseRepository.existsById(updatedPurchase.getId()));
        assertTrue(employeeRepository.existsById(updatedEmployee.getId()));
        assertAttributesEquals(savedPurchase, updatedPurchase, true);
    }

    /**
     * Update the payment details of an entry editing different attributes and check if the fields are changed correctly
     * and that the purchase was updated.
     */
    @Test
    public void testUpdatePurchasePaymentDetails() {
        // get a purchase from the repository, modify its payment details and update the purchase object
        Purchase savedPurchase = dummyPurchases.get(0);
        Payment savedPayment = savedPurchase.getPaymentDetails();

        savedPayment.setPaymentDate(LocalDate.now().minusDays(1));
        savedPayment.setCVC("120");
        savedPayment.setCardNumber("0000000000000001");
        savedPayment.setCardholderName("Nuovonome Nuovocognome");

        savedPayment = paymentRepository.save(savedPayment);

        // clear the memory in order to get new istances of the saved purchase and payment details from the db
        purchaseRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Purchase updatedPurchase = purchaseRepository.findById(savedPurchase.getId()).get();
        Payment updatedPayment = updatedPurchase.getPaymentDetails();

        assertTrue(purchaseRepository.existsById(updatedPurchase.getId()));
        assertTrue(paymentRepository.existsById(updatedPayment.getId()));
        assertAttributesEquals(savedPurchase, updatedPurchase, true);
    }

    /**
     * Update the items of an entry editing different attributes and check if the fields are changed correctly and
     * that the purchase was updated.
     */
    @Test
    public void testUpdatePurchaseItems() {
        // get a purchase from the repository, modify its payment details and update the purchase object
        Purchase savedPurchase = dummyPurchases.get(0);
        Set<Item> savedItems = savedPurchase.getItems();

        for (Item item: savedItems) {
            item.setQuantityPerUnit(2);
            item.setUnitPrice(BigDecimal.valueOf(34.99));

            item = itemRepository.save(item);
        }

        // clear the memory in order to get new istances of the saved purchase and items from the db
        purchaseRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Purchase updatedPurchase = purchaseRepository.findById(savedPurchase.getId()).get();
        Set<Item> updatedItems = updatedPurchase.getItems();

        assertTrue(purchaseRepository.existsById(updatedPurchase.getId()));
        for (Item i: updatedItems) {
            assertTrue(itemRepository.existsById(i.getId()));
        }
        assertAttributesEquals(savedPurchase, updatedPurchase, true);
        assertAssociationEquals(savedItems, updatedItems, true);
    }

    /**
     * Delete an entry and check if the purchase was removed correctly.
     */
    @Test
    public void testDeletePurchase() {
        // get a purchase from the repository and delete it
        Purchase savedPurchase = dummyPurchases.get(0);
        purchaseRepository.delete(savedPurchase);

        // check that the purchase has been deleted correctly
        assertEquals(purchaseRepository.findById(dummyPurchases.get(0).getId()), Optional.empty());
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllPurchases() {
        purchaseRepository.deleteAll();
        assertTrue(purchaseRepository.findAll().isEmpty());
    }

    /**
     * Throws an exception when attempting to delete a the customer from a purchase.
     */
    @Test
    public void testDeletePurchaseCustomer() {
        // get a purchase from the repository and delete his customer
        Purchase savedPurchase = dummyPurchases.get(0);
        Customer savedCustomer = savedPurchase.getCustomer();
        customerRepository.delete(savedCustomer);

        // throws an exception when attempting to delete the customer
        assertThrows(AssertionFailedError.class, () -> {
            assertFalse(customerRepository.existsById(savedCustomer.getId()));
        }, "It's not possible to delete a customer if he was associated with a purchase!");
    }

    /**
     * Throws an exception when attempting to delete the employee of a purchase.
     */
    @Test
    public void testDeletePurchaseEmployee() {
        // get a purchase from the repository and delete his employee
        Purchase savedPurchase = dummyPurchases.get(0);
        Employee savedEmployee = savedPurchase.getEmployee();
        employeeRepository.delete(savedEmployee);

        // throws an exception when attempting to delete the employee
        assertThrows(AssertionFailedError.class, () -> {
            assertFalse(employeeRepository.existsById(savedEmployee.getId()));
        }, "It's not possible to delete an employee if he was associated with a purchase!");
    }

    /**
     * Throws an exception when attempting to delete the payment details from the purchase.
     */
    @Test
    public void testDeletePurchasePaymentDetails() {
        // get a purchase from the repository and delete payment details
        Purchase savedPurchase = dummyPurchases.get(0);
        Payment savedPayment = savedPurchase.getPaymentDetails();
        paymentRepository.delete(savedPayment);

        // throws an exception when attempting to delete the payment details
        assertThrows(AssertionFailedError.class, () -> {
            assertFalse(paymentRepository.existsById(savedPayment.getId()));
        }, "It's not possible to delete payment details if they were associated with a purchase!");
    }

    /**
     * Throws an exception when attempting to delete all the items associated to a purchase.
     */
    @Test
    public void testDeletePurchaseItems() {
        // get a purchase from the repository and delete his items
        Purchase savedPurchase = dummyPurchases.get(0);
        Set<Item> items = savedPurchase.getItems();

        itemRepository.deleteAll(items);

        // throws an exception when attempting to delete the items
        assertThrows(AssertionFailedError.class, () -> {
            assertFalse(itemRepository.findAll().containsAll(items));
        }, "It's not possible to delete items associated with a purchase!");
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedEmployees = employeeRepository.findAll();
        var savedCustomers = customerRepository.findAll();
        var savedItems = itemRepository.findAll();
        var savedPayments = paymentRepository.findAll();
        var savedPurchases = purchaseRepository.findAll();

        // check if all the purchases are correctly added to the repository
        assertTrue(savedPurchases.containsAll(dummyPurchases), "findAll should fetch all dummy purchases");
        assertFalse(savedEmployees.isEmpty());
        for (Purchase p: dummyPurchases) {
            assertTrue(savedEmployees.contains(p.getEmployee()), "findAll should fetch all dummy employees");
        }
        assertFalse(savedCustomers.isEmpty());
        for (Purchase p: dummyPurchases) {
            assertTrue(savedCustomers.contains(p.getCustomer()), "findAll should fetch all dummy customers");
        }
        assertFalse(savedItems.isEmpty());
        for (Purchase p: dummyPurchases) {
            assertTrue(savedItems.containsAll(p.getItems()), "findAll should fetch all dummy items");
        }
        assertFalse(savedPayments.isEmpty());
        for (Purchase p: dummyPurchases) {
            assertTrue(savedPayments.contains(p.getPaymentDetails()), "findAll should fetch all dummy payments");
        }
    }

    @Test
    public void testFindById() {
        // check the correct reading of the purchase via findById
        var foundPurchase = purchaseRepository.findById(dummyPurchases.get(0).getId());

        assertEquals(foundPurchase.get(), dummyPurchases.get(0));
        assertEquals(foundPurchase.get().getId(), dummyPurchases.get(0).getId());
    }

    @Test
    public void testFindByOrderDate() {
        // check the correct reading of all the purchases via findByOrderDate
        var foundPurchases = purchaseRepository.findByOrderDate(dummyPurchases.get(0).getOrderDate());

        assertTrue(foundPurchases.contains(dummyPurchases.get(0)));
        for (Purchase p: foundPurchases) {
            assertEquals(p.getOrderDate(), dummyPurchases.get(0).getOrderDate());
        }

        // try to search for purchases by an not existing order date
        var notFoundPurchases = purchaseRepository.findByOrderDate(LocalDate.now().minusYears(2));

        assertTrue(notFoundPurchases.isEmpty());
    }

    @Test
    public void testFindByCustomer() {
        // check the correct reading of all the purchases via findByCustomer
        var foundPurchases = purchaseRepository.findByCustomer(dummyPurchases.get(0).getCustomer());

        assertTrue(foundPurchases.contains(dummyPurchases.get(0)));
        for (Purchase p: foundPurchases) {
            assertEquals(p.getCustomer(), dummyPurchases.get(0).getCustomer());
        }

        // try to search for purchases by an not existing customer
        var notFoundCustomer = customerFactory.createValidEntity(2);
        notFoundCustomer = customerRepository.save(notFoundCustomer);
        var notFoundPurchases = purchaseRepository.findByCustomer(notFoundCustomer);

        assertTrue(notFoundPurchases.isEmpty());
    }

    @Test
    public void testFindByEmployee() {
        // check the correct reading of all the purchases via findByEmployee
        var foundPurchases = purchaseRepository.findByEmployee(dummyPurchases.get(0).getEmployee());

        assertTrue(foundPurchases.contains(dummyPurchases.get(0)));
        for (Purchase p: foundPurchases) {
            assertEquals(p.getEmployee(), dummyPurchases.get(0).getEmployee());
        }

        // try to search for purchases by an not existing employee
        var notFoundEmployee = employeeFactory.createValidEntity(2);
        notFoundEmployee = employeeRepository.save(notFoundEmployee);
        var notFoundPurchases = purchaseRepository.findByEmployee(notFoundEmployee);

        assertTrue(notFoundPurchases.isEmpty());
    }
}
