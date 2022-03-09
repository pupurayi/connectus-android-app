package com.connectus.mobile.ui.old.services;

public class EmalyamiService {
    private String id;
    private String name;
    private String description;
    private int iconResource;

    public EmalyamiService(String id, String name, String description, int iconResource) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResource = iconResource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }
}
