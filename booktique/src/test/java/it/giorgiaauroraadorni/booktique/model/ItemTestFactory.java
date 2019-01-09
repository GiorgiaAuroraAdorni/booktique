package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ItemTestFactory implements EntityTestFactory<Item> {

    private EntityTestFactory<Book> bookFactory;
    private EntityTestFactory<Supplier> supplierFactory;

//    private Book book;
//    private Supplier supplier;

    @Autowired
    public ItemTestFactory(EntityTestFactory<Supplier> supplierFactory, EntityTestFactory<Book> bookFactory) {
        this.bookFactory = bookFactory;
        this.supplierFactory = supplierFactory;

//        var book = bookFactory.createValidEntity(0);
//        var supplier = supplierFactory.createValidEntity(0);
    }

    @Override
    public Item createValidEntity(int idx) {
        var item = new Item();
        var book = bookFactory.createValidEntity(idx);
        var supplier = supplierFactory.createValidEntity(idx);

        // attributes
        item.setBookItem(book);
        item.setSupplier(supplier);
        item.setUnitPrice(BigDecimal.valueOf(13.49));
        item.setQuantityPerUnit(1);

        return item;
    }

    @Override
    public void updateValidEntity(Item entity) {

    }
}
