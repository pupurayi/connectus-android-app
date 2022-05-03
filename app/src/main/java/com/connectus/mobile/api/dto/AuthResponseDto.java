package com.connectus.mobile.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponseDto {
    @SerializedName("user")
    @Expose
    UserDto profile;

    @SerializedName("jwt")
    @Expose
    JWT jwt;

    public UserDto getProfile() {
        return profile;
    }

    public void setProfile(UserDto profile) {
        this.profile = profile;
    }

    public JWT getJwt() {
        return jwt;
    }

    public void setJwt(JWT jwt) {
        this.jwt = jwt;
    }
}

