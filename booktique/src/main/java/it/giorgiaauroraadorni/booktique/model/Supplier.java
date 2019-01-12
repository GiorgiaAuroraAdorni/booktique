package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Entity
@Table(name="suppliers")
public class Supplier extends AuditModel {

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

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributes(Object expectedObject) {
        Supplier supplier = (Supplier) expectedObject;
        return Objects.equals(getId(), supplier.getId()) &&
                this.equalsByAttributesWithoutId(expectedObject);
    }

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributesWithoutId(Object expectedObject) {
        if (this == expectedObject) return true;
        if (!(expectedObject instanceof Supplier)) return false;
        Supplier supplier = (Supplier) expectedObject;
        return Objects.equals(getCompanyName(), supplier.getCompanyName()) &&
                getEmail().equals(supplier.getEmail()) &&
                getPhoneNumber().equals(supplier.getPhoneNumber()) &&
                getAddress().equalsByAttributesWithoutId(supplier.getAddress());
    }
}
