package com.connectus.mobile.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInRequest {

    @SerializedName("msisdn")
    @Expose
    private String msisdn;

    @SerializedName("password")
    @Expose
    private String password;


    @SerializedName("fcmToken")
    @Expose
    private String fcmToken;


    public SignInRequest(String msisdn, String password, String fcmToken) {
        this.msisdn = msisdn;
        this.password = password;
        this.fcmToken = fcmToken;
    }
}

