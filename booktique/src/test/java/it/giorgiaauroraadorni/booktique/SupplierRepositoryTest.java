package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Supplier;
import it.giorgiaauroraadorni.booktique.repository.AddressRepository;
import it.giorgiaauroraadorni.booktique.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
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

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateSupplier() {
        List<Supplier> savedSuppliers = new ArrayList<>();

        for (int i = 0; i < dummySuppliers.size(); i++) {
            // check if the suppliers id are correctly automatic generated
            assertNotNull(supplierRepository.getOne(dummySuppliers.get(i).getId()));
            savedSuppliers.add(supplierRepository.getOne(dummySuppliers.get(i).getId()));

            // check if the suppliers contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedSuppliers.get(i).getCreatedAt());
            assertNotNull(savedSuppliers.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedSuppliers.get(i).getCompanyName(), dummySuppliers.get(i).getCompanyName());
            assertEquals(savedSuppliers.get(i).getEmail(), dummySuppliers.get(i).getEmail());
            assertEquals(savedSuppliers.get(i).getPhoneNumber(), dummySuppliers.get(i).getPhoneNumber());
            assertEquals(savedSuppliers.get(i).getAddress(), dummySuppliers.get(i).getAddress());
            assertEquals(savedSuppliers.get(i).getId(), dummySuppliers.get(i).getId());
        }
    }

    @Test
    public void testSupplierAddress() {
        // check if the addresses are set correctly
        assertNull(supplierRepository.findById(dummySuppliers.get(0).getId()).get().getAddress());
        assertNotNull(supplierRepository.findById(dummySuppliers.get(1).getId()).get().getAddress());
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateSupplier() {
        // get a supplier from the repository
        Supplier savedSupplier = supplierRepository.findById(dummySuppliers.get(0).getId()).get();

        // change some attributes
        savedSupplier.setCompanyName("Centibook Supplier S.r.l.");
        savedSupplier.setPhoneNumber("045612185");

        // update the supplier object
        supplierRepository.save(savedSupplier);
        Supplier updatedSupplier = supplierRepository.findById(savedSupplier.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedSupplier);
        assertEquals(savedSupplier, updatedSupplier);
        assertEquals("Centibook Supplier S.r.l.", updatedSupplier.getCompanyName());
        assertEquals("045612185", updatedSupplier.getPhoneNumber());
    }

    /**
     * Throws an exception when attempting to create a supplier without mandatory attributes
     */
    @Test
    public void testIllegalCreateSupplier() {
        Supplier invalidSupplier = new Supplier();

        assertThrows(DataIntegrityViolationException.class, () -> {
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }

    /**
     * Throws an exception when attempting to create or update a supplier with illegal size for the attributes
     */
    @Test
    public void testIllegalSizeAttributes() {
        Supplier invalidSupplier = new Supplier();

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidSupplier.setCompanyName("Centibook Fast Supplier S.r.l.s.");
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }

    /*
     * Throws an exception when attempting to create a supplier with illegal email format type
     */
    @Test
    public void testIllegalEmailFormat() {
        Supplier invalidSupplier = new Supplier();

        invalidSupplier.setCompanyName("Fast Supplier S.r.l.");

        assertThrows(ConstraintViolationException.class, () -> {
            invalidSupplier.setEmail("fastsupplier@srl@mail.com");
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }

    /*
     * Throws an exception when attempting to create a supplier with illegal phone number format type
     */
    @Test
    public void testIllegalPhoneNumberFormat() {
        Supplier invalidSupplier = new Supplier();

        invalidSupplier.setCompanyName("Fast Supplier S.r.l.");

        assertThrows(ConstraintViolationException.class, () -> {
            invalidSupplier.setPhoneNumber("01234567");
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }
}
