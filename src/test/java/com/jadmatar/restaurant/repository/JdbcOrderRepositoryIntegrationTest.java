package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import com.jadmatar.restaurant.domain.Order;
import com.jadmatar.restaurant.domain.OrderItem;
import com.jadmatar.restaurant.domain.OrderStatus;
import com.jadmatar.restaurant.domain.OrderType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcOrderRepositoryIntegrationTest {

    private JdbcOrderRepository repository;

    private Integer employeeId;
    private Integer menuItemId;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new JdbcOrderRepository();

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            assertTestDatabase(connection);

            employeeId = insertTestEmployee(connection);
            menuItemId = insertTestMenuItem(connection);

            menuItem = new MenuItem(
                    menuItemId,"Integration Test Burger",
                    new BigDecimal("10.00"),
                    MenuCategory.BURGERS,
                    true
            );
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            if (employeeId != null) {
                deleteTestOrders(connection, employeeId);
            }

            if (menuItemId != null) {
                try (PreparedStatement statement =
                             connection.prepareStatement("""
                                     DELETE FROM MENU_ITEM
                                     WHERE MENU_ITEM_ID = ?
                                     """)) {

                    statement.setInt(1, menuItemId);
                    statement.executeUpdate();
                }
            }

            if (employeeId != null) {
                try (PreparedStatement statement =
                             connection.prepareStatement("""
                                     DELETE FROM EMPLOYEE
                                     WHERE EMPLOYEE_ID = ?
                                     """)) {

                    statement.setInt(1, employeeId);
                    statement.executeUpdate();
                }
            }
        }
    }

    @Test
    void addAndFindByIdPersistCompleteOrder() {
        Order order = createPlacedOrder();

        Order savedOrder = repository.add(order);

        assertNotNull(savedOrder.getId());
        assertTrue(savedOrder.getId() > 0);

        assertNotNull(savedOrder.getOrderNumber());
        assertTrue(savedOrder.getOrderNumber() > 0);

        Order loadedOrder =
                repository.findById(savedOrder.getId());

        assertNotNull(loadedOrder);
        assertEquals(savedOrder.getId(), loadedOrder.getId());
        assertEquals(employeeId.intValue(), loadedOrder.getEmployeeId());
        assertEquals(OrderStatus.PLACED, loadedOrder.getStatus());
        assertEquals(OrderType.PICKUP, loadedOrder.getOrderType());
        assertEquals(
                savedOrder.getOrderNumber(),
                loadedOrder.getOrderNumber()
        );

        assertEquals(1, loadedOrder.getItems().size());

        OrderItem loadedItem =
                loadedOrder.getItems().getFirst();

        assertEquals(
                menuItemId,
                loadedItem.getMenuItem().getId()
        );

        assertEquals(2, loadedItem.getQuantity());

        assertEquals(
                0,
                new BigDecimal("10.00")
                        .compareTo(loadedItem.getUnitPrice())
        );

        assertEquals(
                0,
                new BigDecimal("10.00")
                        .compareTo(
                                loadedItem.getDiscountPercentage()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("18.00")
                        .compareTo(loadedOrder.calculateTotal())
        );
    }

    @Test
    void updateOrderStatusPersistsNewStatus() {
        Order order = repository.add(createPlacedOrder());

        order.markPreparing();
        repository.updateOrderStatus(order);

        Order loadedOrder =
                repository.findById(order.getId());

        assertNotNull(loadedOrder);
        assertEquals(
                OrderStatus.PREPARING,
                loadedOrder.getStatus()
        );
    }

    @Test
    void findAllContainsSavedOrder() {
        Order savedOrder =
                repository.add(createPlacedOrder());

        List<Order> orders = repository.findAll();

        boolean found = orders.stream()
                .anyMatch(order ->
                        order.getId().equals(savedOrder.getId())
                );

        assertTrue(found);
    }

    private Order createPlacedOrder() {
        Order order = new Order(
                employeeId,
                OrderType.PICKUP
        );

        OrderItem orderItem = new OrderItem(
                menuItem,
                2,
                new BigDecimal("10.00"),
                new BigDecimal("10.00")
        );

        order.addItem(orderItem);
        order.markPlaced();

        return order;
    }

    private void assertTestDatabase(
            Connection connection
    ) throws SQLException {

        try (Statement statement =
                     connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery("""
                             SELECT DB_NAME()
                             """)) {

            if (!resultSet.next()) {
                fail("Could not determine database name");
            }

            String databaseName = resultSet.getString(1);

            assertEquals(
                    "RestaurantManagementTest",
                    databaseName,
                    "Integration tests must use the test database"
            );
        }
    }

    private int insertTestEmployee(
            Connection connection
    ) throws SQLException {

        String sql = """
                INSERT INTO EMPLOYEE
                (
                    NAME,
                    PHONE_NUMBER,
                    EMPLOYMENT_DATE,
                    POSITION,
                    IS_ACTIVE
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(
                    1,
                    "Order Integration Test Cashier"
            );

            statement.setString(
                    2,
                    "9" + Long.toUnsignedString(
                            System.nanoTime()
                    )
            );

            statement.setDate(
                    3,
                    Date.valueOf(LocalDate.now())
            );

            statement.setString(4, "CASHIER");
            statement.setBoolean(5, true);

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException(
                            "No employee ID generated"
                    );
                }

                return keys.getInt(1);
            }
        }
    }

    private int insertTestMenuItem(
            Connection connection
    ) throws SQLException {

        String sql = """
                INSERT INTO MENU_ITEM
                (
                    ITEM_PRICE,
                    ITEM_NAME,
                    ITEM_CATEGORY,
                    IS_AVAILABLE
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setBigDecimal(
                    1,
                    new BigDecimal("10.00")
            );

            statement.setString(
                    2,
                    "Integration Test Burger "
                            + System.nanoTime()
            );

            statement.setString(3, "BURGERS");
            statement.setBoolean(4, true);

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException(
                            "No menu-item ID generated"
                    );
                }

                return keys.getInt(1);
            }
        }
    }

    private void deleteTestOrders(
            Connection connection,
            int testEmployeeId
    ) throws SQLException {

        String deleteLinesSql = """
                DELETE FROM LINE_ORDER
                WHERE ORDER_ID IN
                (
                    SELECT ORDER_ID
                    FROM SALES_ORDER
                    WHERE EMPLOYEE_ID = ?
                )
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             deleteLinesSql
                     )) {

            statement.setInt(1, testEmployeeId);
            statement.executeUpdate();
        }

        String deleteOrdersSql = """
                DELETE FROM SALES_ORDER
                WHERE EMPLOYEE_ID = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             deleteOrdersSql
                     )) {

            statement.setInt(1, testEmployeeId);
            statement.executeUpdate();
        }
    }
}