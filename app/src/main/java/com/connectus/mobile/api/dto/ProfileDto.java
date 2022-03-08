package com.connectus.mobile.api.dto;

import com.connectus.mobile.common.Sex;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class ProfileDto {
    private UUID id;
    private String msisdn;
    private String email;
    private String firstName;
    private String lastName;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    private String userStatus;
    private boolean avatarAvailable;
    private UUID profileId;
    private String referralCode;
    private Date dob;
    private Sex sex;
    private AddressResponse address;
    private IdentificationResponse identification;
    private String profileStatus;
    private Set<BalanceDTO> balances;
    private PaymateDTO paymate;


    public ProfileDto(UUID id, String msisdn, String userStatus, boolean avatarAvailable, UUID profileId, String referralCode, String email, String firstName, String lastName, Date dob, Sex sex, AddressResponse address, IdentificationResponse identification, String profileStatus, PaymateDTO paymate, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.msisdn = msisdn;
        this.userStatus = userStatus;
        this.avatarAvailable = avatarAvailable;
        this.profileId = profileId;
        this.referralCode = referralCode;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.sex = sex;
        this.address = address;
        this.identification = identification;
        this.profileStatus = profileStatus;
        this.paymate = paymate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public AddressResponse getAddress() {
        return address;
    }

    public void setAddress(AddressResponse address) {
        this.address = address;
    }

    public IdentificationResponse getIdentification() {
        return identification;
    }

    public void setIdentification(IdentificationResponse identification) {
        this.identification = identification;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }

    public Set<BalanceDTO> getBalances() {
        return balances;
    }

    public void setBalances(Set<BalanceDTO> balances) {
        this.balances = balances;
    }

    public PaymateDTO getPaymate() {
        return paymate;
    }

    public void setPaymate(PaymateDTO paymate) {
        this.paymate = paymate;
    }
}
