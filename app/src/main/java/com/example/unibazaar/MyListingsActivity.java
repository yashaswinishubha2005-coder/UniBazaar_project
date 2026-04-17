package com.example.unibazaar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyListingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyListings;
    private TextView textViewEmptyState;
    private Button buttonAllListings, buttonBooksListings, buttonElectronicsListings;
    private List<Listing> listingList;
    private ListingAdapter listingAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        recyclerViewMyListings = findViewById(R.id.recyclerViewMyListings);
        textViewEmptyState = findViewById(R.id.textViewMyListingsEmptyState);
        buttonAllListings = findViewById(R.id.buttonAllListings);
        buttonBooksListings = findViewById(R.id.buttonBooksListings);
        buttonElectronicsListings = findViewById(R.id.buttonElectronicsListings);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listingList = new ArrayList<>();
        listingAdapter = new ListingAdapter(this, listingList);

        recyclerViewMyListings.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewMyListings.setAdapter(listingAdapter);

        buttonAllListings.setOnClickListener(v -> selectCategory("All"));
        buttonBooksListings.setOnClickListener(v -> selectCategory("Books"));
        buttonElectronicsListings.setOnClickListener(v -> selectCategory("Electronics"));
        updateCategoryButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMyListings(selectedCategory);
    }

    private void selectCategory(String category) {
        selectedCategory = category;
        updateCategoryButtons();
        loadMyListings(selectedCategory);
    }

    private void updateCategoryButtons() {
        styleCategoryButton(buttonAllListings, "All".equals(selectedCategory));
        styleCategoryButton(buttonBooksListings, "Books".equals(selectedCategory));
        styleCategoryButton(buttonElectronicsListings, "Electronics".equals(selectedCategory));
    }

    private void styleCategoryButton(Button button, boolean isSelected) {
        button.setBackgroundResource(isSelected ? R.drawable.chip_selected : R.drawable.chip_unselected);
        button.setTextColor(getColor(isSelected ? R.color.white : R.color.ub_deep));
    }

    private void loadMyListings(String category) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Query query = db.collection("listings")
                .whereEqualTo("userId", currentUser.getUid());
        if (!"All".equals(category)) {
            query = query.whereEqualTo("category", category);
        }

        query
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
