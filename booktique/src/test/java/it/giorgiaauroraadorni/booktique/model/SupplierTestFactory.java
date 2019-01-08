package it.giorgiaauroraadorni.booktique.model;

import org.springframework.stereotype.Component;

@Component
public class SupplierTestFactory implements EntityTestFactory<Supplier> {

    @Override
    public Supplier createValidEntity(int idx) {
        var supplier = new Supplier();

        // mandatory attribute
        supplier.setCompanyName("Compagnia" + idx);

        // other attributes
        supplier.setEmail(supplier.getCompanyName() + "@mail.com");
        supplier.setPhoneNumber("02000000" + idx);

        // the association with the address isn't created, so the attribute is initially null

        return supplier;
    }

    @Override
    public void updateValidEntity(Supplier entity) {

    }
}
