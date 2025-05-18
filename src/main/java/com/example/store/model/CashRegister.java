package com.example.store.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CashRegister implements Serializable {
    private final String id;
    private Cashier assignedCashier;
    private final List<Receipt> receipts;

    public CashRegister(String id) {
        this.id = id;
        this.receipts = new ArrayList<>();
    }

    public void assignCashier(Cashier cashier) {
        if (this.assignedCashier != null && this.assignedCashier != cashier) {
            throw new IllegalStateException("Cash register already has an assigned cashier");
        }
        this.assignedCashier = cashier;
    }

    public void removeCashier() {
        this.assignedCashier = null;
    }

    public Receipt createReceipt() {
        if (assignedCashier == null) {
            throw new IllegalStateException("Cannot create receipt: no cashier assigned to register");
        }
        Receipt receipt = new Receipt(assignedCashier);
        receipts.add(receipt);
        return receipt;
    }

    // Getters
    public String getId() { return id; }
    public Cashier getAssignedCashier() { return assignedCashier; }
    public List<Receipt> getReceipts() { return new ArrayList<>(receipts); }

    @Override
    public String toString() {
        return String.format("CashRegister{id='%s', cashier=%s}", 
                           id, assignedCashier != null ? assignedCashier.getName() : "none");
    }
} 