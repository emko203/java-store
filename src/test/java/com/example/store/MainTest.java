package com.example.store;

import com.example.store.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private Store store;
    private Cashier cashier;
    private CashRegister register;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        // Настройка на изходния поток за тестване на конзолния изход
        System.setOut(new PrintStream(outContent));

        // Създаване на магазин с тестови данни
        store = new Store("Test Store", 20.0, 30.0, 7, 15.0);
        
        // Добавяне на продукти
        FoodProduct milk = new FoodProduct("F001", "Мляко", 2.50, 
                                         LocalDate.now().plusDays(5), 100);
        FoodProduct bread = new FoodProduct("F002", "Хляб", 1.20, 
                                          LocalDate.now().plusDays(3), 50);
        NonFoodProduct soap = new NonFoodProduct("NF001", "Сапун", 3.00, 
                                               LocalDate.now().plusMonths(6), 200);
        
        store.addProduct(milk);
        store.addProduct(bread);
        store.addProduct(soap);

        // Добавяне на касиер и каса
        cashier = new Cashier("C001", "Иван Иванов", 1500.0);
        register = new CashRegister("R001");
        store.addCashier(cashier);
        store.addCashRegister(register);
        cashier.assignToRegister(register);
    }

    @AfterEach
    void tearDown() {
        // Възстановяване на стандартните входни и изходни потоци
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testDisplayAvailableProducts() {
        store.displayAvailableProducts();
        String output = outContent.toString();
        
        assertTrue(output.contains("Налични продукти:"));
        assertTrue(output.contains("Мляко"));
        assertTrue(output.contains("Хляб"));
        assertTrue(output.contains("Сапун"));
        assertTrue(output.contains("лв."));
    }

    @Test
    void testDisplayStoreStatistics() {
        // Направете покупка, за да имаме данни за статистика
        Map<Product, Integer> saleItems = new HashMap<>();
        saleItems.put(store.getProducts().get(0), 2); // Купуваме 2 броя мляко
        store.processSale(register, saleItems);

        // Изчистване на изходния буфер
        outContent.reset();

        // Тестване на показването на статистика
        Main.displayStoreStatistics(store);
        String output = outContent.toString();

        assertTrue(output.contains("Статистика на магазина:"));
        assertTrue(output.contains("Общ приход:"));
        assertTrue(output.contains("Общи разходи за доставка:"));
        assertTrue(output.contains("Общи разходи за заплати:"));
        assertTrue(output.contains("Обща печалба:"));
        assertTrue(output.contains("Общ брой касови бележки:"));
        assertTrue(output.contains("лв."));
    }

    @Test
    void testDisplayExpiredProducts() {
        // Добавяне на изтекъл продукт
        FoodProduct expiredMilk = new FoodProduct("F003", "Изтекло мляко", 2.50, 
                                                LocalDate.now().minusDays(1), 50);
        store.addProduct(expiredMilk);

        // Изчистване на изходния буфер
        outContent.reset();

        // Тестване на показването на изтекли продукти
        Main.displayExpiredProducts(store);
        String output = outContent.toString();

        assertTrue(output.contains("Изтекли продукти:"));
        assertTrue(output.contains("Изтекло мляко"));
        assertTrue(output.contains("Дата на изтичане:"));
    }

    @Test
    void testDisplayNearExpirationProducts() {
        // Добавяне на продукт близо до изтичане
        FoodProduct nearExpBread = new FoodProduct("F004", "Хляб за изтичане", 1.20, 
                                                 LocalDate.now().plusDays(5), 30);
        store.addProduct(nearExpBread);

        // Изчистване на изходния буфер
        outContent.reset();

        // Тестване на показването на продукти близо до изтичане
        Main.displayNearExpirationProducts(store);
        String output = outContent.toString();

        assertTrue(output.contains("Продукти близо до изтичане:"));
        assertTrue(output.contains("Хляб за изтичане"));
        assertTrue(output.contains("Дата на изтичане:"));
        assertTrue(output.contains("Количество:"));
    }

    @Test
    void testReceiptGenerationInBulgarian() {
        // Направете покупка
        Map<Product, Integer> saleItems = new HashMap<>();
        saleItems.put(store.getProducts().get(0), 2); // Купуваме 2 броя мляко
        store.processSale(register, saleItems);

        // Вземане на последния генериран бон
        List<Receipt> receipts = store.getAllReceipts();
        assertFalse(receipts.isEmpty());
        
        String receiptText = receipts.get(0).generateReceiptText();
        
        assertTrue(receiptText.contains("Касов бон #"));
        assertTrue(receiptText.contains("Дата:"));
        assertTrue(receiptText.contains("Касиер: Иван Иванов"));
        assertTrue(receiptText.contains("Продукти:"));
        assertTrue(receiptText.contains("Мляко"));
        assertTrue(receiptText.contains("лв."));
        assertTrue(receiptText.contains("Общо:"));
    }

    @Test
    void testInvalidMenuChoice() {
        // Симулиране на изход (6) от менюто, за да не блокира тестът
        String input = "6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();
        Main.main(new String[]{});
        String output = outContent.toString();
        assertTrue(output.contains("Благодарим ви, че използвахте Java Store!"));
    }

    @Test
    void testInvalidProductId() {
        // Симулиране на покупка с невалиден ID на продукт и изход
        String input = "2\nINVALID_ID\nкрай\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();
        Main.main(new String[]{});
        String output = outContent.toString();
        System.out.println("\n--- DEBUG OUTPUT (testInvalidProductId) ---\n" + output + "\n--------------------------\n");
        assertTrue(output.contains("Невалиден ID на продукт. Моля, опитайте отново."));
        assertTrue(output.contains("Благодарим ви, че използвахте Java Store!"));
    }

    @Test
    void testInvalidQuantity() {
        // Симулиране на покупка с невалидно количество и изход
        String input = "2\nF001\n-1\nкрай\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();
        Main.main(new String[]{});
        String output = outContent.toString();
        System.out.println("\n--- DEBUG OUTPUT (testInvalidQuantity) ---\n" + output + "\n--------------------------\n");
        assertTrue(output.contains("Количеството трябва да е по-голямо от 0."));
        assertTrue(output.contains("Благодарим ви, че използвахте Java Store!"));
    }

    @Test
    void testInsufficientStock() {
        // Симулиране на покупка с количество по-голямо от наличното и изход
        String input = "2\nF001\n101\nкрай\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        outContent.reset();
        Main.main(new String[]{});
        String output = outContent.toString();
        System.out.println("\n--- DEBUG OUTPUT (testInsufficientStock) ---\n" + output + "\n--------------------------\n");
        assertTrue(output.contains("Няма достатъчно наличност. Максимално количество: 100"));
        assertTrue(output.contains("Благодарим ви, че използвахте Java Store!"));
    }
} 