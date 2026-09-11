package com.jadmatar.restaurant.ui;

import com.jadmatar.restaurant.domain.Employee;
import com.jadmatar.restaurant.domain.EmployeePosition;
import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import com.jadmatar.restaurant.domain.Order;
import com.jadmatar.restaurant.domain.OrderItem;
import com.jadmatar.restaurant.domain.OrderType;
import com.jadmatar.restaurant.service.EmployeeService;
import com.jadmatar.restaurant.service.MenuItemService;
import com.jadmatar.restaurant.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntConsumer;

public class ConsoleMenu {

    private final EmployeeService employeeService;
    private final MenuItemService menuItemService;
    private final OrderService orderService;
    private final Scanner scanner;

    public ConsoleMenu(
            EmployeeService employeeService,
            MenuItemService menuItemService,
            OrderService orderService
    ) {
        if (
                employeeService == null ||
                        menuItemService == null ||
                        orderService == null
        ) {
            throw new IllegalArgumentException(
                    "SERVICES CANNOT BE NULL"
            );
        }

        this.employeeService = employeeService;
        this.menuItemService = menuItemService;
        this.orderService = orderService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== RESTAURANT MANAGEMENT ===");
            System.out.println("1. Employee Management");
            System.out.println("2. Menu Management");
            System.out.println("3. Order Management");
            System.out.println("0. Exit");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> employeeManagementMenu();
                case 2 -> menuItemManagementMenu();
                case 3 -> orderManagementMenu();
                case 0 -> running = false;
                default -> System.out.println("Invalid menu option.");
            }
        }

        System.out.println("Application exited successfully.");
    }

    // =========================================================
    // EMPLOYEE MANAGEMENT
    // =========================================================

    private void employeeManagementMenu() {
        boolean inEmployeeMenu = true;

        while (inEmployeeMenu) {
            showEmployeeMenu();

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> createEmployee();
                case 2 -> findEmployeeById();
                case 3 -> viewAllEmployees();
                case 4 -> updateEmployeeName();
                case 5 -> deactivateEmployee();
                case 6 -> reactivateEmployee();
                case 0 -> inEmployeeMenu = false;
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private void showEmployeeMenu() {
        System.out.println();
        System.out.println("=== EMPLOYEE MANAGEMENT ===");
        System.out.println("1. Add a new employee");
        System.out.println("2. Find employee by ID");
        System.out.println("3. View all employees");
        System.out.println("4. Update employee name");
        System.out.println("5. Deactivate employee");
        System.out.println("6. Reactivate employee");
        System.out.println("0. Back");
    }

    private void createEmployee() {
        System.out.println();
        System.out.println("=== ADD EMPLOYEE ===");

        String name =
                readNonBlank(
                        "Enter name: "
                );

        String phoneNumber =
                readNonBlank(
                        "Enter phone number: "
                );

        LocalDate employmentDate =
                readDate();

        EmployeePosition position =
                readPosition();

        try {
            Employee employee =
                    employeeService.createEmployee(
                            name,
                            phoneNumber,
                            employmentDate,
                            position
                    );

            System.out.println(
                    "Employee created successfully:"
            );

            System.out.println(employee);

        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Could not create employee: "
                            + exception.getMessage()
            );
        }
    }

    private void findEmployeeById() {
        int id =
                readPositiveInt(
                        "Enter employee ID: "
                );

        Employee employee =
                employeeService.findEmployeeById(id);

        if (employee == null) {
            System.out.println(
                    "Employee not found."
            );
            return;
        }

        System.out.println(employee);
    }

    private void viewAllEmployees() {
        List<Employee> employees =
                employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println(
                    "No employees have been added."
            );
            return;
        }

        System.out.println();
        System.out.println(
                "=== ALL EMPLOYEES ==="
        );

        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    private void updateEmployeeName() {
        int id =
                readPositiveInt(
                        "Enter employee ID: "
                );

        Employee existingEmployee =
                employeeService.findEmployeeById(id);

        if (existingEmployee == null) {
            System.out.println(
                    "Employee not found."
            );
            return;
        }

        String newName =
                readNonBlank(
                        "Enter new name: "
                );

        try {
            Employee updatedEmployee =
                    employeeService.updateEmployeeName(
                            id,
                            newName
                    );

            System.out.println(
                    "Employee name updated successfully:"
            );

            System.out.println(
                    updatedEmployee
            );

        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Could not update employee: "
                            + exception.getMessage()
            );
        }
    }

    private void deactivateEmployee() {
        int id =
                readPositiveInt(
                        "Enter employee ID: "
                );

        Employee employee =
                employeeService.deactivateEmployee(id);

        if (employee == null) {
            System.out.println(
                    "Employee not found."
            );
            return;
        }

        if (!employee.isActive()) {
            System.out.println(
                    "Employee deactivated successfully."
            );
        }
    }

    private void reactivateEmployee() {
        int id =
                readPositiveInt(
                        "Enter employee ID: "
                );

        Employee employee =
                employeeService.reactivateEmployee(id);

        if (employee == null) {
            System.out.println(
                    "Employee not found."
            );
            return;
        }

        if (employee.isActive()) {
            System.out.println(
                    "Employee reactivated successfully."
            );
        }
    }

    // =========================================================
    // MENU ITEM MANAGEMENT
    // =========================================================

    private void menuItemManagementMenu() {
        boolean inMenuItemManagement = true;

        while (inMenuItemManagement) {
            showMenuItemMenu();

            int choice =
                    readInt(
                            "Enter your choice: "
                    );

            switch (choice) {
                case 1 -> createMenuItem();
                case 2 -> findMenuItemById();
                case 3 -> viewAllMenuItems();
                case 4 -> updateMenuItemName();
                case 5 -> updateMenuItemPrice();
                case 6 -> updateMenuItemCategory();
                case 7 -> markMenuItemAvailable();
                case 8 -> markMenuItemUnavailable();
                case 0 -> inMenuItemManagement = false;
                default -> System.out.println(
                        "Invalid menu option."
                );
            }
        }
    }

    private void showMenuItemMenu() {
        System.out.println();
        System.out.println(
                "=== MENU ITEM MANAGEMENT ==="
        );

        System.out.println("1. Add a menu item");
        System.out.println("2. Find menu item by ID");
        System.out.println("3. View all menu items");
        System.out.println("4. Update item name");
        System.out.println("5. Update item price");
        System.out.println("6. Update item category");
        System.out.println("7. Mark item available");
        System.out.println("8. Mark item unavailable");
        System.out.println("0. Back");
    }

    private void createMenuItem() {
        System.out.println();
        System.out.println(
                "=== ADD MENU ITEM ==="
        );

        String itemName =
                readNonBlank(
                        "Enter item name: "
                );

        BigDecimal itemPrice =
                readBigDecimal(
                        "Enter item price: "
                );

        MenuCategory menuCategory =
                readMenuCategory(
                        "Enter item category"
                );

        try {
            MenuItem menuItem =
                    menuItemService.createMenuItem(
                            itemPrice,
                            itemName,
                            menuCategory
                    );

            System.out.println(
                    "Menu Item created successfully"
            );

            System.out.println(
                    menuItem
            );

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Could not create menu item. "
                            + e.getMessage()
            );
        }
    }

    private void findMenuItemById() {
        System.out.println();
        System.out.println(
                "=== FIND MENU ITEM ==="
        );

        int id =
                readPositiveInt(
                        "Enter menu item ID: "
                );

        MenuItem menuItem =
                menuItemService.findItemById(id);

        if (menuItem == null) {
            System.out.println(
                    "Menu item not found."
            );
            return;
        }

        System.out.println(menuItem);
    }

    private void viewAllMenuItems() {
        List<MenuItem> menuItems =
                menuItemService.getAllMenuItems();

        if (menuItems.isEmpty()) {
            System.out.println(
                    "No menu items have been added."
            );
            return;
        }

        System.out.println();
        System.out.println(
                "=== ALL MENU ITEMS ==="
        );

        for (MenuItem menuItem : menuItems) {
            System.out.println(menuItem);
        }
    }

    private void updateMenuItemName() {
        System.out.println();
        System.out.println(
                "=== UPDATE MENU ITEM NAME ==="
        );

        int id =
                readPositiveInt(
                        "Enter menu item ID: "
                );

        MenuItem existingItem =
                menuItemService.findItemById(id);

        if (existingItem == null) {
            System.out.println(
                    "Menu item not found."
            );
            return;
        }

        String newName =
                readNonBlank(
                        "Enter new item name: "
                );

        try {
            MenuItem updatedItem =
                    menuItemService.updateItemName(
                            id,
                            newName
                    );

            System.out.println(
                    "Menu item name updated successfully:"
            );

            System.out.println(
                    updatedItem
            );

        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Could not update menu item name: "
                            + exception.getMessage()
            );
        }
    }

    private void updateMenuItemPrice() {
        System.out.println();
        System.out.println(
                "=== UPDATE MENU ITEM PRICE ==="
        );

        int id =
                readPositiveInt(
                        "Enter menu item ID: "
                );

        MenuItem existingItem =
                menuItemService.findItemById(id);

        if (existingItem == null) {
            System.out.println(
                    "Menu item not found."
            );
            return;
        }

        BigDecimal newPrice =
                readBigDecimal(
                        "Enter new item price: "
                );

        try {
            MenuItem updatedItem =
                    menuItemService.updateItemPrice(
                            id,
                            newPrice
                    );

            System.out.println(
                    "Menu item price updated successfully:"
            );

            System.out.println(
                    updatedItem
            );

        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Could not update menu item price: "
                            + exception.getMessage()
            );
        }
    }

    private void updateMenuItemCategory() {
        System.out.println();
        System.out.println(
                "=== UPDATE MENU ITEM CATEGORY ==="
        );

        int id =
                readPositiveInt(
                        "Enter menu item ID: "
                );

        MenuItem existingItem =
                menuItemService.findItemById(id);

        if (existingItem == null) {
            System.out.println(
                    "Menu item not found."
            );
            return;
        }

        MenuCategory newCategory =
                readMenuCategory(
                        "Available categories:"
                );

        try {
            MenuItem updatedItem =
                    menuItemService.updateItemCategory(
                            id,
                            newCategory
                    );

            System.out.println(
                    "Menu item category updated successfully:"
            );

            System.out.println(
                    updatedItem
            );

        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Could not update menu item category: "
                            + exception.getMessage()
            );
        }
    }

    private void markMenuItemAvailable() {
        System.out.println();
        System.out.println(
                "=== MARK MENU ITEM AVAILABLE ==="
        );

        int id =
                readPositiveInt(
                        "Enter menu item ID: "
                );

        MenuItem menuItem =
                menuItemService.markItemAvailable(id);

        if (menuItem == null) {
            System.out.println(
                    "Menu item not found."
            );
            return;
        }

        System.out.println(
                "Menu item marked as available:"
        );

        System.out.println(
                menuItem
        );
    }

    private void markMenuItemUnavailable() {
        System.out.println();
        System.out.println(
                "=== MARK MENU ITEM UNAVAILABLE ==="
        );

        int id =
                readPositiveInt(
                        "Enter menu item ID: "
                );

        MenuItem menuItem =
                menuItemService.markItemUnavailable(id);

        if (menuItem == null) {
            System.out.println(
                    "Menu item not found."
            );
            return;
        }

        System.out.println(
                "Menu item marked as unavailable:"
        );

        System.out.println(
                menuItem
        );
    }

    // =========================================================
    // ORDER MANAGEMENT
    // =========================================================

    private void orderManagementMenu() {
        boolean inOrderManagement = true;

        while (inOrderManagement) {
            showOrderMenu();

            int choice =
                    readInt(
                            "Enter your choice: "
                    );

            switch (choice) {
                case 1 -> createOrder();

                case 2 -> viewSelectedOrder();

                case 3 -> viewAllOrders();

                case 4 -> updateOrderStatus(
                        "MARK ORDER PREPARING",
                        "Order marked as preparing successfully.",
                        orderService::markOrderPreparing
                );

                case 5 -> updateOrderStatus(
                        "MARK ORDER READY",
                        "Order marked as ready successfully.",
                        orderService::markOrderReady
                );

                case 6 -> updateOrderStatus(
                        "MARK ORDER PICKED UP",
                        "Order marked as picked up successfully.",
                        orderService::markOrderPickedUp
                );

                case 7 -> updateOrderStatus(
                        "DISPATCH ORDER",
                        "Order dispatched successfully.",
                        orderService::markOrderDispatched
                );

                case 8 -> updateOrderStatus(
                        "MARK ORDER ON THE WAY",
                        "Order marked as on the way successfully.",
                        orderService::markOrderOnTheWay
                );

                case 9 -> completeOrder();

                case 10 -> cancelOrder();

                case 0 -> inOrderManagement = false;

                default -> System.out.println(
                        "Invalid menu option."
                );
            }
        }
    }

    private void showOrderMenu() {
        System.out.println();
        System.out.println(
                "=== ORDER MANAGEMENT ==="
        );

        System.out.println("1. Create order");
        System.out.println("2. View an order");
        System.out.println("3. View all orders");
        System.out.println("4. Mark order preparing");
        System.out.println("5. Mark order ready");
        System.out.println("6. Mark pickup order picked up");
        System.out.println("7. Dispatch delivery order");
        System.out.println("8. Mark delivery order on the way");
        System.out.println("9. Complete order");
        System.out.println("10. Cancel order");
        System.out.println("0. Back");
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    private void createOrder() {
        System.out.println();
        System.out.println(
                "=== CREATE ORDER ==="
        );

        Employee employee =
                selectActiveEmployee();

        if (employee == null) {
            return;
        }

        OrderType orderType =
                readOrderType();

        Order order;

        try {
            order =
                    new Order(
                            employee.getId(),
                            orderType
                    );

        } catch (IllegalArgumentException exception) {

            System.out.println(
                    "Could not create order: "
                            + exception.getMessage()
            );

            return;
        }

        boolean addingItems = true;

        while (addingItems) {

            System.out.println();
            System.out.println(
                    "=== ADD ORDER ITEM ==="
            );

            MenuItem menuItem =
                    selectAvailableMenuItem();

            if (menuItem != null) {

                int quantity =
                        readPositiveInt(
                                "Enter quantity: "
                        );

                BigDecimal discount =
                        readNonNegativeBigDecimal(
                                "Enter discount percentage: "
                        );

                try {
                    OrderItem orderItem =
                            new OrderItem(
                                    menuItem,
                                    quantity,
                                    menuItem.getItemPrice(),
                                    discount
                            );

                    order.addItem(
                            orderItem
                    );

                    System.out.println(
                            "Item added successfully."
                    );

                } catch (
                        IllegalArgumentException |
                        IllegalStateException exception
                ) {

                    System.out.println(
                            "Could not add item: "
                                    + exception.getMessage()
                    );
                }
            }

            System.out.println();
            System.out.println(
                    "1. Add another item"
            );

            System.out.println(
                    "0. Finish order"
            );

            int choice =
                    readInt(
                            "Enter your choice: "
                    );

            if (choice == 0) {
                addingItems = false;
            }
        }

        try {
            Order placedOrder =
                    orderService.placeOrder(
                            order
                    );

            System.out.println();
            System.out.println(
                    "Order placed successfully:"
            );

            displayOrder(
                    placedOrder
            );

        } catch (
                IllegalArgumentException |
                IllegalStateException exception
        ) {

            System.out.println(
                    "Could not place order: "
                            + exception.getMessage()
            );
        }
    }

    // =========================================================
    // EMPLOYEE SELECTION FOR ORDER
    // =========================================================

    private Employee selectActiveEmployee() {

        List<Employee> employees =
                employeeService.getAllEmployees();

        List<Employee> activeEmployees =
                new ArrayList<>();

        for (Employee employee : employees) {

            if (employee.isActive()) {
                activeEmployees.add(
                        employee
                );
            }
        }

        if (activeEmployees.isEmpty()) {

            System.out.println(
                    "No active employees are available."
            );

            return null;
        }

        System.out.println();
        System.out.println(
                "=== SELECT EMPLOYEE ==="
        );

        for (
                int i = 0;
                i < activeEmployees.size();
                i++
        ) {

            System.out.println(
                    (i + 1)
                            + ". "
                            + activeEmployees.get(i)
            );
        }

        System.out.println(
                "0. Cancel"
        );

        while (true) {

            int choice =
                    readInt(
                            "Choose employee: "
                    );

            if (choice == 0) {
                return null;
            }

            if (
                    choice >= 1 &&
                            choice <= activeEmployees.size()
            ) {

                return activeEmployees.get(
                        choice - 1
                );
            }

            System.out.println(
                    "Invalid employee selection."
            );
        }
    }

    // =========================================================
    // MENU ITEM SELECTION FOR ORDER
    // =========================================================

    private MenuItem selectAvailableMenuItem() {

        List<MenuItem> menuItems =
                menuItemService.getAllMenuItems();

        List<MenuItem> availableItems =
                new ArrayList<>();

        for (MenuItem menuItem : menuItems) {

            if (menuItem.isAvailable()) {
                availableItems.add(
                        menuItem
                );
            }
        }

        if (availableItems.isEmpty()) {

            System.out.println(
                    "No menu items are currently available."
            );

            return null;
        }

        System.out.println();
        System.out.println(
                "=== AVAILABLE MENU ITEMS ==="
        );

        for (
                int i = 0;
                i < availableItems.size();
                i++
        ) {

            System.out.println(
                    (i + 1)
                            + ". "
                            + availableItems.get(i)
            );
        }

        System.out.println(
                "0. Cancel"
        );

        while (true) {

            int choice =
                    readInt(
                            "Choose menu item: "
                    );

            if (choice == 0) {
                return null;
            }

            if (
                    choice >= 1 &&
                            choice <= availableItems.size()
            ) {

                return availableItems.get(
                        choice - 1
                );
            }

            System.out.println(
                    "Invalid menu item selection."
            );
        }
    }

    // =========================================================
    // ORDER SELECTION
    // =========================================================

    private Order selectOrder() {

        List<Order> orders =
                orderService.findAll();

        if (orders.isEmpty()) {

            System.out.println(
                    "No orders have been placed."
            );

            return null;
        }

        System.out.println();
        System.out.println(
                "=== SELECT ORDER ==="
        );

        for (
                int i = 0;
                i < orders.size();
                i++
        ) {

            Order order =
                    orders.get(i);

            System.out.println(
                    (i + 1)
                            + ". Order #"
                            + order.getOrderNumber()
                            + " | "
                            + order.getOrderType()
                            + " | "
                            + order.getStatus()
                            + " | Total: "
                            + order.calculateTotal()
            );
        }

        System.out.println(
                "0. Cancel"
        );

        while (true) {

            int choice =
                    readInt(
                            "Choose order: "
                    );

            if (choice == 0) {
                return null;
            }

            if (
                    choice >= 1 &&
                            choice <= orders.size()
            ) {

                return orders.get(
                        choice - 1
                );
            }

            System.out.println(
                    "Invalid order selection."
            );
        }
    }

    // =========================================================
    // ORDER ACTIONS
    // =========================================================

    private void viewSelectedOrder() {

        Order order =
                selectOrder();

        if (order == null) {
            return;
        }

        displayOrder(
                order
        );
    }

    private void updateOrderStatus(
            String heading,
            String successMessage,
            IntConsumer statusUpdate
    ) {

        System.out.println();
        System.out.println(
                "=== "
                        + heading
                        + " ==="
        );

        Order order =
                selectOrder();

        if (order == null) {
            return;
        }

        try {

            statusUpdate.accept(
                    order.getId()
            );

            System.out.println(
                    successMessage
            );

        } catch (
                IllegalArgumentException |
                IllegalStateException exception
        ) {

            System.out.println(
                    "Could not update order status: "
                            + exception.getMessage()
            );
        }
    }

    private void viewAllOrders() {

        List<Order> orders =
                orderService.findAll();

        if (orders.isEmpty()) {

            System.out.println(
                    "No orders have been placed."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "=== ALL ORDERS ==="
        );

        for (Order order : orders) {

            displayOrder(
                    order
            );

            System.out.println(
                    "------------------------------"
            );
        }
    }

    private void completeOrder() {

        System.out.println();
        System.out.println(
                "=== COMPLETE ORDER ==="
        );

        Order order =
                selectOrder();

        if (order == null) {
            return;
        }

        try {

            orderService.completeOrder(
                    order.getId()
            );

            System.out.println(
                    "Order completed successfully."
            );

        } catch (
                IllegalArgumentException |
                IllegalStateException exception
        ) {

            System.out.println(
                    "Could not complete order: "
                            + exception.getMessage()
            );
        }
    }

    private void cancelOrder() {

        System.out.println();
        System.out.println(
                "=== CANCEL ORDER ==="
        );

        Order order =
                selectOrder();

        if (order == null) {
            return;
        }

        try {

            orderService.cancelOrder(
                    order.getId()
            );

            System.out.println(
                    "Order cancelled successfully."
            );

        } catch (
                IllegalArgumentException |
                IllegalStateException exception
        ) {

            System.out.println(
                    "Could not cancel order: "
                            + exception.getMessage()
            );
        }
    }

    // =========================================================
    // ORDER DISPLAY
    // =========================================================

    private void displayOrder(
            Order order
    ) {

        System.out.println();

        System.out.println(
                "Order Number: #"
                        + order.getOrderNumber()
        );

        Employee employee =
                employeeService.findEmployeeById(
                        order.getEmployeeId()
                );

        if (employee != null) {

            System.out.println(
                    "Employee: "
                            + employee
            );
        }

        System.out.println(
                "Created At: "
                        + order.getCreatedAt()
        );

        System.out.println(
                "Order Type: "
                        + order.getOrderType()
        );

        System.out.println(
                "Status: "
                        + order.getStatus()
        );

        System.out.println(
                "Items:"
        );

        List<OrderItem> items =
                order.getItems();

        if (items.isEmpty()) {

            System.out.println(
                    "No items."
            );

        } else {

            for (OrderItem item : items) {

                System.out.println(
                        "Menu Item: "
                                + item.getMenuItem()
                );

                System.out.println(
                        "Quantity: "
                                + item.getQuantity()
                );

                System.out.println(
                        "Unit Price: "
                                + item.getUnitPrice()
                );

                System.out.println(
                        "Discount: "
                                + item.getDiscountPercentage()
                );

                System.out.println(
                        "Line Total: "
                                + item.totalPrice()
                );

                System.out.println();
            }
        }

        System.out.println(
                "Order Total: "
                        + order.calculateTotal()
        );
    }

    // =========================================================
    // INPUT HELPERS
    // =========================================================

    private int readInt(
            String prompt
    ) {

        while (true) {

            System.out.print(
                    prompt
            );

            String input =
                    scanner.nextLine()
                            .trim();

            try {

                return Integer.parseInt(
                        input
                );

            } catch (
                    NumberFormatException exception
            ) {

                System.out.println(
                        "Please enter a valid integer."
                );
            }
        }
    }

    private int readPositiveInt(
            String prompt
    ) {

        while (true) {

            int value =
                    readInt(
                            prompt
                    );

            if (value > 0) {
                return value;
            }

            System.out.println(
                    "The number must be greater than zero."
            );
        }
    }

    private String readNonBlank(
            String prompt
    ) {

        while (true) {

            System.out.print(
                    prompt
            );

            String input =
                    scanner.nextLine()
                            .trim();

            if (!input.isBlank()) {
                return input;
            }

            System.out.println(
                    "This field cannot be blank."
            );
        }
    }

    private LocalDate readDate() {

        while (true) {

            System.out.print(
                    "Enter employment date (YYYY-MM-DD): "
            );

            String input =
                    scanner.nextLine()
                            .trim();

            try {

                return LocalDate.parse(
                        input
                );

            } catch (
                    DateTimeParseException exception
            ) {

                System.out.println(
                        "Invalid date. Use the format YYYY-MM-DD."
                );
            }
        }
    }

    private EmployeePosition readPosition() {

        while (true) {

            System.out.println(
                    "Available positions:"
            );

            for (
                    EmployeePosition position :
                    EmployeePosition.values()
            ) {

                System.out.println(
                        "- "
                                + position
                );
            }

            System.out.print(
                    "Enter position: "
            );

            String input =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase()
                            .replace(
                                    ' ',
                                    '_'
                            )
                            .replace(
                                    '-',
                                    '_'
                            );

            try {

                return EmployeePosition.valueOf(
                        input
                );

            } catch (
                    IllegalArgumentException exception
            ) {

                System.out.println(
                        "Invalid position. Choose one from the list."
                );
            }
        }
    }

    private BigDecimal readBigDecimal(
            String prompt
    ) {

        while (true) {

            System.out.println(
                    prompt
            );

            try {

                BigDecimal input =
                        scanner.nextBigDecimal();

                scanner.nextLine();

                return input;

            } catch (
                    InputMismatchException e
            ) {

                System.out.println(
                        "Enter a valid price: "
                );

                scanner.nextLine();
            }
        }
    }

    private BigDecimal readNonNegativeBigDecimal(
            String prompt
    ) {

        while (true) {

            BigDecimal value =
                    readBigDecimal(
                            prompt
                    );

            if (
                    value.compareTo(
                            BigDecimal.ZERO
                    ) >= 0
            ) {

                return value;
            }

            System.out.println(
                    "Value cannot be negative."
            );
        }
    }

    private MenuCategory readMenuCategory(
            String prompt
    ) {

        while (true) {

            System.out.println(
                    prompt
            );

            for (
                    MenuCategory category :
                    MenuCategory.values()
            ) {

                System.out.println(
                        "- "
                                + category
                );
            }

            System.out.println(
                    "Enter Category: "
            );

            String input =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase()
                            .replace(
                                    ' ',
                                    '_'
                            )
                            .replace(
                                    '-',
                                    '_'
                            );

            try {

                return MenuCategory.valueOf(
                        input
                );

            } catch (
                    IllegalArgumentException exception
            ) {

                System.out.println(
                        "Invalid Category. Choose one from the list."
                );
            }
        }
    }

    private OrderType readOrderType() {

        while (true) {

            System.out.println(
                    "Available order types:"
            );

            for (
                    OrderType orderType :
                    OrderType.values()
            ) {

                System.out.println(
                        "- "
                                + orderType
                );
            }

            System.out.println(
                    "Enter order type: "
            );

            String input =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase()
                            .replace(
                                    ' ',
                                    '_'
                            )
                            .replace(
                                    '-',
                                    '_'
                            );

            try {

                return OrderType.valueOf(
                        input
                );

            } catch (
                    IllegalArgumentException exception
            ) {

                System.out.println(
                        "Invalid order type. Choose one from the list."
                );
            }
        }
    }
}