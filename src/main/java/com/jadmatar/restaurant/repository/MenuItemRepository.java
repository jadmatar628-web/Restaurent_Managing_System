package com.jadmatar.restaurant.repository;
import com.jadmatar.restaurant.domain.MenuItem;

import java.util.List;

public interface MenuItemRepository {
    void add(MenuItem menuItem);

    MenuItem findById(int id);

    List<MenuItem> findAll();

    void update(MenuItem menuItem);
}
