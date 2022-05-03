package com.connectus.mobile.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpRequest {

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("msisdn")
    @Expose
    private String msisdn;

    @SerializedName("password")
    @Expose
    private String password;

    public SignUpRequest(String firstName, String lastName, String email, String msisdn, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.msisdn = msisdn;
        this.password = password;
    }
}

