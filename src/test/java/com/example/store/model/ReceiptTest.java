package com.example.store.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class ReceiptTest {
    private Cashier cashier;
    private CashRegister register;
    private FoodProduct milk;
    private NonFoodProduct soap;

    @BeforeEach
    void setUp() {
        Receipt.resetReceiptNumber();
        cashier = new Cashier("C001", "John Doe", 1500.0);
        register = new CashRegister("R001");
        cashier.assignToRegister(register);
        
        milk = new FoodProduct("F001", "Мляко", 2.50, 
                             LocalDate.now().plusDays(5), 100);
        soap = new NonFoodProduct("NF001", "Сапун", 3.00, 
                                LocalDate.now().plusMonths(6), 200);
    }

    @Test
    void testReceiptCreation() {
        Receipt receipt = register.createReceipt();
        assertNotNull(receipt);
        assertEquals(1, receipt.getReceiptNumber());
        assertEquals(cashier, receipt.getCashier());
        assertTrue(ChronoUnit.SECONDS.between(LocalDateTime.now(), receipt.getTimestamp()) < 1);
        assertEquals(0.0, receipt.getTotalAmount());
        assertTrue(receipt.getItems().isEmpty());
    }

    @Test
    void testAddItemsToReceipt() {
        Receipt receipt = register.createReceipt();
        receipt.addItem(milk, 2, 3.00);
        receipt.addItem(soap, 1, 4.00);

        assertEquals(2, receipt.getItems().size());
        assertEquals(10.0, receipt.getTotalAmount()); // (2 * 3.00) + (1 * 4.00)
        assertEquals(98, milk.getQuantity()); // 100 - 2
        assertEquals(199, soap.getQuantity()); // 200 - 1
    }

    @Test
    void testAddExpiredProductToReceipt() {
        Receipt receipt = register.createReceipt();
        FoodProduct expiredMilk = new FoodProduct("F002", "Expired Milk", 2.50, 
                                                LocalDate.now().minusDays(1), 50);
        
        assertThrows(IllegalStateException.class, () -> 
            receipt.addItem(expiredMilk, 1, 3.00));
    }

    @Test
    void testReceiptTextGeneration() {
        Receipt receipt = register.createReceipt();
        receipt.addItem(milk, 2, 3.00);
        receipt.addItem(soap, 1, 4.00);

        String receiptText = receipt.generateReceiptText();
        
        assertTrue(receiptText.contains("Касов бон #1"));
        assertTrue(receiptText.contains("Касиер: John Doe"));
        assertTrue(receiptText.contains("Мляко x2 @ 3.00 лв."));
        assertTrue(receiptText.contains("Сапун x1 @ 4.00 лв."));
        assertTrue(receiptText.contains("Общо: 10.00 лв."));
    }

    @Test
    void testCashRegisterReceiptCreation() {
        Receipt receipt1 = register.createReceipt();
        Receipt receipt2 = register.createReceipt();
        
        assertEquals(1, receipt1.getReceiptNumber());
        assertEquals(2, receipt2.getReceiptNumber());
        assertEquals(2, register.getReceipts().size());
    }

    @Test
    void testCashRegisterWithoutCashier() {
        cashier.removeFromRegister();
        
        assertThrows(IllegalStateException.class, () -> 
            register.createReceipt());
    }

    @Test
    void testCashierAssignment() {
        Cashier newCashier = new Cashier("C002", "Jane Smith", 1600.0);
        
        cashier.removeFromRegister();
        newCashier.assignToRegister(register);
        
        Receipt newReceipt = register.createReceipt();
        assertEquals(newCashier, newReceipt.getCashier());
    }
} 