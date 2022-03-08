package com.connectus.mobile.api.dto.airtime;

public class MobileTopupRequest {
    private String recipientPhone;
    private long operatorId;
    private double amount;

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public MobileTopupRequest(String recipientPhone, long operatorId, double amount) {


        this.recipientPhone = recipientPhone;
        this.operatorId = operatorId;
        this.amount = amount;
    }
}
