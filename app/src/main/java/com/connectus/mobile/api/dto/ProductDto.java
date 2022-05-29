package com.connectus.mobile.api.dto;

import java.util.UUID;

public class ProductDto {
    private UUID id;
    private UUID userId;
    private String category;
    private String name;
    private String description;
    private double price;
    private String imageFirst;
    private double lat;
    private double lng;
    private float rating;
    private String created;
    private String updated;

    public ProductDto(UUID id, UUID userId, String category, String name, String description, double price, String imageFirst, double lat, double lng, float rating, String created, String updated) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageFirst = imageFirst;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.created = created;
        this.updated = updated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageFirst() {
        return imageFirst;
    }

    public void setImageFirst(String imageFirst) {
        this.imageFirst = imageFirst;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
