package com.connectus.mobile.common;

public enum IdType {
    PASSPORT("Passport"),
    DRIVERS_LICENCE("Drivers License"),
    NATIONAL_ID("National ID");

    String label;

    IdType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
