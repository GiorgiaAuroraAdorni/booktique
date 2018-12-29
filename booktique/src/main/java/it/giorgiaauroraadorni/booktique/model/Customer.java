package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;

@Entity
@Table(name="customers")
public class Customer extends Person {

    private Person customer;

    @Column(unique = true, length = 32, nullable = false)
    //@Size(min = 5)
    private String username;

    @Column(length = 128, nullable = false)
    //@Size(min = 8)
    private String password;

    @Column(length = 11)
    private String vatNumber;

    // This is a full postal address for the contact represented by this object.
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    // Getters and Setters
    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
