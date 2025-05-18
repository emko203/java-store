package com.example.store.model;

import java.time.LocalDate;

public abstract class Product {
    private final String id;
    private final String name;
    private final double deliveryPrice;
    private final ProductCategory category;
    private final LocalDate expirationDate;
    private int quantity;

    public Product(String id, String name, double deliveryPrice, ProductCategory category, 
                  LocalDate expirationDate, int quantity) {
        this.id = id;
        this.name = name;
        this.deliveryPrice = deliveryPrice;
        this.category = category;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    public abstract double calculateSellingPrice(double markupPercentage, 
                                              int daysUntilDiscount, 
                                              double discountPercentage);

    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    public boolean isNearExpiration(int days) {
        return !isExpired() &&
               expirationDate != null &&
               !expirationDate.isBefore(LocalDate.now()) &&
               !expirationDate.isAfter(LocalDate.now().plusDays(days));
    }
    

    public void decreaseQuantity(int amount) {
        if (amount > quantity) {
            throw new InsufficientStockException(this, amount - quantity);
        }
        quantity -= amount;
    }

    public void increaseQuantity(int amount) {
        quantity += amount;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getDeliveryPrice() { return deliveryPrice; }
    public ProductCategory getCategory() { return category; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public int getQuantity() { return quantity; }

    public enum ProductCategory {
        FOOD,
        NON_FOOD
    }

    public static class InsufficientStockException extends RuntimeException {
        private final Product product;
        private final int missingQuantity;

        public InsufficientStockException(Product product, int missingQuantity) {
            super(String.format("Insufficient stock for product %s. Missing quantity: %d", 
                              product.getName(), missingQuantity));
            this.product = product;
            this.missingQuantity = missingQuantity;
        }

        public Product getProduct() { return product; }
        public int getMissingQuantity() { return missingQuantity; }
    }
} 