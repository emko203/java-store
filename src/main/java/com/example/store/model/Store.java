package com.example.store.model;

import java.io.*;
import java.util.*;

public class Store implements Serializable {
    private final String name;
    private final double foodMarkupPercentage;
    private final double nonFoodMarkupPercentage;
    private final int daysUntilDiscount;
    private final double discountPercentage;
    
    private final List<Product> products;
    private final List<Cashier> cashiers;
    private final List<CashRegister> registers;
    private final List<Receipt> allReceipts;
    
    private double totalRevenue;
    private double totalDeliveryCosts;
    private double totalSalaryCosts;

    public Store(String name, double foodMarkupPercentage, double nonFoodMarkupPercentage,
                int daysUntilDiscount, double discountPercentage) {
        this.name = name;
        this.foodMarkupPercentage = foodMarkupPercentage;
        this.nonFoodMarkupPercentage = nonFoodMarkupPercentage;
        this.daysUntilDiscount = daysUntilDiscount;
        this.discountPercentage = discountPercentage;
        
        this.products = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.registers = new ArrayList<>();
        this.allReceipts = new ArrayList<>();
        
        this.totalRevenue = 0.0;
        this.totalDeliveryCosts = 0.0;
        this.totalSalaryCosts = 0.0;
    }

    public void addProduct(Product product) {
        products.add(product);
        totalDeliveryCosts += product.getDeliveryPrice() * product.getQuantity();
    }

    public void addCashier(Cashier cashier) {
        cashiers.add(cashier);
        totalSalaryCosts += cashier.getMonthlySalary();
    }

    public void addCashRegister(CashRegister register) {
        registers.add(register);
    }

    public double calculateProductPrice(Product product) {
        double markupPercentage = product.getCategory() == Product.ProductCategory.FOOD 
            ? foodMarkupPercentage 
            : nonFoodMarkupPercentage;
            
        return product.calculateSellingPrice(markupPercentage, daysUntilDiscount, discountPercentage);
    }

    public void processSale(CashRegister register, Map<Product, Integer> items) {
        if (register.getAssignedCashier() == null) {
            throw new IllegalStateException("No cashier assigned to register");
        }

        Receipt receipt = register.createReceipt();
        
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            
            if (product.isExpired()) {
                throw new IllegalStateException("Cannot sell expired product: " + product.getName());
            }
            
            double unitPrice = calculateProductPrice(product);
            receipt.addItem(product, quantity, unitPrice);
        }

        totalRevenue += receipt.getTotalAmount();
        allReceipts.add(receipt);
        
        // Save receipt to file
        saveReceiptToFile(receipt);
    }

    private void saveReceiptToFile(Receipt receipt) {
        String filename = String.format("receipt_%d.txt", receipt.getReceiptNumber());
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.write(receipt.generateReceiptText());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save receipt to file: " + filename, e);
        }
    }

    public double calculateProfit() {
        return totalRevenue - totalDeliveryCosts - totalSalaryCosts;
    }

    public List<Product> getExpiredProducts() {
        return products.stream()
                      .filter(Product::isExpired)
                      .toList();
    }

    public List<Product> getProductsNearExpiration() {
        return products.stream()
                      .filter(p -> p.isNearExpiration(daysUntilDiscount))
                      .toList();
    }

    // Getters
    public String getName() { return name; }
    public List<Product> getProducts() { return new ArrayList<>(products); }
    public List<Cashier> getCashiers() { return new ArrayList<>(cashiers); }
    public List<CashRegister> getRegisters() { return new ArrayList<>(registers); }
    public List<Receipt> getAllReceipts() { return new ArrayList<>(allReceipts); }
    public double getTotalRevenue() { return totalRevenue; }
    public double getTotalDeliveryCosts() { return totalDeliveryCosts; }
    public double getTotalSalaryCosts() { return totalSalaryCosts; }
    public int getTotalReceiptsCount() { return allReceipts.size(); }

    public void displayAvailableProducts() {
        System.out.println("\nНалични продукти:");
        System.out.println("------------------");
        for (Product product : products) {
            if (!product.isExpired() && product.getQuantity() > 0) {
                System.out.printf("ID: %s | Име: %s | Цена: %.2f лв. | Количество: %d | Дата на изтичане: %s%n",
                    product.getId(),
                    product.getName(),
                    calculateProductPrice(product),
                    product.getQuantity(),
                    product.getExpirationDate());
            }
        }
        System.out.println("------------------");
    }
} 