package com.example.futurefridgesapp;

import com.google.firebase.Firebase;

import java.util.ArrayList;

public class Order {
    private String id;
    private String supplier;
    private String status;
    private ArrayList<FridgeItem> items;

    public Order(String id, String supplier, String status, ArrayList<FridgeItem> items) {
        this.id = id;
        this.supplier = supplier;
        this.status = status;
        this.items = items;
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

    public ArrayList<FridgeItem> getItems() {
        return items;
    }
}
