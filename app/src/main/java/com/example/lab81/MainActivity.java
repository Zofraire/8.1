package com.example.lab81;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView txtDetails;
    private EditText inputName, inputEmail;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDetails = findViewById(R.id.txt_user);
        inputName = findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        btnSave = findViewById(R.id.btn_save);

        // Initialize Firebase
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // Set initial app title in the database
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        // Retrieve and set the app title from Firebase
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String appTitle = dataSnapshot.getValue(String.class);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(appTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        // Save button click listener to create or update a user
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();

                if (TextUtils.isEmpty(userId)) {
                    createUser(name, email);
                } else {
                    updateUser(name, email);
                }
            }
        });
    }

    private void createUser(String name, String email) {
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        User user = new User(name, email);
        mFirebaseDatabase.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User created successfully in Firebase.");
                    } else {
                        Log.e(TAG, "Failed to create user.", task.getException());
                    }
                });
    }

    private void updateUser(String name, String email) {
        mFirebaseDatabase.child(userId).child("name").setValue(name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User name updated successfully.");
                    } else {
                        Log.e(TAG, "Failed to update user name.", task.getException());
                    }
                });

        mFirebaseDatabase.child(userId).child("email").setValue(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User email updated successfully.");
                    } else {
                        Log.e(TAG, "Failed to update user email.", task.getException());
                    }
                });
    }
}
