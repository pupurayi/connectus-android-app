package com.connectus.mobile.api.dto;

public class ResetPasswordRequest {
    private String msisdn;
    private String otp;
    private String password;

    public ResetPasswordRequest(String msisdn, String otp, String password) {
        this.msisdn = msisdn;
        this.otp = otp;
        this.password = password;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
