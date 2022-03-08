package com.connectus.mobile.api.dto;

public class CashWithdrawalDTO {
    private long paymateCode;
    private String currency;
    private double amount;

    public CashWithdrawalDTO(long paymateCode, String currency, double amount) {
        this.paymateCode = paymateCode;
        this.currency = currency;
        this.amount = amount;
    }

    public long getPaymateCode() {
        return paymateCode;
    }

    public void setPaymate(long paymateCode) {
        this.paymateCode = paymateCode;
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
