package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Embeddable
public class Payment {

    // This is a Card Number. The format allows 16 numbers in groups of 4 separated by -, ,or nothing.
    @Pattern(regexp = "^(\\d{4}-){3}\\d{4}$|^(\\d{4} ){3}\\d{4}$|^\\d{16}$")
    private String cardNumber;

    @Pattern(regexp = "^([A-Z][a-z]+)\\s([A-Z][a-zA-Z-]+)$")
    private String cardholderName;

    private LocalDate expireDate;

    // This is a Card Verification Codes. The format allows 3 or 4 numerical digits.
    @Pattern(regexp = "\\d{3,4}$")
    private String CVC;

    private LocalDate paymentDate;

    // Getters and Setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getCVC() {
        return CVC;
    }

    public void setCVC(String CVC) {
        this.CVC = CVC;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
