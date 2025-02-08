package com.example.futurefridgesapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EditOrderActivity extends AppCompatActivity {

    TableLayout tableLayout;
    ArrayList<FridgeItem> itemList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        itemList = new ArrayList<>();
        tableLayout = findViewById(R.id.item_table);
        loadInitialItems();
    }

    public void loadInitialItems() {

        db.collection("Orders").whereEqualTo("deliveryID", orderId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    for(QueryDocumentSnapshot document : querySnapshot) {
                        String deliveryID = document.getString("deliveryID");
                        String firebaseID = document.getString("id");
                        String status = document.getString("status");
                        String supplier = document.getString("supplier");
                        List<DocumentReference> itemReferences = (List<DocumentReference>) document.get("items");
                        if (itemReferences != null) {
                            for (DocumentReference itemRef : itemReferences) {
                                itemRef.get().addOnCompleteListener(itemTask -> {
                                    if (itemTask.isSuccessful() && itemTask.getResult().exists()) {
                                        DocumentSnapshot itemSnapshot = itemTask.getResult();

                                        String name = itemSnapshot.getString("name");
                                        String stockID = itemRef.getId();
                                        String expiry = itemSnapshot.getString("expiry");
                                        int quantity = Integer.valueOf(itemSnapshot.getString("quantity"));

                                        FridgeItem fridgeItem = new FridgeItem(name, null, stockID, expiry, quantity, null);
                                        addItem(fridgeItem.getName(), fridgeItem.getItemId(), fridgeItem.getStockId(), fridgeItem.getExpiry(), fridgeItem.getQuantity(), fridgeItem.getDateAdded());
                                        Log.d("OrdersActivity", "Item: " + fridgeItem.toString());
                                    } else {
                                        Log.e("OrdersActivity", "Failed to fetch item: " + itemRef.getId());
                                    }
                                });

                            }
                        }
                    }
                } else{
                    Log.d("InventoryManager", "Error getting Inventory document: ", task.getException());
                }
            }
        });

    }

    private void addItem(String name, String itemId,String stockID, String expiry, int quantity, String dateAdded) {
        FridgeItem newItem = new FridgeItem(name, itemId,stockID, expiry, quantity, dateAdded);
        itemList.add(newItem);
        refreshTable(itemList);
    }

    private void removeItem(FridgeItem item) {
        itemList.remove(item);

        db.collection("Orders")
                .whereEqualTo("deliveryID", orderId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first matching document
                        DocumentSnapshot orderDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String docId = orderDoc.getId(); // Get the Firestore document ID

                        // Create the exact DocumentReference for the stock item
                        DocumentReference stockRef = db.collection("Stock").document(item.getStockId());

                        // Now update the correct document by removing the reference
                        db.collection("Orders").document(docId)
                                .update("items", FieldValue.arrayRemove(stockRef)) // Pass the reference correctly
                                .addOnSuccessListener(aVoid -> Log.d("InventoryManager", "Item removed from order"))
                                .addOnFailureListener(e -> Log.e("InventoryManager", "Error removing item", e));

                        refreshTable(itemList);
                    } else {
                        Log.e("InventoryManager", "No order found with deliveryID: " + orderId);
                    }
                })
                .addOnFailureListener(e -> Log.e("InventoryManager", "Error finding order", e));
    }

    private void refreshTable(List<FridgeItem> items) {
        tableLayout.removeAllViews();

        // Create a header row
        TableRow headerRow = new TableRow(tableLayout.getContext());

        TextView headerName = new TextView(tableLayout.getContext());
        headerName.setText("Name");
        headerName.setTypeface(null, Typeface.BOLD);

        TextView headerQuantity = new TextView(tableLayout.getContext());
        headerQuantity.setText("Quantity");
        headerQuantity.setTypeface(null, Typeface.BOLD);

        TextView headerActions = new TextView(tableLayout.getContext());
        headerActions.setText("Actions");
        headerActions.setTypeface(null, Typeface.BOLD);

        // Add header views to the row
        headerRow.addView(headerName);
        headerRow.addView(headerQuantity);
        headerRow.addView(headerActions);

        // Add the header row to the table layout
        tableLayout.addView(headerRow);


        for (FridgeItem item : items) {
            TableRow row = new TableRow(this);



            TextView nameView = new TextView(this);
            nameView.setText(item.getName());

            TextView quantityView = new TextView(this);
            quantityView.setText(String.valueOf(item.getQuantity()));

            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> removeItem(item));

            row.addView(nameView);
            row.addView(quantityView);
            row.addView(deleteButton);
            tableLayout.addView(row);
        }
    }
}