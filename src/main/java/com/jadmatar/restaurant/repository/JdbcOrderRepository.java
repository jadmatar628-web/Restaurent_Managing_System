package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderRepository implements OrderRepository {
    @Override
    public Order add(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Only a PLACED order can be stored");
        }

        if (order.getId() != null) {
            throw new IllegalStateException("Order has already been stored");
        }

        if (order.getOrderNumber() != null) {
            throw new IllegalStateException("Order already has an order number");
        }

        List<OrderItem> items = order.getItems();

        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot store an empty order");
        }

        String insertOrderSql = """
                INSERT INTO SALES_ORDER
                (
                    EMPLOYEE_ID,
                    CREATED_AT,
                    ORDER_STATUS,
                    ORDER_TYPE,
                    ORDER_NUMBER,
                    TOTAL
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String insertLineSql = """
                INSERT INTO LINE_ORDER
                (
                    MENU_ITEM_ID,
                    ORDER_ID,
                    UNIT_PRICE,
                    QUANTITY,
                    DISCOUNT,
                    LINE_TOTAL,
                    LINE_NUMBER
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            int nextOrderNumber;
            int generatedOrderId;

            try {
                nextOrderNumber = getNextDailyOrderNumber(connection, LocalDate.now());

                try (PreparedStatement orderStatement = connection.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {

                    orderStatement.setInt(1, order.getEmployeeId());

                    orderStatement.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));

                    orderStatement.setString(3, order.getStatus().name());

                    orderStatement.setString(4, order.getOrderType().name());

                    orderStatement.setInt(5, nextOrderNumber);

                    orderStatement.setBigDecimal(6, order.calculateTotal());

                    int affectedRows = orderStatement.executeUpdate();

                    if (affectedRows != 1) {
                        throw new SQLException("Expected to insert one order but inserted " + affectedRows);
                    }

                    try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {

                        if (!generatedKeys.next()) {
                            throw new SQLException("No generated order ID returned");
                        }

                        generatedOrderId = generatedKeys.getInt(1);
                    }
                }

                try (PreparedStatement lineStatement = connection.prepareStatement(insertLineSql)) {

                    int lineNumber = 1;

                    for (OrderItem item : items) {
                        lineStatement.setInt(1, item.getMenuItem().getId());

                        lineStatement.setInt(2, generatedOrderId);

                        lineStatement.setBigDecimal(3, item.getUnitPrice());

                        lineStatement.setInt(4, item.getQuantity());

                        lineStatement.setBigDecimal(5, item.getDiscountPercentage());

                        lineStatement.setBigDecimal(6, item.totalPrice());

                        lineStatement.setInt(7, lineNumber);

                        lineStatement.addBatch();
                        lineNumber++;
                    }

                    int[] lineResults = lineStatement.executeBatch();

                    if (lineResults.length != items.size()) {
                        throw new SQLException("Not all order lines were inserted");
                    }

                    for (int result : lineResults) {
                        if (result == Statement.EXECUTE_FAILED) {
                            throw new SQLException("An order line failed to insert");
                        }
                    }
                }

                connection.commit();

            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }

            // Assign only after the complete transaction succeeds.
            order.assignId(generatedOrderId);
            order.assignOrderNumber(nextOrderNumber);

            return order;

        } catch (SQLException e) {
            throw new RuntimeException("Cannot add order", e);
        }
    }

    @Override
    public Order findById(int id) {
        if (id < 1) {
            throw new IllegalArgumentException(
                    "Order ID must be positive"
            );
        }

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            return loadOrder(connection, id);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Cannot find order with ID " + id,
                    e
            );
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = """
            SELECT ORDER_ID
            FROM SALES_ORDER
            ORDER BY CREATED_AT DESC, ORDER_ID DESC
            """;

        List<Order> orders = new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            List<Integer> orderIds = new ArrayList<>();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql);
                 ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    orderIds.add(
                            resultSet.getInt("ORDER_ID")
                    );
                }
            }

            for (int orderId : orderIds) {
                Order order = loadOrder(connection, orderId);

                if (order != null) {
                    orders.add(order);
                }
            }

            return orders;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Cannot retrieve orders",
                    e
            );
        }
    }

    @Override
    public void updateOrderStatus(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order cannot be null"
            );
        }

        if (order.getId() == null) {
            throw new IllegalStateException(
                    "Cannot update an order that has not been stored"
            );
        }

        if (order.getStatus() == OrderStatus.OPEN) {
            throw new IllegalStateException(
                    "An OPEN order should not exist in the database"
            );
        }

        String sql = """
            UPDATE SALES_ORDER
            SET ORDER_STATUS = ?
            WHERE ORDER_ID = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    order.getStatus().name()
            );

            statement.setInt(
                    2,
                    order.getId()
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new RuntimeException(
                        "Expected to update one order but updated "
                                + affectedRows
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Cannot update order status",
                    e
            );
        }
    }
    private Order loadOrder(
            Connection connection,
            int orderId
    ) throws SQLException {

        String sql = """
            SELECT
                ORDER_ID,
                EMPLOYEE_ID,
                CREATED_AT,
                ORDER_STATUS,
                ORDER_TYPE,
                ORDER_NUMBER
            FROM SALES_ORDER
            WHERE ORDER_ID = ?
            """;

        int employeeId;
        LocalDateTime createdAt;
        OrderStatus status;
        OrderType orderType;
        int orderNumber;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                employeeId =
                        resultSet.getInt("EMPLOYEE_ID");

                createdAt = resultSet.getTimestamp("CREATED_AT").toLocalDateTime();

                status = OrderStatus.valueOf(
                        resultSet.getString("ORDER_STATUS")
                );

                orderType = OrderType.valueOf(
                        resultSet.getString("ORDER_TYPE")
                );

                orderNumber =
                        resultSet.getInt("ORDER_NUMBER");
            }
        }

        List<OrderItem> items =
                loadOrderItems(connection, orderId);

        return new Order(
                orderId,
                employeeId,
                createdAt,
                status,
                orderType,
                orderNumber,
                items
        );
    }
    private List<OrderItem> loadOrderItems(
            Connection connection,
            int orderId
    ) throws SQLException {

        String sql = """
            SELECT
                L.LINE_NUMBER,
                L.UNIT_PRICE,
                L.QUANTITY,
                L.DISCOUNT,
                M.MENU_ITEM_ID,
                M.ITEM_PRICE,
                M.ITEM_NAME,
                M.ITEM_CATEGORY,
                M.IS_AVAILABLE
            FROM LINE_ORDER L
            INNER JOIN MENU_ITEM M
                ON L.MENU_ITEM_ID = M.MENU_ITEM_ID
            WHERE L.ORDER_ID = ?
            ORDER BY L.LINE_NUMBER
            """;

        List<OrderItem> items = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    MenuItem menuItem = new MenuItem(
                            resultSet.getInt("MENU_ITEM_ID"),
                            resultSet.getString("ITEM_NAME"),
                            resultSet.getBigDecimal("ITEM_PRICE"),
                            MenuCategory.valueOf(
                                    resultSet.getString(
                                            "ITEM_CATEGORY"
                                    )
                            ),
                            resultSet.getBoolean("IS_AVAILABLE")
                    );

                    OrderItem orderItem = new OrderItem(
                            menuItem,
                            resultSet.getInt("QUANTITY"),
                            resultSet.getBigDecimal("UNIT_PRICE"),
                            resultSet.getBigDecimal("DISCOUNT")
                    );

                    items.add(orderItem);
                }
            }
        }

        return items;
    }
    private int getNextDailyOrderNumber(Connection connection, LocalDate businessDay) throws SQLException {

        String selectSql = """
                SELECT LAST_ORDER_NUMBER
                FROM DAILY_ORDER_COUNTER WITH (UPDLOCK, HOLDLOCK)
                WHERE BUSINESS_DAY = ?
                """;

        Integer currentNumber = null;

        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {

            statement.setDate(1, Date.valueOf(businessDay));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currentNumber = resultSet.getInt("LAST_ORDER_NUMBER");
                }
            }
        }

        if (currentNumber == null) {
            String insertSql = """
                    INSERT INTO DAILY_ORDER_COUNTER
                    (
                        BUSINESS_DAY,
                        LAST_ORDER_NUMBER
                    )
                    VALUES (?, ?)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {

                statement.setDate(1, Date.valueOf(businessDay));
                statement.setInt(2, 1);

                int affectedRows = statement.executeUpdate();

                if (affectedRows != 1) {
                    throw new SQLException("Could not create daily order counter");
                }
            }

            return 1;
        }

        int nextNumber = currentNumber + 1;

        String updateSql = """
                UPDATE DAILY_ORDER_COUNTER
                SET LAST_ORDER_NUMBER = ?
                WHERE BUSINESS_DAY = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {

            statement.setInt(1, nextNumber);
            statement.setDate(2, Date.valueOf(businessDay));

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Could not update daily order counter");
            }
        }

        return nextNumber;
    }
}
