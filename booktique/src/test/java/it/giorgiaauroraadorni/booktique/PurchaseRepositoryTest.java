package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PurchaseRepositoryTest {
    // Set automatically the attribute to the purchaseRepository instance
    @Autowired
    private PurchaseRepository purchaseRepository;

}
