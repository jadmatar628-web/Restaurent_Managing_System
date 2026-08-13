package com.jadmatar.restaurant.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeTest {

    @Test
    void constructorWithValidDataCreatesActiveEmployee() {
        // Arrange
        int expectedId = 1;
        String expectedName = "Jad";
        String expectedPhoneNumber = "03123456";
        LocalDate expectedEmploymentDate = LocalDate.of(2025, 1, 1);
        EmployeePosition expectedPosition = EmployeePosition.MANAGER;

        // Act
        Employee employee = new Employee(
                expectedId,
                expectedName,
                expectedPhoneNumber,
                expectedEmploymentDate,
                expectedPosition
        );

        // Assert
        assertEquals(expectedId, employee.getId());
        assertEquals(expectedName, employee.getName());
        assertEquals(expectedPhoneNumber, employee.getPhoneNumber());
        assertEquals(expectedEmploymentDate, employee.getEmploymentDate());
        assertEquals(expectedPosition, employee.getPosition());
        assertTrue(employee.isActive());
    }

    @Test
    void deactivateMakesEmployeeInactive() {
        // Arrange
        Employee employee = new Employee(
                2,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        // Act
        employee.deactivate();

        // Assert
        assertFalse(employee.isActive());
    }

    @Test
    void reactivateMakesEmployeeActiveAgain() {
        // Arrange
        Employee employee = new Employee(
                2,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );
        employee.deactivate();

        // Act
        employee.reactivate();

        // Assert
        assertTrue(employee.isActive());
    }

    @Test
    void constructorRejectsZeroId() {
        // Arrange
        int invalidId = 0;

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        invalidId,
                        "Jad",
                        "81850192",
                        LocalDate.now(),
                        EmployeePosition.MANAGER
                )
        );
    }

    @Test
    void constructorRejectsBlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        1,
                        "   ",
                        "81850192",
                        LocalDate.now(),
                        EmployeePosition.MANAGER
                )
        );
    }

    @Test
    void constructorRejectsBlankPhoneNumber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        1,
                        "Jad",
                        "   ",
                        LocalDate.now(),
                        EmployeePosition.MANAGER
                )
        );
    }

    @Test
    void constructorRejectsNullEmploymentDate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        1,
                        "Jad",
                        "81850192",
                        null,
                        EmployeePosition.MANAGER
                )
        );
    }

    @Test
    void constructorRejectsEmploymentDateMoreThanThirtyDaysAhead() {
        // Arrange
        LocalDate invalidDate = LocalDate.now().plusDays(31);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new Employee(
                        1,
                        "Jad",
                        "81850192",
                        invalidDate,
                        EmployeePosition.MANAGER
                )
        );
    }

    @Test
    void constructorAcceptsEmploymentDateExactlyThirtyDaysAhead() {
        // Arrange
        LocalDate validDate = LocalDate.now().plusDays(30);

        // Act
        Employee employee = new Employee(
                1,
                "Jad",
                "81850192",
                validDate,
                EmployeePosition.MANAGER
        );

        // Assert
        assertEquals(validDate, employee.getEmploymentDate());
    }

    @Test
    void setPhoneNumberUpdatesPhoneNumber() {
        // Arrange
        Employee employee = new Employee(
                2,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );
        String newPhoneNumber = "0123456";

        // Act
        employee.setPhoneNumber(newPhoneNumber);

        // Assert
        assertEquals(newPhoneNumber, employee.getPhoneNumber());
    }

    @Test
    void setPhoneNumberRejectsBlankValue() {
        // Arrange
        Employee employee = new Employee(
                2,
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );
        String blankPhoneNumber = "   ";

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> employee.setPhoneNumber(blankPhoneNumber)
        );
    }
}