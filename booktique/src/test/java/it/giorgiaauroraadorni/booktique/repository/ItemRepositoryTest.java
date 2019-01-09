package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private EntityTestFactory<Item> itemFactory;

    @Autowired
    private EntityTestFactory<Book> bookFactory;

    private List<Item> dummyItems;

    @BeforeEach
    void createDummySuppliers() {
        // create a list of valid item entities
        dummyItems = itemFactory.createValidEntities(2);

        // save the created entities in the itemRepository and persist books and suppliers
        dummyItems = itemRepository.saveAll(dummyItems);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

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
        Item invalidItem = itemFactory.createValidEntity(3);

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
