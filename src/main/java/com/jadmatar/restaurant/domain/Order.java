package com.jadmatar.restaurant.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final int id;
    private final int employeeId;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private List<OrderItem> items;
    private OrderType orderType;
    public Order(int id, int employeeId, LocalDateTime createdAt,OrderStatus status, List<OrderItem> items, OrderType orderType){
        id=1;

    }
}
