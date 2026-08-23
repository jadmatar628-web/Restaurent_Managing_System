package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.domain.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMenuItemRepository implements MenuItemRepository {

    private final List<MenuItem> menuItems = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void add(MenuItem item) {

        if (item == null) {
            throw new IllegalArgumentException(
                    "ITEM CANNOT BE NULL"
            );
        }

        item.setId(nextId++);

        menuItems.add(item);
    }

    @Override
    public MenuItem findById(int id) {

        for (MenuItem menuItem : menuItems) {

            if (menuItem.getId() == id) {
                return menuItem;
            }
        }

        return null;
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(menuItems);
    }

    @Override
    public void update(MenuItem menuItem) {
        // Nothing needed for the in-memory version.
        // The stored object and the object modified by the service
        // are the same Java object.
    }
}