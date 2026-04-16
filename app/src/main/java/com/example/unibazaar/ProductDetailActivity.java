package com.example.unibazaar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private Button buttonChat;
    private String sellerUserId;

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

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String price = getIntent().getStringExtra("price");
        String category = getIntent().getStringExtra("category");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        sellerUserId = getIntent().getStringExtra("userId");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_unibazaar_logo)
                .error(R.drawable.ic_unibazaar_logo)
                .into(imageViewProduct);
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        textViewPrice.setText("Rs. " + price);
        textViewCategory.setText("Category: " + category);

        buttonChat.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(ProductDetailActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
                finish();
                return;
            }

            if (TextUtils.isEmpty(sellerUserId)) {
                Toast.makeText(ProductDetailActivity.this, "Seller details are missing for this listing", Toast.LENGTH_LONG).show();
                return;
            }

            String currentUserId = currentUser.getUid();

            if (currentUserId.equals(sellerUserId)) {
                buttonChat.setEnabled(false);
                buttonChat.setText("This is your listing");
                return;
            }

            Intent intent = new Intent(ProductDetailActivity.this, ChatActivity.class);
            intent.putExtra("otherUserId", sellerUserId);
            startActivity(intent);
        });
    }
}