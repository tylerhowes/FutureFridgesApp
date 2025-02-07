package com.example.futurefridgesapp;



public class FridgeItem {

    private final String name;
    private final String id;
    private final String expiry;
    private int quantity;
    private final String dateAdded;

    public FridgeItem(String name, String id, String expiry, int quantity, String dateAdded) {
        this.name = name;
        this.id = id;
        this.expiry = expiry;
        this.quantity = quantity;
        this.dateAdded = dateAdded;
    }

    public String getDateAdded() {
        return dateAdded;
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
