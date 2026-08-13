package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.domain.Employee;
import com.jadmatar.restaurant.domain.EmployeePosition;
import com.jadmatar.restaurant.repository.InMemoryEmployeeRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Test
    void constructorRejectsNullRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new EmployeeService(null)
        );
    }

    @Test
    void createEmployeeCreatesAndStoresEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee createdEmployee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        // Assert
        assertEquals(1, createdEmployee.getId());
        assertEquals("Jad", createdEmployee.getName());
        assertEquals("81850192", createdEmployee.getPhoneNumber());
        assertEquals(EmployeePosition.MANAGER, createdEmployee.getPosition());
        assertTrue(createdEmployee.isActive());
        assertSame(createdEmployee, repository.findById(1));
    }

    @Test
    void createEmployeeIncrementsIdForEachEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee firstEmployee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        Employee secondEmployee = service.createEmployee(
                "Ali",
                "03123456",
                LocalDate.now(),
                EmployeePosition.CASHIER
        );

        // Assert
        assertEquals(1, firstEmployee.getId());
        assertEquals(2, secondEmployee.getId());
    }

    @Test
    void constructorCalculatesNextIdFromExistingEmployees() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        Employee employee1 = new Employee(
                3,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        Employee employee2 = new Employee(
                8,
                "Ali",
                "03123456",
                LocalDate.now(),
                EmployeePosition.CASHIER
        );

        repository.add(employee1);
        repository.add(employee2);

        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee createdEmployee = service.createEmployee(
                "Sara",
                "70123456",
                LocalDate.now(),
                EmployeePosition.CASHIER
        );

        // Assert
        assertEquals(9, createdEmployee.getId());
    }

    @Test
    void deactivateEmployeeDeactivatesExistingEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        Employee employee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        // Act
        Employee result = service.deactivateEmployee(employee.getId());

        // Assert
        assertSame(employee, result);
        assertFalse(employee.isActive());
    }

    @Test
    void deactivateEmployeeReturnsNullWhenEmployeeDoesNotExist() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee result = service.deactivateEmployee(999);

        // Assert
        assertNull(result);
    }

    @Test
    void reactivateEmployeeReactivatesExistingEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        Employee employee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        employee.deactivate();

        // Act
        Employee result = service.reactivateEmployee(employee.getId());

        // Assert
        assertSame(employee, result);
        assertTrue(employee.isActive());
    }

    @Test
    void reactivateEmployeeReturnsNullWhenEmployeeDoesNotExist() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee result = service.reactivateEmployee(999);

        // Assert
        assertNull(result);
    }

    @Test
    void findEmployeeByIdReturnsExistingEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        Employee employee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        // Act
        Employee result = service.findEmployeeById(employee.getId());

        // Assert
        assertSame(employee, result);
    }

    @Test
    void findEmployeeByIdReturnsNullWhenEmployeeDoesNotExist() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee result = service.findEmployeeById(999);

        // Assert
        assertNull(result);
    }

    @Test
    void getAllEmployeesReturnsAllStoredEmployees() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        Employee employee1 = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        Employee employee2 = service.createEmployee(
                "Ali",
                "03123456",
                LocalDate.now(),
                EmployeePosition.CASHIER
        );

        // Act
        ArrayList<Employee> employees = service.getAllEmployees();

        // Assert
        assertEquals(2, employees.size());
        assertSame(employee1, employees.get(0));
        assertSame(employee2, employees.get(1));
    }

    @Test
    void updateEmployeeNameUpdatesExistingEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        Employee employee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        // Act
        Employee result = service.updateEmployeeName(
                employee.getId(),
                "Jad Matar"
        );

        // Assert
        assertSame(employee, result);
        assertEquals("Jad Matar", employee.getName());
    }

    @Test
    void updateEmployeeNameReturnsNullWhenEmployeeDoesNotExist() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee result = service.updateEmployeeName(999, "Jad");

        // Assert
        assertNull(result);
    }

    @Test
    void updatePhoneNumberUpdatesExistingEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        Employee employee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        // Act
        Employee result = service.updatePhoneNumber(
                employee.getId(),
                "70123456"
        );

        // Assert
        assertSame(employee, result);
        assertEquals("70123456", employee.getPhoneNumber());
    }

    @Test
    void updatePhoneNumberReturnsNullWhenEmployeeDoesNotExist() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee result = service.updatePhoneNumber(999, "70123456");

        // Assert
        assertNull(result);
    }

    @Test
    void updateEmployeePositionUpdatesExistingEmployee() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        Employee employee = service.createEmployee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        // Act
        Employee result = service.updateEmployeePosition(
                employee.getId(),
                EmployeePosition.CASHIER
        );

        // Assert
        assertSame(employee, result);
        assertEquals(EmployeePosition.CASHIER, employee.getPosition());
    }

    @Test
    void updateEmployeePositionReturnsNullWhenEmployeeDoesNotExist() {
        // Arrange
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();
        EmployeeService service = new EmployeeService(repository);

        // Act
        Employee result = service.updateEmployeePosition(
                999,
                EmployeePosition.CASHIER
        );

        // Assert
        assertNull(result);
    }
}