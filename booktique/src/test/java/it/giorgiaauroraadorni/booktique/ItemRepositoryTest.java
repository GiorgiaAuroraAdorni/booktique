package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.model.Item;
import it.giorgiaauroraadorni.booktique.model.Supplier;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import it.giorgiaauroraadorni.booktique.repository.ItemRepository;
import it.giorgiaauroraadorni.booktique.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
                .range(0, 2)
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

        // create a supplier with only the mandatory parameter
        dummySuppliers.get(0).setCompanyName("Centibook Supplier S.r.l.s.");

        // create a supplier with all the attributes
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


}
