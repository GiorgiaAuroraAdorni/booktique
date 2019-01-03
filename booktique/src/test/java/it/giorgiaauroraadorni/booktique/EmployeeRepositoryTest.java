package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Employee;
import it.giorgiaauroraadorni.booktique.repository.AddressRepository;
import it.giorgiaauroraadorni.booktique.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmployeeRepositoryTest {

    // Set automatically the attribute to the EmployeeRepository instance
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AddressRepository addressRepository;

    private List<Employee> dummyEmployees;
    private List<Address> dummyAddresses;

    /**
     * Create a list of addresses entities that will be use in the test
     */
    private void createDummyAddress() {
        dummyAddresses = IntStream
                .range(0, 3)
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

        // create an addresses with all the possible attributes
        dummyAddresses.get(2).setStreetAddress("Via Leone 1");
        dummyAddresses.get(2).setCity("Milano");
        dummyAddresses.get(2).setProvince("MI");
        dummyAddresses.get(2).setPostalCode("41845");
        dummyAddresses.get(2).setCountry("Italia");
        dummyAddresses.get(2).setBuilding("Piano 8");

        // save the addresses in the repository
        dummyAddresses = addressRepository.saveAll(dummyAddresses);
    }

    /**
     * Create a list of employees entities that will be use in the test
     */
    private void createDummyEmployee() {
        dummyEmployees = IntStream
                .range(0, 3)
                .mapToObj(i -> new Employee())
                .collect(Collectors.toList());

        // create a employees with only the mandatory parameter (inherited from person)
        dummyEmployees.get(0).setFiscalCode("GRGBVR75C13G224W");
        dummyEmployees.get(0).setName("Beverley");
        dummyEmployees.get(0).setSurname("Gregory");
        dummyEmployees.get(0).setUsername("BeverleyGregory75");
        dummyEmployees.get(0).setPassword("yJmKKSjRJX4HZXrvxjBs");

        // create a employees with all the person attributes
        dummyEmployees.get(1).setFiscalCode("STNPTR70A11C933C");
        dummyEmployees.get(1).setName("Peter");
        dummyEmployees.get(1).setSurname("Stone");
        dummyEmployees.get(1).setUsername("PeterStone70");
        dummyEmployees.get(1).setPassword("XxzNh9jMkfWaHhzG2YVG");
        dummyEmployees.get(1).setDateOfBirth(LocalDate.of(1970, 11, 3));
        dummyEmployees.get(1).setEmail("peter.stone40@example.com");
        dummyEmployees.get(1).setMobilePhone("+393733733730");

        // create a employees with many attributes
        dummyEmployees.get(2).setFiscalCode("STWJSP77T12A271K");
        dummyEmployees.get(2).setName("Josephine");
        dummyEmployees.get(2).setSurname("Stewart");
        dummyEmployees.get(2).setAddress(dummyAddresses.get(0));
        dummyEmployees.get(2).setDateOfBirth(LocalDate.of(1977, 1, 10));
        dummyEmployees.get(2).setEmail("josephine.stewart85@mail.com");
        dummyEmployees.get(2).setMobilePhone("3263261001");
        dummyEmployees.get(2).setUsername("JosephineStewart");
        dummyEmployees.get(2).setPassword("QQP6XH67PRNV42UZYPEM");
        dummyEmployees.get(2).setSupervisor(dummyEmployees.get(1));
        dummyEmployees.get(2).setHireDate(LocalDate.of(1997, 2, 1));

        // save the employees in the repository
        dummyEmployees = employeeRepository.saveAll(dummyEmployees);
    }

    @BeforeEach
    void createDummyEntities() {
        createDummyAddress();
        createDummyEmployee();
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedEmployees = employeeRepository.findAll();
        var savedAddresses = addressRepository.findAll();

        // check if all the employees are correctly added to the repository
        assertTrue(savedEmployees.containsAll(dummyEmployees), "findAll should fetch all dummy employees");
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy addresses");
    }

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateEmployee() {
        List<Employee> savedEmployees = new ArrayList<>();

        for (int i = 0; i < dummyEmployees.size(); i++) {
            // check if the employees id are correctly automatic generated
            assertNotNull(employeeRepository.getOne(dummyEmployees.get(i).getId()));
            savedEmployees.add(employeeRepository.getOne(dummyEmployees.get(i).getId()));

            // check if the employees contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedEmployees.get(i).getCreatedAt());
            assertNotNull(savedEmployees.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedEmployees.get(i).getFiscalCode(), dummyEmployees.get(i).getFiscalCode());
            assertEquals(savedEmployees.get(i).getName(), dummyEmployees.get(i).getName());
            assertEquals(savedEmployees.get(i).getSurname(), dummyEmployees.get(i).getSurname());
            assertEquals(savedEmployees.get(i).getDateOfBirth(), dummyEmployees.get(i).getDateOfBirth());
            assertEquals(savedEmployees.get(i).getEmail(), dummyEmployees.get(i).getEmail());
            assertEquals(savedEmployees.get(i).getMobilePhone(), dummyEmployees.get(i).getMobilePhone());
            assertEquals(savedEmployees.get(i).getUsername(), dummyEmployees.get(i).getUsername());
            assertEquals(savedEmployees.get(i).getPassword(), dummyEmployees.get(i).getPassword());
            assertEquals(savedEmployees.get(i).getAddress(), dummyEmployees.get(i).getAddress());
            assertEquals(savedEmployees.get(i).getSupervisor(), dummyEmployees.get(i).getSupervisor());
            assertEquals(savedEmployees.get(i).getHireDate(), dummyEmployees.get(i).getHireDate());
            assertEquals(savedEmployees.get(i).getId(), dummyEmployees.get(i).getId());
        }
    }
}
