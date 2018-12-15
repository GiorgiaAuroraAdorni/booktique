package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name="orders")
public class Order extends AuditModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Customer customer;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Employee employee;

    @NotNull
    private LocalDate orderDate;

    @NotNull
    private LocalDate shippingDate;

    @NotNull
    private double amount;

    public enum Status {
        canceled,
        completed,
        pendingPayment,
        processing,
        inProduction,
        paymentReview,
        shipped
    }

    @Enumerated(EnumType.STRING)
    private Order.Status status;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Payment payment;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
