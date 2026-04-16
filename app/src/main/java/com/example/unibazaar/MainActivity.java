package com.example.unibazaar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        startActivity(new Intent(MainActivity.this, RoleSelectionActivity.class));
                    } else {
                        String role = documentSnapshot.getString("role");

                        if ("buyer".equals(role)) {
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        } else if ("seller".equals(role)) {
                            startActivity(new Intent(MainActivity.this, SellerHomeActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, RoleSelectionActivity.class));
                        }

                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                });
    }
}
