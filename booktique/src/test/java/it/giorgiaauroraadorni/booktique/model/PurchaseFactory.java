package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class PurchaseFactory implements EntityFactory<Purchase> {
    @Autowired
    private EntityFactory<Employee> employeeFactory;

    @Autowired
    private EntityFactory<Customer> customerFactory;

    @Autowired
    private EntityFactory<Item> itemFactory;

    @Autowired
    private EntityFactory<Payment> paymentFactory;

    @Override
    public Purchase createValidEntity(int idx) {
        var purchase = new Purchase();
        var employee = employeeFactory.createValidEntity(idx);
        var customer = customerFactory.createValidEntity(idx);
        var item = itemFactory.createValidEntity(idx);
        var payment = paymentFactory.createValidEntity(idx);

        // mandatory attribute
        purchase.setOrderDate(LocalDate.now().minusDays(idx));

        // mandatory associations
        purchase.setCustomer(customer);
        purchase.setEmployee(employee);
        Set<Item> items = Set.of(item);
        purchase.setItems(items);
        purchase.setPaymentDetails(payment);

        // other attributes
        purchase.setStatus(Purchase.Status.IN_PRODUCTION);

        return purchase;
    }
}
