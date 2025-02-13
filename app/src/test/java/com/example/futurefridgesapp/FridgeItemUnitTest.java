package com.example.futurefridgesapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class FridgeItemUnitTest {

    private FridgeItem fridgeItem;

    @Before
    public void setUp() {
        fridgeItem = new FridgeItem("Milk", "item123", "stock456", "15/02/2025", 5, "10/02/2025");
    }

    @Test
    public void testGetName() {
        assertEquals("Milk", fridgeItem.getName());
    }

    @Test
    public void testGetItemId() {
        assertEquals("item123", fridgeItem.getItemId());
    }

    @Test
    public void testGetStockId() {
        assertEquals("stock456", fridgeItem.getStockId());
    }

    @Test
    public void testGetExpiry() {
        assertEquals("15/02/2025", fridgeItem.getExpiry());
    }

    @Test
    public void testGetQuantity() {
        assertEquals(5, fridgeItem.getQuantity());
    }

    @Test
    public void testSetQuantity() {
        fridgeItem.setQuantity(10);
        assertEquals(10, fridgeItem.getQuantity());
    }

    @Test
    public void testGetDateAdded() {
        assertEquals("10/02/2025", fridgeItem.getDateAdded());
    }

    @Test
    public void testObjectCreation() {
        assertNotNull(fridgeItem);
    }
}
