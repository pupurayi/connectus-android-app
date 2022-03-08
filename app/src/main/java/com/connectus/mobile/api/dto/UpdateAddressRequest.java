package com.connectus.mobile.api.dto;

public class UpdateAddressRequest {
    private String addressLine1;
    private String city;
    private String province;
    private String countryCode;

    public UpdateAddressRequest(String addressLine1, String city, String province, String countryCode) {
        this.addressLine1 = addressLine1;
        this.city = city;
        this.province = province;
        this.countryCode = countryCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
