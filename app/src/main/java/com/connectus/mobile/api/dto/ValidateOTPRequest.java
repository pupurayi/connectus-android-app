package com.connectus.mobile.api.dto;

public class ValidateOTPRequest {
    private String otp;

    public ValidateOTPRequest(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

