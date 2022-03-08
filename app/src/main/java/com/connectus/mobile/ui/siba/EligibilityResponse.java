package com.connectus.mobile.ui.siba;

public class EligibilityResponse {
    private Long profileId;
    private String msisdn;
    private String firstName;
    private String lastName;

    public EligibilityResponse(Long profileId, String msisdn, String firstName, String lastName) {
        this.profileId = profileId;
        this.msisdn = msisdn;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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
