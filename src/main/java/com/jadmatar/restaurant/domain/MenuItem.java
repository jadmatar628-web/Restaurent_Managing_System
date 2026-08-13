package com.jadmatar.restaurant.domain;

import java.math.BigDecimal;

public class MenuItem {

    private final int id;
    private BigDecimal itemPrice;
    private String itemName;
    private boolean available = true;
    private MenuCategory itemCategory;

    public MenuItem(
            int id,
            BigDecimal itemPrice,
            String itemName,
            MenuCategory itemCategory
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID must be greater than zero"
            );
        }

        if (itemPrice == null ||
                itemPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be null or negative"
            );
        }

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException(
                    "Name cannot be null or blank"
            );
        }

        if (itemCategory == null) {
            throw new IllegalArgumentException(
                    "Category cannot be null"
            );
        }

        this.id = id;
        this.itemPrice = itemPrice;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public MenuCategory getItemCategory() {
        return itemCategory;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        if (itemPrice == null ||
                itemPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be null or negative"
            );
        }

        this.itemPrice = itemPrice;
    }

    public void setItemName(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException(
                    "Name cannot be null or blank"
            );
        }

        this.itemName = itemName;
    }

    public void setItemCategory(MenuCategory itemCategory) {
        if (itemCategory == null) {
            throw new IllegalArgumentException(
                    "Category cannot be null"
            );
        }

        this.itemCategory = itemCategory;
    }

    public void markAvailable() {
        available = true;
    }

    public void markUnavailable() {
        available = false;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + itemName + '\'' +
                ", price=" + itemPrice +
                ", category=" + itemCategory +
                ", available=" + available +
                '}';
    }
}