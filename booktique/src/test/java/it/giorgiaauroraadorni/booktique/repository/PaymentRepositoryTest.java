package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.EntityFactory;
import it.giorgiaauroraadorni.booktique.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class PaymentRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    // Set automatically the attribute to the purchaseRepository instance
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EntityFactory<Payment> paymentFactory;

    private List<Payment> dummyPayments;

    @BeforeEach
    void createDummyPayments() {
        // create a list of valid address entities and save the created entities in the addressRepository
        dummyPayments = (paymentFactory.createValidEntities(2));
        dummyPayments = paymentRepository.saveAll(dummyPayments);
    }

    @Test
    void repositoryLoads() {}

    /* Test CRUD operations */

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct.
     */
    @Test
    public void testCreatePayment() {
        for (int i = 0; i < dummyPayments.size(); i++) {
            // check if the repository is populated
            assertNotEquals(0, paymentRepository.count());
            assertTrue(paymentRepository.existsById(dummyPayments.get(i).getId()));

            // check if the payments contain the createdAt and updatedAt annotation that are automatically populate,
            // and check if the payments id are correctly automatic generated
            assertNotNull(dummyPayments.get(i).getCreatedAt());
            assertNotNull(dummyPayments.get(i).getUpdatedAt());
            assertNotNull(dummyPayments.get(i).getId());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals("Nome Cognome", dummyPayments.get(i).getCardholderName());
            assertEquals("0000000000000000", dummyPayments.get(i).getCardNumber());
            assertEquals("000", dummyPayments.get(i).getCVC());
            assertEquals(LocalDate.now().plusYears(5), dummyPayments.get(i).getExpireDate());
            assertEquals(LocalDate.now(), dummyPayments.get(i).getPaymentDate());
        }
    }

    /**
     * Throws an exception when attempting to create a payment without mandatory attributes.
     */
    @Test
    public void testIllegalCreatePayment() {
        Payment invalidPayment = new Payment();

        assertThrows(DataIntegrityViolationException.class, () -> {
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    @Test
    public void testSave() {
        Payment payment = paymentFactory.createValidEntity(2);

        assertDoesNotThrow(() -> paymentRepository.save(payment));
    }

    /**
     * Throws an exception when attempting to create a purchase with illegal card number format type.
     */
    @Test
    public void testIllegalCardNumber() {
        Payment invalidPayment = paymentFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCardNumber("4643r17337747076");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase with illegal cardholder name format type.
     */
    @Test
    public void testIllegalCardholderName() {
        Payment invalidPayment = paymentFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCardholderName("Nome Cognome 4");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase with illegal cvc format type.
     */
    @Test
    public void testIllegalCVC() {
        Payment invalidPayment = paymentFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCVC("0O0");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create or update a payment with illegal size for the Card Number attribute.
     */
    @Test
    public void testIllegalCardNumberSize() {
        Payment invalidPayment = paymentFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCardNumber("46430173377470767");
            paymentRepository.saveAndFlush(invalidPayment);
        });

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidPayment.setCardNumber("464301733774707");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create or update a payment with illegal size for the CVC attribute.
     */
    @Test
    public void testIllegalCVCSize() {
        Payment invalidPayment = paymentFactory.createValidEntity(2);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCVC("12345356");
            paymentRepository.saveAndFlush(invalidPayment);
        });

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidPayment.setCVC("1");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Update one entry editing different attributes and check if the fields are changed correctly.
     */
    @Test
    public void testUpdatePayment() {
        // get a payment from the repository, change some attributes and update the payment object
        Payment savedPayment = dummyPayments.get(0);

        savedPayment.setPaymentDate(LocalDate.now().minusDays(1));
        savedPayment.setCVC("120");
        savedPayment.setCardNumber("0000000000000001");
        savedPayment.setCardholderName("Nuovonome Nuovocognome");

        savedPayment = paymentRepository.save(savedPayment);

        // clear the memory in order to get a new istance of the saved payment from the db
        paymentRepository.flush();
        entityManager.clear();

        // check that all the attributes have been updated correctly and contain the expected value
        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).get();

        assertTrue(paymentRepository.existsById(updatedPayment.getId()));
        assertTrue(updatedPayment.equalsByAttributes(savedPayment));
    }

    /**
     * Delete an entry and check if the payment was removed correctly.
     */
    @Test
    public void testDeletePayment() {
        // get a payment from the repository and delete it
        Payment savedPayment = dummyPayments.get(0);
        paymentRepository.delete(savedPayment);

        // check that the payment has been deleted correctly
        assertFalse(paymentRepository.existsById(savedPayment.getId()));
    }

    /**
     * Delete all the entries verifying that the operation has been carried out correctly.
     */
    @Test
    public void testDeleteAllPayments() {
        paymentRepository.deleteAll();
        assertTrue(paymentRepository.findAll().isEmpty());
    }

    /* Test search operations */

    @Test
    void repositoryFindAll() {
        var savedPayments = paymentRepository.findAll();

        // check if all the payments are correctly added to the repository
        assertTrue(savedPayments.containsAll(dummyPayments), "findAll should fetch all dummy payments");
    }

    @Test
    public void testFindById() {
        // check the correct reading of the payment via findById
        var foundPayment = paymentRepository.findById(dummyPayments.get(0).getId());

        assertEquals(foundPayment.get(), dummyPayments.get(0));
        assertEquals(foundPayment.get().getId(), dummyPayments.get(0).getId());
    }

    @Test
    public void testFindByCardholderName() {
        // check the correct reading of all the payments via findByCardholderName
        var foundPayments = paymentRepository.findByCardholderName(dummyPayments.get(0).getCardholderName());

        assertTrue(foundPayments.contains(dummyPayments.get(0)));
        for (Payment p: foundPayments) {
            assertEquals(p.getCardholderName(), dummyPayments.get(0).getCardholderName());
        }

        // try to search for payments by a not existing cardholder name
        var notFoundPayment = paymentRepository.findByCardholderName("Titolare Insesistente");

        assertTrue(notFoundPayment.isEmpty());
    }

    @Test
    public void testFindByPaymentDate() {
        // check the correct reading of all the payments via findByPaymentDate
        var foundPayments = paymentRepository.findByPaymentDate(dummyPayments.get(1).getPaymentDate());

        assertTrue(foundPayments.contains(dummyPayments.get(1)));
        for (Payment p: foundPayments) {
            assertEquals(p.getPaymentDate(), dummyPayments.get(1).getPaymentDate());
        }

        // try to search for payments by a not existing payment date
        var notFoundPayment = paymentRepository.findByPaymentDate(LocalDate.now().minusYears(2));

        assertTrue(notFoundPayment.isEmpty());
    }
}
