package it.giorgiaauroraadorni.booktique.model;

import it.giorgiaauroraadorni.booktique.utility.EntityToDict;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "items")
public class Item extends AuditModel implements EntityToDict {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Book bookItem;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Supplier supplier;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
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

    public int getQuantityPerUnit() {
        return quantityPerUnit;
    }

    public void setQuantityPerUnit(Integer quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
    }

    public Map<String, Object> entityToDict(boolean optionalId) {
        Map<String, Object> dictionaryAttributes = new HashMap<>();

        if (optionalId) {
            dictionaryAttributes.put("id", this.getId());
        }
        if (this.getBookItem() != null) {
            dictionaryAttributes.put("bookItem", this.getBookItem().entityToDict(optionalId));
        }
        if (this.getSupplier() != null) {
            dictionaryAttributes.put("supplier", this.getSupplier().entityToDict(optionalId));
        }
        dictionaryAttributes.put("unitPrice", this.getUnitPrice().stripTrailingZeros());
        dictionaryAttributes.put("quantityPerUnit", this.getQuantityPerUnit());

        return dictionaryAttributes;
    }

//    /**
//     *
//     * @param expectedObject
//     * @return
//     */
//    public boolean equalsByAttributes(Object expectedObject) {
//        Item item = (Item) expectedObject;
//        return Objects.equals(getId(), item.getId()) &&
//                this.equalsByAttributesWithoutIdAndAssociations(expectedObject);
//    }
//
//    /**
//     *
//     * @param expectedObject
//     * @return
//     */
//    public boolean equalsByAttributesWithoutIdAndAssociations(Object expectedObject) {
//        if (this == expectedObject) return true;
//        if (!(expectedObject instanceof Item)) return false;
//        Item item = (Item) expectedObject;
//        return Objects.equals(getBookItem(), item.getBookItem()) &&
//                Objects.equals(getSupplier(), item.getSupplier()) &&
//                Objects.equals(getUnitPrice(), item.getUnitPrice()) &&
//                Objects.equals(getQuantityPerUnit(), item.getQuantityPerUnit());
//    }
}
