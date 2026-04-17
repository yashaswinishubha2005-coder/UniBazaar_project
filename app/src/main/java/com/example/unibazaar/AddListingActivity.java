package com.example.unibazaar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddListingActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextPrice, editTextImageUrl;
    private Spinner spinnerCategory;
    private Button buttonPostListing;
    private String[] categories;
    private boolean isEditMode;
    private String listingId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextImageUrl = findViewById(R.id.editTextImageUrl);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonPostListing = findViewById(R.id.buttonPostListing);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        categories = new String[]{"Books", "Electronics", "Furniture", "Clothes", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        setupEditMode();

        buttonPostListing.setOnClickListener(v -> saveListing());
    }

    private void setupEditMode() {
        listingId = getIntent().getStringExtra("listingId");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String price = getIntent().getStringExtra("price");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String category = getIntent().getStringExtra("category");

        isEditMode = !TextUtils.isEmpty(listingId);
        if (!isEditMode) {
            return;
        }

        editTextTitle.setText(title);
        editTextDescription.setText(description);
        editTextPrice.setText(price);
        editTextImageUrl.setText(imageUrl);
        buttonPostListing.setText("Update Listing");

        if (!TextUtils.isEmpty(category)) {
            for (int i = 0; i < categories.length; i++) {
                if (category.equals(categories[i])) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveListing() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String imageUrl = editTextImageUrl.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Enter title");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Enter description");
            return;
        }

        if (TextUtils.isEmpty(price)) {
            editTextPrice.setError("Enter price");
            return;
        }

        if (TextUtils.isEmpty(imageUrl)) {
            editTextImageUrl.setError("Enter image URL");
            return;
        }

        if (!Patterns.WEB_URL.matcher(imageUrl).matches()) {
            editTextImageUrl.setError("Enter a valid image URL");
            return;
        }

        progressDialog.setMessage("Saving listing...");
        progressDialog.show();

        String docId = isEditMode ? listingId : db.collection("listings").document().getId();
        String userId = mAuth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        Listing listing = new Listing(
                docId,
                title,
                description,
                price,
                category,
                imageUrl,
                userId,
                timestamp
        );

        db.collection("listings")
                .document(docId)
                .set(listing)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    String message = isEditMode ? "Listing updated successfully" : "Listing posted successfully";
                    Toast.makeText(AddListingActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddListingActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
