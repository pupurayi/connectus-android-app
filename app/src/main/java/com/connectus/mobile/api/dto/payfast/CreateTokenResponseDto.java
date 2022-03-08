package com.connectus.mobile.api.dto.payfast;

import java.util.UUID;

public class CreateTokenResponseDto {
    private UUID token;
    private String redirectUrl;

    public CreateTokenResponseDto(UUID token, String redirectUrl) {
        this.token = token;
        this.redirectUrl = redirectUrl;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
