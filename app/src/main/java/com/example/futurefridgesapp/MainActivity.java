package com.example.futurefridgesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin;
    EditText passcodeTV;

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

            passcodeTV = findViewById(R.id.passcodeText);
            buttonLogin = findViewById(R.id.buttonLogin);


            buttonLogin.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {

                    String passcode = passcodeTV.getText().toString();
                    ValidateUser(passcode);
                }
            });
            return insets;
        });
    }

    private void signIn(String email, String password){

        Intent signInIntent = new Intent(this, DashboardActivity.class);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if(authTask.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();
                if(user != null){
                    Log.d("Login", "Logged in as: " + user.getEmail());
                    startActivity(signInIntent);
                }
                else {
                    Log.e("Login", "Error: " + authTask.getException());
                }
            }
        });
    }

    private void ValidateUser (String passcode){

        db.collection("Users").whereEqualTo("passcode", passcode).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    if(!snapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : snapshot) {

                            String email = document.getString("email");
                            String password = document.getString("password");

                            if (email != null && password != null) {
                                signIn(email, password);
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid user data", Toast.LENGTH_SHORT).show();
                            }
                            break; // Exit after finding the first match
                        }

                    } else{
                        Toast.makeText(MainActivity.this, "No user found with this passcode", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Log.e("Firestore", "Error fetching documents: ", task.getException());
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
