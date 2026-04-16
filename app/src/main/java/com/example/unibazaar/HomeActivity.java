package com.example.unibazaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewListings;
    private Button buttonLogout;

    private List<Listing> listingList;
    private ListingAdapter listingAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerViewListings = findViewById(R.id.recyclerViewListings);
        buttonLogout = findViewById(R.id.buttonLogout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        listingList = new ArrayList<>();
        listingAdapter = new ListingAdapter(this, listingList);

        recyclerViewListings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewListings.setAdapter(listingAdapter);

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadListings();
    }

    private void loadListings() {
        db.collection("listings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listingList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Listing listing = document.toObject(Listing.class);
                        listingList.add(listing);
                    }

                    listingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(HomeActivity.this,
                                "Failed to load listings: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}
