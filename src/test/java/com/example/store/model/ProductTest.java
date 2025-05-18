package com.example.store.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class ProductTest {
    private FoodProduct milk;
    private NonFoodProduct soap;
    private LocalDate tomorrow;
    private LocalDate nextWeek;
    private LocalDate lastWeek;

    @BeforeEach
    void setUp() {
        tomorrow = LocalDate.now().plusDays(1);
        nextWeek = LocalDate.now().plusDays(7);
        lastWeek = LocalDate.now().minusDays(7);
        
        milk = new FoodProduct("F001", "Milk", 2.50, tomorrow, 100);
        soap = new NonFoodProduct("NF001", "Soap", 3.00, nextWeek, 200);
    }

    @Test
    void testProductCreation() {
        assertEquals("F001", milk.getId());
        assertEquals("Milk", milk.getName());
        assertEquals(2.50, milk.getDeliveryPrice());
        assertEquals(Product.ProductCategory.FOOD, milk.getCategory());
        assertEquals(100, milk.getQuantity());
        
        assertEquals("NF001", soap.getId());
        assertEquals("Soap", soap.getName());
        assertEquals(3.00, soap.getDeliveryPrice());
        assertEquals(Product.ProductCategory.NON_FOOD, soap.getCategory());
        assertEquals(200, soap.getQuantity());
    }

    @Test
    void testExpiration() {
        FoodProduct expiredMilk = new FoodProduct("F002", "Expired Milk", 2.50, lastWeek, 50);
        assertTrue(expiredMilk.isExpired());
        assertFalse(milk.isExpired());
        assertFalse(soap.isExpired());
    }

    @Test
    void testNearExpiration() {
        assertTrue(milk.isNearExpiration(2));
        assertFalse(soap.isNearExpiration(2));
    }

    @Test
    void testQuantityManagement() {
        milk.decreaseQuantity(50);
        assertEquals(50, milk.getQuantity());
        
        milk.increaseQuantity(25);
        assertEquals(75, milk.getQuantity());
        
        assertThrows(Product.InsufficientStockException.class, () -> 
            milk.decreaseQuantity(100));
    }

    @Test
    void testPriceCalculation() {
        // Test food product price calculation
        double foodPrice = milk.calculateSellingPrice(20.0, 2, 15.0);
        double expectedFoodPrice = 2.50 * 1.20 * 0.85; // delivery price * markup * discount
        assertEquals(expectedFoodPrice, foodPrice, 0.001);

        // Test non-food product price calculation
        double nonFoodPrice = soap.calculateSellingPrice(30.0, 2, 15.0);
        double expectedNonFoodPrice = 3.00 * 1.30; // delivery price * markup (no discount)
        assertEquals(expectedNonFoodPrice, nonFoodPrice, 0.001);
    }

    @Test
    void testExpiredProductPriceCalculation() {
        FoodProduct expiredMilk = new FoodProduct("F002", "Expired Milk", 2.50, lastWeek, 50);
        assertThrows(IllegalStateException.class, () -> 
            expiredMilk.calculateSellingPrice(20.0, 2, 15.0));
    }
} 