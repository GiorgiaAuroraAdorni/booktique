package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class PurchaseTestFactory implements EntityTestFactory<Purchase> {
    @Autowired
    private EntityTestFactory<Employee> employeeFactory;

    @Autowired
    private EntityTestFactory<Customer> customerFactory;

    @Autowired
    private EntityTestFactory<Item> itemFactory;

    @Autowired
    private EntityTestFactory<Payment> paymentFactory;

    @Override
    public Purchase createValidEntity(int idx) {
        var purchase = new Purchase();
        var employee = employeeFactory.createValidEntity(idx);
        var customer = customerFactory.createValidEntity(idx + 2);
        var item = itemFactory.createValidEntity(idx);
        var payment = paymentFactory.createValidEntity(idx);

        // mandatory attribute
        purchase.setCustomer(customer);
        purchase.setEmployee(employee);
        Set<Item> items = Set.of(item); // FIXME
        purchase.setItems(items);
        purchase.setOrderDate(LocalDate.now().minusDays(idx));
        purchase.setPaymentDetails(payment);

        // other attributes
        purchase.setStatus(Purchase.Status.inProduction);

        return purchase;
    }

    @Override
    public void updateValidEntity(Purchase entity) {

    }
}
