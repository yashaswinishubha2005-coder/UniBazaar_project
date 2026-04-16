package com.example.unibazaar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyListingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyListings;
    private TextView textViewEmptyState;
    private List<Listing> listingList;
    private ListingAdapter listingAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        recyclerViewMyListings = findViewById(R.id.recyclerViewMyListings);
        textViewEmptyState = findViewById(R.id.textViewMyListingsEmptyState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listingList = new ArrayList<>();
        listingAdapter = new ListingAdapter(this, listingList);

        recyclerViewMyListings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyListings.setAdapter(listingAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMyListings();
    }

    private void loadMyListings() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("listings")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listingList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Listing listing = document.toObject(Listing.class);
                        listingList.add(listing);
                    }

                    listingAdapter.notifyDataSetChanged();
                    updateEmptyState();
                })
                .addOnFailureListener(e -> {
                    updateEmptyState();
                    Toast.makeText(MyListingsActivity.this,
                            "Failed to load listings: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateEmptyState() {
        if (listingList.isEmpty()) {
            textViewEmptyState.setVisibility(View.VISIBLE);
            recyclerViewMyListings.setVisibility(View.GONE);
        } else {
            textViewEmptyState.setVisibility(View.GONE);
            recyclerViewMyListings.setVisibility(View.VISIBLE);
        }
    }
}