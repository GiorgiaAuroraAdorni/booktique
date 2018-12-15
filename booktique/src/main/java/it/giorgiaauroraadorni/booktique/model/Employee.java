package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee extends Person {

    private Person employee;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    private LocalDate hireDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Employee supervisor;

    // Getters and Setters
    public Person getEmployee() {
        return employee;
    }

    public void setEmployee(Person employee) {
        this.employee = employee;
    }

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
}
