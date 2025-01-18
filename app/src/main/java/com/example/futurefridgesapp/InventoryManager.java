package com.example.futurefridgesapp;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private TableLayout tableLayout;
    private ArrayList<FridgeItem> itemList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public InventoryManager(TableLayout tableLayout, ArrayList<FridgeItem> itemList){
        this.tableLayout = tableLayout;
        this.itemList = itemList;

    }

    public void loadInitialItems() {
    //        addItem("Tomato", "01", "Jun 10, 2024", 1);
    //        addItem("Milk", "02", "Jun 10, 2024", 1);
    //        addItem("Chicken", "03", "Jun 10, 2024", 1);
    //        addItem("Broth", "04", "Jun 10, 2024", 1);
    //        addItem("Chillies", "05", "Jun 10, 2024", 1);
    //        addItem("Salmon", "06", "Jun 10, 2024", 1);
    //        addItem("Cod", "07", "Jun 10, 2024", 1);


        db.collection("Inventory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        addItem(document.getString("name"), document.getId(), document.getString("expiry"), Integer.parseInt(document.getString("quantity")));
                    }
                } else{
                    Log.d("InventoryManager", "Error getting Inventory document: ", task.getException());
                }
            }
        });
    }

    private void addItem(String name, String id, String expiry, int quantity) {
        FridgeItem newItem = new FridgeItem(name, id, expiry, quantity);
        itemList.add(newItem);
        refreshTable(itemList);
    }

    private void removeItem(FridgeItem item) {
        itemList.remove(item);
        refreshTable(itemList);
    }

    private void updateItemQuantity(FridgeItem item, int quantity) {
        if (quantity >= 0) {
            item.setQuantity(quantity);
            db.collection("Inventory").document(item.getId()).set(item);
            refreshTable(itemList);
        }
    }

    public void searchItems(String query) {
        List<FridgeItem> filteredItems = new ArrayList<>();
        for (FridgeItem item : itemList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        refreshTable(filteredItems);
    }

    private void refreshTable(List<FridgeItem> items) {
        tableLayout.removeAllViews();

        for (FridgeItem item : items) {
            TableRow row = new TableRow(tableLayout.getContext());

            ImageButton plusButton = new ImageButton(tableLayout.getContext());
            plusButton.setImageResource(R.drawable.baseline_plus_one_24);
            plusButton.setOnClickListener(v -> updateItemQuantity(item, item.getQuantity() + 1));

            ImageButton minusButton = new ImageButton(tableLayout.getContext());
            minusButton.setImageResource(R.drawable.baseline_exposure_neg_1_24);
            minusButton.setOnClickListener(v -> updateItemQuantity(item, item.getQuantity() - 1));

            TextView nameView = new TextView(tableLayout.getContext());
            nameView.setText(item.getName());

//            TextView idView = new TextView(tableLayout.getContext());
//            idView.setText(item.getId());

            TextView expiryView = new TextView(tableLayout.getContext());
            expiryView.setText(item.getExpiry());

            TextView quantityView = new TextView(tableLayout.getContext());
            quantityView.setText(String.valueOf(item.getQuantity()));

            Button deleteButton = new Button(tableLayout.getContext());
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> removeItem(item));

            row.addView(plusButton);
            row.addView(minusButton);
            row.addView(nameView);
//            row.addView(idView);
            row.addView(expiryView);
            row.addView(quantityView);
            row.addView(deleteButton);

            tableLayout.addView(row);
        }
    }

}
