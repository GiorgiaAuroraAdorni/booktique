package it.giorgiaauroraadorni.booktique.model;

import it.giorgiaauroraadorni.booktique.utility.EntityEqualsByAttributes;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "employees")
public class Employee extends Person implements EntityEqualsByAttributes {

    @Column(unique = true, length = 32, nullable = false)
    @Size(min = 5)
    private String username;

    @Column(length = 128, nullable = false)
    @Size(min = 8)
    private String password;

    private LocalDate hireDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Employee supervisor;

    // Getters and Setters
    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Employee getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Employee supervisor) {
        this.supervisor = supervisor;
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

    @Override
    public boolean equalsByAttributes(Object expectedObject, boolean optionalId) {
        if (this == expectedObject) return true;
        if (!(expectedObject instanceof Employee)) return false;
        Employee employee = (Employee) expectedObject;
        if(!(super.equalsByAttributes(employee, optionalId))) return false;
        return Objects.equals(getUsername(), employee.getUsername()) &&
                Objects.equals(getPassword(), employee.getPassword()) &&
                (getHireDate() == employee.getHireDate() || (getHireDate() != null &&
                        getHireDate().isEqual(employee.getHireDate()))) &&
                (getAddress() == employee.getAddress() || (getAddress() != null &&
                        getAddress().equalsByAttributes(employee.getAddress(), optionalId)));
                // ignore supervisor to avoid StackOverflowError
    }
}
