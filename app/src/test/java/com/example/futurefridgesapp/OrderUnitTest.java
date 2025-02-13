package com.example.futurefridgesapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class OrderUnitTest {

    private Order order;
    private ArrayList<FridgeItem> items;

    @Before
    public void setUp() {
        items = new ArrayList<>();
        items.add(new FridgeItem("Milk", "item123", "stock456", "15/02/2025", 5, "10/02/2025"));
        items.add(new FridgeItem("Eggs", "item789", "stock987", "18/02/2025", 12, "11/02/2025"));

        order = new Order("order001", "firebase123", "Dairy Supplier", "Open", items);
    }

    @Test
    public void testGetId() {
        assertEquals("order001", order.getId());
    }

    @Test
    public void testGetFirebaseId() {
        assertEquals("firebase123", order.getFirebaseId());
    }

    @Test
    public void testGetSupplier() {
        assertEquals("Dairy Supplier", order.getSupplier());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Open", order.getStatus());
    }

    @Test
    public void testGetItems() {
        assertNotNull(order.getItems());
        assertEquals(2, order.getItems().size());
    }

    @Test
    public void testItemDetails() {
        FridgeItem firstItem = order.getItems().get(0);
        assertEquals("Milk", firstItem.getName());
        assertEquals("item123", firstItem.getItemId());
        assertEquals("stock456", firstItem.getStockId());
        assertEquals(5, firstItem.getQuantity());

        FridgeItem secondItem = order.getItems().get(1);
        assertEquals("Eggs", secondItem.getName());
        assertEquals("item789", secondItem.getItemId());
        assertEquals("stock987", secondItem.getStockId());
        assertEquals(12, secondItem.getQuantity());
    }

    @Test
    public void testEmptyOrder() {
        Order emptyOrder = new Order("order002", "firebase456", "Grocery Supplier", "Closed", new ArrayList<>());
        assertTrue(emptyOrder.getItems().isEmpty());
    }
}
