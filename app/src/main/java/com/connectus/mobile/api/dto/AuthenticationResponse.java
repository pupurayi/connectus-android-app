package com.connectus.mobile.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {
    @SerializedName("accountDTO")
    @Expose
    ProfileDTO accountDTO;

    @SerializedName("jwtDTO")
    @Expose
    JWT JWT;

    public ProfileDTO getProfile() {
        return accountDTO;
    }

    public void setProfile(ProfileDTO profileDTO) {
        this.accountDTO = profileDTO;
    }

    public JWT getJWT() {
        return JWT;
    }

    public void setJWT(JWT JWT) {
        this.JWT = JWT;
    }
}

