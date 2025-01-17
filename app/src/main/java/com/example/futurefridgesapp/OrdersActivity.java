package com.example.futurefridgesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        TableLayout tableLayout = findViewById(R.id.ordersTable);

        ArrayList<FridgeItem> items = new ArrayList<>();
        items.add(new FridgeItem("Cheese", "01", "Jun 10, 2024", 1) );

        // Example list of orders
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("01", "X", getString(R.string.completed), items));


        // Populate the table
        for (Order order : orders) {
            TableRow row = new TableRow(this);

            // Order ID
            TextView orderIdView = new TextView(this);
            orderIdView.setText(order.getId());
            orderIdView.setGravity(Gravity.CENTER);
            orderIdView.setPadding(16, 16, 16, 16);

            // Supplier
            TextView supplierView = new TextView(this);
            supplierView.setText(order.getSupplier());
            supplierView.setGravity(Gravity.CENTER);
            supplierView.setPadding(16, 16, 16, 16);


            // Status
            TextView statusView = new TextView(this);
            statusView.setText(order.getStatus());
            statusView.setGravity(Gravity.CENTER);
            statusView.setPadding(16, 16, 16, 16);

            // Action
            Button actionView = new Button(this);
            actionView.setText(getString(R.string.edit));
            actionView.setGravity(Gravity.CENTER);
            actionView.setPadding(16, 16, 16, 16);

            // Add all views to the row
            row.addView(orderIdView);
            row.addView(supplierView);
            row.addView(statusView);
            row.addView(actionView);

            // Add the row to the table
            tableLayout.addView(row);
        }


        Button dashboardButton = findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdersActivity.this, DashboardActivity.class);

                startActivity(intent);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }
}

