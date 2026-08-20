package com.jadmatar.restaurant.domain;

import java.math.BigDecimal;

public class MenuItem {

    private Integer id;
    private BigDecimal itemPrice;
    private String itemName;
    private boolean isAvailable;
    private MenuCategory itemCategory;

    public MenuItem(
            Integer id,
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
        this.isAvailable=true;
    }
    public MenuItem(
            Integer id,
            BigDecimal itemPrice,
            String itemName,
            MenuCategory itemCategory,
            boolean isAvailable
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
        this.isAvailable=isAvailable;
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
        return isAvailable;
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
        isAvailable = true;
    }

    public void markUnavailable() {
        isAvailable = false;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + itemName + '\'' +
                ", price=" + itemPrice +
                ", category=" + itemCategory +
                ", isAvailable=" + isAvailable +
                '}';
    }
    public void setId(int Id){
        if(Id<=0){
            throw new IllegalArgumentException("ID cannot be <=0");
        }
        if(this.id!=null){
            throw new RuntimeException("Item ID already taken");
        }
        this.id=Id;
    }
}