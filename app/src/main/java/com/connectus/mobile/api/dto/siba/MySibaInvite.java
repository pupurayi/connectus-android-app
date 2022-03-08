package com.connectus.mobile.api.dto.siba;

import java.util.Date;

public class MySibaInvite {
    private String invite;
    private String subject;
    private String createdBy;
    private Date createdAt;

    public MySibaInvite(String invite, String subject, String createdBy, Date createdAt) {
        this.invite = invite;
        this.subject = subject;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
