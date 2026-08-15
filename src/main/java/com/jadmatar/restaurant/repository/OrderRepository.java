package com.jadmatar.restaurant.repository;
import com.jadmatar.restaurant.domain.Order;

import java.util.List;

public interface OrderRepository {
    Order order(Order order);
    Order findById(int id);
    List<Order> findAll();
    void update(Order order);
}
