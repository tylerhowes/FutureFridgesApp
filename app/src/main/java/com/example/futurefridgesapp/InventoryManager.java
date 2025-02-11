package com.example.futurefridgesapp;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

public class InventoryManager {

    private TableLayout tableLayout;
    private ArrayList<FridgeItem> itemList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userRole;
    boolean addingItem = false;

    public InventoryManager(){

    }

    public InventoryManager(TableLayout tableLayout, ArrayList<FridgeItem> itemList, String userRole){
        this.tableLayout = tableLayout;
        this.itemList = itemList;
        this.userRole = userRole;
    }

    public void loadInitialItems() {

        db.collection("Inventory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        int expiry = Integer.parseInt(document.getString("expiry"));
                        String dateAdded = document.getString("dateAdded");
                        String expiryDate = calculateExpiry(dateAdded, expiry);
                        String stockId = document.getString("stockId");
                        String name = document.getString("name");
                        String itemId = document.getString("itemId");
                        int quantity = Integer.valueOf(document.get("quantity").toString());


                        addItem(name,
                                itemId,
                                stockId,
                                expiryDate,
                                quantity,
                                dateAdded
                                );
                    }
                } else{
                    Log.d("InventoryManager", "Error getting Inventory document: ", task.getException());
                }
            }
        });
    }

    private String calculateExpiry(String dateAdded, int numberOfDays){
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate addedDate = LocalDate.parse(dateAdded, formatter); // Convert dateAdded string to LocalDate
            LocalDate expiryDate = addedDate.plusDays(numberOfDays); // Add expiry days
            return expiryDate.format(formatter);
        }
        return "00/00/0000";
    }

    private void checkExpiry(FridgeItem item){

        String expiryDate = item.getExpiry();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = df.format(c);

        int difference = (int) getDateDiff(df, currentDate, expiryDate);
        Log.d("InventoryManager", "The difference in date is: " + difference + " Days");

        if(difference <= 0){
            NotificationActivity.Notification expiryNotification = new NotificationActivity.Notification(null, "Expiry Date", currentDate, item.getName() + " is expiring", "Expired");

            db.collection("Notifications")
                    .add(expiryNotification)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String generatedId = documentReference.getId();
                            Log.d("Inventory Manager", "DocumentSnapshot written with ID: " + generatedId);

                            // Update the Firestore document with its generated ID
                            documentReference.update("id", generatedId)
                                    .addOnSuccessListener(aVoid -> Log.d("Inventory Manager", "Notification updated with ID"))
                                    .addOnFailureListener(e -> Log.w("Inventory Manager", "Error updating document with ID", e));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Inventory Manager", "Error adding document", e);
                        }
                    });
        }
    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            Date oldDateObject = new Date(format.parse(oldDate).getTime());
            Date newDateObject = new Date(format.parse(newDate).getTime());

            return TimeUnit.DAYS.convert(
                    removeTime(newDateObject).getTime() - removeTime(oldDateObject).getTime(),
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void addItem(String name, String itemId,String stockID, String expiry, int quantity, String dateAdded) {
        FridgeItem newItem = new FridgeItem(name, itemId,stockID, expiry, quantity, dateAdded);
        checkExpiry(newItem);
        itemList.add(newItem);
        refreshTable(itemList);
    }

    private void removeItem(FridgeItem item) {
        itemList.remove(item);
        db.collection("Inventory").document(item.getItemId()).delete();
        refreshTable(itemList);
    }

    private void updateItemQuantity(FridgeItem item, int quantity) {
        Log.d("InventoryManager", "Updating item: " + item.getItemId() + " to quantity " + quantity);

        if (quantity >= 0) {
            item.setQuantity(quantity);
            db.collection("Inventory").document(item.getItemId()).update("quantity", quantity);
            refreshTable(itemList);
        }

        if (!addingItem) {
            if (quantity <= 3 && (quantity + 1 != 0)) {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                Log.d("Inventory Manager", "Date: " + formattedDate);

                NotificationActivity.Notification lowStockNotification = new NotificationActivity.Notification(
                        null, "Low Stock", formattedDate, item.getName() + " is low on stock", "Low Stock"
                );

                db.collection("Notifications")
                        .add(lowStockNotification)
                        .addOnSuccessListener(documentReference -> {
                            String generatedId = documentReference.getId();
                            Log.d("Inventory Manager", "DocumentSnapshot written with ID: " + generatedId);

                            documentReference.update("id", generatedId)
                                    .addOnSuccessListener(aVoid -> Log.d("Inventory Manager", "Notification updated with ID"))
                                    .addOnFailureListener(e -> Log.w("Inventory Manager", "Error updating document with ID", e));
                        })
                        .addOnFailureListener(e -> Log.w("Inventory Manager", "Error adding document", e));

                // Find open order and add the item reference
                db.collection("Orders")
                        .whereEqualTo("status", "Open")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentReference stockItemRef = db.collection("Stock").document(item.getStockId());

                                for (QueryDocumentSnapshot snap : task.getResult()) {
                                    DocumentReference orderRef = snap.getReference();

                                    // Directly add reference using arrayUnion
                                    orderRef.update("items", FieldValue.arrayUnion(stockItemRef))
                                            .addOnSuccessListener(aVoid ->
                                                    Log.d("Firestore", "Item reference added successfully"))
                                            .addOnFailureListener(e ->
                                                    Log.e("Firestore", "Error updating document", e));
                                }
                            } else {
                                Log.e("Firestore", "No open orders found or error fetching documents", task.getException());
                            }
                        });

                addingItem = false;
            }
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

        Log.d("INVENTORY MANAGE", "Role signing in: " + userRole);
        for (FridgeItem item : items) {
            if(userRole.equals("headchef") || userRole.equals("admin")){
                TableRow row = new TableRow(tableLayout.getContext());

                ImageButton plusButton = new ImageButton(tableLayout.getContext());
                plusButton.setImageResource(R.drawable.baseline_plus_one_24);
                plusButton.setOnClickListener(v -> {
                    addingItem = true;
                    updateItemQuantity(item, item.getQuantity() + 1);
                });

                ImageButton minusButton = new ImageButton(tableLayout.getContext());
                minusButton.setImageResource(R.drawable.baseline_exposure_neg_1_24);
                minusButton.setOnClickListener(v -> {
                    addingItem = false;
                    updateItemQuantity(item, item.getQuantity() - 1);
                });

                TextView nameView = new TextView(tableLayout.getContext());
                nameView.setText(item.getName());

                TextView expiryView = new TextView(tableLayout.getContext());
                expiryView.setText(item.getExpiry());

                TextView quantityView = new TextView(tableLayout.getContext());
                quantityView.setText(String.valueOf(item.getQuantity()));

                Button deleteButton = new Button(tableLayout.getContext());
                deleteButton.setText("Delete");
                deleteButton.setOnClickListener(v -> removeItem(item));

                row.addView(minusButton);
                row.addView(plusButton);
                row.addView(nameView);
                row.addView(expiryView);
                row.addView(quantityView);
                row.addView(deleteButton);
                tableLayout.addView(row);
            }
            else{
                TableRow row = new TableRow(tableLayout.getContext());

                ImageButton minusButton = new ImageButton(tableLayout.getContext());
                minusButton.setImageResource(R.drawable.baseline_exposure_neg_1_24);
                minusButton.setOnClickListener(v -> updateItemQuantity(item, item.getQuantity() - 1));

                TextView nameView = new TextView(tableLayout.getContext());
                nameView.setText(item.getName());

                TextView expiryView = new TextView(tableLayout.getContext());
                expiryView.setText(item.getExpiry());

                TextView quantityView = new TextView(tableLayout.getContext());
                quantityView.setText(String.valueOf(item.getQuantity()));


                row.addView(nameView);
                row.addView(expiryView);
                row.addView(quantityView);
                row.addView(minusButton);

                tableLayout.addView(row);
            }
        }
    }

    public void addOrder(List<FridgeItem> orderItems){
        for(FridgeItem item : orderItems){
            db.collection("Inventory").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentReference.update("itemId", documentReference.getId());
                }
            });
        }
    }
}
