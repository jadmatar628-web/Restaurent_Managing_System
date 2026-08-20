package com.jadmatar.restaurant.ui;

import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.repository.InMemoryEmployeeRepository;
import com.jadmatar.restaurant.repository.InMemoryMenuItemRepository;
import com.jadmatar.restaurant.service.EmployeeService;
import com.jadmatar.restaurant.service.MenuItemService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleMenuTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private EmployeeService employeeService;
    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        InMemoryEmployeeRepository employeeRepository =
                new InMemoryEmployeeRepository();

        InMemoryMenuItemRepository menuItemRepository =
                new InMemoryMenuItemRepository();

        employeeService =
                new EmployeeService(employeeRepository);

        menuItemService =
                new MenuItemService(menuItemRepository);
    }

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private String runConsoleWithInput(String input) {
        ByteArrayInputStream simulatedInput =
                new ByteArrayInputStream(
                        input.getBytes(StandardCharsets.UTF_8)
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
                        menuItemService
                );

        consoleMenu.start();

        return capturedOutput.toString(StandardCharsets.UTF_8);
    }

    @Test
    void enteringZeroExitsApplication() {
        String output = runConsoleWithInput("0\n");

        assertTrue(
                output.contains(
                        "Application exited successfully."
                )
        );
    }

    @Test
    void createMenuItemAddsItem() {
        MenuCategory category = MenuCategory.values()[0];

        String input = """
            2
            1
            Burger
            12.50
            %s
            0
            0
            """.formatted(category.name());

        String output = runConsoleWithInput(input);

        assertEquals(
                1,
                menuItemService.getAllMenuItems().size()
        );

        assertTrue(
                output.contains("Menu Item created successfully")
        );

        assertTrue(output.contains("Burger"));
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
                """.formatted(category.name());

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains("Enter a valid price")
        );

        assertEquals(
                1,
                menuItemService.getAllMenuItems().size()
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
                """.formatted(category.name());

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Invalid Category. Choose one from the list."
                )
        );

        assertEquals(
                1,
                menuItemService.getAllMenuItems().size()
        );
    }

    @Test
    void findMenuItemByIdDisplaysExistingItem() {
        MenuCategory category =
                MenuCategory.values()[0];

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                category
        );

        String input = """
                2
                2
                1
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(output.contains("Burger"));
    }

    @Test
    void findMenuItemByIdDisplaysNotFoundMessage() {
        String input = """
                2
                2
                999
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains("Menu item not found.")
        );
    }

    @Test
    void viewAllMenuItemsDisplaysEveryItem() {
        MenuCategory category =
                MenuCategory.values()[0];

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                category
        );

        menuItemService.createMenuItem(
                new BigDecimal("8.25"),
                "Salad",
                category
        );

        String input = """
                2
                3
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(output.contains("Burger"));
        assertTrue(output.contains("Salad"));
    }

    @Test
    void viewAllMenuItemsDisplaysMessageWhenEmpty() {
        String input = """
                2
                3
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "No menu items have been added."
                )
        );
    }

    @Test
    void updateMenuItemNameUpdatesExistingItem() {
        MenuCategory category =
                MenuCategory.values()[0];

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                category
        );

        String input = """
                2
                4
                1
                Double Burger
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item name updated successfully:"
                )
        );

        assertTrue(output.contains("Double Burger"));
    }

    @Test
    void updateMenuItemNameDisplaysNotFoundMessage() {
        String input = """
                2
                4
                999
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains("Menu item not found.")
        );
    }

    @Test
    void updateMenuItemPriceUpdatesExistingItem() {
        MenuCategory category =
                MenuCategory.values()[0];

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                category
        );

        String input = """
                2
                5
                1
                17.75
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item price updated successfully:"
                )
        );

        assertTrue(output.contains("17.75"));
    }

    @Test
    void updateMenuItemPriceDisplaysNotFoundMessage() {
        String input = """
                2
                5
                999
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains("Menu item not found.")
        );
    }

    @Test
    void updateMenuItemCategoryUpdatesExistingItem() {
        MenuCategory originalCategory =
                MenuCategory.values()[0];

        MenuCategory newCategory =
                MenuCategory.values()[1];

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                originalCategory
        );

        String input = """
                2
                6
                1
                %s
                0
                0
                """.formatted(newCategory.name());

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item category updated successfully:"
                )
        );

        assertTrue(
                output.contains(newCategory.name())
        );
    }

    @Test
    void updateMenuItemCategoryDisplaysNotFoundMessage() {
        String input = """
                2
                6
                999
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains("Menu item not found.")
        );
    }

    @Test
    void markMenuItemAvailableDisplaysSuccessMessage() {
        MenuCategory category =
                MenuCategory.values()[0];

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                category
        );

        menuItemService.markItemUnavailable(1);

        String input = """
                2
                7
                1
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item marked as available:"
                )
        );
    }

    @Test
    void markMenuItemAvailableDisplaysNotFoundMessage() {
        String input = """
                2
                7
                999
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains("Menu item not found.")
        );
    }

    @Test
    void markMenuItemUnavailableDisplaysSuccessMessage() {
        MenuCategory category =
                MenuCategory.values()[0];

        menuItemService.createMenuItem(
                new BigDecimal("12.50"),
                "Burger",
                category
        );

        String input = """
                2
                8
                1
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains(
                        "Menu item marked as unavailable:"
                )
        );
    }

    @Test
    void markMenuItemUnavailableDisplaysNotFoundMessage() {
        String input = """
                2
                8
                999
                0
                0
                """;

        String output = runConsoleWithInput(input);

        assertTrue(
                output.contains("Menu item not found.")
        );
    }
}