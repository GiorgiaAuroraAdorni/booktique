package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EmployeeTestFactory implements EntityTestFactory<Employee> {
    @Autowired
    private EntityTestFactory<Address> addressFactory;

    @Override
    public Employee createValidEntity(int idx) {
        var employee = new Employee();
        var address = addressFactory.createValidEntity();

        // mandatory attribute
        employee.setFiscalCode("CGNNMO00T00L00" + idx + "S");
        employee.setName("Nome" + idx);
        employee.setSurname("Cognome" +idx);
        employee.setUsername("UserNo" + idx);
        employee.setPassword("Qwerty1234");

        // other attributes
        employee.setDateOfBirth(LocalDate.now().minusYears(30 + idx));
        employee.setEmail(employee.getName() + employee.getSurname() + "@mail.com");
        employee.setMobilePhone("333000000" + idx);
        employee.setHireDate(LocalDate.now().minusYears(5).plusMonths(idx));
        employee.setAddress(address);

        // the self-association with the supervisor isn't created, so the attribute is initially null

        return employee;
    }

    @Override
    public void updateValidEntity(Employee entity) {

    }
}
