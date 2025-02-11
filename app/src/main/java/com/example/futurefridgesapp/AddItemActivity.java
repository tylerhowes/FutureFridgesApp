package com.example.futurefridgesapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    TableLayout tableLayout;
    ArrayList<FridgeItem> itemList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);

//        listenForStockChanges();

        loadInitialItems();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            tableLayout = findViewById(R.id.item_table);
            itemList = new ArrayList<>();

            return insets;
        });
    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        loadInitialItems();
//    }

    public void loadInitialItems() {

        db.collection("Stock").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    itemList.clear();
                    QuerySnapshot querySnapshot = task.getResult();
                    for(QueryDocumentSnapshot document : querySnapshot) {
                        String name = document.getString("name");
                        String expiry = document.getString("expiry");
                        int quantity = Integer.valueOf(document.getString("quantity"));
                        String stockId = document.getId();

                        FridgeItem item = new FridgeItem(name, null, stockId, expiry, quantity, null);
                        itemList.add(item);
                    }
                    refreshTable(itemList);
                } else{
                    Log.d("InventoryManager", "Error getting Stock document: ", task.getException());
                }
            }
        });
    }

//    private void listenForStockChanges() {
//        db.collection("Stock").addSnapshotListener((querySnapshot, error) -> {
//            if (error != null) {
//                Log.e("Firestore", "Error listening to stock updates", error);
//                return;
//            }
//            if (querySnapshot != null) {
//                itemList.clear();
//                for (QueryDocumentSnapshot document : querySnapshot) {
//                    String name = document.getString("name");
//                    String expiry = document.getString("expiry");
//                    int quantity = Integer.parseInt(document.getString("quantity"));
//                    String stockId = document.getId();
//
//                    FridgeItem item = new FridgeItem(name, null, stockId, expiry, quantity, null);
//                    itemList.add(item);
//                }
//                refreshTable(itemList);
//            }
//        });
//    }


    private void addItem(FridgeItem item) {
        db.collection("Orders")
                .whereEqualTo("status", "Open")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentReference stockItemRef = db.collection("Stock").document(item.getStockId());

                        for (QueryDocumentSnapshot snap : task.getResult()) {
                            DocumentReference orderRef = snap.getReference();

                            // Fetch the existing list of items
                            orderRef.get().addOnSuccessListener(documentSnapshot -> {
                                List<DocumentReference> itemRefs = (List<DocumentReference>) documentSnapshot.get("items");

                                if (itemRefs == null) {
                                    itemRefs = new ArrayList<>();
                                }

                                // Add the reference (even if it's a duplicate)
                                itemRefs.add(stockItemRef);

                                // Update the document with the new array
                                orderRef.update("items", itemRefs)
                                        .addOnSuccessListener(aVoid ->
                                                Log.d("Firestore", "Duplicate item reference added successfully"))
                                        .addOnFailureListener(e ->
                                                Log.e("Firestore", "Error updating document", e));

                                setResult(RESULT_OK);
                                finish();
                            }).addOnFailureListener(e ->
                                    Log.e("Firestore", "Error fetching document", e)
                            );
                        }
                    } else {
                        Log.e("Firestore", "No open orders found or error fetching documents", task.getException());
                    }
                });
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

            Button addButton = new Button(this);
            addButton.setText("Add to Order");
            addButton.setOnClickListener(v -> addItem(item));

            row.addView(nameView);
            row.addView(quantityView);
            row.addView(addButton);
            tableLayout.addView(row);
        }
    }
}