package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CustomerTestFactory implements EntityTestFactory<Customer> {
    @Autowired
    private EntityTestFactory<Address> addressFactory;

    @Override
    public Customer createValidEntity(int idx) {
        var customer = new Customer();
        var address = addressFactory.createValidEntity();

        // mandatory attributes
        customer.setFiscalCode("CGNNMO00T00L00" + idx + "S");
        customer.setName("Nome" + idx);
        customer.setSurname("Cognome" +idx);
        customer.setUsername("UserNo" + idx);
        customer.setPassword("Qwerty1234");

        // mandatory association
        customer.setAddress(address);

        // other attributes
        customer.setDateOfBirth(LocalDate.now().minusYears(20 + idx));
        customer.setEmail(customer.getName() + customer.getSurname() + "@mail.com");
        customer.setMobilePhone("333111111" + idx);
        customer.setVatNumber("IT10000000000");

        return customer;
    }

    @Override
    public void updateValidEntity(Customer entity) {

    }
}
