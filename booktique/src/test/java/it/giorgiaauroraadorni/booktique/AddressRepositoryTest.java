package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @BeforeEach
    void createDummyAddress() {
        /*
         * Create a list of addresses entities that will be use in the test
         */
        dummyAddresses = IntStream
                .range(0, 2)
                .mapToObj(i -> new Address())
                .collect(Collectors.toList());

        // create a addresses with only the mandatory parameter
        dummyAddresses.get(0).setSreetAddress("Via Vinicio 59");
        dummyAddresses.get(0).setCity("Macerata");
        dummyAddresses.get(0).setPostalCode("04017");
        dummyAddresses.get(0).setCountry("Italia");

        // create an addresses with all the possible attributes
        dummyAddresses.get(1).setSreetAddress("Via Tancredi 96");
        dummyAddresses.get(1).setCity("Grosseto");
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
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy adresses");
    }

    @Test
    public void testDeleteAddress() {
        /*
         * Delete an entry and check if the book was removed correctly
         */
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
