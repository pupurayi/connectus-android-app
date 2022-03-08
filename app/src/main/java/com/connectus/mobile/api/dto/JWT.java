package com.connectus.mobile.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JWT {
    @SerializedName("token_type")
    @Expose
    private String token_type;

    @SerializedName("access_token")
    @Expose
    private String access_token;

    @SerializedName("expires_in")
    @Expose
    private long expires_in;

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
