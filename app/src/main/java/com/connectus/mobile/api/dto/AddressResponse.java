package com.connectus.mobile.api.dto;

import com.connectus.mobile.common.DocumentStatus;

import java.util.UUID;

public class AddressResponse {
    private UUID addressId;
    private String addressLine1;
    private String province;
    private String city;
    private String countryCode;
    private DocumentStatus status;
    private String statusMessage;
    private String fileName;

    public AddressResponse(UUID addressId, String addressLine1, String province, String city, String countryCode, DocumentStatus status, String statusMessage, String fileName) {
        this.addressId = addressId;
        this.addressLine1 = addressLine1;
        this.province = province;
        this.city = city;
        this.countryCode = countryCode;
        this.status = status;
        this.statusMessage = statusMessage;
        this.fileName = fileName;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
