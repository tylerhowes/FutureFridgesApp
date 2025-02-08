package com.example.futurefridgesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private OrderManager orderManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        //Order Manager and Table
        TableLayout tableLayout = findViewById(R.id.ordersTable);

        ArrayList<FridgeItem> items = new ArrayList<>();
        //items.add(new FridgeItem("Cheese", "01", "Jun 10, 2024", 1, "25/01/2025") );

        // Example list of orders
        ArrayList<Order> orders = new ArrayList<>();
        //orders.add(new Order("01", "test", "X", getString(R.string.completed), items));

        CollectionReference ordersRef = db.collection("Orders");
        ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        final int[] totalOrders = {task.getResult().size()};
                        int[] fetchedOrders = {0};  // Counter to track how many orders are fetched

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String deliveryID = document.getString("deliveryID");
                            String firebaseID = document.getString("id");
                            ArrayList<FridgeItem> items = new ArrayList<>();
                            String status = document.getString("status");
                            String supplier = document.getString("supplier");

                            List<DocumentReference> itemReferences = (List<DocumentReference>) document.get("items");
                            if (itemReferences != null) {
                                for (DocumentReference itemRef : itemReferences) {
                                    itemRef.get().addOnCompleteListener(itemTask -> {
                                        if (itemTask.isSuccessful() && itemTask.getResult().exists()) {
                                            DocumentSnapshot itemSnapshot = itemTask.getResult();

                                            String name = itemSnapshot.getString("name");
                                            String itemId = itemSnapshot.getString("itemId");
                                            String stockID = itemSnapshot.getString("stockId");
                                            String expiry = itemSnapshot.getString("expiry");
                                            int quantity = Integer.valueOf(itemSnapshot.getLong("quantity").toString());
                                            String dateAdded = itemSnapshot.getString("dateAdded");

                                            FridgeItem fridgeItem = new FridgeItem(name, itemId, stockID, expiry, quantity, dateAdded);
                                            items.add(fridgeItem);

                                            Log.d("OrdersActivity", "Item: " + fridgeItem.toString());
                                        } else {
                                            Log.e("OrdersActivity", "Failed to fetch item: " + itemRef.getId());
                                        }


                                        if (++fetchedOrders[0] == totalOrders[0]) {
                                            Order order = new Order(deliveryID, firebaseID, supplier,status, items);
                                            orders.add(order);
                                            orderManager.refreshTable();
                                        }
                                    });
                                }
                            } else {

                                Order order = new Order(deliveryID, firebaseID, status, supplier, items);
                                orders.add(order);
                                if (++fetchedOrders[0] == totalOrders[0]) {
                                    orderManager.refreshTable();
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(OrdersActivity.this, "result is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(OrdersActivity.this, "Error getting order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderManager = new OrderManager(tableLayout, orders);


        //Bottom buttons
        Button dashboardButton = findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OrdersActivity.this, DashboardActivity.class);

                startActivity(intent);
            }
        });

        //CREATE NEW ORDER BUTTON
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }
}

