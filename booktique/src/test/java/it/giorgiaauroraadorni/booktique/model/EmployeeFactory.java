package it.giorgiaauroraadorni.booktique.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EmployeeFactory implements EntityFactory<Employee> {
    @Autowired
    private EntityFactory<Address> addressFactory;

    @Override
    public Employee createValidEntity(int idx) {
        var employee = new Employee();
        var address = addressFactory.createValidEntity();

        // mandatory attributes
        employee.setFiscalCode("CGNNMO00T00L00" + idx + "E");
        employee.setName("Nome" + idx);
        employee.setSurname("Cognome" +idx);
        employee.setUsername("EUserNo" + idx);
        employee.setPassword("Qwerty1234");

        // other attributes
        employee.setDateOfBirth(LocalDate.now().minusYears(30 + idx));
        employee.setEmail(employee.getName() + employee.getSurname() + "@employee-mail.com");
        employee.setMobilePhone("333000000" + idx);
        employee.setHireDate(LocalDate.now().minusYears(5).plusMonths(idx));
        employee.setAddress(address);

        // the self-association with the supervisor isn't created, so the attribute is initially null

        return employee;
    }
}
