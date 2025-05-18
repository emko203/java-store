package com.example.store;

import com.example.store.model.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Create a store with specific markup and discount settings
        Store store = new Store("Java Store", 
                               20.0,  // 20% markup for food
                               30.0,  // 30% markup for non-food
                               7,     // 7 days until discount
                               15.0); // 15% discount for near-expiration items

        // Add some products
        FoodProduct milk = new FoodProduct("F001", "Milk", 2.50, 
                                         LocalDate.now().plusDays(5), 100);
        FoodProduct bread = new FoodProduct("F002", "Bread", 1.20, 
                                          LocalDate.now().plusDays(3), 50);
        NonFoodProduct soap = new NonFoodProduct("NF001", "Soap", 3.00, 
                                               LocalDate.now().plusMonths(6), 200);

        store.addProduct(milk);
        store.addProduct(bread);
        store.addProduct(soap);

        // Add cashiers
        Cashier cashier1 = new Cashier("C001", "John Doe", 1500.0);
        Cashier cashier2 = new Cashier("C002", "Jane Smith", 1600.0);
        
        store.addCashier(cashier1);
        store.addCashier(cashier2);

        // Add cash registers
        CashRegister register1 = new CashRegister("R001");
        CashRegister register2 = new CashRegister("R002");
        
        store.addCashRegister(register1);
        store.addCashRegister(register2);

        // Assign cashiers to registers
        cashier1.assignToRegister(register1);
        cashier2.assignToRegister(register2);

        // Process a sale
        try {
            Map<Product, Integer> saleItems = new HashMap<>();
            saleItems.put(milk, 2);
            saleItems.put(bread, 3);
            saleItems.put(soap, 1);

            store.processSale(register1, saleItems);
            System.out.println("Sale processed successfully!");
            
            // Print store statistics
            System.out.println("\nStore Statistics:");
            System.out.println("Total Revenue: $" + store.getTotalRevenue());
            System.out.println("Total Delivery Costs: $" + store.getTotalDeliveryCosts());
            System.out.println("Total Salary Costs: $" + store.getTotalSalaryCosts());
            System.out.println("Total Profit: $" + store.calculateProfit());
            System.out.println("Total Receipts: " + store.getTotalReceiptsCount());
            
            // Print expired and near-expiration products
            System.out.println("\nExpired Products:");
            store.getExpiredProducts().forEach(p -> 
                System.out.println("- " + p.getName()));
            
            System.out.println("\nProducts Near Expiration:");
            store.getProductsNearExpiration().forEach(p -> 
                System.out.println("- " + p.getName()));

        } catch (Exception e) {
            System.err.println("Error processing sale: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 