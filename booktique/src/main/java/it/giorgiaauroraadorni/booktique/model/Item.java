package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name="items")
public class Item extends AuditModel {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Book bookItem;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Supplier supplier;

    @NotNull
    @Digits(integer = 10 /*precision*/, fraction = 2 /*scale*/)
    private BigDecimal unitPrice;

    @NotNull
    private Integer quantityPerUnit;

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

    public Integer getQuantityPerUnit() {
        return quantityPerUnit;
    }

    public void setQuantityPerUnit(Integer quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
    }
}
