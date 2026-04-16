package com.example.unibazaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SellerHomeActivity extends AppCompatActivity {

    private Button buttonAddListing, buttonMyListings, buttonViewChats, buttonLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        mAuth = FirebaseAuth.getInstance();

        buttonAddListing = findViewById(R.id.buttonAddListing);
        buttonMyListings = findViewById(R.id.buttonMyListings);
        buttonViewChats = findViewById(R.id.buttonViewChats);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonAddListing.setOnClickListener(v ->
                startActivity(new Intent(SellerHomeActivity.this, AddListingActivity.class)));

        buttonMyListings.setOnClickListener(v ->
                startActivity(new Intent(SellerHomeActivity.this, MyListingsActivity.class)));

        buttonViewChats.setOnClickListener(v ->
                startActivity(new Intent(SellerHomeActivity.this, ChatListActivity.class)));

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(SellerHomeActivity.this, LoginActivity.class));
            finish();
        });
    }
}