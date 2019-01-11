package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class PurchaseRepositoryTest {
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
        // create a list of valid purchases entities
        dummyPurchases = purchaseFactory.createValidEntities(2);
        // save the created entities in the purchaseRepository
        dummyPurchases = purchaseRepository.saveAll(dummyPurchases);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreatePurchase() {
        List<Purchase> savedPurchases = new ArrayList<>();

        for (int i = 0; i < dummyPurchases.size(); i++) {
            // check if the purchases id are correctly automatic generated
            assertNotNull(purchaseRepository.getOne(dummyPurchases.get(i).getId()));
            savedPurchases.add(purchaseRepository.getOne(dummyPurchases.get(i).getId()));

            // check if the purchases contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedPurchases.get(i).getCreatedAt());
            assertNotNull(savedPurchases.get(i).getUpdatedAt());

            // check if the purchases contain the amount
            assertNotNull(savedPurchases.get(i).getAmount());
            assertNotEquals(0, savedPurchases.get(i).getAmount());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedPurchases.get(i).getCustomer(), dummyPurchases.get(i).getCustomer());
            assertEquals(savedPurchases.get(i).getEmployee(), dummyPurchases.get(i).getEmployee());
            assertEquals(savedPurchases.get(i).getItems(), dummyPurchases.get(i).getItems());
            assertEquals(savedPurchases.get(i).getOrderDate(), dummyPurchases.get(i).getOrderDate());
            assertEquals(savedPurchases.get(i).getPaymentDetails(), dummyPurchases.get(i).getPaymentDetails());
            assertEquals(savedPurchases.get(i).getShippingDate(), dummyPurchases.get(i).getShippingDate());
            assertEquals(savedPurchases.get(i).getStatus(), dummyPurchases.get(i).getStatus());
            assertEquals(savedPurchases.get(i).getAmount(), dummyPurchases.get(i).getAmount());
            assertEquals(savedPurchases.get(i).getId(), dummyPurchases.get(i).getId());
        }
    }

    /**
     * Update one entry editing different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdatePurchase() {
        // get a purchase from the repository
        Purchase savedPurchase = purchaseRepository.findById(dummyPurchases.get(0).getId()).get();

        // change some attributes
        var oldItems = savedPurchase.getItems();
        var oldOrderDate = savedPurchase.getOrderDate();
        BigDecimal oldAmount = savedPurchase.getAmount();

        Set<Item> newItems = new HashSet<>(itemFactory.createValidEntities(2));
        savedPurchase.setItems(newItems);
        savedPurchase.setOrderDate(LocalDate.of(2038, 1, 4));

        // update the purchase object
        savedPurchase = purchaseRepository.save(savedPurchase);
        Purchase updatedPurchase = purchaseRepository.findById(savedPurchase.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedPurchase);
        assertEquals(savedPurchase, updatedPurchase);
        assertNotEquals(oldItems, newItems);
        assertEquals(newItems, updatedPurchase.getItems());
        assertNotEquals(oldItems, updatedPurchase.getItems());
        assertEquals(LocalDate.of(2038, 1, 4), updatedPurchase.getOrderDate());
        assertNotEquals(oldOrderDate, updatedPurchase.getOrderDate());
        assertNotNull(oldAmount);
        assertNotNull(updatedPurchase.getAmount());
        assertNotEquals(oldAmount, updatedPurchase.getAmount());
    }

    /**
     * Throws an exception when attempting to create a purchase without mandatory attributes
     */
    @Test
    public void testIllegalCreatePurchase() {
        Purchase invalidPurchase = new Purchase();

        assertThrows(DataIntegrityViolationException.class, () -> {
            purchaseRepository.saveAndFlush(invalidPurchase);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase without items
     */
    @Test
    public void testCreationDeniesWithoutItem() {
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

    /**
     * Throws an exception when attempting to create a purchase with illegal status type
     */
    @Test
    public void testIllegalPurchaseStatusFormat() {
        Purchase invalidPurchase = purchaseFactory.createValidEntity(2);

        assertThrows(IllegalArgumentException.class,
                () -> invalidPurchase.setStatus(Purchase.Status.valueOf("UNKNOWN")));
    }

    /**
     * Throws an exception when attempting to add an invalid shipping date to the purchase, in particular that
     * precedes the order date
     */
    @Test
    public void testIllegalShippingDate() {
        // get a purchase from the repository
        Purchase invalidPurchase = purchaseRepository.findById(dummyPurchases.get(0).getId()).get();

        var orderDate = invalidPurchase.getOrderDate();

        // set an invalid shipping date
        invalidPurchase.setShippingDate(orderDate.minusDays(1));

        assertThrows(UnsupportedOperationException.class, () -> purchaseRepository.saveAndFlush(invalidPurchase));
    }

    /**
     * Throws an exception when attempting to create a purchase with the same payment details violating unique
     * constraint
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
     * Delete an entry and check if the purchase was removed correctly
     */
    @Test
    public void testDeletePurchase() {
        // get a purchase from the repository
        Purchase savedPurchase = purchaseRepository.findById(dummyPurchases.get(0).getId()).get();

        // delete the purchase object
        purchaseRepository.delete(savedPurchase);

        // check that the purchase has been deleted correctly
        assertEquals(purchaseRepository.findById(dummyPurchases.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        purchaseRepository.deleteAll();
        assertTrue(purchaseRepository.findAll().isEmpty());
    }

    /**
     * Throws exception when attempting to delete an item
     */
    @Test
    public void testDeleteItem() {
        // get a purchase from the repository
        Purchase savedPurchase = purchaseRepository.findById(dummyPurchases.get(0).getId()).get();

        // it isn't possible set items to null, java.util.Objects.requireNonNull
        assertThrows(NullPointerException.class, () -> savedPurchase.setItems(null));

        // it isn't possible set items to emptySet
        assertThrows(DataIntegrityViolationException.class, () -> {
            savedPurchase.setItems(Collections.emptySet());
            purchaseRepository.saveAndFlush(savedPurchase);
        });

        Purchase purchaseAfterDel = purchaseRepository.findById(savedPurchase.getId()).get();

        // verifying that the purchase object hasn't been updated
        assertNotEquals(purchaseAfterDel.getItems(), Collections.emptySet());
        assertNotNull(purchaseAfterDel.getItems());
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

        // try to search for purchase by an not existing id
        var notFoundPurchase = purchaseRepository.findById(999L);

        assertTrue(notFoundPurchase.isEmpty());
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
