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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the itemRepository instance
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityFactory<Item> itemFactory;

    @Autowired
    private EntityFactory<Book> bookFactory;

    @Autowired
    private EntityFactory<Supplier> supplierFactory;

    private List<Item> dummyItems;

    @BeforeEach
    void createDummySuppliers() {
        // create a list of valid item entities and save them in the itemRepository
        // books and suppliers were persisted
        dummyItems = itemFactory.createValidEntities(2);
        dummyItems = itemRepository.saveAll(dummyItems);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct.
     */
    @Test
    public void testCreateItem() {
        for (int i = 0; i < dummyItems.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, itemRepository.count());
            assertTrue(itemRepository.existsById(dummyItems.get(i).getId()));

            // check if the items contain the createdAt and updatedAt annotation that are automatically populate
            // and check if the items id are correctly automatic generated
            assertNotNull(dummyItems.get(i).getCreatedAt());
            assertNotNull(dummyItems.get(i).getUpdatedAt());
            assertNotNull(dummyItems.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(1, dummyItems.get(i).getQuantityPerUnit());
            assertEquals(BigDecimal.valueOf(13.49), dummyItems.get(i).getUnitPrice());
            assertTrue(dummyItems.get(i).getSupplier().equalsByAttributesWithoutId(supplierFactory.createValidEntity(i)));
            assertTrue(dummyItems.get(i).getBookItem().equalsByAttributesWithoutId(bookFactory.createValidEntity(i)));
        }
    }

    /**
     * Throws an exception when attempting to create an item without mandatory attributes.
     */
    @Test
    public void testIllegalCreateItem() {
        Item invalidItem = new Item();

        assertThrows(DataIntegrityViolationException.class, () -> {
            itemRepository.saveAndFlush(invalidItem);
        });
    }

    @Test
    public void testSave() {
        Item item = itemFactory.createValidEntity(2);

        assertDoesNotThrow(() -> itemRepository.save(item));
    }

    /**
     * Test the correct persistence of item suppliers.
     */
    @Test
    public void testItemSupplier() {
        for (Item item: dummyItems) {
            assertTrue(supplierRepository.existsById(item.getSupplier().getId()));
        }
    }

    /**
     * Test the correct persistence of item books.
     */
    @Test
    public void testItemBook() {
        for (Item item: dummyItems) {
            assertTrue(bookRepository.existsById(item.getBookItem().getId()));
        }
    }

    /**
     * Throws an exception when attempting to create or update an item with illegal precision-scale for the attribute
     * UnitPrice.
     */
    @Test
    public void testIllegalUnitPriceFormat() {
        Item invalidItem = itemFactory.createValidEntity(3);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidItem.setUnitPrice(BigDecimal.valueOf(172345678900987.496));
            itemRepository.saveAndFlush(invalidItem);
        });
    }

    /**
     * Update one entry editing different attributes and check if the fields are changed correctly.
     */
    @Test
    public void testUpdateItem() {
        // get a item from the repository
        Item savedItem = dummyItems.get(0);

        // change some attributes
        savedItem.setQuantityPerUnit(2);
        savedItem.setUnitPrice(BigDecimal.valueOf(34.99));

        // update the item object
        savedItem = itemRepository.save(savedItem);

        // clear the memory in order to get a new istance of the saved item from the db
        itemRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Item updatedItem = itemRepository.findById(savedItem.getId()).get();

        assertTrue(itemRepository.existsById(updatedItem.getId()));
        assertEquals(2, updatedItem.getQuantityPerUnit());
        assertEquals(BigDecimal.valueOf(34.99), updatedItem.getUnitPrice());
    }

    /**
     * Update the supplier of an entry editing different attributes and check if the fields are changed correctly and
     * that the item was updated.
     */
    @Test
    public void testUpdateSupplier() {
        // get a item from the repository, modify his supplier and updated the supplier object
        Item savedItem = dummyItems.get(0);
        Supplier savedSupplier = savedItem.getSupplier();

        savedSupplier.setCompanyName("Nuova Compagnia");
        savedSupplier.setPhoneNumber("045612185");

        savedSupplier = supplierRepository.save(savedSupplier);

        // clear the memory in order to get a new istance of the saved item from the db
        itemRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Item updatedItem = itemRepository.findById(savedItem.getId()).get();
        Supplier updatedSupplier = updatedItem.getSupplier();

        assertTrue(itemRepository.existsById(updatedItem.getId()));
        assertTrue(supplierRepository.existsById(updatedSupplier.getId()));
        assertTrue(updatedSupplier.equalsByAttributes(savedSupplier));
    }

    /**
     * Update the book of an entry editing different attributes and check if the fields are changed correctly and
     * that the item was updated.
     */
    @Test
    public void testUpdateBook() {
        // get a item from the repository, modify his book item and updated the supplier object
        Item savedItem = dummyItems.get(0);
        Book savedBook = savedItem.getBookItem();

        savedBook.setSubtitle("Nuovo sottotitolo");
        savedBook.setTitle("Nuovo titolo");

        savedBook = bookRepository.save(savedBook);

        // clear the memory in order to get a new istance of the saved item from the db
        itemRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Item updatedItem = itemRepository.findById(savedItem.getId()).get();
        Book updatedBook = updatedItem.getBookItem();

        assertTrue(itemRepository.existsById(updatedItem.getId()));
        assertTrue(bookRepository.existsById(updatedBook.getId()));
        assertTrue(updatedBook.equalsByAttributes(savedBook));
    }

    /**
     * Delete an entry and check if the item was removed correctly.
     */
    @Test
    public void testDeleteItem() {
        // get a item from the repository
        Item savedItem = dummyItems.get(0);

        // delete the item object and check that the item has been deleted correctly
        itemRepository.delete(savedItem);
        assertEquals(itemRepository.findById(dummyItems.get(0).getId()), Optional.empty());
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllItems() {
        itemRepository.deleteAll();
        assertTrue(itemRepository.findAll().isEmpty());
    }

    /**
     * Throws an exception when attempting to delete a supplier associated to an item.
     * The correct elimination of a book is allowed only if the item is first re-associate to another existent supplier.
     */
    @Test
    public void testDeleteItemSupplier() {
        // get an item and his supplier from the repository and delete the supplier
        Item item = dummyItems.get(0);
        Supplier supplier = item.getSupplier();
        supplierRepository.delete(supplier);

        // throws an exception when attempting to delete an author of a book
        assertThrows(AssertionFailedError.class, () -> {
            assertFalse(supplierRepository.existsById(supplier.getId()));
            assertNull(item.getSupplier());
            }, "It's not possible to delete a book if it belongs to an item!");
    }

    /**
     * Throws an exception when attempting to delete a book associated to an item.
     * The correct elimination of a book is allowed only if the bookitem is first re-associate to an existent book.
     */
    @Test
    public void testDeleteItemBook() {
        // get an item and his book from the repository and delete the book
        Item item = dummyItems.get(0);
        Book book = item.getBookItem();
        bookRepository.delete(book);

        // throws an exception when attempting to delete an author of a book
        assertThrows(AssertionFailedError.class, () -> {
            assertFalse(bookRepository.existsById(book.getId()));
            assertNull(item.getBookItem());
            }, "It's not possible to delete a book if it belongs to an item!");
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {

        var savedBooks = bookRepository.findAll();
        var savedSuppliers = supplierRepository.findAll();
        var savedItems = itemRepository.findAll();

        // check if all the items are correctly added to the repository
        assertFalse(savedItems.isEmpty());
        assertTrue(savedItems.containsAll(dummyItems), "findAll should fetch all dummy items");
        assertFalse(savedBooks.isEmpty());
        for (Item i : dummyItems) {
            assertTrue(savedBooks.contains(i.getBookItem()), "findAll should fetch all dummy books");
        }
        assertFalse(savedSuppliers.isEmpty());
        for (Item i : dummyItems) {
            assertTrue(savedSuppliers.contains(i.getSupplier()), "findAll should fetch all dummy suppliers");
        }
    }

    @Test
    public void testFindById() {
        // check the correct reading of the item via findById
        var foundItem = itemRepository.findById(dummyItems.get(0).getId());

        assertEquals(foundItem.get(), dummyItems.get(0));
        assertEquals(foundItem.get().getId(), dummyItems.get(0).getId());
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
        var notFoundBook = bookFactory.createValidEntity(3);
        notFoundBook = bookRepository.save(notFoundBook);
        var notFoundItems = itemRepository.findByBookItem(notFoundBook);

        assertTrue(notFoundItems.isEmpty());
    }

    @Test
    public void testFindByBookItemTitle() {
        // check the correct reading of all the items via findByBook_Title
        var foundItems = itemRepository.findByBookItem_Title(dummyItems.get(0).getBookItem().getTitle());

        assertTrue(foundItems.contains(dummyItems.get(0)));
        for (Item i: foundItems) {
            assertEquals(i.getBookItem().getTitle(), dummyItems.get(0).getBookItem().getTitle());
        }

        // try to search for items by a not existing book title
        var notFoundItems = itemRepository.findByBookItem_Title("Titolo inesistente");

        assertTrue(notFoundItems.isEmpty());
    }
}
