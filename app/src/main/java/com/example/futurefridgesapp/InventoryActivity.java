package com.example.futurefridgesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private TableLayout inventoryTable;
    private EditText searchField;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inventoryTable = findViewById(R.id.inventory_table);
        searchField = findViewById(R.id.search_item);
        itemList = new ArrayList<>();

        Button dashboardButton = findViewById(R.id.dashboard_button);
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> searchItems(searchField.getText().toString()));

        loadInitialItems(); // Load sample data
    }

    private void loadInitialItems() {
        addItem("Tomato", "01", "Jun 10, 2024", 1);
        addItem("Milk", "02", "Jun 10, 2024", 1);
        addItem("Chicken", "03", "Jun 10, 2024", 1);
        addItem("Broth", "04", "Jun 10, 2024", 1);
        addItem("Chillies", "05", "Jun 10, 2024", 1);
        addItem("Salmon", "06", "Jun 10, 2024", 1);
        addItem("Cod", "07", "Jun 10, 2024", 1);
    }

    private void addItem(String name, String id, String expiry, int quantity) {
        Item newItem = new Item(name, id, expiry, quantity);
        itemList.add(newItem);
        refreshTable();
    }

    private void removeItem(Item item) {
        itemList.remove(item);
        refreshTable();
    }

    private void updateItemQuantity(Item item, int quantity) {
        if (quantity >= 0) {
            item.setQuantity(quantity);
            refreshTable();
        }
    }

    private void searchItems(String query) {
        List<Item> filteredItems = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        refreshTable(filteredItems);
    }

    private void refreshTable() {
        refreshTable(itemList);
    }

    private void refreshTable(List<Item> items) {
        inventoryTable.removeAllViews();

        for (Item item : items) {
            TableRow row = new TableRow(this);

            ImageButton plusButton = new ImageButton(this);
            plusButton.setImageResource(android.R.drawable.ic_menu_add);
            plusButton.setOnClickListener(v -> updateItemQuantity(item, item.getQuantity() + 1));

            ImageButton minusButton = new ImageButton(this);
            minusButton.setImageResource(android.R.drawable.ic_menu_revert);
            minusButton.setOnClickListener(v -> updateItemQuantity(item, item.getQuantity() - 1));

            TextView nameView = new TextView(this);
            nameView.setText(item.getName());

            TextView idView = new TextView(this);
            idView.setText(item.getId());

            TextView expiryView = new TextView(this);
            expiryView.setText(item.getExpiry());

            TextView quantityView = new TextView(this);
            quantityView.setText(String.valueOf(item.getQuantity()));

            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> removeItem(item));

            row.addView(plusButton);
            row.addView(minusButton);
            row.addView(nameView);
            row.addView(idView);
            row.addView(expiryView);
            row.addView(quantityView);
            row.addView(deleteButton);

            inventoryTable.addView(row);
        }
    }

    static class Item {
        private final String name;
        private final String id;
        private final String expiry;
        private int quantity;

        public Item(String name, String id, String expiry, int quantity) {
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
}
