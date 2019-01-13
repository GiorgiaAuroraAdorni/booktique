package it.giorgiaauroraadorni.booktique.model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person extends AuditModel {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    // This is an italian fiscal code.
    @NaturalId
    @Pattern(regexp = "^[A-Za-z]{6}[0-9]{2}[A-Za-z]{1}[0-9]{2}[A-Za-z]{1}[0-9]{3}[A-Za-z]{1}$")
    private String fiscalCode;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 30, nullable = false)
    private String surname;

    private LocalDate dateOfBirth;

    // This is an email address. The format allow numbers in the domain name and doesn't allow for top level domains
    // that are less than 2 or more than 6 letters.
    @Column(unique = true)
    @Pattern(regexp = "^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$")
    private String email;

    // This is a phone number. It specifies a italian mobile phone numbers.
    @Column(unique = true)
    @Pattern(regexp = "^([+]39)?(3[1-9][0-9])([\\d]{7})$")
    private String mobilePhone;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surnanme) {
        this.surname = surnanme;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     *
     * @param expectedObject
     * @return
     */
    public boolean equalsByAttributes(Object expectedObject, boolean optionalId) {
        if (this == expectedObject) return true;
        if (!(expectedObject instanceof Person)) return false;
        Person person = (Person) expectedObject;
        if (optionalId) {
            if (!(Objects.equals(getId(), person.getId()))) return false;
        }
        return Objects.equals(getFiscalCode(), person.getFiscalCode()) &&
                Objects.equals(getName(), person.getName()) &&
                Objects.equals(getSurname(), person.getSurname()) &&
                (getDateOfBirth() == person.getDateOfBirth() || (getDateOfBirth() != null
                        && getDateOfBirth().isEqual(person.getDateOfBirth()))) &&
                getEmail().equals(person.getEmail()) &&
                getMobilePhone().equals(person.getMobilePhone());
    }
}
