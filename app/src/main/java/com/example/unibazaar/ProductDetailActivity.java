package com.example.unibazaar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageViewProduct;
    private TextView textViewTitle, textViewDescription, textViewPrice, textViewCategory;
    private Button buttonChat, buttonEditListing;
    private String sellerUserId;
    private String listingId;
    private String title;
    private String description;
    private String price;
    private String category;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imageViewProduct = findViewById(R.id.imageViewProduct);
        textViewTitle = findViewById(R.id.textViewDetailTitle);
        textViewDescription = findViewById(R.id.textViewDetailDescription);
        textViewPrice = findViewById(R.id.textViewDetailPrice);
        textViewCategory = findViewById(R.id.textViewDetailCategory);
        buttonChat = findViewById(R.id.buttonChat);
        buttonEditListing = findViewById(R.id.buttonEditListing);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        price = getIntent().getStringExtra("price");
        category = getIntent().getStringExtra("category");
        imageUrl = getIntent().getStringExtra("imageUrl");
        sellerUserId = getIntent().getStringExtra("userId");
        listingId = getIntent().getStringExtra("listingId");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_unibazaar_logo)
                .error(R.drawable.ic_unibazaar_logo)
                .into(imageViewProduct);

        textViewTitle.setText(title);
        textViewDescription.setText(description);
        textViewPrice.setText("Rs. " + price);
        textViewCategory.setText("Category: " + category);

        setupActionButtons();
    }

    private void setupActionButtons() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            buttonChat.setOnClickListener(v -> {
                Toast.makeText(ProductDetailActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
                finish();
            });
            buttonEditListing.setVisibility(View.GONE);
            return;
        }

        String currentUserId = currentUser.getUid();
        boolean isOwner = !TextUtils.isEmpty(sellerUserId) && currentUserId.equals(sellerUserId);

        if (isOwner) {
            buttonChat.setText("This is your listing");
            buttonChat.setEnabled(false);
            buttonEditListing.setVisibility(View.VISIBLE);
            buttonEditListing.setOnClickListener(v -> openEditListingScreen());
            return;
        }

        buttonEditListing.setVisibility(View.GONE);
        buttonChat.setOnClickListener(v -> {
            if (TextUtils.isEmpty(sellerUserId)) {
                Toast.makeText(ProductDetailActivity.this, "Seller details are missing for this listing", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(ProductDetailActivity.this, ChatActivity.class);
            intent.putExtra("otherUserId", sellerUserId);
            startActivity(intent);
        });
    }

    private void openEditListingScreen() {
        if (TextUtils.isEmpty(listingId)) {
            Toast.makeText(this, "Listing details are missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ProductDetailActivity.this, AddListingActivity.class);
        intent.putExtra("listingId", listingId);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("price", price);
        intent.putExtra("category", category);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }
}
