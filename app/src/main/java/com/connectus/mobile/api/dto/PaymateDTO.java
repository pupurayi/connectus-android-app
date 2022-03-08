package com.connectus.mobile.api.dto;

public class PaymateDTO {
    private long paymateCode;
    private String paymateStatus;

    public PaymateDTO(long paymateCode, String paymateStatus) {
        this.paymateCode = paymateCode;
        this.paymateStatus = paymateStatus;
    }

    public long getPaymateCode() {
        return paymateCode;
    }

    public void setPaymateCode(long paymateCode) {
        this.paymateCode = paymateCode;
    }

    public String getPaymateStatus() {
        return paymateStatus;
    }

    public void setPaymateStatus(String paymateStatus) {
        this.paymateStatus = paymateStatus;
    }
}
