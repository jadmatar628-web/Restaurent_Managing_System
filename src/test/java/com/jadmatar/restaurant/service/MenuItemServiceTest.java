package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import com.jadmatar.restaurant.repository.InMemoryMenuItemRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemServiceTest {
    private MenuCategory category() {
        return MenuCategory.values()[0];
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
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem createdItem = service.createMenuItem(
                new BigDecimal("5.50"),
                "Burger",
                category()
        );

        MenuItem storedItem = repository.findById(1);

        assertSame(createdItem, storedItem);
        assertEquals(1, createdItem.getId());
        assertEquals("Burger", createdItem.getItemName());
        assertEquals(
                new BigDecimal("5.50"),
                createdItem.getItemPrice()
        );
        assertEquals(category(), createdItem.getItemCategory());
        assertTrue(createdItem.isAvailable());
    }

    @Test
    void createMenuItemGeneratesSequentialIds() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

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

        assertEquals(1, firstItem.getId());
        assertEquals(2, secondItem.getId());
    }

    @Test
    void constructorCalculatesNextIdFromExistingItems() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();

        repository.addItem(
                new MenuItem(
                        3,
                        new BigDecimal("5.00"),
                        "Burger",
                        category()
                )
        );

        repository.addItem(
                new MenuItem(
                        7,
                        new BigDecimal("2.50"),
                        "Fries",
                        category()
                )
        );

        MenuItemService service = new MenuItemService(repository);

        MenuItem createdItem = service.createMenuItem(
                new BigDecimal("1.50"),
                "Water",
                category()
        );

        assertEquals(8, createdItem.getId());
    }

    @Test
    void findItemByIdReturnsExistingItem() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem createdItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem foundItem = service.findItemById(
                createdItem.getId()
        );

        assertSame(createdItem, foundItem);
    }

    @Test
    void findItemByIdReturnsNullWhenItemDoesNotExist() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem result = service.findItemById(999);

        assertNull(result);
    }

    @Test
    void getAllMenuItemsReturnsEveryItem() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

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

        ArrayList<MenuItem> items = service.getAllMenuItems();

        assertEquals(2, items.size());
        assertSame(firstItem, items.get(0));
        assertSame(secondItem, items.get(1));
    }

    @Test
    void updateItemPriceChangesPrice() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem item = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        service.updateItemPrice(
                item.getId(),
                new BigDecimal("6.50")
        );

        assertEquals(
                new BigDecimal("6.50"),
                item.getItemPrice()
        );
    }

    @Test
    void updateItemNameChangesNameAndReturnsItem() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem originalItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem updatedItem = service.updateItemName(
                originalItem.getId(),
                "Cheese Burger"
        );

        assertSame(originalItem, updatedItem);
        assertEquals(
                "Cheese Burger",
                originalItem.getItemName()
        );
    }

    @Test
    void updateItemNameReturnsNullWhenItemDoesNotExist() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem result = service.updateItemName(
                999,
                "Cheese Burger"
        );

        assertNull(result);
    }

    @Test
    void updateItemCategoryChangesCategoryAndReturnsItem() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

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

        assertSame(originalItem, updatedItem);
        assertEquals(newCategory, originalItem.getItemCategory());
    }

    @Test
    void updateItemCategoryReturnsNullWhenItemDoesNotExist() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem result = service.updateItemCategory(
                999,
                category()
        );

        assertNull(result);
    }

    @Test
    void markItemUnavailableMakesItemUnavailable() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem originalItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        MenuItem updatedItem =
                service.markItemUnavailable(originalItem.getId());

        assertSame(originalItem, updatedItem);
        assertFalse(originalItem.isAvailable());
    }

    @Test
    void markItemAvailableMakesItemAvailable() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        MenuItem originalItem = service.createMenuItem(
                new BigDecimal("5.00"),
                "Burger",
                category()
        );

        service.markItemUnavailable(originalItem.getId());

        MenuItem updatedItem =
                service.markItemAvailable(originalItem.getId());

        assertSame(originalItem, updatedItem);
        assertTrue(originalItem.isAvailable());
    }

    @Test
    void availabilityOperationsReturnNullWhenItemDoesNotExist() {
        InMemoryMenuItemRepository repository = new InMemoryMenuItemRepository();
        MenuItemService service = new MenuItemService(repository);

        assertNull(service.markItemAvailable(999));
        assertNull(service.markItemUnavailable(999));
    }
}