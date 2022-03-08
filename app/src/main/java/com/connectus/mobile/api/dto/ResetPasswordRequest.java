package com.connectus.mobile.api.dto;

public class ResetPasswordRequest {
    private String username;
    private String otp;
    private String password;

    public ResetPasswordRequest(String username, String otp, String password) {
        this.username = username;
        this.otp = otp;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
