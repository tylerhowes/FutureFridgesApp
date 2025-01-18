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

    private OrderManager orderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        //Order Manager and Table
        TableLayout tableLayout = findViewById(R.id.ordersTable);

        ArrayList<FridgeItem> items = new ArrayList<>();
        items.add(new FridgeItem("Cheese", "01", "Jun 10, 2024", 1) );

        // Example list of orders
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("01", "X", getString(R.string.completed), items));

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

