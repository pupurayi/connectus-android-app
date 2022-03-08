package com.connectus.mobile.api.dto;

public class CheckPaymateDTO {
    private long paymateCode;
    private String paymateName;
    private boolean avatarAvailable;
    private String userId;
    private String paymateStatus;

    public CheckPaymateDTO(long paymateCode, String paymateName, boolean avatarAvailable, String userId, String paymateStatus) {
        this.paymateCode = paymateCode;
        this.paymateName = paymateName;
        this.avatarAvailable = avatarAvailable;
        this.userId = userId;
        this.paymateStatus = paymateStatus;
    }

    public long getPaymateCode() {
        return paymateCode;
    }

    public void setPaymateCode(long paymateCode) {
        this.paymateCode = paymateCode;
    }

    public String getPaymateName() {
        return paymateName;
    }

    public void setPaymateName(String paymateName) {
        this.paymateName = paymateName;
    }

    public boolean isAvatarAvailable() {
        return avatarAvailable;
    }

    public void setAvatarAvailable(boolean avatarAvailable) {
        this.avatarAvailable = avatarAvailable;
    }

    public String getId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymateStatus() {
        return paymateStatus;
    }

    public void setPaymateStatus(String paymateStatus) {
        this.paymateStatus = paymateStatus;
    }
}
