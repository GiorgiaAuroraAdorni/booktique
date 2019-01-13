package it.giorgiaauroraadorni.booktique.model;

import it.giorgiaauroraadorni.booktique.utility.EntityEqualsByAttributes;
import it.giorgiaauroraadorni.booktique.utility.EntityToDict;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name="suppliers")
public class Supplier extends AuditModel implements EntityToDict, EntityEqualsByAttributes {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String companyName;

    // This is an email address. The format allow numbers in the domain name and doesn't allow for top level domains
    // that are less than 2 or more than 6 letters.
    @Column(unique = true)
    @Pattern(regexp = "^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$")
    private String email;

    // This is a phone number. It specifies a italian fixed telephone numbers.
    @Column(unique = true)
    @Pattern(regexp = "^0[0-9]{8,9}$")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Address address;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equalsByAttributes(Object expectedObject, boolean optionalId) {
        if (this == expectedObject) return true;
        if (!(expectedObject instanceof Supplier)) return false;
        Supplier supplier = (Supplier) expectedObject;
        if (optionalId) {
            if (!(Objects.equals(getId(), supplier.getId()))) return false;
        }
        return Objects.equals(getCompanyName(), supplier.getCompanyName()) &&
                getEmail().equals(supplier.getEmail()) &&
                getPhoneNumber().equals(supplier.getPhoneNumber()) &&
                getAddress().equalsByAttributes(supplier.getAddress(), false);
    }

    @Override
    public Map<String, Object> entityToDict(boolean optionalId) {
        Map<String, Object> dictionaryAttributes = new HashMap<>();

        if (optionalId) {
            dictionaryAttributes.put("id", this.getId());
        }
        dictionaryAttributes.put("companyName", this.getCompanyName());
        dictionaryAttributes.put("address", this.getAddress());
        dictionaryAttributes.put("email", this.getEmail());
        dictionaryAttributes.put("phoneNumber", this.getPhoneNumber());

        return dictionaryAttributes;
    }
}
