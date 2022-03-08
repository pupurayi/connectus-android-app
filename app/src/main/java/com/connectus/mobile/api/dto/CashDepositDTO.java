package com.connectus.mobile.api.dto;

public class CashDepositDTO {
    private String customer;
    private String currency;
    private double amount;

    public CashDepositDTO(String customer, String currency, double amount) {
        this.customer = customer;
        this.currency = currency;
        this.amount = amount;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
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
