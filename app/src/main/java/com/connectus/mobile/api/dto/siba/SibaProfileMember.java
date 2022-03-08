package com.connectus.mobile.api.dto.siba;

import java.util.UUID;

public class SibaProfileMember {
    private String _id;
    private String profile;
    private UUID memberProfileId;
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private String createdAt;
    private String updatedAt;

    public SibaProfileMember(String _id, String profile, UUID memberProfileId, UUID userId, String username, String firstName, String lastName, String status, String createdAt, String updatedAt) {
        this._id = _id;
        this.profile = profile;
        this.memberProfileId = memberProfileId;
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public UUID getMemberProfileId() {
        return memberProfileId;
    }

    public void setMemberProfileId(UUID memberProfileId) {
        this.memberProfileId = memberProfileId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}