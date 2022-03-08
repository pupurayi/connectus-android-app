package com.connectus.mobile.api.dto;

import java.util.Date;

public class BalanceDTO {
    private long balanceId;
    private String currency;
    private double amount;
    private Date createdAt;
    private Date updatedAt;

    public BalanceDTO(long balanceId, String currency, double amount, Date createdAt, Date updatedAt) {
        this.balanceId = balanceId;
        this.currency = currency;
        this.amount = amount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(long balanceId) {
        this.balanceId = balanceId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
