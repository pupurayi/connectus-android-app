package com.connectus.mobile.api.dto;

import java.util.UUID;

public class CreateProductDto {
    private UUID userId;
    private String category;
    private String name;
    private String description;
    private double price;
    private String imageFirst;
    private String imageSecond;
    private double lat;
    private double lng;

    public CreateProductDto(UUID userId, String category, String name, String description, double price, String imageFirst, String imageSecond, double lat, double lng) {
        this.userId = userId;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageFirst = imageFirst;
        this.imageSecond = imageSecond;
        this.lat = lat;
        this.lng = lng;
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

    public String getImageSecond() {
        return imageSecond;
    }

    public void setImageSecond(String imageSecond) {
        this.imageSecond = imageSecond;
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
}
