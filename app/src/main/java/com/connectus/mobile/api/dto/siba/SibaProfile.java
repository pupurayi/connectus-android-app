package com.connectus.mobile.api.dto.siba;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SibaProfile {
    private UUID id;
    private String subject;
    private UUID adminProfileId;
    private String currency;
    private Double balance;
    private String status;
    private List<SibaProfileMember> members;
    private List<SibaInvite> invites;
    private List<SibaPlan> plans;
    private Date createdAt;
    private Date updatedAt;

    public SibaProfile(UUID id, String subject, UUID adminProfileId, String currency, Double balance, String status, List<SibaProfileMember> members, List<SibaInvite> invites, List<SibaPlan> plans, Date createdAt, Date updatedAt) {
        this.id = id;
        this.subject = subject;
        this.adminProfileId = adminProfileId;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.members = members;
        this.invites = invites;
        this.plans = plans;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public UUID getAdminProfileId() {
        return adminProfileId;
    }

    public void setAdminProfileId(UUID adminProfileId) {
        this.adminProfileId = adminProfileId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SibaProfileMember> getMembers() {
        return members;
    }

    public void setMembers(List<SibaProfileMember> members) {
        this.members = members;
    }

    public List<SibaInvite> getInvites() {
        return invites;
    }

    public void setInvites(List<SibaInvite> invites) {
        this.invites = invites;
    }

    public List<SibaPlan> getPlans() {
        return plans;
    }

    public void setPlans(List<SibaPlan> plans) {
        this.plans = plans;
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
