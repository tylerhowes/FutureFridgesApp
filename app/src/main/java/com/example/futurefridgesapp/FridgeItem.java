package com.example.futurefridgesapp;

public class FridgeItem {
    private String name;
    private String expiryDate;
    private String quantity;
    private String id;

    public FridgeItem(String name, String expiryDate, String quantity, String id) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getId() {
        return id;
    }

}
