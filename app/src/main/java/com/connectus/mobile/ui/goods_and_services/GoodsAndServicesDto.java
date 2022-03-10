package com.connectus.mobile.ui.goods_and_services;

import java.time.ZonedDateTime;
import java.util.UUID;

public class GoodsAndServicesDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private int rating;
    private String created;
    private String updated;

    public GoodsAndServicesDto(UUID id, UUID userId, String name, String description, int rating, String created, String updated) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
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
