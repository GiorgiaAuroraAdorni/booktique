package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.EntityFactory;
import it.giorgiaauroraadorni.booktique.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SupplierRepositoryTest {

    // Set automatically the attribute to the supplierRepository instance
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityFactory<Supplier> supplierFactory;

    @Autowired
    private EntityFactory<Address> addressFactory;

    private List<Supplier> dummySuppliers;

    @BeforeEach
    void createDummySuppliers() {
        // create a list of valid suppliers entities
        dummySuppliers = supplierFactory.createValidEntities(3);

        // save the created entities in the supplierRepository and persist address
        dummySuppliers = supplierRepository.saveAll(dummySuppliers);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

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
        for (Supplier s: dummySuppliers) {
            assertNotNull(supplierRepository.findById(s.getId()).get().getAddress());
        }
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateSupplier() {
        // get a supplier from the repository
        Supplier savedSupplier = supplierRepository.findById(dummySuppliers.get(0).getId()).get();

        // change some attributes
        Address newAddress = addressFactory.createValidEntity(1);
        savedSupplier.setAddress(newAddress);
        savedSupplier.setCompanyName("Centibook Supplier S.r.l.");
        savedSupplier.setPhoneNumber("045612185");

        // update the supplier object
        savedSupplier = supplierRepository.save(savedSupplier);
        Supplier updatedSupplier = supplierRepository.findById(savedSupplier.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedSupplier);
        assertNotNull(updatedSupplier.getAddress());
        assertEquals(savedSupplier, updatedSupplier);
        assertEquals("Centibook Supplier S.r.l.", updatedSupplier.getCompanyName());
        assertEquals("045612185", updatedSupplier.getPhoneNumber());
        assertEquals(newAddress, updatedSupplier.getAddress());
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

    /**
     * Delete an entry and check if it was removed correctly
     */
    @Test
    public void testDeleteSupplier() {
        // get a supplier from the repository
        Supplier savedSupplier = supplierRepository.findById(dummySuppliers.get(1).getId()).get();

        // delete the supplier object
        supplierRepository.delete(savedSupplier);

        // check that the supplier has been deleted correctly
        assertEquals(supplierRepository.findById(dummySuppliers.get(1).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        supplierRepository.deleteAll();
        assertTrue(supplierRepository.findAll().isEmpty());
    }

    /**
     * Delete the supplier address and check if the supplier was updated correctly
     */
    @Test
    public void testDeleteSupplierAddress() {
        // get a supplier from the repository
        Supplier savedSupplier = supplierRepository.findById(dummySuppliers.get(1).getId()).get();
        Address supplierAddress = savedSupplier.getAddress();

        // delete the address object
        addressRepository.delete(supplierAddress);

        // check that the address has been deleted correctly
        assertEquals(addressRepository.findById(supplierAddress.getId()), Optional.empty());

        // throws an exception when attempting to access to a supplier object whose address has been deleted
        assertThrows(AssertionFailedError.class, () -> {
            assertNull(supplierRepository.findById(savedSupplier.getId()).get().getAddress());
            assertNotEquals(supplierAddress, supplierRepository.findById(savedSupplier.getId()).get().getAddress());
        }, "It's not possible to eliminate an address if his supplier haven't been first updated");


        // update the supplier setting null the supplier address
        savedSupplier.setAddress(null);
        supplierRepository.save(savedSupplier);

        Supplier supplierAfterAddressDel = supplierRepository.findById(savedSupplier.getId()).get();

        // check that the supplier has been updated correctly
        assertNull(supplierAfterAddressDel.getAddress());
        assertNotEquals(supplierAddress, supplierAfterAddressDel.getAddress());
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedSuppliers = supplierRepository.findAll();
        var savedAddresses = addressRepository.findAll();

        // check if all the suppliers are correctly added to the repository
        assertTrue(savedSuppliers.containsAll(dummySuppliers), "findAll should fetch all dummy suppliers");
        assertFalse(savedAddresses.isEmpty());
        for (Supplier s : dummySuppliers) {
            assertTrue(savedAddresses.contains(s.getAddress()), "findAll should fetch all dummy addresses");
        }
    }

    @Test
    public void testFindById() {
        // check the correct reading of the supplier via findById
        var foundSupplier = supplierRepository.findById(dummySuppliers.get(0).getId());

        assertEquals(foundSupplier.get(), dummySuppliers.get(0));
        assertEquals(foundSupplier.get().getId(), dummySuppliers.get(0).getId());

        // try to search for suppliers by a not existing id
        var notFoundSupplier = supplierRepository.findById(999L);
        assertTrue(notFoundSupplier.isEmpty());
    }

    @Test
    public void testFindByCompanyName() {
        // check the correct reading of the supplier via findByCompanyName
        var foundSupplier = supplierRepository.findByCompanyName(dummySuppliers.get(0).getCompanyName());

        assertEquals(foundSupplier, dummySuppliers.get(0));
        assertEquals(foundSupplier.getCompanyName(), dummySuppliers.get(0).getCompanyName());

        // try to search for suppliers by a not existing company name
        var notFoundSupplier = supplierRepository.findByCompanyName("Compagnia Inesistente");
        assertNull(notFoundSupplier);
    }

    @Test
    public void testFindByEmail() {
        // check the correct reading of the supplier via findByEmail
        var foundSupplier = supplierRepository.findByEmail(dummySuppliers.get(1).getEmail());

        assertEquals(foundSupplier, dummySuppliers.get(1));
        assertEquals(foundSupplier.getEmail(), dummySuppliers.get(1).getEmail());

        // try to search for suppliers by a not existing mail
        var notFoundSupplier = supplierRepository.findByEmail("emailinesistente@mail.com");
        assertNull(notFoundSupplier);
    }
}
