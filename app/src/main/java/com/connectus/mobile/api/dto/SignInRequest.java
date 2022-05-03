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


    public SignInRequest(String msisdn, String password) {
        this.msisdn = msisdn;
        this.password = password;
    }
}

