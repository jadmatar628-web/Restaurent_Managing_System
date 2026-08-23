package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import com.jadmatar.restaurant.repository.JdbcMenuItemRepository;
import com.jadmatar.restaurant.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemServiceTest {

    private MenuItemRepository repository;
    private MenuItemService service;

    private MenuCategory category() {
        return MenuCategory.values()[0];
    }

    @BeforeEach
    void setUp() throws SQLException {

        clearMenuItems();

        repository = new JdbcMenuItemRepository();
        service = new MenuItemService(repository);
    }

    private void clearMenuItems() throws SQLException {

        String sql = "DELETE FROM MENU_ITEM";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();
        }
    }

    @Test
    void constructorRejectsNullRepository() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new MenuItemService(null)
        );
    }

    @Test
    void createMenuItemCreatesAndStoresItem() {

        MenuItem createdItem = service.createMenuItem(
                new BigDecimal("5.50"),
                "Burger",
                category()
        );

        assertNotNull(createdItem.getId());

        MenuItem storedItem =
                repository.findById(createdItem.getId());

        assertNotNull(storedItem);

        assertEquals(
                createdItem.getId(),
                storedItem.getId()
        );

        assertEquals(
                "Burger",
                storedItem.getItemName()
        );

        assertEquals(
                new BigDecimal("5.50"),
                storedItem.getItemPrice()
        );

        assertEquals(
                category(),
                storedItem.getItemCategory()
        );

        assertTrue(storedItem.isAvailable());
    }

    @Test
    void createMenuItemGeneratesIncreasingIds() {

        MenuItem firstItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem secondItem = service.createMenuItem(
                new BigDecimal("2.50"),
                "Fries",
                category()
        );

        assertNotNull(firstItem.getId());
        assertNotNull(secondItem.getId());

        assertTrue(
                secondItem.getId() > firstItem.getId()
        );
    }

    @Test
    void findItemByIdReturnsExistingItem() {

        MenuItem createdItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem foundItem =
                service.findItemById(createdItem.getId());

        assertNotNull(foundItem);

        assertEquals(
                createdItem.getId(),
                foundItem.getId()
        );

        assertEquals(
                createdItem.getItemName(),
                foundItem.getItemName()
        );

        assertEquals(
                createdItem.getItemPrice(),
                foundItem.getItemPrice()
        );

        assertEquals(
                createdItem.getItemCategory(),
                foundItem.getItemCategory()
        );
    }

    @Test
    void findItemByIdReturnsNullWhenItemDoesNotExist() {

        MenuItem result =
                service.findItemById(999999);

        assertNull(result);
    }

    @Test
    void getAllMenuItemsReturnsEveryItem() {

        MenuItem firstItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem secondItem = service.createMenuItem(
                new BigDecimal("2.50"),
                "Fries",
                category()
        );

        List<MenuItem> items =
                service.getAllMenuItems();

        assertEquals(2, items.size());

        assertEquals(
                firstItem.getId(),
                items.get(0).getId()
        );

        assertEquals(
                secondItem.getId(),
                items.get(1).getId()
        );
    }

    @Test
    void updateItemPriceChangesPrice() {

        MenuItem item = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        service.updateItemPrice(
                item.getId(),
                new BigDecimal("6.50")
        );

        MenuItem updated =
                repository.findById(item.getId());

        assertNotNull(updated);

        assertEquals(
                new BigDecimal("6.50"),
                updated.getItemPrice()
        );
    }

    @Test
    void updateItemNameChangesNameAndReturnsItem() {

        MenuItem originalItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem updatedItem = service.updateItemName(
                originalItem.getId(),
                "Cheese Burger"
        );

        assertNotNull(updatedItem);

        assertEquals(
                originalItem.getId(),
                updatedItem.getId()
        );

        assertEquals(
                "Cheese Burger",
                updatedItem.getItemName()
        );

        MenuItem storedItem =
                repository.findById(originalItem.getId());

        assertEquals(
                "Cheese Burger",
                storedItem.getItemName()
        );
    }

    @Test
    void updateItemNameReturnsNullWhenItemDoesNotExist() {

        MenuItem result = service.updateItemName(
                999999,
                "Cheese Burger"
        );

        assertNull(result);
    }

    @Test
    void updateItemCategoryChangesCategoryAndReturnsItem() {

        MenuCategory originalCategory =
                MenuCategory.values()[0];

        MenuCategory newCategory =
                MenuCategory.values()[
                        MenuCategory.values().length - 1
                        ];

        MenuItem originalItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                originalCategory
        );

        MenuItem updatedItem = service.updateItemCategory(
                originalItem.getId(),
                newCategory
        );

        assertNotNull(updatedItem);

        assertEquals(
                newCategory,
                updatedItem.getItemCategory()
        );

        MenuItem storedItem =
                repository.findById(originalItem.getId());

        assertEquals(
                newCategory,
                storedItem.getItemCategory()
        );
    }

    @Test
    void updateItemCategoryReturnsNullWhenItemDoesNotExist() {

        MenuItem result = service.updateItemCategory(
                999999,
                category()
        );

        assertNull(result);
    }

    @Test
    void markItemUnavailableMakesItemUnavailable() {

        MenuItem originalItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem updatedItem =
                service.markItemUnavailable(
                        originalItem.getId()
                );

        assertNotNull(updatedItem);
        assertFalse(updatedItem.isAvailable());

        MenuItem storedItem =
                repository.findById(originalItem.getId());

        assertFalse(storedItem.isAvailable());
    }

    @Test
    void markItemAvailableMakesItemAvailable() {

        MenuItem originalItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        service.markItemUnavailable(
                originalItem.getId()
        );

        MenuItem updatedItem =
                service.markItemAvailable(
                        originalItem.getId()
                );

        assertNotNull(updatedItem);
        assertTrue(updatedItem.isAvailable());

        MenuItem storedItem =
                repository.findById(originalItem.getId());

        assertTrue(storedItem.isAvailable());
    }

    @Test
    void availabilityOperationsReturnNullWhenItemDoesNotExist() {

        assertNull(
                service.markItemAvailable(999999)
        );

        assertNull(
                service.markItemUnavailable(999999)
        );
    }
}