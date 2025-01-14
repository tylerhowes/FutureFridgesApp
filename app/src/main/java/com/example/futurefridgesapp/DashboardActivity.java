package com.example.futurefridgesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Quick Access Buttons
        Button inventoryButton = findViewById(R.id.inventory_button);
        Button ordersButton = findViewById(R.id.orders_button);
        Button fridgesButton = findViewById(R.id.fridges_button);
        Button notificationButton = findViewById(R.id.notification_button);

        // Navigation Buttons
        ImageButton dashboardButton = findViewById(R.id.dashboard_button);
        ImageButton accountSettingsButton = findViewById(R.id.account_settings_button);

        // Notification Popup
        RelativeLayout notificationPopup = findViewById(R.id.notification_popup);
        Button markAsSeenButton = findViewById(R.id.mark_as_seen_button);

        // Set up listeners for Quick Access buttons
        inventoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        ordersButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, OrdersActivity.class);
            startActivity(intent);
        });

        fridgesButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, FridgesActivity.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        // Navigation Buttons
        dashboardButton.setOnClickListener(v -> finish()); // Go back to the previous screen
        accountSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });

        // Show the notification popup (simulate an alert, e.g., low stock)
        // This could be replaced by actual logic when integrating notifications.
        notificationPopup.setVisibility(View.VISIBLE);

        // Mark as seen button listener
        markAsSeenButton.setOnClickListener(v -> notificationPopup.setVisibility(View.GONE));
    }
}