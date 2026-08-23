package com.jadmatar.restaurant.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final int employeeId;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items = new ArrayList<>();
    private Integer id;
    private OrderStatus status;
    private OrderType orderType;

    public Order(int employeeId, OrderType orderType) {
        if (employeeId < 1) {
            throw new IllegalArgumentException("Employee ID cannot be less than 1");
        }
        this.employeeId = employeeId;
        createdAt = LocalDateTime.now();
        status = OrderStatus.PLACED;
        if (orderType == null) {
            throw new IllegalArgumentException("Order Type cannot be NULL");
        }
        this.orderType = orderType;
    }

    public Integer getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }

        items.add(item);
    }

    public void removeItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item to remove cannot be NULL");
        }
        items.remove(item);
    }

    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.totalPrice());
        }
        return total;
    }
    public void markPreparing(){
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Order cannot be marked as Preparing");
        }
        status = OrderStatus.PREPARING;
    }

    public void markReady() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Order cannot be marked as READY");
        }
        status = OrderStatus.READY;
    }

    public void markDispatched() {
        if (orderType!=OrderType.DELIVERY|| status != OrderStatus.READY) {
            throw new IllegalStateException("Order cannot be marked as Dispatched");
        }
        status = OrderStatus.DISPATCHED;
    }


    public void markOnTheWay() {
        if (status != OrderStatus.DISPATCHED || orderType != OrderType.DELIVERY) {
            throw new IllegalStateException("Order cannot be marked as ON THE WAY");
        }
        status = OrderStatus.ON_THE_WAY;
    }

    public void markCompleted() {
        boolean validPickupCompletion = orderType == OrderType.PICKUP && status==OrderStatus.READY;

        boolean validDeliveryCompletion = orderType == OrderType.DELIVERY && status == OrderStatus.ON_THE_WAY;

        if (!validPickupCompletion && !validDeliveryCompletion) {
            throw new IllegalStateException("Order cannot be marked as COMPLETED");
        }

        status = OrderStatus.COMPLETED;
    }

    public void markVoided() {
        if (status != OrderStatus.PLACED && status != OrderStatus.READY) {
            throw new IllegalStateException("Order cannot be VOIDED");
        }
        status = OrderStatus.VOIDED;
    }

    public void markRefunded() {
        if (status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order cannot be REFUNDED");
        }
        status = OrderStatus.REFUNDED;
    }
    public boolean isEditable() {
        return status == OrderStatus.PLACED || status == OrderStatus.PREPARING;
    }
    public boolean isRefundable(){
        if(status!=OrderStatus.COMPLETED){
            return false;
        }
        return true;
    }
    public boolean isVoidable(){
        if(status!=OrderStatus.READY){
            return false;
        }
        return true;
    }
    public void assignId(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Order ID must be positive");
        }
        if (this.id != null) {
            throw new IllegalStateException("Order already has an ID");
        }

        this.id = id;
    }
}
