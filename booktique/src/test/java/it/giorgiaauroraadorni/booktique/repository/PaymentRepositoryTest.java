package it.giorgiaauroraadorni.booktique.repository;

import it.giorgiaauroraadorni.booktique.model.EntityTestFactory;
import it.giorgiaauroraadorni.booktique.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class PaymentRepositoryTest {
    // Set automatically the attribute to the purchaseRepository instance
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EntityTestFactory<Payment> paymentFactory;

    private List<Payment> dummyPayments;

    @BeforeEach
    void createDummyPayments() {
        // create a list of valid address entities
        dummyPayments = (paymentFactory.createValidEntities(3));
        // save the created entities in the addressRepository
        dummyPayments = paymentRepository.saveAll(dummyPayments);
    }

    /* Test CRUD operations */

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedPayments = paymentRepository.findAll();

        // check if all the payments are correctly added to the repository
        assertTrue(savedPayments.containsAll(dummyPayments), "findAll should fetch all dummy payments");
    }

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreatePayment() {
        List<Payment> savedPayments = new ArrayList<>();

        for (int i = 0; i < dummyPayments.size(); i++) {
            // check if the payments id are correctly automatic generated
            assertNotNull(paymentRepository.getOne(dummyPayments.get(i).getId()));
            savedPayments.add(paymentRepository.getOne(dummyPayments.get(i).getId()));

            // check if the payments contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedPayments.get(i).getCreatedAt());
            assertNotNull(savedPayments.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedPayments.get(i).getCardholderName(), dummyPayments.get(i).getCardholderName());
            assertEquals(savedPayments.get(i).getCardNumber(), dummyPayments.get(i).getCardNumber());
            assertEquals(savedPayments.get(i).getCVC(), dummyPayments.get(i).getCVC());
            assertEquals(savedPayments.get(i).getExpireDate(), dummyPayments.get(i).getExpireDate());
            assertEquals(savedPayments.get(i).getPaymentDate(), dummyPayments.get(i).getPaymentDate());
            assertEquals(savedPayments.get(i).getId(), dummyPayments.get(i).getId());
        }
    }

    /**
     * Update one entry editing different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdatePayment() {
        // get a payment from the repository
        Payment savedPayment = paymentRepository.findById(dummyPayments.get(0).getId()).get();

        // change some attributes
        var oldPaymentDate = savedPayment.getPaymentDate();
        savedPayment.setPaymentDate(LocalDate.now().minusDays(1));
        var oldCVC = savedPayment.getCVC();
        savedPayment.setCVC("120");

        // update the payment object
        savedPayment = paymentRepository.save(savedPayment);
        Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedPayment);
        assertEquals(savedPayment, updatedPayment);
        assertNotEquals(oldPaymentDate, updatedPayment.getPaymentDate());
        assertNotEquals(oldCVC, updatedPayment.getCVC());
        assertEquals(LocalDate.now().minusDays(1), updatedPayment.getPaymentDate());
        assertEquals("120", updatedPayment.getCVC());
    }

    /**
     * Throws an exception when attempting to create a payment without mandatory attributes
     */
    @Test
    public void testIllegalCreatePayment() {
        Payment invalidPayment = new Payment();

        assertThrows(DataIntegrityViolationException.class, () -> {
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase with illegal card number format type
     */
    @Test
    public void testIllegalCardNumberFormat() {
        Payment invalidPayment = new Payment();

        invalidPayment.setCardholderName("Kaitlin Mitchell");
        invalidPayment.setExpireDate(LocalDate.of(2025, 12, 1));
        invalidPayment.setCVC("123");

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCardNumber("4643r17337747076");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase with illegal cardholder name format type
     */
    @Test
    public void testIllegalCardholderNameFormat() {
        Payment invalidPayment = new Payment();

        invalidPayment.setCardNumber("4643017337747076");
        invalidPayment.setExpireDate(LocalDate.of(2025, 12, 1));
        invalidPayment.setCVC("123");

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCardholderName("Kaitlin Mitchell 4");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create a purchase with illegal cvc format type
     */
    @Test
    public void testIllegalCVCFormat() {
        Payment invalidPayment = new Payment();

        invalidPayment.setCardNumber("4643017337747076");
        invalidPayment.setExpireDate(LocalDate.of(2025, 12, 1));
        invalidPayment.setCardholderName("Kaitlin Mitchell");

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCVC("0O0");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Throws an exception when attempting to create or update a payment with illegal size for the attributes
     */
    @Test
    public void testIllegalSizeAttributes() {
        Payment invalidPayment = new Payment();

        invalidPayment.setCardNumber("4643017337747076");
        invalidPayment.setExpireDate(LocalDate.of(2025, 12, 1));
        invalidPayment.setCardholderName("Kaitlin Mitchell");
        invalidPayment.setCVC("123");

        paymentRepository.save(invalidPayment);

        assertThrows(ConstraintViolationException.class, () -> {
            invalidPayment.setCardNumber("46430173377470767");
            paymentRepository.saveAndFlush(invalidPayment);
        });

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidPayment.setCardNumber("464301733774707");
            paymentRepository.saveAndFlush(invalidPayment);
        });

        //FIXME
        assertThrows(JpaSystemException.class, () -> {
            invalidPayment.setCVC("12345356");
            paymentRepository.saveAndFlush(invalidPayment);
        });

        //FIXME
        assertThrows(JpaSystemException.class, () -> {
            invalidPayment.setCVC("1");
            paymentRepository.saveAndFlush(invalidPayment);
        });
    }

    /**
     * Delete an entry and check if the payment was removed correctly
     */
    @Test
    public void testDeleteCustomer() {
        // get a payment from the repository
        Payment savedPayment = paymentRepository.findById(dummyPayments.get(0).getId()).get();

        // delete the payment object
        paymentRepository.delete(savedPayment);

        // check that the payment has been deleted correctly
        assertEquals(paymentRepository.findById(dummyPayments.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        paymentRepository.deleteAll();
        assertTrue(paymentRepository.findAll().isEmpty());
    }

    /* Test search operations */

    @Test
    public void testFindById() {
        // check the correct reading of the payment via findById
        var foundPayment = paymentRepository.findById(dummyPayments.get(0).getId());

        assertEquals(foundPayment.get(), dummyPayments.get(0));
        assertEquals(foundPayment.get().getId(), dummyPayments.get(0).getId());

        // try to search for payments by a not existing id
        var notFoundPayment = paymentRepository.findById(999L);

        assertTrue(notFoundPayment.isEmpty());
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
