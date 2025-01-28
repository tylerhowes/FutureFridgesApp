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

import java.util.ArrayList;
import java.util.List;

public class DeliveryActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    InventoryManager inventoryManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        inventoryManager = new InventoryManager();

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


                CollectionReference ordersRef = db.collection("Orders");
                ordersRef.whereEqualTo("deliveryID", deliveryId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<DocumentReference> itemReferences = (List<DocumentReference>) document.get("items");
                                    if(itemReferences != null) {
                                        List<FridgeItem> items = new ArrayList<>();

                                        for (DocumentReference itemRef : itemReferences) {
                                            itemRef.get().addOnCompleteListener(itemTask -> {
                                                if (itemTask.isSuccessful() && itemTask.getResult().exists()) {
                                                    DocumentSnapshot itemSnapshot = itemTask.getResult();

                                                    String name = itemSnapshot.getString("name");
                                                    String id = itemSnapshot.getString("id");
                                                    String expiry = itemSnapshot.getString("expiry");
                                                    int quantity = itemSnapshot.getLong("quantity").intValue();

                                                    FridgeItem fridgeItem = new FridgeItem(name, id, expiry, quantity);

                                                    items.add(fridgeItem);

                                                    Log.d("DeliveryActivity", "Item: " + fridgeItem.toString());

                                                    if (items.size() == itemReferences.size()) {
                                                        inventoryManager.addOrder(items);
                                                        Toast.makeText(DeliveryActivity.this, "Delivery " + deliveryId + " confirmed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Log.e("DeliveryActivity", "Failed to fetch item: " + itemRef.getId());
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                            else{
                                Toast.makeText(DeliveryActivity.this, "No items in order", Toast.LENGTH_SHORT).show();
                            }
                        }else{
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
