package com.connectus.mobile.common;

import java.util.UUID;

public class Sender {
    private UUID profileId;
    private UUID userId;
    private String nickName;

    public Sender(UUID profileId, UUID userId, String nickName) {
        this.profileId = profileId;
        this.userId = userId;
        this.nickName = nickName;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID getId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}