package com.example.unibazaar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private Context context;
    private List<Listing> listingList;

    public ListingAdapter(Context context, List<Listing> listingList) {
        this.context = context;
        this.listingList = listingList;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listingList.get(position);

        holder.textViewTitle.setText(listing.getTitle());
        holder.textViewDescription.setText(listing.getDescription());
        holder.textViewPrice.setText("Rs. " + listing.getPrice());
        holder.textViewCategory.setText("Category: " + listing.getCategory());
        Glide.with(context)
                .load(listing.getImageUrl())
                .placeholder(R.drawable.ic_unibazaar_logo)
                .error(R.drawable.ic_unibazaar_logo)
                .into(holder.imageViewListing);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("title", listing.getTitle());
            intent.putExtra("description", listing.getDescription());
            intent.putExtra("price", listing.getPrice());
            intent.putExtra("category", listing.getCategory());
            intent.putExtra("imageUrl", listing.getImageUrl());
            intent.putExtra("userId", listing.getUserId());
            intent.putExtra("listingId", listing.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listingList.size();
    }

    public static class ListingViewHolder extends RecyclerView.ViewHolder {

        android.widget.ImageView imageViewListing;
        TextView textViewTitle, textViewDescription, textViewPrice, textViewCategory;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewListing = itemView.findViewById(R.id.imageViewListing);
            textViewTitle = itemView.findViewById(R.id.textViewItemTitle);
            textViewDescription = itemView.findViewById(R.id.textViewItemDescription);
            textViewPrice = itemView.findViewById(R.id.textViewItemPrice);
            textViewCategory = itemView.findViewById(R.id.textViewItemCategory);
        }
    }
}