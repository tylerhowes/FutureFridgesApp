package com.example.futurefridgesapp;



public class FridgeItem {

    private final String name;
    private final String stockID;
    private final String itemId;
    private final String expiry;
    private int quantity;
    private final String dateAdded;

    public FridgeItem(String name, String itemId, String stockId, String expiry, int quantity, String dateAdded) {
        this.name = name;
        this.itemId = itemId;
        this.stockID = stockId;
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

    public String getItemId() {
        return itemId;
    }

    public String getStockId() {
        return stockID;
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
