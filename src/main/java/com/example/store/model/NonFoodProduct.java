package com.example.store.model;

import java.time.LocalDate;

public class NonFoodProduct extends Product {
    public NonFoodProduct(String id, String name, double deliveryPrice, 
                         LocalDate expirationDate, int quantity) {
        super(id, name, deliveryPrice, ProductCategory.NON_FOOD, expirationDate, quantity);
    }

    @Override
    public double calculateSellingPrice(double markupPercentage, 
                                      int daysUntilDiscount, 
                                      double discountPercentage) {
        if (isExpired()) {
            throw new IllegalStateException("Cannot calculate price for expired product: " + getName());
        }

        double basePrice = getDeliveryPrice() * (1 + markupPercentage / 100.0);
        
        if (isNearExpiration(daysUntilDiscount)) {
            return basePrice * (1 - discountPercentage / 100.0);
        }
        
        return basePrice;
    }
} 