package com.connectus.mobile.api.dto;

public class Marker {
    private String productName;
    private double price;
    private double lat;
    private double lng;

    public Marker() {
    }

    public Marker(String productName, double price, double lat, double lng) {
        this.productName = productName;
        this.price = price;
        this.lat = lat;
        this.lng = lng;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
