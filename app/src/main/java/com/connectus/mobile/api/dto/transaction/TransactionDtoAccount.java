package com.connectus.mobile.api.dto.transaction;

public class TransactionDtoAccount {
    private String product;
    private String accountNumber;

    public TransactionDtoAccount(String product, String accountNumber) {
        this.product = product;
        this.accountNumber = accountNumber;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
