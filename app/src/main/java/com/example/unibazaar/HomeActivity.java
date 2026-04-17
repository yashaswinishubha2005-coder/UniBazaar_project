package com.example.unibazaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewListings;
    private Button buttonLogout, buttonAllItems, buttonBooks, buttonElectronics;

    private List<Listing> listingList;
    private ListingAdapter listingAdapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerViewListings = findViewById(R.id.recyclerViewListings);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonAllItems = findViewById(R.id.buttonAllItems);
        buttonBooks = findViewById(R.id.buttonBooks);
        buttonElectronics = findViewById(R.id.buttonElectronics);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        listingList = new ArrayList<>();
        listingAdapter = new ListingAdapter(this, listingList);

        recyclerViewListings.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewListings.setAdapter(listingAdapter);

        buttonAllItems.setOnClickListener(v -> selectCategory("All"));
        buttonBooks.setOnClickListener(v -> selectCategory("Books"));
        buttonElectronics.setOnClickListener(v -> selectCategory("Electronics"));

        updateCategoryButtons();

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadListings(selectedCategory);
    }

    private void selectCategory(String category) {
        selectedCategory = category;
        updateCategoryButtons();
        loadListings(selectedCategory);
    }

    private void updateCategoryButtons() {
        styleCategoryButton(buttonAllItems, "All".equals(selectedCategory));
        styleCategoryButton(buttonBooks, "Books".equals(selectedCategory));
        styleCategoryButton(buttonElectronics, "Electronics".equals(selectedCategory));
    }

    private void styleCategoryButton(Button button, boolean isSelected) {
        button.setBackgroundResource(isSelected ? R.drawable.chip_selected : R.drawable.chip_unselected);
        button.setTextColor(getColor(isSelected ? R.color.white : R.color.ub_deep));
    }

    private void loadListings(String category) {
        Query query = db.collection("listings");

        if (!"All".equals(category)) {
            query = query.whereEqualTo("category", category);
        }

        query.get()
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
