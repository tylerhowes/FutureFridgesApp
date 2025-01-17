package com.example.futurefridgesapp;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        TableLayout tableLayout = findViewById(R.id.ordersTable);

        // Example list of orders
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("01", "X", getString(R.string.completed)));
        orders.add(new Order("02", "Y", getString(R.string.pending)));
        orders.add(new Order("03", "Z", getString(R.string.completed)));
        orders.add(new Order("04", "W", getString(R.string.pending)));
        orders.add(new Order("05", "S", getString(R.string.completed)));

        // Populate the table
        for (Order order : orders) {
            TableRow row = new TableRow(this);
            row.setBackgroundResource(R.drawable.border);

            // Order ID
            TextView orderIdView = new TextView(this);
            orderIdView.setText(order.getId());
            orderIdView.setGravity(Gravity.CENTER);
            orderIdView.setPadding(16, 16, 16, 16);
            orderIdView.setBackgroundResource(R.drawable.border);

            // Supplier
            TextView supplierView = new TextView(this);
            supplierView.setText(order.getSupplier());
            supplierView.setGravity(Gravity.CENTER);
            supplierView.setPadding(16, 16, 16, 16);
            supplierView.setBackgroundResource(R.drawable.border);

            // Status
            TextView statusView = new TextView(this);
            statusView.setText(order.getStatus());
            statusView.setGravity(Gravity.CENTER);
            statusView.setPadding(16, 16, 16, 16);
            statusView.setBackgroundResource(R.drawable.border);

            // Action
            TextView actionView = new TextView(this);
            actionView.setText(getString(R.string.edit));
            actionView.setGravity(Gravity.CENTER);
            actionView.setPadding(16, 16, 16, 16);
            actionView.setBackgroundResource(R.drawable.border);

            // Add all views to the row
            row.addView(orderIdView);
            row.addView(supplierView);
            row.addView(statusView);
            row.addView(actionView);

            // Add the row to the table
            tableLayout.addView(row);
        }
    }
}
