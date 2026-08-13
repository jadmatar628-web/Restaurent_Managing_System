package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.domain.Employee;
import com.jadmatar.restaurant.domain.EmployeePosition;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void addStoresEmployee() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        Employee employee = new Employee(
                1,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        repository.add(employee);

        Employee storedEmployee = repository.findById(employee.getId());
        assertSame(employee, storedEmployee);
    }

    @Test
    void addRejectsNullEmployee() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.add(null)
        );
    }

    @Test
    void findByIdReturnsNullWhenEmployeeDoesNotExist() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        Employee result = repository.findById(1);

        assertNull(result);
    }

    @Test
    void addRejectsDuplicateEmployeeId() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        Employee firstEmployee = new Employee(
                1,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        Employee secondEmployee = new Employee(
                1,
                "Ali",
                "03123456",
                LocalDate.now(),
                EmployeePosition.CASHIER
        );

        repository.add(firstEmployee);

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.add(secondEmployee)
        );
    }

    @Test
    void findAllRetrievesAllEmployees() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        Employee employee1 = new Employee(
                1, "Jad", "81850192",
                LocalDate.now(), EmployeePosition.MANAGER
        );
        Employee employee2 = new Employee(
                2, "Jad", "81850192",
                LocalDate.now(), EmployeePosition.MANAGER
        );
        Employee employee3 = new Employee(
                3, "Jad", "81850192",
                LocalDate.now(), EmployeePosition.MANAGER
        );

        repository.add(employee1);
        repository.add(employee2);
        repository.add(employee3);

        ArrayList<Employee> employees = repository.findAll();

        assertEquals(3, employees.size());
        assertSame(employee1, employees.get(0));
        assertSame(employee2, employees.get(1));
        assertSame(employee3, employees.get(2));
    }

    @Test
    void findAllReturnsDefensiveCopy() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        Employee employee = new Employee(
                1,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );
        repository.add(employee);

        ArrayList<Employee> returnedEmployees = repository.findAll();
        returnedEmployees.clear();

        assertEquals(0, returnedEmployees.size());
        assertEquals(1, repository.findAll().size());
        assertSame(employee, repository.findById(1));
    }
}