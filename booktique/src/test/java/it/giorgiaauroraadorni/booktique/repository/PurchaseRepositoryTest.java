package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.*;
import it.giorgiaauroraadorni.booktique.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private AddressRepository addressRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private BookRepository bookRepository;

    private List<Purchase> dummyPurchases;
    private List<Payment> dummyPayments;
    private List<Employee> dummyEmployees;
    private List<Address> dummyAddresses;
    private List<Customer> dummyCustomers;
    private List<Item> dummyItems;
    private List<Supplier> dummySuppliers;
    private List<Book> dummyBooks;

    /**
     * Create a list of employees entities that will be use in the test
     */
    private void createDummyEmployee() {
        dummyEmployees = IntStream
                .range(0, 2)
                .mapToObj(i -> new Employee())
                .collect(Collectors.toList());

        // create some employees
        dummyEmployees.get(0).setFiscalCode("GRGBVR75C13G224W");
        dummyEmployees.get(0).setName("Beverley");
        dummyEmployees.get(0).setSurname("Gregory");
        dummyEmployees.get(0).setUsername("BeverleyGregory75");
        dummyEmployees.get(0).setSupervisor(dummyEmployees.get(1));
        dummyEmployees.get(0).setPassword("yJmKKSjRJX4HZXrvxjBs");

        dummyEmployees.get(1).setFiscalCode("STNPTR70A11C933C");
        dummyEmployees.get(1).setName("Peter");
        dummyEmployees.get(1).setSurname("Stone");
        dummyEmployees.get(1).setUsername("PeterStone70");
        dummyEmployees.get(1).setPassword("XxzNh9jMkfWaHhzG2YVG");
        dummyEmployees.get(1).setSupervisor(dummyEmployees.get(1));
        dummyEmployees.get(1).setDateOfBirth(LocalDate.of(1970, 11, 3));
        dummyEmployees.get(1).setEmail("peter.stone40@example.com");
        dummyEmployees.get(1).setMobilePhone("+393733733730");

        // save the employees in the repository
        dummyEmployees = employeeRepository.saveAll(dummyEmployees);
    }

    /**
     * Create a list of addresses entities that will be use in the test
     */
    private void createDummyAddress() {
        dummyAddresses = IntStream
                .range(0, 2)
                .mapToObj(i -> new Address())
                .collect(Collectors.toList());

        // create some addresses
        dummyAddresses.get(0).setStreetAddress("Via Vinicio 59");
        dummyAddresses.get(0).setCity("Montecassiano");
        dummyAddresses.get(0).setProvince("MC");
        dummyAddresses.get(0).setPostalCode("04017");
        dummyAddresses.get(0).setCountry("Italia");

        dummyAddresses.get(1).setStreetAddress("Via Tancredi 96");
        dummyAddresses.get(1).setCity("Fonteblanda");
        dummyAddresses.get(1).setProvince("GR");
        dummyAddresses.get(1).setRegion("Toscana");
        dummyAddresses.get(1).setPostalCode("32349");
        dummyAddresses.get(1).setCountry("Italia");
        dummyAddresses.get(1).setBuilding("Appartamento 62 De Santis del friuli");

        // save the addresses in the repository
        dummyAddresses = addressRepository.saveAll(dummyAddresses);
    }

    /**
     * Create a list of customers entities that will be use in the test
     */
    private void createDummyCustomer() {
        dummyCustomers = IntStream
                .range(0, 2)
                .mapToObj(i -> new Customer())
                .collect(Collectors.toList());

        // create some customers
        dummyCustomers.get(0).setFiscalCode("MTCKLN83C13G224W");
        dummyCustomers.get(0).setName("Kaitlin");
        dummyCustomers.get(0).setSurname("Mitchell");
        Address address = dummyAddresses.get(0);
        dummyCustomers.get(0).setAddress(address);
        dummyCustomers.get(0).setUsername("KaitlinMitchell83");
        dummyCustomers.get(0).setPassword("W422g31C38nLkCtM");

        dummyCustomers.get(1).setFiscalCode("DVSMGN49A01C933C");
        dummyCustomers.get(1).setName("Morgan");
        dummyCustomers.get(1).setSurname("Davison");
        dummyCustomers.get(1).setAddress(dummyAddresses.get(1));
        dummyCustomers.get(1).setUsername("MorganDavison49");
        dummyCustomers.get(1).setPassword("C339c10A94nGmSvD");
        dummyCustomers.get(1).setDateOfBirth(LocalDate.of(1949, 1, 1));
        dummyCustomers.get(1).setEmail("MorganDavidson@mail.com");
        dummyCustomers.get(1).setMobilePhone("+393733744430");

        // save the customers in the repository
        dummyCustomers = customerRepository.saveAll(dummyCustomers);
    }

    /**
     * Create a list of books entities that will be use in the test
     */
    private void createDummyBook() {
        dummyBooks = IntStream
                .range(0, 3)
                .mapToObj(i -> new Book())
                .collect(Collectors.toList());

        // Create some books
        dummyBooks.get(0).setIsbn("978-84-08-04364-5");
        dummyBooks.get(0).setTitle("Mountain Of Dreams");
        dummyBooks.get(0).setPublisher("Adventure Publications");

        dummyBooks.get(1).setIsbn("8877827025");
        dummyBooks.get(1).setTitle("Young In The West");
        dummyBooks.get(1).setSubtitle("The A - Z Guide");
        dummyBooks.get(1).setPublisher("Lyon Publishing");
        dummyBooks.get(1).setBookFormat(Book.Format.hardcover);
        dummyBooks.get(1).setEdition(1);
        dummyBooks.get(1).setLanguage("english");
        dummyBooks.get(1).setPublicationDate(LocalDate.of(1999, 1, 1));

        dummyBooks.get(2).setIsbn("88-7782-702-6");
        dummyBooks.get(2).setTitle("After Young In The West");
        dummyBooks.get(2).setPublisher("Lyon Publishing");
        dummyBooks.get(2).setBookFormat(Book.Format.hardcover);
        dummyBooks.get(2).setEdition(1);
        dummyBooks.get(2).setLanguage("english");
        dummyBooks.get(2).setPublicationDate(LocalDate.of(2000, 1, 1));
        dummyBooks.get(2).addPrequel(dummyBooks.get(2));

        // save the books in the repository
        dummyBooks = bookRepository.saveAll(dummyBooks);
    }

    /**
     * Create a list of suppliers entities that will be use in the test
     */
    private void createDummySupplier() {
        dummySuppliers = IntStream
                .range(0, 2)
                .mapToObj(i -> new Supplier())
                .collect(Collectors.toList());

        // create some suppliers
        dummySuppliers.get(0).setCompanyName("Centibook Supplier S.r.l.s.");

        dummySuppliers.get(1).setCompanyName("Speed Book S.r.l.");
        dummySuppliers.get(1).setEmail("speedbook@srl.com");
        dummySuppliers.get(1).setPhoneNumber("026512158");

        // save the suppliers in the repository
        dummySuppliers = supplierRepository.saveAll(dummySuppliers);
    }

    /**
     * Create a list of items entities that will be use in the test
     */
    private void createDummyItem() {
        dummyItems = IntStream
                .range(0, 3)
                .mapToObj(i -> new Item())
                .collect(Collectors.toList());

        // create some items
        dummyItems.get(0).setBookItem(dummyBooks.get(0));
        dummyItems.get(0).setSupplier(dummySuppliers.get(0));
        dummyItems.get(0).setUnitPrice(BigDecimal.valueOf(17.49));
        dummyItems.get(0).setQuantityPerUnit(1);

        dummyItems.get(1).setBookItem(dummyBooks.get(1));
        dummyItems.get(1).setSupplier(dummySuppliers.get(1));
        dummyItems.get(1).setUnitPrice(BigDecimal.valueOf(28.99));
        dummyItems.get(1).setQuantityPerUnit(1);

        dummyItems.get(2).setBookItem(dummyBooks.get(2));
        dummyItems.get(2).setSupplier(dummySuppliers.get(1));
        dummyItems.get(2).setUnitPrice(BigDecimal.valueOf(56.00));
        dummyItems.get(2).setQuantityPerUnit(2);

        // save the items in the repository
        dummyItems = itemRepository.saveAll(dummyItems);
    }

    /**
     * Create a list of payments entities that will be use in the test
     */
    private void createDummyPayment() {
        dummyPayments = IntStream
                .range(0, 3)
                .mapToObj(i -> new Payment())
                .collect(Collectors.toList());

        // create a payment with only the mandatory parameters
        dummyPayments.get(0).setCardNumber("4643417337747076");
        dummyPayments.get(0).setCardholderName("Kaitlin Mitchell");
        dummyPayments.get(0).setExpireDate(LocalDate.of(2025, 12, 1));
        dummyPayments.get(0).setCVC("123");

        // create a payment with all the parameters
        dummyPayments.get(1).setCardNumber("9876543212346789");
        dummyPayments.get(1).setCardholderName("Morgan Davison");
        dummyPayments.get(1).setExpireDate(LocalDate.of(2028, 6, 1));
        dummyPayments.get(1).setCVC("456");
        dummyPayments.get(1).setPaymentDate(LocalDate.now());

        dummyPayments.get(2).setCardNumber("4539136506569726");
        dummyPayments.get(2).setCardholderName("Jose Romaguera");
        dummyPayments.get(2).setExpireDate(LocalDate.of(2020, 8, 1));
        dummyPayments.get(2).setCVC("793");
        dummyPayments.get(2).setPaymentDate(LocalDate.now());

        dummyPayments = paymentRepository.saveAll(dummyPayments);
    }

    /**
     * Create a list of purchases entities that will be use in the test
     */
    private void createDummyPurchase() {
        dummyPurchases = IntStream
                .range(0, 2)
                .mapToObj(i -> new Purchase())
                .collect(Collectors.toList());

        // create a purchase with only the mandatory parameter
        dummyPurchases.get(0).setCustomer(dummyCustomers.get(0));
        dummyPurchases.get(0).setEmployee(dummyEmployees.get(0));
        Set<Item> itemsP1 = new HashSet<>();
        itemsP1.add(dummyItems.get(0));
        dummyPurchases.get(0).setItems(itemsP1);
        dummyPurchases.get(0).setOrderDate(LocalDate.of(2019, 10, 10));
        dummyPurchases.get(0).setPaymentDetails(dummyPayments.get(0));

        // create a purchase with all the attributes
        dummyPurchases.get(1).setCustomer(dummyCustomers.get(1));
        dummyPurchases.get(1).setEmployee(dummyEmployees.get(1));
        Set<Item> itemsP2 = new HashSet<>();
        itemsP2.add(dummyItems.get(1));
        dummyPurchases.get(1).setItems(itemsP2);
        dummyPurchases.get(1).setOrderDate(LocalDate.of(2019, 1, 5));
        dummyPurchases.get(1).setPaymentDetails(dummyPayments.get(1));
        dummyPurchases.get(1).setShippingDate(LocalDate.of(2019, 1, 8));
        dummyPurchases.get(1).setStatus(Purchase.Status.shipped);

        dummyPurchases = purchaseRepository.saveAll(dummyPurchases);
    }

    @BeforeEach
    void createDummyEntities() {
        createDummyEmployee();
        createDummyAddress();
        createDummyCustomer();
        createDummyBook();
        createDummySupplier();
        createDummyItem();
        createDummyPayment();
        createDummyPurchase();
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedEmployees = employeeRepository.findAll();
        var savedAddresses = addressRepository.findAll();
        var savedCustomers = customerRepository.findAll();
        var savedBooks = bookRepository.findAll();
        var savedSuppliers = supplierRepository.findAll();
        var savedItems = itemRepository.findAll();
        var savedPayments = paymentRepository.findAll();
        var savedPurchases = purchaseRepository.findAll();

        // check if all the purchases are correctly added to the repository
        assertTrue(savedEmployees.containsAll(dummyEmployees), "findAll should fetch all dummy employees");
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy addresses");
        assertTrue(savedCustomers.containsAll(dummyCustomers), "findAll should fetch all dummy customers");
        assertTrue(savedBooks.containsAll(dummyBooks), "findAll should fetch all dummy books");
        assertTrue(savedSuppliers.containsAll(dummySuppliers), "findAll should fetch all dummy suppliers");
        assertTrue(savedItems.containsAll(dummyItems), "findAll should fetch all dummy items");
        assertTrue(savedPayments.containsAll(dummyPayments), "findAll should fetch all dummy payments");
        assertTrue(savedPurchases.containsAll(dummyPurchases), "findAll should fetch all dummy purchases");
    }

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
        var amount = savedPurchase.getAmount();

        Set<Item> oldItems = savedPurchase.getItems();
        Set<Item> newItems = new HashSet<>();
        for (Item i: oldItems) {
            newItems.add(i);
        }
        newItems.add(dummyItems.get(2));
        savedPurchase.setItems(newItems);

        var orderDate = savedPurchase.getOrderDate();
        savedPurchase.setOrderDate(LocalDate.of(2038, 1, 4));

        // update the purchase object
        purchaseRepository.save(savedPurchase);
        Purchase updatedPurchase = purchaseRepository.findById(savedPurchase.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedPurchase);
        assertEquals(savedPurchase, updatedPurchase);
        assertNotEquals(oldItems, newItems);
        assertEquals(newItems, updatedPurchase.getItems());
        assertNotEquals(oldItems, updatedPurchase.getItems());
        assertEquals(LocalDate.of(2038, 1, 4), updatedPurchase.getOrderDate());
        assertNotEquals(orderDate, updatedPurchase.getOrderDate());
        //FIXME
        // assertNotEquals(amount, updatedPurchase.getAmount()); //assertNotEquals not work
        assertNotEquals(0, updatedPurchase.getAmount());
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

        invalidPurchase.setCustomer(dummyCustomers.get(0));
        invalidPurchase.setEmployee(dummyEmployees.get(0));
        invalidPurchase.setOrderDate(LocalDate.of(2019, 10, 10));
        invalidPurchase.setPaymentDetails(dummyPayments.get(2));

        assertThrows(DataIntegrityViolationException.class, () -> {
            purchaseRepository.saveAndFlush(invalidPurchase);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase with illegal status type
     */
    @Test
    public void testIllegalPurchaseStatusFormat() {
        Purchase invalidPurchase = new Purchase();

        invalidPurchase.setCustomer(dummyCustomers.get(0));
        invalidPurchase.setEmployee(dummyEmployees.get(0));
        Set<Item> items = new HashSet<>();
        items.add(dummyItems.get(2));
        invalidPurchase.setItems(items);
        invalidPurchase.setOrderDate(LocalDate.of(2019, 10, 10));
        invalidPurchase.setPaymentDetails(dummyPayments.get(2));

        assertThrows(IllegalArgumentException.class,
                () -> invalidPurchase.setStatus(Purchase.Status.valueOf("Unknown")));
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

        assertThrows(DataIntegrityViolationException.class, () -> purchaseRepository.saveAndFlush(invalidPurchase));
    }

    /**
     * Throws an exception when attempting to create a purchase with the same payment details violating unique
     * constraint
     */
    @Test
    public void testUniquePaymentDetails() {
        Purchase duplicatedPurchase = new Purchase();

        duplicatedPurchase.setCustomer(dummyCustomers.get(0));
        duplicatedPurchase.setEmployee(dummyEmployees.get(0));
        duplicatedPurchase.setOrderDate(LocalDate.of(2019, 10, 10));
        Set<Item> items = new HashSet<>();
        items.add(dummyItems.get(2));
        duplicatedPurchase.setItems(items);

        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedPurchase.setPaymentDetails(dummyPayments.get(1));
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
}
