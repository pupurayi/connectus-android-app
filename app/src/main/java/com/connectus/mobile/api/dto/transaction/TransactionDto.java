package com.connectus.mobile.api.dto.transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TransactionDto {
    private String reference;
    private String channel;
    private String type;
    private String msisdn;
    private String payer;
    private String destinationMsisdn;
    private String payee;
    private String paymateCode;
    private TransactionDtoAccount creditAccount;
    private String creditReference;
    private TransactionDtoAccount debitAccount;
    private String debitReference;
    private String currencyCode;
    private BigDecimal amount;
    private List<TransactionBalanceDto> balances;
    private String narration;
    private String extendedType;
    private String responseCode;
    private String responseDescription;
    private String upstreamResponseCode;
    private String upstreamResponseDescription;
    private Map<String, Object> additionalData;
    private Date created;

    public TransactionDto(String channel, String type, String msisdn, TransactionDtoAccount creditAccount, String extendedType) {
        this.channel = channel;
        this.type = type;
        this.msisdn = msisdn;
        this.creditAccount = creditAccount;
        this.extendedType = extendedType;
    }

    public TransactionDto() {
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getDestinationMsisdn() {
        return destinationMsisdn;
    }

    public void setDestinationMsisdn(String destinationMsisdn) {
        this.destinationMsisdn = destinationMsisdn;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getPaymateCode() {
        return paymateCode;
    }

    public void setPaymateCode(String paymateCode) {
        this.paymateCode = paymateCode;
    }

    public TransactionDtoAccount getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(TransactionDtoAccount creditAccount) {
        this.creditAccount = creditAccount;
    }

    public String getCreditReference() {
        return creditReference;
    }

    public void setCreditReference(String creditReference) {
        this.creditReference = creditReference;
    }

    public TransactionDtoAccount getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(TransactionDtoAccount debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getDebitReference() {
        return debitReference;
    }

    public void setDebitReference(String debitReference) {
        this.debitReference = debitReference;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<TransactionBalanceDto> getBalances() {
        return balances;
    }

    public void setBalances(List<TransactionBalanceDto> balances) {
        this.balances = balances;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getExtendedType() {
        return extendedType;
    }

    public void setExtendedType(String extendedType) {
        this.extendedType = extendedType;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getUpstreamResponseCode() {
        return upstreamResponseCode;
    }

    public void setUpstreamResponseCode(String upstreamResponseCode) {
        this.upstreamResponseCode = upstreamResponseCode;
    }

    public String getUpstreamResponseDescription() {
        return upstreamResponseDescription;
    }

    public void setUpstreamResponseDescription(String upstreamResponseDescription) {
        this.upstreamResponseDescription = upstreamResponseDescription;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}

