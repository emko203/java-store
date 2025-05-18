package com.example.store.model;

import java.io.Serializable;

public class Cashier implements Serializable {
    private final String id;
    private final String name;
    private final double monthlySalary;
    private CashRegister assignedRegister;

    public Cashier(String id, String name, double monthlySalary) {
        this.id = id;
        this.name = name;
        this.monthlySalary = monthlySalary;
    }

    public void assignToRegister(CashRegister register) {
        if (this.assignedRegister != null) {
            this.assignedRegister.removeCashier();
        }
        this.assignedRegister = register;
        if (register != null) {
            register.assignCashier(this);
        }
    }

    public void removeFromRegister() {
        if (this.assignedRegister != null) {
            this.assignedRegister.removeCashier();
            this.assignedRegister = null;
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getMonthlySalary() { return monthlySalary; }
    public CashRegister getAssignedRegister() { return assignedRegister; }

    @Override
    public String toString() {
        return String.format("Cashier{id='%s', name='%s', salary=%.2f}", 
                           id, name, monthlySalary);
    }
} 