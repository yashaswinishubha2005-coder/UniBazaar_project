package com.example.unibazaar;

public class Listing {

    private String id;
    private String title;
    private String description;
    private String price;
    private String category;
    private String imageUrl;
    private String userId;
    private long timestamp;

    public Listing() {
        // Required empty constructor for Firestore
    }

    public Listing(String id, String title, String description,
                   String price, String category, String imageUrl,
                   String userId, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}