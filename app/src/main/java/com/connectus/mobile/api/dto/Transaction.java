package com.connectus.mobile.api.dto;

import java.util.Date;

public class Transaction {
    private long transactionId;
    private String reference;
    private String type;
    private boolean success;
    private String currency;
    private double debit;
    private double credit;
    private double charge;
    private String note;
    private Date createdAt;
    private Date updatedAt;

    public Transaction(long transactionId, String reference, String type, boolean success, String currency, double debit, double credit, double charge, String note, Date createdAt, Date updatedAt) {
        this.transactionId = transactionId;
        this.reference = reference;
        this.type = type;
        this.success = success;
        this.currency = currency;
        this.debit = debit;
        this.credit = credit;
        this.charge = charge;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
