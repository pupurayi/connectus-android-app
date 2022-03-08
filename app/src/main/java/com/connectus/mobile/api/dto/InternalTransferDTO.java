package com.connectus.mobile.api.dto;

public class InternalTransferDTO {
    private String to;
    private String currency;
    private double amount;
    private String note;

    public InternalTransferDTO(String to, String currency, double amount, String note) {
        this.to = to;
        this.currency = currency;
        this.amount = amount;
        this.note = note;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
