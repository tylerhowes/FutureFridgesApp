package com.example.futurefridgesapp;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //get user role
        db.collection("Users").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot != null) {

                        userRole = snapshot.getString("role");

                    } else{

                        Log.e("Firestore", "Error fetching documents: ", task.getException());
                    }
                }
                else {
                    Log.e("Firestore", "Error fetching documents: ", task.getException());

                }
            }
        });

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
            intent.putExtra("userRole", userRole);
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
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            this.getSupportFragmentManager().popBackStack();
        });

        // Show the notification popup (simulate an alert, e.g., low stock)
        // This could be replaced by actual logic when integrating notifications.
        notificationPopup.setVisibility(View.VISIBLE);

        // Mark as seen button listener
        markAsSeenButton.setOnClickListener(v -> notificationPopup.setVisibility(View.GONE));

    }
}