package com.connectus.mobile.api.dto.transaction;

import java.math.BigDecimal;

public class TransactionBalanceDto {
    private String profileId;
    private String currency;
    private BigDecimal balance;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
