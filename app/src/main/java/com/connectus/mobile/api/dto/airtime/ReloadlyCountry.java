package com.connectus.mobile.api.dto.airtime;

public class ReloadlyCountry {
    private String isoName;
    private String name;

    public ReloadlyCountry(String isoName, String name) {
        this.isoName = isoName;
        this.name = name;
    }

    public String getIsoName() {
        return isoName;
    }

    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
