package com.connectus.mobile.ui.offering;

import java.time.ZonedDateTime;
import java.util.UUID;

public class OfferingDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private int rating;
    private ZonedDateTime created;
    private ZonedDateTime updated;

    public OfferingDto(UUID id, UUID userId, String name, String description, int rating, ZonedDateTime created, ZonedDateTime updated) {
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

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }
}
