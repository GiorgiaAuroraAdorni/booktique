package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Book;
import it.giorgiaauroraadorni.booktique.model.Item;
import it.giorgiaauroraadorni.booktique.model.Supplier;
import it.giorgiaauroraadorni.booktique.repository.BookRepository;
import it.giorgiaauroraadorni.booktique.repository.ItemRepository;
import it.giorgiaauroraadorni.booktique.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private List<Book> dummybooks;

}
