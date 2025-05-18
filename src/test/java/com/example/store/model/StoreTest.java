package com.example.store.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

class StoreTest {
    private Store store;
    private FoodProduct milk;
    private NonFoodProduct soap;
    private Cashier cashier;
    private CashRegister register;

    @BeforeEach
    void setUp() {
        Receipt.resetReceiptNumber();
        store = new Store("Test Store", 20.0, 30.0, 7, 15.0);
        
        milk = new FoodProduct("F001", "Milk", 2.50, 
                               LocalDate.now().plusDays(5), 100);

        // Коригирана дата за сапуна: да изтича след 5 дни
        soap = new NonFoodProduct("NF001", "Soap", 3.00, 
                                  LocalDate.now().plusDays(5), 200);
        
        cashier = new Cashier("C001", "John Doe", 1500.0);
        register = new CashRegister("R001");
        
        store.addProduct(milk);
        store.addProduct(soap);
        store.addCashier(cashier);
        store.addCashRegister(register);
        cashier.assignToRegister(register);
    }

    @Test
    void testStoreCreation() {
        assertEquals("Test Store", store.getName());
        assertEquals(0.0, store.getTotalRevenue());
        assertEquals(850.0, store.getTotalDeliveryCosts()); // (2.50 * 100) + (3.00 * 200) = 850.0
        assertEquals(1500.0, store.getTotalSalaryCosts());
        assertEquals(0, store.getTotalReceiptsCount());
    }

    @Test
    void testProductManagement() {
        assertEquals(2, store.getProducts().size());
        assertTrue(store.getProducts().contains(milk));
        assertTrue(store.getProducts().contains(soap));
    }

    @Test
    void testCashierManagement() {
        assertEquals(1, store.getCashiers().size());
        assertTrue(store.getCashiers().contains(cashier));
    }

    @Test
    void testRegisterManagement() {
        assertEquals(1, store.getRegisters().size());
        assertTrue(store.getRegisters().contains(register));
    }

    @Test
    void testPriceCalculation() {
        double markup = 30.0; // 30% надценка
        int daysUntilDiscount = 7;
        double discount = 15.0; // 15% отстъпка

        // Продукт НЕ е близо до изтичане
        Product shampoo = new NonFoodProduct("001", "Shampoo", 5.00, 
            LocalDate.now().plusDays(30), 20);
        double expectedPriceWithoutDiscount = 5.00 * (1 + markup / 100.0);
        double actualPriceWithoutDiscount = shampoo.calculateSellingPrice(markup, daysUntilDiscount, discount);

        assertEquals(expectedPriceWithoutDiscount, actualPriceWithoutDiscount, 0.001,
            "Price should be without discount when product is not near expiration");

        // Продукт Е близо до изтичане
        Product soap = new NonFoodProduct("002", "Soap", 3.00, 
            LocalDate.now().plusDays(3), 10);
        double expectedPriceWithDiscount = 3.00 * (1 + markup / 100.0) * (1 - discount / 100.0);
        double actualPriceWithDiscount = soap.calculateSellingPrice(markup, daysUntilDiscount, discount);

        assertEquals(expectedPriceWithDiscount, actualPriceWithDiscount, 0.001,
            "Price should include discount when product is near expiration");
    }




    @Test
    void testSaleProcessing() {
        Map<Product, Integer> items = new HashMap<>();
        items.put(milk, 2);
        items.put(soap, 1);

        store.processSale(register, items);
        
        assertEquals(1, store.getTotalReceiptsCount());
        assertTrue(store.getTotalRevenue() > 0);
        assertEquals(98, milk.getQuantity()); // 100 - 2
        assertEquals(199, soap.getQuantity()); // 200 - 1
    }

    @Test
    void testSaleWithExpiredProduct() {
        FoodProduct expiredMilk = new FoodProduct("F002", "Expired Milk", 2.50, 
                                                  LocalDate.now().minusDays(1), 50);
        store.addProduct(expiredMilk);

        Map<Product, Integer> items = new HashMap<>();
        items.put(expiredMilk, 1);

        assertThrows(IllegalStateException.class, () -> 
            store.processSale(register, items));
    }

    @Test
    void testSaleWithoutCashier() {
        cashier.removeFromRegister();
        
        Map<Product, Integer> items = new HashMap<>();
        items.put(milk, 1);

        assertThrows(IllegalStateException.class, () -> 
            store.processSale(register, items));
    }

    @Test
    void testExpiredProductsList() {
        FoodProduct expiredMilk = new FoodProduct("F002", "Expired Milk", 2.50, 
                                                  LocalDate.now().minusDays(1), 50);
        store.addProduct(expiredMilk);

        assertEquals(1, store.getExpiredProducts().size());
        assertTrue(store.getExpiredProducts().contains(expiredMilk));
    }

    @Test
    void testNearExpirationProductsList() {
        FoodProduct nearExpMilk = new FoodProduct("F003", "Near Exp Milk", 2.50, 
                                                  LocalDate.now().plusDays(5), 50);
        store.addProduct(nearExpMilk);

        assertEquals(3, store.getProductsNearExpiration().size()); // milk, soap, nearExpMilk
        assertTrue(store.getProductsNearExpiration().contains(nearExpMilk));
        assertTrue(store.getProductsNearExpiration().contains(milk));
        assertTrue(store.getProductsNearExpiration().contains(soap));
    }

    @Test
    void testProfitCalculation() {
        Map<Product, Integer> items = new HashMap<>();
        items.put(milk, 10);
        items.put(soap, 5);

        store.processSale(register, items);
        
        double expectedProfit = store.getTotalRevenue() - 
                                store.getTotalDeliveryCosts() - 
                                store.getTotalSalaryCosts();
        
        assertEquals(expectedProfit, store.calculateProfit(), 0.001);
    }
}
