package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.Employee;
import com.jadmatar.restaurant.domain.EmployeePosition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class JdbcEmployeeRepositoryIntegrationTest {

    private static final AtomicInteger PHONE_COUNTER =
            new AtomicInteger();

    private JdbcEmployeeRepository repository;

    private final List<Integer> insertedEmployeeIds =
            new ArrayList<>();

    @BeforeAll
    static void ensureTestDatabase() throws SQLException {
        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            String databaseName = connection.getCatalog();

            assertNotNull(databaseName);

            assertTrue(
                    databaseName.toLowerCase().contains("test"),
                    "JDBC integration tests must use a test database. " +
                            "Current database: " + databaseName
            );
        }
    }

    @BeforeEach
    void setUp() {
        repository = new JdbcEmployeeRepository();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        String sql = """
                DELETE FROM EMPLOYEE
                WHERE EMPLOYEE_ID = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            for (Integer id : insertedEmployeeIds) {
                statement.setInt(1, id);
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    @Test
    void addAssignsGeneratedIdAndPersistsEmployee() {
        Employee employee = createTestEmployee(
                "Jad",
                EmployeePosition.MANAGER
        );

        repository.add(employee);
        rememberForCleanup(employee);

        assertNotNull(employee.getId());
        assertTrue(employee.getId() > 0);

        Employee stored =
                repository.findById(employee.getId());

        assertNotNull(stored);
        assertEquals(employee.getId(), stored.getId());
        assertEquals("Jad", stored.getName());
        assertEquals(
                employee.getPhoneNumber(),
                stored.getPhoneNumber()
        );
        assertEquals(
                employee.getEmploymentDate(),
                stored.getEmploymentDate()
        );
        assertEquals(
                EmployeePosition.MANAGER,
                stored.getPosition()
        );
        assertTrue(stored.isActive());
    }

    @Test
    void findByIdReturnsCorrectEmployee() {
        Employee employee = createTestEmployee(
                "Sarah",
                EmployeePosition.CASHIER
        );

        repository.add(employee);
        rememberForCleanup(employee);

        Employee found =
                repository.findById(employee.getId());

        assertNotNull(found);
        assertEquals(employee.getId(), found.getId());
        assertEquals(employee.getName(), found.getName());
        assertEquals(
                employee.getPhoneNumber(),
                found.getPhoneNumber()
        );
        assertEquals(
                employee.getEmploymentDate(),
                found.getEmploymentDate()
        );
        assertEquals(
                employee.getPosition(),
                found.getPosition()
        );
        assertEquals(
                employee.isActive(),
                found.isActive()
        );
    }

    @Test
    void findByIdReturnsNullWhenEmployeeDoesNotExist() {
        Employee found =
                repository.findById(2_000_000_000);

        assertNull(found);
    }

    @Test
    void findAllContainsStoredEmployees() {
        Employee first = createTestEmployee(
                "First Employee",
                EmployeePosition.DRIVER
        );

        Employee second = createTestEmployee(
                "Second Employee",
                EmployeePosition.DISPATCHER
        );

        repository.add(first);
        rememberForCleanup(first);

        repository.add(second);
        rememberForCleanup(second);

        ArrayList<Employee> employees =
                repository.findAll();

        assertNotNull(employees);

        assertTrue(
                employees.stream().anyMatch(
                        employee ->
                                employee.getId().equals(first.getId())
                )
        );

        assertTrue(
                employees.stream().anyMatch(
                        employee ->
                                employee.getId().equals(second.getId())
                )
        );
    }

    @Test
    void updatePersistsAllMutableFields() {
        Employee employee = createTestEmployee(
                "Original Name",
                EmployeePosition.CASHIER
        );

        repository.add(employee);
        rememberForCleanup(employee);

        String updatedPhone = uniquePhoneNumber();

        employee.setName("Updated Name");
        employee.setPhoneNumber(updatedPhone);
        employee.setPosition(EmployeePosition.MANAGER);
        employee.deactivate();

        repository.update(employee);

        Employee updated =
                repository.findById(employee.getId());

        assertNotNull(updated);
        assertEquals("Updated Name", updated.getName());
        assertEquals(
                updatedPhone,
                updated.getPhoneNumber()
        );
        assertEquals(
                EmployeePosition.MANAGER,
                updated.getPosition()
        );
        assertFalse(updated.isActive());
    }

    @Test
    void updateCanPersistReactivation() {
        Employee employee = createTestEmployee(
                "Inactive Employee",
                EmployeePosition.CLEANER
        );

        repository.add(employee);
        rememberForCleanup(employee);

        employee.deactivate();
        repository.update(employee);

        employee.reactivate();
        repository.update(employee);

        Employee updated =
                repository.findById(employee.getId());

        assertNotNull(updated);
        assertTrue(updated.isActive());
    }

    @Test
    void addRejectsNullEmployee() {
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.add(null)
        );
    }

    @Test
    void updateRejectsEmployeeWithoutId() {
        Employee employee = createTestEmployee(
                "Unsaved Employee",
                EmployeePosition.CASHIER
        );

        assertNull(employee.getId());

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.update(employee)
        );
    }

    @Test
    void duplicatePhoneNumberIsRejected() {
        String phoneNumber = uniquePhoneNumber();

        Employee first = new Employee(
                "First Employee",
                phoneNumber,
                LocalDate.now(),
                EmployeePosition.CASHIER
        );

        Employee second = new Employee(
                "Second Employee",
                phoneNumber,
                LocalDate.now(),
                EmployeePosition.DRIVER
        );

        repository.add(first);
        rememberForCleanup(first);

        assertThrows(
                RuntimeException.class,
                () -> repository.add(second)
        );
    }

    private Employee createTestEmployee(
            String name,
            EmployeePosition position
    ) {
        return new Employee(
                name,
                uniquePhoneNumber(),
                LocalDate.now(),
                position
        );
    }

    private String uniquePhoneNumber() {
        return Long.toString(System.currentTimeMillis())
                + PHONE_COUNTER.incrementAndGet();
    }

    private void rememberForCleanup(Employee employee) {
        if (employee.getId() != null) {
            insertedEmployeeIds.add(employee.getId());
        }
    }
}