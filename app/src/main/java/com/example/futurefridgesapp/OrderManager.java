package com.example.futurefridgesapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    private ArrayList<Order> orders;
    private TableLayout tableLayout;
    private Context context;

    public OrderManager(TableLayout tableLayout, ArrayList<Order> orders, Context context){

        this.context = context;
        this.orders = orders;
        this.tableLayout = tableLayout;
        refreshTable();
    }

    private void addOrder(String id, String firebaseId, String supplier, String status, ArrayList<FridgeItem> items) {
        Order newOrder = new Order(id, firebaseId, supplier, status, items);
        orders.add(newOrder);
        refreshTable();
    }

    public void refreshTable(){
        tableLayout.removeAllViews();

        // Create a header row
        TableRow headerRow = new TableRow(tableLayout.getContext());

        TextView headerName = new TextView(tableLayout.getContext());
        headerName.setText("Order ID");
        headerName.setTypeface(null, Typeface.BOLD);

        TextView headerQuantity = new TextView(tableLayout.getContext());
        headerQuantity.setText("Supplier");
        headerQuantity.setTypeface(null, Typeface.BOLD);

        TextView headerActions = new TextView(tableLayout.getContext());
        headerActions.setText("Status");
        headerActions.setTypeface(null, Typeface.BOLD);

        // Add header views to the row
        headerRow.addView(headerName);
        headerRow.addView(headerQuantity);
        headerRow.addView(headerActions);

        // Add the header row to the table layout
        tableLayout.addView(headerRow);

        // Populate the table
        for (Order order : orders) {
            TableRow row = new TableRow(tableLayout.getContext());

            // Order ID
            TextView orderIdView = new TextView(tableLayout.getContext());
            orderIdView.setText(order.getId());
            orderIdView.setGravity(Gravity.CENTER);
            orderIdView.setPadding(16, 16, 16, 16);

            // Supplier
            TextView supplierView = new TextView(tableLayout.getContext());
            supplierView.setText(order.getSupplier());
            supplierView.setGravity(Gravity.CENTER);
            supplierView.setPadding(16, 16, 16, 16);


            // Status
            TextView statusView = new TextView(tableLayout.getContext());
            statusView.setText(order.getStatus());
            statusView.setGravity(Gravity.CENTER);
            statusView.setPadding(16, 16, 16, 16);


            // Add all views to the row
            row.addView(orderIdView);
            row.addView(supplierView);
            row.addView(statusView);

            Log.d("Order Manager", "Order status: " + order.getStatus());
            // Action
            if(order.getStatus().equals("Open")){
                Button actionView = new Button(tableLayout.getContext());
                actionView.setText("Edit");
                actionView.setGravity(Gravity.CENTER);
                actionView.setPadding(16, 16, 16, 16);
                row.addView(actionView);

                actionView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, EditOrderActivity.class);
                    intent.putExtra("orderId", order.getId());
                    context.startActivity(intent);


                });
            }else{
                View view = new View(tableLayout.getContext());
                view.setPadding(16,16,16,16);
                row.addView(view);
            }


            // Add the row to the table
            tableLayout.addView(row);
        }
    }
}
