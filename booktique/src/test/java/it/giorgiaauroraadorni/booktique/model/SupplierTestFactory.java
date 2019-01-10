package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplierTestFactory implements EntityTestFactory<Supplier> {
    @Autowired
    private EntityTestFactory<Address> addressFactory;

    @Override
    public Supplier createValidEntity(int idx) {
        var supplier = new Supplier();
        var address = addressFactory.createValidEntity();

        // mandatory attribute
        supplier.setCompanyName("Compagnia" + idx);

        // other attributes
        supplier.setEmail(supplier.getCompanyName() + "@mail.com");
        supplier.setPhoneNumber("02000000" + idx);

        // association with address
        supplier.setAddress(address);

        return supplier;
    }

    @Override
    public void updateValidEntity(Supplier entity) {

    }
}
