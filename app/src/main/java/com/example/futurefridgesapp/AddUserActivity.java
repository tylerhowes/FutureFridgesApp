package com.example.futurefridgesapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);

        //get the spinner from the xml.
        Spinner roleDropdown = findViewById(R.id.user_role_spinner);
        //create a list of items for the spinner.
        String[] items = new String[]{"chef", "headchef", "admin", "delivery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        roleDropdown.setAdapter(adapter);

        EditText userEmail = findViewById(R.id.user_email);
        EditText userPassword = findViewById(R.id.user_password);
        EditText userPasscode = findViewById(R.id.user_passcode);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String passcode = userPasscode.getText().toString();
                String role = roleDropdown.getSelectedItem().toString();

                if (!email.trim().isEmpty() && !password.trim().isEmpty() && !passcode.trim().isEmpty() && !role.trim().isEmpty()) {
                    if (password.length() >= 6 && passcode.length() == 3) {
                        try {
                            Integer.parseInt(passcode); // Ensure passcode is numeric

                            // Query Firestore to check if passcode exists
                            db.collection("Users")
                                    .whereEqualTo("passcode", passcode)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            // Passcode already exists
                                            Toast.makeText(AddUserActivity.this, "Passcode already in use, choose a different one", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Passcode is unique, proceed with creating the user
                                            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
                                                if (authTask.isSuccessful()) {
                                                    String newUserId = authTask.getResult().getUser().getUid();

                                                    Map<String, String> userDetails = new HashMap<>();
                                                    userDetails.put("email", email);
                                                    userDetails.put("password", password);
                                                    userDetails.put("passcode", passcode);
                                                    userDetails.put("role", role);
                                                    userDetails.put("userId", newUserId); // Store Firebase Auth user ID in Firestore

                                                    db.collection("Users").document(newUserId).set(userDetails)
                                                            .addOnSuccessListener(unused -> Toast.makeText(AddUserActivity.this, "User added successfully", Toast.LENGTH_SHORT).show())
                                                            .addOnFailureListener(e -> Log.e("Firestore", "Error adding user: " + e.getMessage()));
                                                } else {
                                                    Log.e("FirebaseAuth", "Error creating user: " + authTask.getException().getMessage());
                                                    Toast.makeText(AddUserActivity.this, "Error creating user: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });

                        } catch (NumberFormatException e) {
                            Toast.makeText(AddUserActivity.this, "Passcode must be 3 digits", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddUserActivity.this, "Password must be at least 6 characters and passcode must be 3 digits", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddUserActivity.this, "Make sure all fields are complete", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}