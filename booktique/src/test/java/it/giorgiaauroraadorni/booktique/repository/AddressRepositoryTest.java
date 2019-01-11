package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AddressRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the customerRepository instance
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityFactory<Address> addressFactory;

    private List<Address> dummyAddresses;

    @BeforeEach
    void createDummyAddresses() {
        // create a list of valid address entities and save the created entities in the addressRepository
        dummyAddresses = (addressFactory.createValidEntities(2));
        dummyAddresses = addressRepository.saveAll(dummyAddresses);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateAddress() {
        for (int i = 0; i < dummyAddresses.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, addressRepository.count());
            assertNotNull(addressRepository.existsById(dummyAddresses.get(i).getId()));

            // check if the books contain the createdAt and updatedAt annotation that are automatically populate,
            // and check if the books id are correctly automatic generated
            assertNotNull(dummyAddresses.get(i).getCreatedAt());
            assertNotNull(dummyAddresses.get(i).getUpdatedAt());
            assertNotNull(dummyAddresses.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals("Via Nomevia 99", dummyAddresses.get(i).getStreetAddress());
            assertEquals("Appartamento 2 terzo piano", dummyAddresses.get(i).getBuilding());
            assertEquals("Città", dummyAddresses.get(i).getCity());
            assertEquals("CT", dummyAddresses.get(i).getProvince());
            assertEquals("Stato", dummyAddresses.get(i).getCountry());
            assertEquals("00000", dummyAddresses.get(i).getPostalCode());
            assertEquals("Regione", dummyAddresses.get(i).getRegion());
            assertNotNull(dummyAddresses.get(i).getId());
        }
    }

    /**
     * Throws an exception when attempting to create an address without mandatory attributes
     */
    @Test
    public void testIllegalCreateAddress() {
        Address invalidAddress = new Address();

        assertThrows(DataIntegrityViolationException.class, () -> {
            addressRepository.saveAndFlush(invalidAddress);
        });
    }

    @Test
    public void testSave() {
        var address = addressFactory.createValidEntity(3);

        assertDoesNotThrow(() -> addressRepository.save(address));
    }

    /**
     * Throws an exception when attempting to create an address with illegal postal code format type
     */
    @Test
    public void testIllegalPostalCodeFormat() {
        Address invalidAddress = addressFactory.createValidEntity(3);
        assertThrows(ConstraintViolationException.class,() -> {
            invalidAddress.setPostalCode("AAAAA");
            addressRepository.saveAndFlush(invalidAddress);
        });
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateAddress() {
        // get an Address from the repository
        Address savedAddress = dummyAddresses.get(0);

        // change attributes and update the address object
        savedAddress.setStreetAddress("Largo Nomelargo 100");
        savedAddress.setCity("Nuova Città");
        savedAddress.setProvince("NC");
        savedAddress.setPostalCode("11111");
        savedAddress.setRegion("Nuova Regione");
        savedAddress.setCountry("Nuovo Stato");
        savedAddress.setBuilding("Edificio 1");

        savedAddress = addressRepository.save(savedAddress);

        // clear the memory in order to get a new istance of the saved book from the db
        addressRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Address updatedAddress = addressRepository.findById(savedAddress.getId()).get();

        assertTrue(addressRepository.existsById(savedAddress.getId()));
        assertEquals("Largo Nomelargo 100", updatedAddress.getStreetAddress());
        assertEquals("Nuova Città", updatedAddress.getCity());
        assertEquals("NC", updatedAddress.getProvince());
        assertEquals("11111", updatedAddress.getPostalCode());
        assertEquals("Nuova Regione", updatedAddress.getRegion());
        assertEquals("Nuovo Stato", updatedAddress.getCountry());
        assertEquals("Edificio 1", updatedAddress.getBuilding());
    }

    /**
     * Throws an exception when attempting to create or update an address with illegal size for the city attribute
     */
    @Test
    public void testIllegalCitySize() {
        Address invalidAddress = addressFactory.createValidEntity(3);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAddress.setCity("Milano Città metropolitana MI Mediolanum");
            addressRepository.saveAndFlush(invalidAddress);
        });
    }

    /**
     * Throws an exception when attempting to create or update an address with illegal size for the region attribute
     */
    @Test
    public void testIllegalRegionSize() {
        Address invalidAddress = addressFactory.createValidEntity(3);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAddress.setRegion("Regione Lombardia capoluogo Milano");
            addressRepository.saveAndFlush(invalidAddress);
        });
    }

    /**
     * Throws an exception when attempting to create or update an address with illegal size for the province attribute
     */
    @Test
    public void testIllegalProvinceSize() {
        Address invalidAddress = addressFactory.createValidEntity(3);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAddress.setProvince("MIL");
            addressRepository.saveAndFlush(invalidAddress);
        });
    }

    /**
     * Throws an exception when attempting to create or update an address with illegal size for the postal
     * code attribute
     */
    @Test
    public void testIllegalPostalCodeSize() {
        Address invalidAddress = addressFactory.createValidEntity(3);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAddress.setPostalCode("111111");
            addressRepository.saveAndFlush(invalidAddress);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidAddress.setPostalCode("1111");
            addressRepository.saveAndFlush(invalidAddress);
        });
    }

    /**
     * Throws an exception when attempting to create or update an address with illegal size for the country attribute
     */
    @Test
    public void testIllegalCountrySize() {
        Address invalidAddress = addressFactory.createValidEntity(3);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAddress.setCountry("Stato Italia Repubblica Italiana Continente Europa");
            addressRepository.saveAndFlush(invalidAddress);
        });
    }

    /**
     * Delete an entry and check if the address was removed correctly
     */
    @Test
    public void testDeleteAddress() {

        // get a Book from the repository
        Address savedBook = addressRepository.findById(dummyAddresses.get(0).getId()).get();

        // delete the Book object
        addressRepository.delete(savedBook);

        // check that the book has been deleted correctly
        assertEquals(addressRepository.findById(dummyAddresses.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        addressRepository.deleteAll();
        assertTrue(addressRepository.findAll().isEmpty());
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedAddresses = addressRepository.findAll();

        // check if all the authors are correctly added to the repository
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy addresses");
    }

    @Test
    public void testFindById() {
        // check the correct reading of the address via findById
        var foundAddress = addressRepository.findById(dummyAddresses.get(0).getId());

        assertEquals(foundAddress.get(), dummyAddresses.get(0));
        assertEquals(foundAddress.get().getId(), dummyAddresses.get(0).getId());
    }
}
