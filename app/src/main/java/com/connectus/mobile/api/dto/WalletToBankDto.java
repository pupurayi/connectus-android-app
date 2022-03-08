package com.connectus.mobile.api.dto;

public class WalletToBankDto {
    private String bankCode;
    private String profileNumber;
    private String currency;
    private double amount;

    public WalletToBankDto(String bankCode, String profileNumber, String currency, double amount) {
        this.bankCode = bankCode;
        this.profileNumber = profileNumber;
        this.currency = currency;
        this.amount = amount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getProfileNumber() {
        return profileNumber;
    }

    public void setProfileNumber(String profileNumber) {
        this.profileNumber = profileNumber;
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
}
