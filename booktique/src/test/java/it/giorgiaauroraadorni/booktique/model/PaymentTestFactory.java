package it.giorgiaauroraadorni.booktique.model;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PaymentTestFactory implements EntityTestFactory<Payment> {
    @Override
    public Payment createValidEntity(int idx) {
        var payment = new Payment();

        // mandatory attributes
        payment.setCardNumber("0000000000000000");
        payment.setCardholderName("Nome Cognome");
        payment.setExpireDate(LocalDate.now().plusYears(5));
        payment.setCVC("000");

        // other attributes
        payment.setPaymentDate(LocalDate.now());

        return payment;
    }

    @Override
    public void updateValidEntity(Payment entity) {

    }
}
