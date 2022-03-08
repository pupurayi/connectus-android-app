package com.connectus.mobile.ui.siba;

public class EligibilityResponse {
    private Long profileId;
    private String username;
    private String firstName;
    private String lastName;

    public EligibilityResponse(Long profileId, String username, String firstName, String lastName) {
        this.profileId = profileId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
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
}
