package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.domain.MenuItem;

import java.util.ArrayList;

public class InMemoryMenuItemRepository implements MenuItemRepository {
    private final ArrayList<MenuItem> menuItems = new ArrayList<>();

    public void add(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("ITEM CANNOT BE NULL");
        }
        for (MenuItem old : menuItems) {
            if (old.getId() == item.getId()) {
                throw new IllegalArgumentException("ITEM ALREADY EXISTS");
            }
        }
        menuItems.add(item);
    }
    public MenuItem findById(int id){
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getId() == id) {
                return menuItem;
            }
        }
        return null;
    }
    public ArrayList<MenuItem> findAll()
    {
        return new ArrayList<>(menuItems);
    }
    public void update(MenuItem menuItem){
        return ;
    }
}
