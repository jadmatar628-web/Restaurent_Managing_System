package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcMenuItemRepositoryIntegrationTest {

    private JdbcMenuItemRepository repository;

    @BeforeEach
    void setUp() throws SQLException {

        ensureTestDatabase();

        repository = new JdbcMenuItemRepository();

        clearMenuItems();
    }

    private void ensureTestDatabase() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            String databaseName = connection.getCatalog();

            assertTrue(
                    databaseName.toLowerCase().contains("test"),
                    "JDBC integration tests must use a test database. Current database: "
                            + databaseName
            );
        }
    }

    private void clearMenuItems() throws SQLException {

        String sql = """
                DELETE FROM MENU_ITEM
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.executeUpdate();
        }
    }

    private MenuItem createTestItem(String name, String price) {

        return new MenuItem(
                new BigDecimal(price),
                name,
                MenuCategory.BURGERS
        );
    }


    @Test
    void addStoresMenuItemAndAssignsGeneratedId() {

        MenuItem item = createTestItem(
                "Test Burger",
                "5.50"
        );

        repository.add(item);

        assertNotNull(item.getId());
        assertTrue(item.getId() > 0);

        String sql = """
                SELECT
                    ITEM_NAME,
                    ITEM_PRICE,
                    ITEM_CATEGORY,
                    IS_AVAILABLE
                FROM MENU_ITEM
                WHERE MENU_ITEM_ID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, item.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());

                assertEquals(
                        "Test Burger",
                        resultSet.getString("ITEM_NAME")
                );

                assertEquals(
                        0,
                        new BigDecimal("5.50").compareTo(
                                resultSet.getBigDecimal("ITEM_PRICE")
                        )
                );

                assertEquals(
                        "BURGERS",
                        resultSet.getString("ITEM_CATEGORY")
                );

                assertTrue(
                        resultSet.getBoolean("IS_AVAILABLE")
                );
            }
        } catch (SQLException e) {
            fail(e);
        }
    }


    @Test
    void findByIdReturnsStoredMenuItem() {

        MenuItem item = createTestItem(
                "Chicken Burger",
                "7.25"
        );

        repository.add(item);

        MenuItem found =
                repository.findById(item.getId());

        assertNotNull(found);

        assertEquals(
                item.getId(),
                found.getId()
        );

        assertEquals(
                "Chicken Burger",
                found.getItemName()
        );

        assertEquals(
                0,
                new BigDecimal("7.25").compareTo(
                        found.getItemPrice()
                )
        );

        assertEquals(
                MenuCategory.BURGERS,
                found.getItemCategory()
        );

        assertTrue(
                found.isAvailable()
        );
    }


    @Test
    void findByIdReturnsNullWhenItemDoesNotExist() {

        MenuItem result =
                repository.findById(999999);

        assertNull(result);
    }


    @Test
    void findAllReturnsStoredMenuItems() {

        MenuItem first =
                createTestItem(
                        "Burger One",
                        "5.00"
                );

        MenuItem second =
                createTestItem(
                        "Burger Two",
                        "6.00"
                );

        repository.add(first);
        repository.add(second);

        List<MenuItem> items =
                repository.findAll();

        assertEquals(
                2,
                items.size()
        );

        assertEquals(
                first.getId(),
                items.get(0).getId()
        );

        assertEquals(
                second.getId(),
                items.get(1).getId()
        );
    }


    @Test
    void updateChangesStoredMenuItem() {

        MenuItem item =
                createTestItem(
                        "Old Burger",
                        "5.00"
                );

        repository.add(item);

        item.setItemName("Updated Burger");
        item.setItemPrice(new BigDecimal("8.50"));
        item.setItemCategory(MenuCategory.SANDWICHES);
        item.markUnavailable();

        repository.update(item);

        MenuItem updated =
                repository.findById(item.getId());

        assertNotNull(updated);

        assertEquals(
                "Updated Burger",
                updated.getItemName()
        );

        assertEquals(
                0,
                new BigDecimal("8.50").compareTo(
                        updated.getItemPrice()
                )
        );

        assertEquals(
                MenuCategory.SANDWICHES,
                updated.getItemCategory()
        );

        assertFalse(
                updated.isAvailable()
        );
    }
}