package com.connectus.mobile.api.dto.siba;

import java.util.Date;

public class SibaInvite {
    private String _id;
    private String profile;
    private Long invitedProfileId;
    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private Date createdAt;
    private Date updatedAt;

    public SibaInvite(String _id, String profile, Long invitedProfileId, String username, String firstName, String lastName, String status, Date createdAt, Date updatedAt) {
        this._id = _id;
        this.profile = profile;
        this.invitedProfileId = invitedProfileId;
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

    public Long getInvitedProfileId() {
        return invitedProfileId;
    }

    public void setInvitedProfileId(Long invitedProfileId) {
        this.invitedProfileId = invitedProfileId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
