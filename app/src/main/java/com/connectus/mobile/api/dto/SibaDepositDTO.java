package com.connectus.mobile.api.dto;

public class SibaDepositDTO {
    private String currency;
    private double amount;
    private String contributionType;

    public SibaDepositDTO(String currency, double amount, String contributionType) {
        this.currency = currency;
        this.amount = amount;
        this.contributionType = contributionType;
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

    public String getContributionType() {
        return contributionType;
    }

    public void setContributionType(String contributionType) {
        this.contributionType = contributionType;
    }
}
