package com.example.futurefridgesapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewItemActivity extends AppCompatActivity {

    EditText nameText;
    EditText expiryText;
    EditText quantityText;

    Button saveButton;
    Button cancelButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameText = findViewById(R.id.name_text);
        expiryText = findViewById(R.id.expiry_text);
        quantityText = findViewById(R.id.quantity_text);

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameText.getText().toString();
                String expiry = expiryText.getText().toString();
                String quantity = quantityText.getText().toString();

                if(!name.isEmpty() && !expiry.isEmpty() && !quantity.isEmpty()){
                    //check name
                    //check expiry days
                    //check quantity

                    Map<String, Object> data = new HashMap<>();

                    data.put("expiry", expiry);
                    data.put("name", name);
                    data.put("quantity", quantity);
                    //Add ID

                    db.collection("Stock").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String documentID = documentReference.getId();
                            documentReference.update("id", documentID);
                            AddNewItemActivity.this.finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewItemActivity.this, "Failed to add new item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(AddNewItemActivity.this,"Please complete all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewItemActivity.this.finish();
            }
        });
    }
}