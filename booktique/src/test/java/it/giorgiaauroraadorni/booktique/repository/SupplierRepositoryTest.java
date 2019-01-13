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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;

import static it.giorgiaauroraadorni.booktique.utility.Assertions.assertAttributesEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SupplierRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the supplierRepository instance
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityFactory<Supplier> supplierFactory;

    private List<Supplier> dummySuppliers;

    @BeforeEach
    void createDummySuppliers() {
        // create a list of valid suppliers entities
        dummySuppliers = supplierFactory.createValidEntities(2);

        // save the created entities in the supplierRepository and persist address
        dummySuppliers = supplierRepository.saveAll(dummySuppliers);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct.
     */
    @Test
    public void testCreateSupplier() {
        for (int i = 0; i < dummySuppliers.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, supplierRepository.count());
            assertTrue(supplierRepository.existsById(dummySuppliers.get(i).getId()));

            // check if the suppliers contain the createdAt and updatedAt annotation that are automatically populate
            // check if the suppliers id are correctly automatic generated
            assertNotNull(dummySuppliers.get(i).getCreatedAt());
            assertNotNull(dummySuppliers.get(i).getUpdatedAt());
            assertNotNull(dummySuppliers.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertAttributesEquals(supplierFactory.createValidEntity(i), dummySuppliers.get(i), false);
        }
    }

    /**
     * Throws an exception when attempting to create a supplier without mandatory attributes.
     */
    @Test
    public void testIllegalCreateSupplier() {
        Supplier invalidSupplier = new Supplier();

        assertThrows(DataIntegrityViolationException.class, () -> {
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }

    @Test
    public void testSave() {
        var supplier = supplierFactory.createValidEntity(2);

        assertDoesNotThrow(() -> supplierRepository.save(supplier));
    }

    @Test
    public void testSupplierAddress() {
        // check if the addresses are set correctly
        for (Supplier s: dummySuppliers) {
            assertNotNull(supplierRepository.findById(s.getId()).get().getAddress());
        }
    }

    /**
     * Throws an exception when attempting to create or update a supplier with illegal size for the attributes.
     */
    @Test
    public void testIllegalCompanyNameSize() {
        Supplier invalidSupplier = supplierFactory.createValidEntity(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidSupplier.setCompanyName("Centibook Fast Supplier S.r.l.s.");
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }

    /*
     * Throws an exception when attempting to create a supplier with illegal email format type.
     */
    @Test
    public void testIllegalEmail() {
        Supplier invalidSupplier = supplierFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidSupplier.setEmail("fastsupplier@srl@mail.com");
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }

    /*
     * Throws an exception when attempting to create a supplier with illegal phone number format type.
     */
    @Test
    public void testIllegalPhoneNumber() {
        Supplier invalidSupplier = supplierFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidSupplier.setPhoneNumber("01234567");
            supplierRepository.saveAndFlush(invalidSupplier);
        });
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly.
     */
    @Test
    public void testUpdateSupplier() {
        // get a supplier from the repository, change some attributes and update the supplier object
        Supplier savedSupplier = dummySuppliers.get(0);

        savedSupplier.setCompanyName("Nuovo Nome");
        savedSupplier.setPhoneNumber("045612185");
        savedSupplier.setEmail("NuovoNome@mail.com");

        savedSupplier = supplierRepository.save(savedSupplier);

        // clear the memory in order to get a new instance of the saved supplier from the db
        supplierRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Supplier updatedSupplier = supplierRepository.findById(savedSupplier.getId()).get();

        assertTrue(supplierRepository.existsById(updatedSupplier.getId()));
        assertAttributesEquals(savedSupplier, updatedSupplier, true);
    }

    /**
     * Update the address of an entry editing different attributes and check if the fields are changed
     * correctly and that the supplier was updated.
     */
    @Test
    public void testUpdateSupplierAddress() {
        // get a supplier from the repository, modify his address and update the supplier object
        Supplier savedSupplier = dummySuppliers.get(0);
        Address savedAddress = savedSupplier.getAddress();

        savedAddress.setStreetAddress("Largo Nomelargo 100");
        savedAddress.setRegion("Nuova Regione");

        savedAddress = addressRepository.save(savedAddress);

        // clear the memory in order to get a new instance of the saved supplier from the db
        supplierRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Supplier updatedSupplier = supplierRepository.findById(savedSupplier.getId()).get();
        Address updatedAddress = updatedSupplier.getAddress();

        assertTrue(supplierRepository.existsById(updatedSupplier.getId()));
        assertTrue(addressRepository.existsById(updatedAddress.getId()));
        assertAttributesEquals(savedSupplier, updatedSupplier, true);
    }

    /**
     * Delete an entry and check if it was removed correctly.
     */
    @Test
    public void testDeleteSupplier() {
        // get a supplier from the repository and delete it
        Supplier savedSupplier = dummySuppliers.get(0);
        supplierRepository.delete(savedSupplier);

        // check that the supplier has been deleted correctly
        assertFalse(supplierRepository.existsById(savedSupplier.getId()));
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllSupplier() {
        supplierRepository.deleteAll();
        assertTrue(supplierRepository.findAll().isEmpty());
    }

    /**
     * Throws an exception when attempting to delete the address of a supplier. The elimination is allowed only if
     * the address is first re-allocated or set null.
     */
    @Test
    public void testDeleteSupplierAddress() {
        // get a supplier from the repository and delete the address object
        Supplier savedSupplier = supplierRepository.findById(dummySuppliers.get(1).getId()).get();
        Address savedAddress = savedSupplier.getAddress();

        addressRepository.delete(savedAddress);

        // throws an exception when attempting to access to a supplier object whose address has been deleted
        assertThrows(AssertionFailedError.class, () -> assertFalse(addressRepository.existsById(savedAddress.getId())),
                "It's not possible to eliminate an address if his supplier haven't been first updated");

        // update the supplier setting null the supplier address
        savedSupplier.setAddress(null);
        addressRepository.delete(savedAddress);
        supplierRepository.save(savedSupplier);

        // clear the memory in order to get a new instance of the saved supplier from the db
        supplierRepository.flush();
        entityManager.clear();

        // check that the supplier has been updated correctly
        Supplier updatedSupplier = supplierRepository.findById(savedSupplier.getId()).get();

        assertTrue(supplierRepository.existsById(updatedSupplier.getId()));
        assertFalse(addressRepository.existsById(savedAddress.getId()));
        assertNull(updatedSupplier.getAddress());
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
