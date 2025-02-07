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

                        addItem(document.getString("name"), document.getId(), expiryDate, Integer.valueOf(document.get("quantity").toString()), document.getString("dateAdded"));
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
            NotificationActivity.Notification lowStockNotification = new NotificationActivity.Notification("Expiry Date", currentDate, item.getName() + " is expiring", "Expired");

            db.collection("Notifications")
                    .add(lowStockNotification)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Inventory Manager", "DocumentSnapshot written with ID: " + documentReference.getId());
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

    private void addItem(String name, String id, String expiry, int quantity, String dateAdded) {
        FridgeItem newItem = new FridgeItem(name, id, expiry, quantity, dateAdded);
        checkExpiry(newItem);
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

        if(!addingItem){
            if(quantity <= 3 && (quantity+1 != 0)){

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                Log.d("Inventory Manager", "Date: " + formattedDate);
                NotificationActivity.Notification lowStockNotification = new NotificationActivity.Notification("Low Stock", formattedDate, item.getName() + " is low on stock", "Low Stock");

                db.collection("Notifications")
                        .add(lowStockNotification)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Inventory Manager", "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Inventory Manager", "Error adding document", e);
                            }
                        });

                db.collection("Orders").whereEqualTo("status", "Open").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot snap : task.getResult()) {
                                        DocumentReference orderRef = snap.getReference();

                                        // Get the existing item references list
                                        List<DocumentReference> itemRefs = (List<DocumentReference>) snap.get("items");
                                        if (itemRefs == null) {
                                            itemRefs = new ArrayList<>();
                                        }

                                        // Reference to the FridgeItem document
                                        DocumentReference fridgeItemRef = db.collection("Inventory").document(item.getId());

                                        // Check if the item reference already exists
                                        boolean exists = false;
                                        for (DocumentReference existingRef : itemRefs) {
                                            if (existingRef.getPath().equals(fridgeItemRef.getPath())) {
                                                exists = true;
                                                break;
                                            }
                                        }

                                        // Append the item reference only if it does not already exist
                                        if (!exists) {
                                            itemRefs.add(fridgeItemRef);
                                            orderRef.update("items", itemRefs)
                                                    .addOnSuccessListener(aVoid ->
                                                            Log.d("Firestore", "Item reference added successfully"))
                                                    .addOnFailureListener(e ->
                                                            Log.e("Firestore", "Error updating document", e));
                                        } else {
                                            Log.d("Firestore", "Item reference already exists in the array");
                                        }
                                    }
                                } else {
                                    Log.e("Firestore", "No open orders found or error fetching documents", task.getException());
                                }
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
            if(userRole.equals("headchef")){
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

            db.collection("Inventory").document(item.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot docSnapshot = task.getResult();
                        int oldQuantity = docSnapshot.getLong("quantity").intValue();
                        item.setQuantity(oldQuantity+1);
                        db.collection("Inventory").document(item.getId()).set(item);
                    }
                }
            });
        }
    }
}
