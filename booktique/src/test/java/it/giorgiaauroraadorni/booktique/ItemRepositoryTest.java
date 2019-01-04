package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.model.Item;
import it.giorgiaauroraadorni.booktique.model.Supplier;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import it.giorgiaauroraadorni.booktique.repository.ItemRepository;
import it.giorgiaauroraadorni.booktique.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

}
