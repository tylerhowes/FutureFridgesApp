package com.example.futurefridgesapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryLogsActivity extends AppCompatActivity {




    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TableLayout tableLayout;
    ArrayList<InventoryLog> logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory_logs);

        Button closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        logs = new ArrayList<>();
        tableLayout = findViewById(R.id.table_layout);
        loadInitialItems();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void loadInitialItems() {
        Log.d("FirestoreData", "Fetching inventory logs...");
        db.collection("InventoryLogs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                Log.d("FirestoreData", "Query successful, documents found: " + querySnapshot.size());

                if (querySnapshot.isEmpty()) {
                    Log.d("FirestoreData", "No documents found in InventoryLogs");
                    return;
                }

                for (QueryDocumentSnapshot document : querySnapshot) {
                    DocumentReference userRef = document.getDocumentReference("user");
                    DocumentReference logRef = document.getDocumentReference("log");

                    if (userRef == null || logRef == null) {
                        Log.d("FirestoreData", "Missing user or log reference in document: " + document.getId());
                        continue;
                    }

                    userRef.get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful() && userTask.getResult().exists()) {
                            DocumentSnapshot userDoc = userTask.getResult();
                            String userName = userDoc.getString("email");

                            logRef.get().addOnCompleteListener(logTask -> {
                                if (logTask.isSuccessful() && logTask.getResult().exists()) {
                                    DocumentSnapshot logDoc = logTask.getResult();
                                    String itemName = logDoc.getString("name");
                                    String stockId = logDoc.getString("stockId");
                                    String expiry = logDoc.getString("expiry");
                                    Long quantityLong = logDoc.getLong("quantity");
                                    int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;
                                    String dateAdded = logDoc.getString("dateAdded");

                                    FridgeItem item = new FridgeItem(itemName, logDoc.getId(), stockId, expiry, quantity, dateAdded);

                                    String date = document.getString("dateTime");

                                    InventoryLog log = new InventoryLog(userName, item, date);
                                    logs.add(log);

                                    Log.d("FirestoreData", "User: " + userName + ", Item: " + itemName + ", Date: " + date);

                                    // Refresh the table after every addition instead of waiting for all logs
                                    runOnUiThread(() -> refreshTable(logs));
                                } else {
                                    Log.e("FirestoreData", "Failed to retrieve log data: ", logTask.getException());
                                }
                            });
                        } else {
                            Log.e("FirestoreData", "Failed to retrieve user data: ", userTask.getException());
                        }
                    });
                }
            } else {
                Log.e("FirestoreData", "Error getting InventoryLogs documents: ", task.getException());
            }
        });
    }
    private void refreshTable(ArrayList<InventoryLog> logs) {
        tableLayout.removeAllViews();

        // Create a header row
        TableRow headerRow = new TableRow(tableLayout.getContext());

        TextView headerUser = new TextView(tableLayout.getContext());
        headerUser.setText("User");
        headerUser.setTypeface(null, Typeface.BOLD);
        headerUser.setPadding(10, 10, 10, 10);

        TextView headerItem = new TextView(tableLayout.getContext());
        headerItem.setText("Item");
        headerItem.setTypeface(null, Typeface.BOLD);
        headerItem.setPadding(10, 10, 10, 10);

        TextView headerDate = new TextView(tableLayout.getContext());
        headerDate.setText("Date");
        headerDate.setTypeface(null, Typeface.BOLD);
        headerDate.setPadding(10, 10, 10, 10);

        headerRow.addView(headerUser);
        headerRow.addView(headerItem);
        headerRow.addView(headerDate);
        tableLayout.addView(headerRow);

        for (InventoryLog log : logs) {

            TableRow row = new TableRow(this);

            TextView usernameView = new TextView(this);
            usernameView.setText(log.email);
            usernameView.setPadding(20, 10, 10, 10);

            TextView itemView = new TextView(this);
            itemView.setText(log.item.getName());
            itemView.setPadding(20, 10, 10, 10);

            TextView dateView = new TextView(this);
            dateView.setText(log.date);
            dateView.setPadding(20, 10, 10, 10);

            row.addView(usernameView);
            row.addView(itemView);
            row.addView(dateView);

            Log.d("TableLayout", "Adding row for: " + log.item.getName());

            tableLayout.addView(row);

        }
    }

    static class InventoryLog{

        private String email;
        private FridgeItem item;
        private String date;

        public InventoryLog(String email, FridgeItem item, String date){
            this.email = email;
            this.item = item;
            this.date = date;
        }
    }
}

