package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CustomerFactory implements EntityFactory<Customer> {
    @Autowired
    private EntityFactory<Address> addressFactory;

    @Override
    public Customer createValidEntity(int idx) {
        var customer = new Customer();
        var address = addressFactory.createValidEntity(idx);

        // mandatory attributes
        customer.setFiscalCode("CGNNMO00T00L00" + idx + "C");
        customer.setName("Nome" + idx);
        customer.setSurname("Cognome" + idx);
        customer.setUsername("CUserNo" + idx);
        customer.setPassword("Qwerty1234");

        // mandatory association
        customer.setAddress(address);

        // other attributes
        customer.setDateOfBirth(LocalDate.now().minusYears(20 + idx));
        customer.setEmail(customer.getName() + customer.getSurname() + "@customer-mail.com");
        customer.setMobilePhone("333111111" + idx);
        customer.setVatNumber("IT10000000000");

        return customer;
    }
}
