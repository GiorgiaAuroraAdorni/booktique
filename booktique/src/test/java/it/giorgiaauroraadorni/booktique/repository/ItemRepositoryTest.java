package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.model.Item;
import it.giorgiaauroraadorni.booktique.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    // Set automatically the attribute to the itemRepository instance
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private BookRepository bookRepository;

    private List<Item> dummyItems;

    private List<Supplier> dummySuppliers;

    private List<Book> dummyBooks;

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
                .range(0, 3)
                .mapToObj(i -> new Supplier())
                .collect(Collectors.toList());

        // create some suppliers
        dummySuppliers.get(0).setCompanyName("Centibook Supplier S.r.l.s.");

        dummySuppliers.get(1).setCompanyName("Speed Book S.r.l.");
        dummySuppliers.get(1).setEmail("speedbook@srl.com");
        dummySuppliers.get(1).setPhoneNumber("026512158");

        dummySuppliers.get(2).setCompanyName("Fast Book supplier S.r.l.");
        dummySuppliers.get(2).setPhoneNumber("0465656565");

        // save the suppliers in the repository
        dummySuppliers = supplierRepository.saveAll(dummySuppliers);
    }

    /**
     * Create a list of items entities that will be use in the test
     */
    private void createDummyItem() {
        dummyItems = IntStream
                .range(0, 2)
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

        // save the items in the repository
        dummyItems = itemRepository.saveAll(dummyItems);
    }

    @BeforeEach
    void createDummyEntities() {
        createDummyBook();
        createDummySupplier();
        createDummyItem();
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedBooks = bookRepository.findAll();
        var savedSuppliers = supplierRepository.findAll();
        var savedItems = itemRepository.findAll();

        // check if all the items are correctly added to the repository
        assertTrue(savedBooks.containsAll(dummyBooks), "findAll should fetch all dummy books");
        assertTrue(savedSuppliers.containsAll(dummySuppliers), "findAll should fetch all dummy suppliers");
        assertTrue(savedItems.containsAll(dummyItems), "findAll should fetch all dummy items");
    }

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateItem() {
        List<Item> savedItems = new ArrayList<>();

        for (int i = 0; i < dummyItems.size(); i++) {
            // check if the items id are correctly automatic generated
            assertNotNull(itemRepository.getOne(dummyItems.get(i).getId()));
            savedItems.add(itemRepository.getOne(dummyItems.get(i).getId()));

            // check if the items contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedItems.get(i).getCreatedAt());
            assertNotNull(savedItems.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedItems.get(i).getQuantityPerUnit(), dummyItems.get(i).getQuantityPerUnit());
            assertEquals(savedItems.get(i).getUnitPrice(), dummyItems.get(i).getUnitPrice());
            assertEquals(savedItems.get(i).getBookItem(), dummyItems.get(i).getBookItem());
            assertEquals(savedItems.get(i).getSupplier(), dummyItems.get(i).getSupplier());
            assertEquals(savedItems.get(i).getId(), dummyItems.get(i).getId());
        }
    }

    /**
     * Update one entry editing different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateItem() {
        // get a item from the repository
        Item savedItem = itemRepository.findById(dummyItems.get(0).getId()).get();

        // change some attributes
        savedItem.setQuantityPerUnit(2);
        savedItem.setUnitPrice(BigDecimal.valueOf(34.99));

        // update the item object
        itemRepository.save(savedItem);
        Item updatedItem = itemRepository.findById(savedItem.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedItem);
        assertEquals(savedItem, updatedItem);
        assertEquals(2, updatedItem.getQuantityPerUnit());
        assertEquals(BigDecimal.valueOf(34.99), updatedItem.getUnitPrice());
    }

    /**
     * Throws an exception when attempting to create an item without mandatory attributes
     */
    @Test
    public void testIllegalCreateItem() {
        Item invalidItem = new Item();

        assertThrows(DataIntegrityViolationException.class, () -> {
            itemRepository.saveAndFlush(invalidItem);
        });
    }

    /**
     * Throws an exception when attempting to create or update an item with illegal precision-scale for the attribute UnitPrice
     */
    @Test
    public void testIllegalUnitPriceFormat() {
        Item invalidItem = new Item();

        invalidItem.setBookItem(bookRepository.getOne(dummyBooks.get(2).getId()));
        invalidItem.setSupplier(supplierRepository.getOne(dummySuppliers.get(2).getId()));
        invalidItem.setQuantityPerUnit(1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidItem.setUnitPrice(BigDecimal.valueOf(172345678900987.496));
            itemRepository.saveAndFlush(invalidItem);
        });
    }

    /**
     * Delete an entry and check if the item was removed correctly
     */
    @Test
    public void testDeleteBook() {
        // get a item from the repository
        Item savedItem = itemRepository.findById(dummyItems.get(0).getId()).get();

        // delete the item object and check that the item has been deleted correctly
        itemRepository.delete(savedItem);
        assertEquals(itemRepository.findById(dummyItems.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        itemRepository.deleteAll();
        assertTrue(itemRepository.findAll().isEmpty());
    }

    /* Test search operations */

    @Test
    public void testFindById() {
        // check the correct reading of the item via findById
        var foundItem = itemRepository.findById(dummyItems.get(0).getId());

        assertEquals(foundItem.get(), dummyItems.get(0));
        assertEquals(foundItem.get().getId(), dummyItems.get(0).getId());

        // try to search for an item by a not existing id
        var notFoundItem = itemRepository.findById(999L);

        assertTrue(notFoundItem.isEmpty());
    }

    @Test
    public void testFindByBookItem() {
        // check the correct reading of all the items via findByBookItem
        var foundItems = itemRepository.findByBookItem(dummyItems.get(0).getBookItem());

        assertTrue(foundItems.contains(dummyItems.get(0)));
        for (Item i: foundItems) {
            assertEquals(i.getBookItem(), dummyItems.get(0).getBookItem());
        }

        // try to search for items by a not existing book item
        var notFoundItems = itemRepository.findByBookItem(dummyBooks.get(2));

        assertTrue(notFoundItems.isEmpty());
    }
}
