package com.connectus.mobile.api.dto.siba;

import java.util.List;

public class CreateSibaProfileDTO {
    private String subject;
    private String currency;
    private List<Long> invites;

    public CreateSibaProfileDTO(String subject, String currency, List<Long> invites) {
        this.subject = subject;
        this.currency = currency;
        this.invites = invites;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Long> getInvites() {
        return invites;
    }

    public void setInvites(List<Long> invites) {
        this.invites = invites;
    }
}
