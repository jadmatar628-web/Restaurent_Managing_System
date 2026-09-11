package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.*;
import com.jadmatar.restaurant.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private int employeeId;
    private int menuItemId;

    private int createTestEmployeeAndReturnId() {
        EmployeeRepository employeeRepository =
                new JdbcEmployeeRepository();

        Employee employee = new Employee(
                "Jad",
                "81850192",
                LocalDate.now(),
                EmployeePosition.MANAGER
        );

        employeeRepository.add(employee);

        return employee.getId();
    }

    private int createTestMenuItemAndReturnId() {
        MenuItemRepository menuItemRepository =
                new JdbcMenuItemRepository();

        MenuItem menuItem = new MenuItem(
                BigDecimal.ONE,
                "JAD",
                MenuCategory.BURGERS
        );

        menuItemRepository.add(menuItem);

        return menuItem.getId();
    }

    private Order createValidOpenOrder() {
        Order order =
                new Order(employeeId, OrderType.FAST_ORDER);

        MenuItem menuItem = new MenuItem(
                menuItemId,
                BigDecimal.ONE,
                "JAD",
                MenuCategory.BURGERS
        );

        OrderItem orderItem = new OrderItem(
                menuItem,
                1,
                BigDecimal.ONE,
                BigDecimal.ZERO
        );

        order.addItem(orderItem);

        return order;
    }

    @BeforeEach
    void cleanDatabase() throws SQLException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate("DELETE FROM LINE_ORDER");
            statement.executeUpdate("DELETE FROM SALES_ORDER");
            statement.executeUpdate("DELETE FROM DAILY_ORDER_COUNTER");

            statement.executeUpdate("DELETE FROM EMPLOYEE");
            statement.executeUpdate("DELETE FROM MENU_ITEM");
        }

        employeeId = createTestEmployeeAndReturnId();
        menuItemId = createTestMenuItemAndReturnId();
    }

    @Test
    public void findOrderByIdReturnsExistingOrder() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order = createValidOpenOrder();

        orderService.placeOrder(order);

        int expectedOrderId = order.getId();

        // Act
        Order found =
                orderService.findOrderById(expectedOrderId);

        // Assert
        assertNotNull(found);
        assertEquals(expectedOrderId, found.getId());
    }

    @Test
    public void findOrderByIdWithNonExistentOrderThrowsIllegalStateException() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> orderService.findOrderById(999999)
        );
    }

    @Test
    public void validOpenOrderBecomesPlacedAfterPlacement() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order = createValidOpenOrder();

        // Act
        orderService.placeOrder(order);

        // Assert
        assertEquals(
                OrderStatus.PLACED,
                order.getStatus()
        );

        assertNotNull(order.getId());
    }

    @Test
    public void nullOrderThrowsIllegalArgumentException() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        // Act + Assert
        assertThrowsExactly(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(null)
        );
    }

    @Test
    public void emptyOpenOrderThrowsIllegalStateExceptionWhenPlaced() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order =
                new Order(employeeId, OrderType.FAST_ORDER);

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> orderService.placeOrder(order)
        );
    }

    @Test
    public void alreadyPlacedOrderThrowsIllegalStateExceptionWhenPlacedAgain() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order = createValidOpenOrder();

        orderService.placeOrder(order);

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> orderService.placeOrder(order)
        );
    }

    @Test
    public void findAllReturnsStoredOrders() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order = createValidOpenOrder();

        orderService.placeOrder(order);

        int expectedOrderId = order.getId();

        // Act
        List<Order> orders =
                orderService.findAll();

        // Assert
        assertEquals(1, orders.size());

        assertEquals(
                expectedOrderId,
                orders.getFirst().getId()
        );
    }

    @Test
    public void completeOrderChangesStatusToCompleted() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order = createValidOpenOrder();

        order.changeOrderType(OrderType.PICKUP);

        orderService.placeOrder(order);

        /*
         * These transitions must also be persisted,
         * because completeOrder(id) reloads the Order
         * from the database.
         */

        order.markPreparing();
        repo.updateOrderStatus(order);

        order.markReady();
        repo.updateOrderStatus(order);

        order.markPickedUp();
        repo.updateOrderStatus(order);

        // Act
        orderService.completeOrder(order.getId());

        // Assert
        Order found =
                orderService.findOrderById(order.getId());

        assertEquals(
                OrderStatus.COMPLETED,
                found.getStatus()
        );
    }

    @Test
    public void completeOrderWithNonExistingIdThrowsIllegalStateException() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        // Act + Assert
        assertThrows(
                IllegalStateException.class,
                () -> orderService.completeOrder(999999)
        );
    }

    @Test
    public void cancelPlacedOrderChangesStatusToVoided() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order = createValidOpenOrder();

        orderService.placeOrder(order);

        int orderId = order.getId();

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        Order found =
                orderService.findOrderById(orderId);

        assertEquals(
                OrderStatus.VOIDED,
                found.getStatus()
        );
    }

    @Test
    public void cancelCompletedOrderChangesStatusToRefunded() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        Order order = createValidOpenOrder();

        order.changeOrderType(OrderType.PICKUP);

        orderService.placeOrder(order);

        order.markPreparing();
        repo.updateOrderStatus(order);

        order.markReady();
        repo.updateOrderStatus(order);

        order.markPickedUp();
        repo.updateOrderStatus(order);

        order.markCompleted();
        repo.updateOrderStatus(order);

        int orderId = order.getId();

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        Order found =
                orderService.findOrderById(orderId);

        assertEquals(
                OrderStatus.REFUNDED,
                found.getStatus()
        );
    }

    @Test
    public void cancelOrderWithInvalidIdThrowsIllegalArgumentException() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder(0)
        );
    }

    @Test
    public void cancelOrderWithNonExistingIdThrowsIllegalArgumentException() {

        // Arrange
        OrderRepository repo =
                new JdbcOrderRepository();

        OrderService orderService =
                new OrderService(repo);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder(999999)
        );
    }
}
