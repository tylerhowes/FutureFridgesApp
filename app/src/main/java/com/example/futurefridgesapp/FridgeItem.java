package com.example.futurefridgesapp;



public class FridgeItem {

    private final String name;
    private final String id;
    private final String expiry;
    private int quantity;

    public FridgeItem(String name, String id, String expiry, int quantity) {
        this.name = name;
        this.id = id;
        this.expiry = expiry;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getExpiry() {
        return expiry;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
