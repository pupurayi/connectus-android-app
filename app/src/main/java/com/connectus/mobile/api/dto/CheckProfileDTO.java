package com.connectus.mobile.api.dto;

import java.util.UUID;

public class CheckProfileDTO {
    private UUID userId;
    private String msisdn;
    private String userStatus;
    private boolean avatarAvailable;
    private UUID profileId;
    private String firstName;
    private String lastName;
    private String profileStatus;

    public CheckProfileDTO(UUID userId, String msisdn, String userStatus, boolean avatarAvailable, UUID profileId, String firstName, String lastName, String profileStatus) {
        this.userId = userId;
        this.msisdn = msisdn;
        this.userStatus = userStatus;
        this.avatarAvailable = avatarAvailable;
        this.profileId = profileId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileStatus = profileStatus;
    }

    public UUID getId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isAvatarAvailable() {
        return avatarAvailable;
    }

    public void setAvatarAvailable(boolean avatarAvailable) {
        this.avatarAvailable = avatarAvailable;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }
}

