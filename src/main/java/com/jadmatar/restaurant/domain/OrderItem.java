package com.jadmatar.restaurant.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderItem {
    private static final BigDecimal ONE_HUNDRED =
            BigDecimal.valueOf(100);

    private final MenuItem menuItem;
    private final BigDecimal unitPrice;
    private final BigDecimal discountPercentage;
    private final int quantity;

    public OrderItem(
            MenuItem menuItem,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal discountPercentage
    ) {
        if (menuItem == null) {
            throw new IllegalArgumentException(
                    "Menu item cannot be null"
            );
        }

        if (menuItem.getId() == null) {
            throw new IllegalArgumentException(
                    "Menu item must already be stored in the database"
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (unitPrice == null) {
            throw new IllegalArgumentException(
                    "Unit price cannot be null"
            );
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Unit price cannot be negative"
            );
        }

        BigDecimal actualDiscount = validateDiscount(discountPercentage);

        this.menuItem = menuItem;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountPercentage = actualDiscount;
    }

    private static BigDecimal validateDiscount(BigDecimal discountPercentage) {
        BigDecimal actualDiscount =
                discountPercentage == null
                        ? BigDecimal.ZERO
                        : discountPercentage;

        if (actualDiscount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Discount cannot be negative"
            );
        }

        if (actualDiscount.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException(
                    "Discount cannot exceed 100%"
            );
        }
        return actualDiscount;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public BigDecimal totalPrice() {
        BigDecimal subtotal =
                unitPrice.multiply(BigDecimal.valueOf(quantity));

        BigDecimal remainingPercentage =
                ONE_HUNDRED.subtract(discountPercentage);

        return subtotal
                .multiply(remainingPercentage)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

}