package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="customers")
public class Customer extends Person {

    private Person customer;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    private String vatNumber;

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
