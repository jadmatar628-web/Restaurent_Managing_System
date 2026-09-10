package com.jadmatar.restaurant.repository;
import com.jadmatar.restaurant.domain.Order;

import java.util.List;

public interface OrderRepository {
    Order add(Order order);
    Order findById(int id);
    List<Order> findAll();
    void updateOrderStatus(Order order);
}
