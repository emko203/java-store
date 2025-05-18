package com.example.store.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Receipt implements Serializable {
    private static int nextReceiptNumber = 1;
    private final int receiptNumber;
    private final Cashier cashier;
    private final LocalDateTime timestamp;
    private final List<ReceiptItem> items;
    private double totalAmount;

    public static void resetReceiptNumber() {
        nextReceiptNumber = 1;
    }

    public Receipt(Cashier cashier) {
        this.receiptNumber = nextReceiptNumber++;
        this.cashier = cashier;
        this.timestamp = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public void addItem(Product product, int quantity, double unitPrice) {
        if (product.isExpired()) {
            throw new IllegalStateException("Cannot add expired product to receipt: " + product.getName());
        }
        
        product.decreaseQuantity(quantity);
        double itemTotal = unitPrice * quantity;
        items.add(new ReceiptItem(product, quantity, unitPrice, itemTotal));
        totalAmount += itemTotal;
    }

    public String generateReceiptText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Receipt #").append(receiptNumber).append("\n");
        sb.append("Date: ").append(timestamp).append("\n");
        sb.append("Cashier: ").append(cashier.getName()).append("\n");
        sb.append("Items:\n");
        
        for (ReceiptItem item : items) {
            sb.append(String.format("- %s x%d @ %.2f = %.2f\n",
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal()));
        }
        
        sb.append("Total: ").append(String.format("%.2f", totalAmount));
        return sb.toString();
    }

    // Getters
    public int getReceiptNumber() { return receiptNumber; }
    public Cashier getCashier() { return cashier; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<ReceiptItem> getItems() { return new ArrayList<>(items); }
    public double getTotalAmount() { return totalAmount; }

    public static class ReceiptItem implements Serializable {
        private final Product product;
        private final int quantity;
        private final double unitPrice;
        private final double total;

        public ReceiptItem(Product product, int quantity, double unitPrice, double total) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.total = total;
        }

        // Getters
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getTotal() { return total; }
    }
} 