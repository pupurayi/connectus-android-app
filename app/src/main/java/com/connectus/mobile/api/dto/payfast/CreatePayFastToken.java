package com.connectus.mobile.api.dto.payfast;

public class CreatePayFastToken {
    private String currency;
    private double amount;
    private String itemName;
    private String itemDescription;

    public CreatePayFastToken(String currency, double amount, String itemName, String itemDescription) {
        this.currency = currency;
        this.amount = amount;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
