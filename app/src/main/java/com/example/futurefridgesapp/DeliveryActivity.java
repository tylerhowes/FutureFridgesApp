package com.example.futurefridgesapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DeliveryActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    InventoryManager inventoryManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference testRef = db.document("/Stock/6mOwFfRek9lP5864B0E6");

        testRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("Firestore Debug", "Document exists! Data: " + documentSnapshot.getData());
            } else {
                Log.e("Firestore Debug", "Document does NOT exist: " + testRef.getPath());
            }
        }).addOnFailureListener(e -> Log.e("Firestore Debug", "Error fetching document", e));


        inventoryManager = new InventoryManager();

        // Find views
        EditText deliveryIdInput = findViewById(R.id.deliveryIdInput);
        Button confirmDeliveryButton = findViewById(R.id.confirmDeliveryButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        ImageView backIcon = findViewById(R.id.backIcon);
        ImageView profileIcon = findViewById(R.id.profileIcon);

        confirmDeliveryButton.setOnClickListener(v -> {
            String deliveryId = deliveryIdInput.getText().toString().trim();
            if (!deliveryId.isEmpty()) {
                db.collection("Orders").whereEqualTo("deliveryID", deliveryId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<Object> itemReferences = (List<Object>) document.get("items");
                                    if (itemReferences != null) {
                                        List<FridgeItem> items = new ArrayList<>();

                                        for (Object obj : itemReferences) {
                                            // Ensure it's a DocumentReference, if it's not, convert it.
                                            DocumentReference itemRef;
                                            if (obj instanceof DocumentReference) {
                                                itemRef = (DocumentReference) obj;
                                            } else if (obj instanceof String) {
                                                itemRef = db.document((String) obj);
                                            } else {
                                                Log.e("DeliveryActivity", "Invalid item reference type");
                                                continue;
                                            }

                                            Log.d("DeliveryActivity", "Item Reference: " + itemRef.getPath());
                                            itemRef.get().addOnCompleteListener(itemTask -> {
                                                if (itemTask.isSuccessful() && itemTask.getResult().exists()) {
                                                    DocumentSnapshot itemSnapshot = itemTask.getResult();
                                                    String name = itemSnapshot.getString("name");
                                                    String stockID = itemSnapshot.getId();
                                                    String expiry = itemSnapshot.getString("expiry");
                                                    int quantity = Integer.parseInt(itemSnapshot.getString("quantity"));

                                                    String dateFormatted = "0/0/0000";
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                        LocalDate date = LocalDate.now();
                                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                                        dateFormatted = date.format(formatter);
                                                    }

                                                    FridgeItem fridgeItem = new FridgeItem(name, null, stockID, expiry, quantity, dateFormatted);
                                                    items.add(fridgeItem);
                                                    Log.d("DeliveryActivity", "Item: " + fridgeItem.getName());

                                                    // Check if all items are fetched
                                                    if (items.size() == itemReferences.size()) {
                                                        inventoryManager.addOrder(items);
                                                        Toast.makeText(DeliveryActivity.this, "Delivery " + deliveryId + " confirmed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Log.e("DeliveryActivity", "Failed to fetch item: " + itemRef.getPath());
                                                }
                                            });
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(DeliveryActivity.this, "No items in order", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DeliveryActivity.this, "Error getting order", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(DeliveryActivity.this, "Please enter a Delivery ID.", Toast.LENGTH_SHORT).show();
            }
        });




        // Set Log Out button click listener
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(DeliveryActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            finish(); // Close the current activity and go back
            // You can add logic to navigate to the login screen here
        });

        // Set Back Icon click listener
        backIcon.setOnClickListener(v -> {
            finish(); // Close the current activity and go back
        });

        // Set Profile Icon click listener
        profileIcon.setOnClickListener(v -> {
            Toast.makeText(DeliveryActivity.this, "Profile clicked.", Toast.LENGTH_SHORT).show();

        });
    }
}
