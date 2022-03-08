package com.connectus.mobile.api.dto;

import com.connectus.mobile.common.IdType;

public class UpdateIdentificationRequest {
    private IdType type;
    private String number;
    private String countryCode;

    public UpdateIdentificationRequest(IdType type, String number, String countryCode) {
        this.type = type;
        this.number = number;
        this.countryCode = countryCode;
    }

    public IdType getType() {
        return type;
    }

    public void setType(IdType type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
