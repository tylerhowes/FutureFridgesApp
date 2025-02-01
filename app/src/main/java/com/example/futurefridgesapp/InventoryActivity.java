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
    private ArrayList<FridgeItem> itemList;
    private InventoryManager inventoryManager;

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

        itemList = new ArrayList<>();
        inventoryTable = findViewById(R.id.inventory_table);
        searchField = findViewById(R.id.search_item);

        Intent dashIntent = getIntent();
        String userRole = dashIntent.getStringExtra("userRole");

        inventoryManager = new InventoryManager(inventoryTable, itemList, userRole);

        Button dashboardButton = findViewById(R.id.dashboard_button);
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> inventoryManager.searchItems(searchField.getText().toString()));

        inventoryManager.loadInitialItems();
    }
}