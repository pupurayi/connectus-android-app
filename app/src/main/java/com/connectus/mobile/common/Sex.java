package com.connectus.mobile.common;

public enum Sex {
    M("Male"),
    F("Female");

    private final String title;

    Sex(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

