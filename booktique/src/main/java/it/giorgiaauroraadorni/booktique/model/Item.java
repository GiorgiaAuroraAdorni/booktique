package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
public class Item extends AuditModel {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    //@JoinColumn(name = "book_item_id", unique = true)
    private Book bookItem;

    @ManyToOne(optional = false)
    //@JoinColumn(name = "supplier_id", unique = true)
    private Supplier supplier;

    @Column(nullable = false, precision = 10, scale = 2)
    //@Digits(integer = 10 /*precision*/, fraction = 2 /*scale*/)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantityPerUnit;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBookItem() {
        return bookItem;
    }

    public void setBookItem(Book bookItem) {
        this.bookItem = bookItem;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantityPerUnit() {
        return quantityPerUnit;
    }

    public void setQuantityPerUnit(int quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
    }
}
