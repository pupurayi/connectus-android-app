package com.connectus.mobile.api.dto.airtime;


import java.math.BigDecimal;

public class ReloadlyFX {
    private BigDecimal rate;
    private String currencyCode;

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
