package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Supplier;
import it.giorgiaauroraadorni.booktique.repository.AddressRepository;
import it.giorgiaauroraadorni.booktique.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SupplierRepositoryTest {

    // Set automatically the attribute to the supplierRepository instance
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private AddressRepository addressRepository;

    private List<Supplier> dummySuppliers;

    private List<Address> dummyAddresses;

    /**
     * Create a list of addresses entities that will be use in the test
     */
    private void createDummyAddress() {
        dummyAddresses = IntStream
                .range(0, 1)
                .mapToObj(i -> new Address())
                .collect(Collectors.toList());

        dummyAddresses.get(0).setStreetAddress("Via Tancredi 96");
        dummyAddresses.get(0).setCity("Fonteblanda");
        dummyAddresses.get(0).setProvince("GR");
        dummyAddresses.get(0).setRegion("Toscana");
        dummyAddresses.get(0).setPostalCode("32349");
        dummyAddresses.get(0).setCountry("Italia");
        dummyAddresses.get(0).setBuilding("Appartamento 62 De Santis del friuli");

        // save the addresses in the repository
        dummyAddresses = addressRepository.saveAll(dummyAddresses);
    }

    /**
     * Create a list of suppliers entities that will be use in the test
     */
    private void createDummySuppliers() {
        dummySuppliers = IntStream
                .range(0, 2)
                .mapToObj(i -> new Supplier())
                .collect(Collectors.toList());

        // create a supplier with only the mandatory parameter
        dummySuppliers.get(0).setCompanyName("Centibook Supplier S.r.l.s.");

        // create a supplier with all the attributes
        dummySuppliers.get(1).setCompanyName("Speed Book S.r.l.");
        dummySuppliers.get(1).setAddress(dummyAddresses.get(0));
        dummySuppliers.get(1).setEmail("speedbook@srl.com");
        dummySuppliers.get(1).setPhoneNumber("026512158");

        // save the suppliers in the repository
        dummySuppliers = supplierRepository.saveAll(dummySuppliers);
    }

    @BeforeEach
    void createDummyEntities() {
        createDummyAddress();
        createDummySuppliers();
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedSuppliers = supplierRepository.findAll();
        var savedAddresses = addressRepository.findAll();

        // check if all the suppliers are correctly added to the repository
        assertTrue(savedSuppliers.containsAll(dummySuppliers), "findAll should fetch all dummy suppliers");
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy addresses");
    }
}
