package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.EntityTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AddressRepositoryTest {
    // Set automatically the attribute to the customerRepository instance
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityTestFactory<Address> addressFactory;

    private List<Address> dummyAddresses;

    @BeforeEach
    void createDummyAddresses() {
        // create a list of valid address entities
        dummyAddresses = (addressFactory.createValidEntities(3));
        // save the created entities in the addressRepository
        dummyAddresses = addressRepository.saveAll(dummyAddresses);
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedAddresses = addressRepository.findAll();

        // check if all the authors are correctly added to the repository
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy addresses");
    }

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateAddress() {
        List<Address> savedAddresses = new ArrayList<>();

        for (int i = 0; i < dummyAddresses.size(); i++) {
            // check if the books id are correctly automatic generated
            assertNotNull(addressRepository.getOne(dummyAddresses.get(i).getId()));
            savedAddresses.add(addressRepository.getOne(dummyAddresses.get(i).getId()));

            // check if the books contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedAddresses.get(i).getCreatedAt());
            assertNotNull(savedAddresses.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedAddresses.get(i).getStreetAddress(), dummyAddresses.get(i).getStreetAddress());
            assertEquals(savedAddresses.get(i).getBuilding(), dummyAddresses.get(i).getBuilding());
            assertEquals(savedAddresses.get(i).getCity(), dummyAddresses.get(i).getCity());
            assertEquals(savedAddresses.get(i).getProvince(), dummyAddresses.get(i).getProvince());
            assertEquals(savedAddresses.get(i).getCountry(), dummyAddresses.get(i).getCountry());
            assertEquals(savedAddresses.get(i).getPostalCode(), dummyAddresses.get(i).getPostalCode());
            assertEquals(savedAddresses.get(i).getRegion(), dummyAddresses.get(i).getRegion());
            assertEquals(savedAddresses.get(i).getId(), dummyAddresses.get(i).getId());
        }
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateAddress() {
        // get an Address from the repository
        Address savedAddress = addressRepository.findById(dummyAddresses.get(0).getId()).get();

        // change attributes and update the address object
        addressFactory.updateValidEntity(savedAddress);
        savedAddress = addressRepository.save(savedAddress);

        // check that all the attributes have been updated correctly and contain the expected value
        Address updatedAddress = addressRepository.getOne(savedAddress.getId());
        assertTrue(addressRepository.existsById(savedAddress.getId()));
        assertNotNull(updatedAddress);

        assertEquals("Largo Nomelargo 100", updatedAddress.getStreetAddress());
        assertEquals("Nuova Città", updatedAddress.getCity());
        assertEquals("NC", updatedAddress.getProvince());
        assertEquals("11111", updatedAddress.getPostalCode());
        assertEquals("Nuova Regione", updatedAddress.getRegion());
        assertEquals("Nuovo Stato", updatedAddress.getCountry());
        assertEquals("Edificio 1", updatedAddress.getBuilding());
    }

    /**
     * Throws an exception when attempting to create an address without mandatory attributes
     */
    @Test
    public void testIllegalCreateAddress() {
        Address invalidAddress = new Address();

        assertThrows(DataIntegrityViolationException.class, () -> {
            addressRepository.save(invalidAddress);
            addressRepository.flush();
        });
    }
    /**
     * Throws an exception when attempting to create or update an address with illegal size for the attributes
     */
    @Test
    public void testIllegalSizeAttributes() {
        Address invalidAddress = new Address();

        invalidAddress.setStreetAddress("Via Leone 1");
        invalidAddress.setCity("Milano");
        invalidAddress.setProvince("MI");
        invalidAddress.setPostalCode("41845");
        invalidAddress.setCountry("Italia");

        addressRepository.save(invalidAddress);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidAddress.setCity("Stato Italia Regione Lombardia Città metropolitana Provincia di Milano MI dal " +
                    "toponimo Mediolanum");
            addressRepository.saveAndFlush(invalidAddress);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidAddress.setRegion("Regione Lombardia Piemonte Emilia-Romagna Trentino-Alto Adige Veneto Cantone " +
                    "dei Grigioni (Svizzera Svizzera) Canton Ticino (Svizzera Svizzera)");
            addressRepository.saveAndFlush(invalidAddress);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidAddress.setProvince("Stato Italia Regione Lombardia Città metropolitana Provincia di Milano MI dal " +
                    "toponimo Mediolanum");
            addressRepository.saveAndFlush(invalidAddress);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidAddress.setPostalCode("41845418454184541845418454184541845");
            addressRepository.saveAndFlush(invalidAddress);
        });

        assertThrows(JpaSystemException.class, () -> {
            invalidAddress.setCountry("Stato Italia Repubblica Italiana Continente EuropaStato Italia Repubblica Italiana Continente Europa");
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
}
