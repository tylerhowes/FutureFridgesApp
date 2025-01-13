package com.example.futurefridgesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin;
    EditText passwordTV;
    EditText emailTV;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            emailTV = findViewById(R.id.emailText);
            passwordTV = findViewById(R.id.passwordText);

            buttonLogin = findViewById(R.id.buttonLogin);

            Intent loginIntent = new Intent(this, HomeScreen.class);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String passwordText = passwordTV.getText().toString();
                    String email = emailTV.getText().toString();
                    auth.signInWithEmailAndPassword(email, passwordText)
                            .addOnCompleteListener(authTask -> {
                                if(authTask.isSuccessful()){
                                    FirebaseUser user = auth.getCurrentUser();
                                    if(user != null){
                                        Log.d("Login", "Logged in as: " + user.getEmail());
                                        startActivity(loginIntent);
                                    }
                                    else {
                                        Log.e("Login", "Error: " + authTask.getException());
                                    }
                                }
                            });




                }
            });

            return insets;
        });
    }
}