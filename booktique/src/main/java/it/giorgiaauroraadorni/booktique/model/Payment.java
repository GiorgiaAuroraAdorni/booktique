package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name="payment")
public class Payment extends AuditModel {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull
    private LocalDate paymentDate;

    public enum type {
        creditCard,
        cash,
        check,
        giftCard
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    private Payment.type paymentType;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public type getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(type paymentType) {
        this.paymentType = paymentType;
    }
}
