package com.jadmatar.restaurant.ui;

import com.jadmatar.restaurant.database.DatabaseConnection;

import com.jadmatar.restaurant.domain.Employee;
import com.jadmatar.restaurant.domain.EmployeePosition;
import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import com.jadmatar.restaurant.domain.Order;
import com.jadmatar.restaurant.domain.OrderItem;
import com.jadmatar.restaurant.domain.OrderStatus;
import com.jadmatar.restaurant.domain.OrderType;

import com.jadmatar.restaurant.repository.EmployeeRepository;
import com.jadmatar.restaurant.repository.JdbcEmployeeRepository;
import com.jadmatar.restaurant.repository.JdbcMenuItemRepository;
import com.jadmatar.restaurant.repository.JdbcOrderRepository;
import com.jadmatar.restaurant.repository.MenuItemRepository;
import com.jadmatar.restaurant.repository.OrderRepository;

import com.jadmatar.restaurant.service.EmployeeService;
import com.jadmatar.restaurant.service.MenuItemService;
import com.jadmatar.restaurant.service.OrderService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import java.math.BigDecimal;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleMenuTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private EmployeeService employeeService;
    private MenuItemService menuItemService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {

        clearTestDatabase();

        EmployeeRepository employeeRepository =
                new JdbcEmployeeRepository();

        MenuItemRepository menuItemRepository =
                new JdbcMenuItemRepository();

        OrderRepository orderRepository =
                new JdbcOrderRepository();

        employeeService =
                new EmployeeService(
                        employeeRepository
                );

        menuItemService =
                new MenuItemService(
                        menuItemRepository
                );

        orderService =
                new OrderService(
                        orderRepository
                );
    }

    @AfterEach
    void tearDown() {

        System.setIn(originalIn);
        System.setOut(originalOut);

        clearTestDatabase();
    }

    // =========================================================
    // DATABASE CLEANUP
    // =========================================================

    private void clearTestDatabase() {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(
                    "DELETE FROM LINE_ORDER"
            );

            statement.executeUpdate(
                    "DELETE FROM SALES_ORDER"
            );

            statement.executeUpdate(
                    "DELETE FROM DAILY_ORDER_COUNTER"
            );

            statement.executeUpdate(
                    "DELETE FROM MENU_ITEM"
            );

            statement.executeUpdate(
                    "DELETE FROM EMPLOYEE"
            );

        } catch (SQLException exception) {

            throw new RuntimeException(
                    "Could not clean test database",
                    exception
            );
        }
    }

    // =========================================================
    // CONSOLE HELPER
    // =========================================================

    private String runConsoleWithInput(
            String input
    ) {

        ByteArrayInputStream simulatedInput =
                new ByteArrayInputStream(
                        input.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        ByteArrayOutputStream capturedOutput =
                new ByteArrayOutputStream();

        PrintStream simulatedConsole =
                new PrintStream(
                        capturedOutput,
                        true,
                        StandardCharsets.UTF_8
                );

        System.setIn(simulatedInput);
        System.setOut(simulatedConsole);

        ConsoleMenu consoleMenu =
                new ConsoleMenu(
                        employeeService,
                        menuItemService,
                        orderService
                );

        consoleMenu.start();

        return capturedOutput.toString(
                StandardCharsets.UTF_8
        );
    }

    // =========================================================
    // MAIN MENU
    // =========================================================

    @Test
    void enteringZeroExitsApplication() {

        String output =
                runConsoleWithInput(
                        "0\n"
                );

        assertTrue(
                output.contains(
                        "Application exited successfully."
                )
        );
    }

    @Test
    void mainMenuDisplaysOrderManagement() {

        String output =
                runConsoleWithInput(
                        "0\n"
                );

        assertTrue(
                output.contains(
                        "3. Order Management"
                )
        );
    }

    // =========================================================
    // EMPLOYEE UI
    // =========================================================

    @Test
    void createEmployeeAddsEmployee() {

        EmployeePosition position =
                EmployeePosition.values()[0];

        String input = """
                1
                1
                Jad Test
                70123456
                2026-09-11
                %s
                0
                0
                """.formatted(
                position.name()
        );

        String output =
                runConsoleWithInput(input);

        List<Employee> employees =
                employeeService.getAllEmployees();

        assertEquals(
                1,
                employees.size()
        );

        assertTrue(
                output.contains(
                        "Employee created successfully:"
                )
        );

        assertTrue(
                output.contains(
                        "Jad Test"
                )
        );
    }

    @Test
    void findEmployeeByIdDisplaysExistingEmployee() {

        Employee employee =
                employeeService.createEmployee(
                        "Jad Test",
                        "70123456",
                        LocalDate.of(
                                2026,
                                9,
                                11
                        ),
                        EmployeePosition.values()[0]
                );

        String input = """
                1
                2
                %d
                0
                0
                """.formatted(
                employee.getId()
        );

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Jad Test"
                )
        );
    }

    @Test
    void viewAllEmployeesDisplaysEveryEmployee() {

        employeeService.createEmployee(
                "Employee One",
                "70111111",
                LocalDate.of(
                        2026,
                        9,
                        11
                ),
                EmployeePosition.values()[0]
        );

        employeeService.createEmployee(
                "Employee Two",
                "70222222",
                LocalDate.of(
                        2026,
                        9,
                        11
                ),
                EmployeePosition.values()[0]
        );

        String input = """
                1
                3
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Employee One"
                )
        );

        assertTrue(
                output.contains(
                        "Employee Two"
                )
        );
    }

    @Test
    void updateEmployeeNameUpdatesExistingEmployee() {

        Employee employee =
                employeeService.createEmployee(
                        "Old Name",
                        "70123456",
                        LocalDate.of(
                                2026,
                                9,
                                11
                        ),
                        EmployeePosition.values()[0]
                );

        String input = """
                1
                4
                %d
                New Name
                0
                0
                """.formatted(
                employee.getId()
        );

        String output =
                runConsoleWithInput(input);

        Employee updated =
                employeeService.findEmployeeById(
                        employee.getId()
                );

        assertEquals(
                "New Name",
                updated.getName()
        );

        assertTrue(
                output.contains(
                        "Employee name updated successfully:"
                )
        );
    }

    @Test
    void deactivateEmployeeWorks() {

        Employee employee =
                employeeService.createEmployee(
                        "Employee",
                        "70123456",
                        LocalDate.of(
                                2026,
                                9,
                                11
                        ),
                        EmployeePosition.values()[0]
                );

        String input = """
                1
                5
                %d
                0
                0
                """.formatted(
                employee.getId()
        );

        String output =
                runConsoleWithInput(input);

        Employee updated =
                employeeService.findEmployeeById(
                        employee.getId()
                );

        assertTrue(
                !updated.isActive()
        );

        assertTrue(
                output.contains(
                        "Employee deactivated successfully."
                )
        );
    }

    @Test
    void reactivateEmployeeWorks() {

        Employee employee =
                employeeService.createEmployee(
                        "Employee",
                        "70123456",
                        LocalDate.of(
                                2026,
                                9,
                                11
                        ),
                        EmployeePosition.values()[0]
                );

        employeeService.deactivateEmployee(
                employee.getId()
        );

        String input = """
                1
                6
                %d
                0
                0
                """.formatted(
                employee.getId()
        );

        String output =
                runConsoleWithInput(input);

        Employee updated =
                employeeService.findEmployeeById(
                        employee.getId()
                );

        assertTrue(
                updated.isActive()
        );

        assertTrue(
                output.contains(
                        "Employee reactivated successfully."
                )
        );
    }

    // =========================================================
    // MENU ITEM UI
    // =========================================================

    @Test
    void createMenuItemAddsItem() {

        MenuCategory category =
                MenuCategory.values()[0];

        String input = """
                2
                1
                Burger
                12.50
                %s
                0
                0
                """.formatted(
                category.name()
        );

        String output =
                runConsoleWithInput(input);

        assertEquals(
                1,
                menuItemService
                        .getAllMenuItems()
                        .size()
        );

        assertTrue(
                output.contains(
                        "Menu Item created successfully"
                )
        );

        assertTrue(
                output.contains(
                        "Burger"
                )
        );
    }

    @Test
    void createMenuItemRetriesAfterInvalidPrice() {

        MenuCategory category =
                MenuCategory.values()[0];

        String input = """
                2
                1
                Burger
                not-a-price
                12.50
                %s
                0
                0
                """.formatted(
                category.name()
        );

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Enter a valid price"
                )
        );

        assertEquals(
                1,
                menuItemService
                        .getAllMenuItems()
                        .size()
        );
    }

    @Test
    void createMenuItemRetriesAfterInvalidCategory() {

        MenuCategory category =
                MenuCategory.values()[0];

        String input = """
                2
                1
                Burger
                12.50
                NOT_A_REAL_CATEGORY
                %s
                0
                0
                """.formatted(
                category.name()
        );

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Invalid Category. Choose one from the list."
                )
        );

        assertEquals(
                1,
                menuItemService
                        .getAllMenuItems()
                        .size()
        );
    }

    @Test
    void findMenuItemByIdDisplaysExistingItem() {

        MenuItem menuItem =
                menuItemService.createMenuItem(
                        new BigDecimal("12.50"),
                        "Burger",
                        MenuCategory.values()[0]
                );

        String input = """
                2
                2
                %d
                0
                0
                """.formatted(
                menuItem.getId()
        );

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Burger"
                )
        );
    }

    @Test
    void findMenuItemByIdDisplaysNotFoundMessage() {

        String input = """
                2
                2
                999999
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item not found."
                )
        );
    }

    @Test
    void viewAllMenuItemsDisplaysEveryItem() {

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                MenuCategory.values()[0]
        );

        menuItemService.createMenuItem(
                new BigDecimal("8.25"),
                "Salad",
                MenuCategory.values()[0]
        );

        String input = """
                2
                3
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "SANDWICHES"
                )
        );

        assertTrue(
                output.contains(
                        "SANDWICHES"
                )
        );
    }

    @Test
    void viewAllMenuItemsDisplaysMessageWhenEmpty() {

        String input = """
                2
                3
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "No menu items have been added."
                )
        );
    }

    @Test
    void updateMenuItemNameUpdatesExistingItem() {

        MenuItem menuItem =
                menuItemService.createMenuItem(
                        new BigDecimal("12.50"),
                        "Burger",
                        MenuCategory.values()[0]
                );

        String input = """
                2
                4
                %d
                Double Burger
                0
                0
                """.formatted(
                menuItem.getId()
        );

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item name updated successfully:"
                )
        );

        MenuItem updated =
                menuItemService.findItemById(
                        menuItem.getId()
                );

        assertEquals(
                "Double Burger",
                updated.getItemName()
        );
    }

    @Test
    void updateMenuItemPriceUpdatesExistingItem() {

        MenuItem menuItem =
                menuItemService.createMenuItem(
                        new BigDecimal("12.50"),
                        "Burger",
                        MenuCategory.values()[0]
                );

        String input = """
                2
                5
                %d
                17.75
                0
                0
                """.formatted(
                menuItem.getId()
        );

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item price updated successfully:"
                )
        );

        MenuItem updated =
                menuItemService.findItemById(
                        menuItem.getId()
                );

        assertEquals(
                0,
                new BigDecimal("17.75")
                        .compareTo(
                                updated.getItemPrice()
                        )
        );
    }

    @Test
    void updateMenuItemCategoryUpdatesExistingItem() {

        MenuCategory originalCategory =
                MenuCategory.values()[0];

        MenuCategory newCategory =
                MenuCategory.values()[1];

        MenuItem menuItem =
                menuItemService.createMenuItem(
                        new BigDecimal("12.50"),
                        "Burger",
                        originalCategory
                );

        String input = """
                2
                6
                %d
                %s
                0
                0
                """.formatted(
                menuItem.getId(),
                newCategory.name()
        );

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item category updated successfully:"
                )
        );

        MenuItem updated =
                menuItemService.findItemById(
                        menuItem.getId()
                );

        assertEquals(
                newCategory,
                updated.getItemCategory()
        );
    }

    @Test
    void markMenuItemUnavailableWorks() {

        MenuItem menuItem =
                menuItemService.createMenuItem(
                        new BigDecimal("12.50"),
                        "Burger",
                        MenuCategory.values()[0]
                );

        String input = """
                2
                8
                %d
                0
                0
                """.formatted(
                menuItem.getId()
        );

        String output =
                runConsoleWithInput(input);

        MenuItem updated =
                menuItemService.findItemById(
                        menuItem.getId()
                );

        assertTrue(
                !updated.isAvailable()
        );

        assertTrue(
                output.contains(
                        "Menu item marked as unavailable:"
                )
        );
    }

    @Test
    void markMenuItemAvailableWorks() {

        MenuItem menuItem =
                menuItemService.createMenuItem(
                        new BigDecimal("12.50"),
                        "Burger",
                        MenuCategory.values()[0]
                );

        menuItemService.markItemUnavailable(
                menuItem.getId()
        );

        String input = """
                2
                7
                %d
                0
                0
                """.formatted(
                menuItem.getId()
        );

        String output =
                runConsoleWithInput(input);

        MenuItem updated =
                menuItemService.findItemById(
                        menuItem.getId()
                );

        assertTrue(
                updated.isAvailable()
        );

        assertTrue(
                output.contains(
                        "Menu item marked as available:"
                )
        );
    }

    // =========================================================
    // ORDER UI
    // =========================================================

    @Test
    void createOrderPlacesOrderSuccessfully() {

        createEmployeeForOrder();
        createMenuItemForOrder();

        String input = """
                3
                1
                1
                PICKUP
                1
                2
                0
                0
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        List<Order> orders =
                orderService.findAll();

        assertEquals(
                1,
                orders.size()
        );

        assertTrue(
                output.contains(
                        "Order placed successfully:"
                )
        );

        assertEquals(
                1,
                orders.get(0)
                        .getItems()
                        .size()
        );

        assertEquals(
                2,
                orders.get(0)
                        .getItems()
                        .get(0)
                        .getQuantity()
        );
    }

    @Test
    void createOrderCanContainMultipleItems() {

        createEmployeeForOrder();

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                MenuCategory.values()[0]
        );

        menuItemService.createMenuItem(
                new BigDecimal("4.00"),
                "Fries",
                MenuCategory.values()[0]
        );

        String input = """
                3
                1
                1
                PICKUP
                1
                2
                0
                1
                2
                1
                0
                0
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        List<Order> orders =
                orderService.findAll();

        assertEquals(
                1,
                orders.size()
        );

        assertEquals(
                2,
                orders.get(0)
                        .getItems()
                        .size()
        );

        assertTrue(
                output.contains(
                        "Item added successfully."
                )
        );
    }

    @Test
    void createOrderRetriesAfterInvalidOrderType() {

        createEmployeeForOrder();
        createMenuItemForOrder();

        String input = """
                3
                1
                1
                NOT_A_REAL_TYPE
                PICKUP
                1
                1
                0
                0
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Invalid order type. Choose one from the list."
                )
        );

        assertEquals(
                1,
                orderService.findAll().size()
        );
    }

    @Test
    void createOrderDisplaysMessageWhenNoActiveEmployeesExist() {

        String input = """
                3
                1
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "No active employees are available."
                )
        );

        assertTrue(
                orderService.findAll().isEmpty()
        );
    }

    @Test
    void createOrderDisplaysMessageWhenNoMenuItemsAreAvailable() {

        createEmployeeForOrder();

        String input = """
                3
                1
                1
                PICKUP
                0
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "No menu items are currently available."
                )
        );

        assertTrue(
                orderService.findAll().isEmpty()
        );
    }

    @Test
    void viewSelectedOrderDisplaysExistingOrder() {

        Order order =
                createPlacedOrder();

        String input = """
                3
                2
                1
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Order Number: #"
                                + order.getOrderNumber()
                )
        );

        assertTrue(
                output.contains(
                        "Burger"
                )
        );
    }

    @Test
    void viewAllOrdersDisplaysStoredOrders() {

        Order order =
                createPlacedOrder();

        String input = """
                3
                3
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "=== ALL ORDERS ==="
                )
        );

        assertTrue(
                output.contains(
                        "Order Number: #"
                                + order.getOrderNumber()
                )
        );

        assertTrue(
                output.contains(
                        "Burger"
                )
        );
    }

    @Test
    void viewAllOrdersDisplaysMessageWhenEmpty() {

        String input = """
                3
                3
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "No orders have been placed."
                )
        );
    }

    @Test
    void cancelOrderCancelsStoredOrder() {

        Order order =
                createPlacedOrder();

        String input = """
                3
                10
                1
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Order cancelled successfully."
                )
        );

        Order loaded =
                orderService.findOrderById(
                        order.getId()
                );

        assertNotNull(loaded);

        assertEquals(
                OrderStatus.VOIDED,
                loaded.getStatus()
        );
    }

    @Test
    void completingFreshlyPlacedOrderDisplaysFailure() {

        Order order =
                createPlacedOrder();

        String input = """
                3
                9
                1
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Could not complete order:"
                )
        );

        assertEquals(
                OrderStatus.PLACED,
                orderService.findOrderById(
                        order.getId()
                ).getStatus()
        );
    }

    @Test
    void pickupOrderCanProgressToCompleted() {

        Order order =
                createPlacedOrder(
                        OrderType.PICKUP
                );

        String input = """
                3
                4
                1
                5
                1
                6
                1
                9
                1
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Order marked as preparing successfully."
                )
        );

        assertTrue(
                output.contains(
                        "Order marked as ready successfully."
                )
        );

        assertTrue(
                output.contains(
                        "Order marked as picked up successfully."
                )
        );

        assertTrue(
                output.contains(
                        "Order completed successfully."
                )
        );

        assertEquals(
                OrderStatus.COMPLETED,
                orderService.findOrderById(
                        order.getId()
                ).getStatus()
        );
    }

    @Test
    void deliveryOrderCanProgressToCompleted() {

        Order order =
                createPlacedOrder(
                        OrderType.DELIVERY
                );

        String input = """
                3
                4
                1
                5
                1
                7
                1
                8
                1
                9
                1
                0
                0
                """;

        String output =
                runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Order marked as preparing successfully."
                )
        );

        assertTrue(
                output.contains(
                        "Order marked as ready successfully."
                )
        );

        assertTrue(
                output.contains(
                        "Order dispatched successfully."
                )
        );

        assertTrue(
                output.contains(
                        "Order marked as on the way successfully."
                )
        );

        assertTrue(
                output.contains(
                        "Order completed successfully."
                )
        );

        assertEquals(
                OrderStatus.COMPLETED,
                orderService.findOrderById(
                        order.getId()
                ).getStatus()
        );
    }

    // =========================================================
    // TEST DATA HELPERS
    // =========================================================

    private Employee createEmployeeForOrder() {

        return employeeService.createEmployee(
                "Order Employee",
                "70999999",
                LocalDate.of(
                        2026,
                        9,
                        11
                ),
                EmployeePosition.values()[0]
        );
    }

    private MenuItem createMenuItemForOrder() {

        return menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                MenuCategory.values()[0]
        );
    }

    private Order createPlacedOrder() {

        return createPlacedOrder(
                OrderType.PICKUP
        );
    }

    private Order createPlacedOrder(
            OrderType orderType
    ) {

        Employee employee =
                createEmployeeForOrder();

        MenuItem menuItem =
                createMenuItemForOrder();

        Order order =
                new Order(
                        employee.getId(),
                        orderType
                );

        OrderItem orderItem =
                new OrderItem(
                        menuItem,
                        2,
                        menuItem.getItemPrice(),
                        BigDecimal.ZERO
                );

        order.addItem(
                orderItem
        );

        return orderService.placeOrder(
                order
        );
    }
}