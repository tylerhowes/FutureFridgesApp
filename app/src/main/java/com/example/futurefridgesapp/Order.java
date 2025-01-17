package com.example.futurefridgesapp;

public class Order {
    private String id;
    private String supplier;
    private String status;

    public Order(String id, String supplier, String status) {
        this.id = id;
        this.supplier = supplier;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getStatus() {
        return status;
    }
}
