package com.example.unibazaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RoleSelectionActivity extends AppCompatActivity {

    private Button buttonBuyer, buttonSeller;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        buttonBuyer = findViewById(R.id.buttonBuyer);
        buttonSeller = findViewById(R.id.buttonSeller);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonBuyer.setOnClickListener(v -> saveRoleAndContinue("buyer"));
        buttonSeller.setOnClickListener(v -> saveRoleAndContinue("seller"));
    }

    private void saveRoleAndContinue(String role) {
        String uid = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("email", email);
        userMap.put("role", role);

        db.collection("users")
                .document(uid)
                .set(userMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RoleSelectionActivity.this, "Role saved: " + role, Toast.LENGTH_SHORT).show();

                    if (role.equals("buyer")) {
                        startActivity(new Intent(RoleSelectionActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(RoleSelectionActivity.this, SellerHomeActivity.class));
                    }

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(RoleSelectionActivity.this,
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}
