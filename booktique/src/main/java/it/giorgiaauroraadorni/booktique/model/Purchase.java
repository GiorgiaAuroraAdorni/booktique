package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name="purchases")
public class Purchase extends AuditModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Customer customer;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Employee employee;

    @OneToMany
    private Set<Item> items;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private LocalDate shippingDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

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
    private Purchase.Status status;

    @Embedded
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
