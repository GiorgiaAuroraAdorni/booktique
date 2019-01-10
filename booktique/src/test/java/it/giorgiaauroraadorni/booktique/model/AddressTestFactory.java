package it.giorgiaauroraadorni.booktique.model;

import org.springframework.stereotype.Component;

@Component
public class AddressTestFactory implements EntityTestFactory<Address> {

    @Override
    public Address createValidEntity(int idx) {
        var address = new Address();

        // mandatory attributes
        address.setStreetAddress("Via Nomevia 99");
        address.setCity("Città");
        address.setProvince("CT");
        address.setPostalCode("00000");
        address.setCountry("Stato");

        // other attributes
        address.setRegion("Regione");
        address.setBuilding("Appartamento 2 terzo piano");

        return address;
    }

    @Override
    public void updateValidEntity(Address entity) {

    }
}
