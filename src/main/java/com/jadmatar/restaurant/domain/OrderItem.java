package com.jadmatar.restaurant.domain;

import java.math.BigDecimal;

public class OrderItem {
    private final MenuItem menuItem;
    private final BigDecimal unitPrice;
    private int quantity;
    private final BigDecimal discountPercentage;

    public OrderItem(MenuItem menuItem, int quantity, BigDecimal unitPrice, BigDecimal discountPercentage) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu Item cannot be null");
        }
        this.menuItem = menuItem;
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be 0");
        }
        this.quantity = quantity;
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be less than 0");
        }
        this.unitPrice = unitPrice;
        if (discountPercentage == null) {
            discountPercentage = BigDecimal.ZERO;
        }

        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }

        if (discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed 100%");
        }
        this.discountPercentage=discountPercentage;
    }
}