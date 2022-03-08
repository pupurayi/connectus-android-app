package com.connectus.mobile.api.dto;

public enum TransactionType {
    CASH_DEPOSIT("Cash Deposit"),
    WIRE_DEPOSIT("Deposit"),
    AGENT_CASH_DEPOSIT("Customer Deposit"),
    CASH_WITHDRAWAL("Cash Withdrawal"),
    AGENT_CASH_WITHDRAWAL("Customer Withdrawal"),
    INTERNAL_TRANSFER("Transfer"),
    GOODS_AND_SERVICES("Goods & Services");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
