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
    private Integer orderNumber;

    public Order(int employeeId, OrderType orderType) { //to create brand-new order
        if (employeeId < 1) {
            throw new IllegalArgumentException("Employee ID cannot be less than 1");
        }
        this.employeeId = employeeId;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.OPEN;
        if (orderType == null) {
            throw new IllegalArgumentException("Order Type cannot be NULL");
        }
        this.orderType = orderType;
        this.orderNumber=null;
    }
    public Order(
            int id,
            int employeeId,
            LocalDateTime createdAt,
            OrderStatus status,
            OrderType orderType,
            int orderNumber,
            List<OrderItem> loadedItems
    ) {
        if (id < 1) {
            throw new IllegalArgumentException(
                    "Order ID must be positive"
            );
        }

        if (employeeId < 1) {
            throw new IllegalArgumentException(
                    "Employee ID must be positive"
            );
        }

        if (createdAt == null) {
            throw new IllegalArgumentException(
                    "Creation time cannot be null"
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Order status cannot be null"
            );
        }

        if (orderType == null) {
            throw new IllegalArgumentException(
                    "Order type cannot be null"
            );
        }

        if (orderNumber < 1) {
            throw new IllegalArgumentException(
                    "Order number must be positive"
            );
        }

        if (loadedItems == null) {
            throw new IllegalArgumentException(
                    "Loaded order items cannot be null"
            );
        }

        for (OrderItem item : loadedItems) {
            if (item == null) {
                throw new IllegalArgumentException(
                        "Loaded order cannot contain a null item"
                );
            }
        }

        this.id = id;
        this.employeeId = employeeId;
        this.createdAt = createdAt;
        this.status = status;
        this.orderType = orderType;
        this.orderNumber = orderNumber;

        // Intentionally bypasses addItem(), because this is database restoration.
        this.items.addAll(loadedItems);
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
        if (!isEditable()) {
            throw new IllegalStateException(
                    "Items can only be added to an OPEN order"
            );
        }

        if (item == null) {
            throw new IllegalArgumentException(
                    "Order item cannot be null"
            );
        }

        items.add(item);
    }

    public void removeItem(OrderItem item) {
        if (!isEditable()) {
            throw new IllegalStateException(
                    "Items can only be removed from an OPEN order"
            );
        }

        if (item == null) {
            throw new IllegalArgumentException(
                    "Order item cannot be null"
            );
        }

        if (!items.remove(item)) {
            throw new IllegalArgumentException(
                    "Order does not contain the specified item"
            );
        }
    }
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.totalPrice());
        }
        return total;
    }
    public void markPlaced() {
        if (status != OrderStatus.OPEN) {
            throw new IllegalStateException("Only an OPEN order can be placed");
        }

        if (items.isEmpty()) {
            throw new IllegalStateException("An empty order cannot be placed");
        }

        // OrderService calls this only after payment succeeds.
        status = OrderStatus.PLACED;
    }

    public void markPreparing() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException(
                    "Only a PLACED order can be marked as PREPARING"
            );
        }

        status = OrderStatus.PREPARING;
    }

    public void markReady() {
        if (status != OrderStatus.PREPARING) {
            throw new IllegalStateException(
                    "Only a PREPARING order can be marked as READY"
            );
        }

        status = OrderStatus.READY;
    }
    public void markPickedUp() {
        if (orderType != OrderType.PICKUP) {
            throw new IllegalStateException(
                    "Only a PICKUP order can be marked as PICKED_UP"
            );
        }

        if (status != OrderStatus.READY) {
            throw new IllegalStateException(
                    "Only a READY order can be marked as PICKED_UP"
            );
        }

        status = OrderStatus.PICKED_UP;
    }


    public void markDispatched() {
        if (orderType != OrderType.DELIVERY) {
            throw new IllegalStateException(
                    "Only a DELIVERY order can be dispatched"
            );
        }

        if (status != OrderStatus.READY) {
            throw new IllegalStateException(
                    "Only a READY order can be dispatched"
            );
        }

        status = OrderStatus.DISPATCHED;
    }

    public void markOnTheWay() {
        if (orderType != OrderType.DELIVERY) {
            throw new IllegalStateException(
                    "Only a DELIVERY order can be marked as ON_THE_WAY"
            );
        }

        if (status != OrderStatus.DISPATCHED) {
            throw new IllegalStateException(
                    "Only a DISPATCHED order can be marked as ON_THE_WAY"
            );
        }

        status = OrderStatus.ON_THE_WAY;
    }

    public void markCompleted() {
        boolean validPickupCompletion =
                orderType == OrderType.PICKUP
                        && status == OrderStatus.PICKED_UP;

        boolean validDeliveryCompletion =
                orderType == OrderType.DELIVERY
                        && status == OrderStatus.ON_THE_WAY;

        if (!validPickupCompletion && !validDeliveryCompletion) {
            throw new IllegalStateException(
                    "Order cannot be marked as COMPLETED from its current state"
            );
        }

        status = OrderStatus.COMPLETED;
    }

    public void markVoided() {
        if (!isVoidable()) {
            throw new IllegalStateException(
                    "Order cannot be VOIDED from its current state"
            );
        }

        status = OrderStatus.VOIDED;
    }

    public void markRefunded() {
        if (!isRefundable()) {
            throw new IllegalStateException(
                    "Only a COMPLETED order can be REFUNDED"
            );
        }

        status = OrderStatus.REFUNDED;
    }

    public boolean isEditable() {
        return status == OrderStatus.OPEN;
    }

    public boolean isVoidable() {
        return status == OrderStatus.PLACED
                || status == OrderStatus.PREPARING
                || status == OrderStatus.READY;
    }

    public boolean isRefundable() {
        return status == OrderStatus.COMPLETED;
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

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void assignOrderNumber(int orderNumber) {
        if (orderNumber < 1) {
            throw new IllegalArgumentException(
                    "Order number must be positive"
            );
        }

        if (this.orderNumber != null) {
            throw new IllegalStateException(
                    "Order number has already been assigned"
            );
        }

        this.orderNumber = orderNumber;
    }
    public void changeOrderType(OrderType newOrderType) {
        if (!isEditable()) {
            throw new IllegalStateException(
                    "Order type can only be changed while the order is OPEN"
            );
        }

        if (newOrderType == null) {
            throw new IllegalArgumentException(
                    "Order type cannot be null"
            );
        }

        this.orderType = newOrderType;
    }
}
