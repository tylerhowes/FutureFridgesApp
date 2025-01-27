package com.example.futurefridgesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeliveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        // Find views
        EditText deliveryIdInput = findViewById(R.id.deliveryIdInput);
        Button confirmDeliveryButton = findViewById(R.id.confirmDeliveryButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        ImageView backIcon = findViewById(R.id.backIcon);
        ImageView profileIcon = findViewById(R.id.profileIcon);

        // Set Confirm Delivery button click listener
        confirmDeliveryButton.setOnClickListener(v -> {
            String deliveryId = deliveryIdInput.getText().toString().trim();
            if (!deliveryId.isEmpty()) {
                Toast.makeText(DeliveryActivity.this, "Delivery " + deliveryId + " confirmed.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DeliveryActivity.this, "Please enter a Delivery ID.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set Log Out button click listener
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(DeliveryActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            // You can add logic to navigate to the login screen here
        });

        // Set Back Icon click listener
        backIcon.setOnClickListener(v -> {
            finish(); // Close the current activity and go back
        });

        // Set Profile Icon click listener
        profileIcon.setOnClickListener(v -> {
            Toast.makeText(DeliveryActivity.this, "Profile clicked.", Toast.LENGTH_SHORT).show();
            // You can add logic to navigate to the profile screen here
        });
    }
}
