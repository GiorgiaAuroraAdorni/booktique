package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AddressRepositoryTest {
    // Set automatically the attribute to the customerRepository instance
    @Autowired
    private AddressRepository addressRepository;

    private List<Address> dummyAddresses;

    /**
     * Create a list of addresses entities that will be use in the test
     */
    @BeforeEach
    void createDummyAddress() {
        dummyAddresses = IntStream
                .range(0, 2)
                .mapToObj(i -> new Address())
                .collect(Collectors.toList());

        // create a addresses with only the mandatory parameter
        dummyAddresses.get(0).setStreetAddress("Via Vinicio 59");
        dummyAddresses.get(0).setCity("Montecassiano");
        dummyAddresses.get(0).setProvince("MC");
        dummyAddresses.get(0).setPostalCode("04017");
        dummyAddresses.get(0).setCountry("Italia");

        // create an addresses with all the possible attributes
        dummyAddresses.get(1).setStreetAddress("Via Tancredi 96");
        dummyAddresses.get(1).setCity("Fonteblanda");
        dummyAddresses.get(1).setProvince("GR");
        dummyAddresses.get(1).setRegion("Toscana");
        dummyAddresses.get(1).setPostalCode("32349");
        dummyAddresses.get(1).setCountry("Italia");
        dummyAddresses.get(1).setBuilding("Appartamento 62 De Santis del friuli");

        // save the addresses in the repository
        addressRepository.saveAll(dummyAddresses);
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
        Address savedAddress = addressRepository.getOne(dummyAddresses.get(0).getId());

        // change attributes
        savedAddress.setRegion("Marche");
        savedAddress.setPostalCode("62100");

        // update the address object
        addressRepository.save(savedAddress);

        Address updatedAddress = addressRepository.findById(savedAddress.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedAddress);
        assertEquals(savedAddress, updatedAddress);
        assertEquals("Marche", updatedAddress.getRegion());
        assertEquals("62100", updatedAddress.getPostalCode());
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
