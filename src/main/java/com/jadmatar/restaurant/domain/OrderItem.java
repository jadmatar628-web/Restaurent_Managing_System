package com.jadmatar.restaurant.domain;

import java.math.BigDecimal;

public class OrderItem {

    private final MenuItem menuItem;
    private int quantity;
    private final BigDecimal unitPrice;
    private BigDecimal discount;

    public OrderItem(MenuItem menuItem, int quantity,BigDecimal unitPrice,BigDecimal discount) {
        if(menuItem==null){
            throw new IllegalArgumentException("Menu Item cannot be null");
        }
        this.menuItem = menuItem;
        if(quantity==0){
            throw new IllegalArgumentException("Quantity cannot be 0");
        }
        this.quantity = quantity;
        if(unitPrice.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException("Price cannot be 0");
        }
        this.unitPrice = unitPrice;
    }
}