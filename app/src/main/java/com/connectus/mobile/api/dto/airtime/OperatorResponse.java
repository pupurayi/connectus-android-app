package com.connectus.mobile.api.dto.airtime;

import java.math.BigDecimal;
import java.util.ArrayList;

public class OperatorResponse {
    private long operatorId;
    private String name;
    private boolean bundle;
    private boolean data;
    private boolean pin;
    private boolean supportsLocalAmounts;
    private DenominationType denominationType;
    private String senderCurrencyCode;
    private String senderCurrencySymbol;
    private String destinationCurrencyCode;
    private String destinationCurrencySymbol;
    private BigDecimal commission;
    private BigDecimal internationalDiscount;
    private BigDecimal localDiscount;
    private BigDecimal mostPopularAmount;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal localMinAmount;
    private BigDecimal localMaxAmount;
    private ReloadlyCountry country;
    private ReloadlyFX fx;
    private ArrayList<String> logoUrls;
    private ArrayList<String> fixedAmounts;
    private Object fixedAmountsDescriptions;
    private ArrayList<String> localFixedAmounts;
    private Object localFixedAmountsDescriptions;
    private ArrayList<String> suggestedAmounts;
    private Object suggestedAmountsMap;
    private Object promotions;

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBundle() {
        return bundle;
    }

    public void setBundle(boolean bundle) {
        this.bundle = bundle;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public boolean isSupportsLocalAmounts() {
        return supportsLocalAmounts;
    }

    public void setSupportsLocalAmounts(boolean supportsLocalAmounts) {
        this.supportsLocalAmounts = supportsLocalAmounts;
    }

    public DenominationType getDenominationType() {
        return denominationType;
    }

    public void setDenominationType(DenominationType denominationType) {
        this.denominationType = denominationType;
    }

    public String getSenderCurrencyCode() {
        return senderCurrencyCode;
    }

    public void setSenderCurrencyCode(String senderCurrencyCode) {
        this.senderCurrencyCode = senderCurrencyCode;
    }

    public String getSenderCurrencySymbol() {
        return senderCurrencySymbol;
    }

    public void setSenderCurrencySymbol(String senderCurrencySymbol) {
        this.senderCurrencySymbol = senderCurrencySymbol;
    }

    public String getDestinationCurrencyCode() {
        return destinationCurrencyCode;
    }

    public void setDestinationCurrencyCode(String destinationCurrencyCode) {
        this.destinationCurrencyCode = destinationCurrencyCode;
    }

    public String getDestinationCurrencySymbol() {
        return destinationCurrencySymbol;
    }

    public void setDestinationCurrencySymbol(String destinationCurrencySymbol) {
        this.destinationCurrencySymbol = destinationCurrencySymbol;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getInternationalDiscount() {
        return internationalDiscount;
    }

    public void setInternationalDiscount(BigDecimal internationalDiscount) {
        this.internationalDiscount = internationalDiscount;
    }

    public BigDecimal getLocalDiscount() {
        return localDiscount;
    }

    public void setLocalDiscount(BigDecimal localDiscount) {
        this.localDiscount = localDiscount;
    }

    public BigDecimal getMostPopularAmount() {
        return mostPopularAmount;
    }

    public void setMostPopularAmount(BigDecimal mostPopularAmount) {
        this.mostPopularAmount = mostPopularAmount;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getLocalMinAmount() {
        return localMinAmount;
    }

    public void setLocalMinAmount(BigDecimal localMinAmount) {
        this.localMinAmount = localMinAmount;
    }

    public BigDecimal getLocalMaxAmount() {
        return localMaxAmount;
    }

    public void setLocalMaxAmount(BigDecimal localMaxAmount) {
        this.localMaxAmount = localMaxAmount;
    }

    public ReloadlyCountry getCountry() {
        return country;
    }

    public void setCountry(ReloadlyCountry country) {
        this.country = country;
    }

    public ReloadlyFX getFx() {
        return fx;
    }

    public void setFx(ReloadlyFX fx) {
        this.fx = fx;
    }

    public ArrayList<String> getLogoUrls() {
        return logoUrls;
    }

    public void setLogoUrls(ArrayList<String> logoUrls) {
        this.logoUrls = logoUrls;
    }

    public ArrayList<String> getFixedAmounts() {
        return fixedAmounts;
    }

    public void setFixedAmounts(ArrayList<String> fixedAmounts) {
        this.fixedAmounts = fixedAmounts;
    }

    public Object getFixedAmountsDescriptions() {
        return fixedAmountsDescriptions;
    }

    public void setFixedAmountsDescriptions(Object fixedAmountsDescriptions) {
        this.fixedAmountsDescriptions = fixedAmountsDescriptions;
    }

    public ArrayList<String> getLocalFixedAmounts() {
        return localFixedAmounts;
    }

    public void setLocalFixedAmounts(ArrayList<String> localFixedAmounts) {
        this.localFixedAmounts = localFixedAmounts;
    }

    public Object getLocalFixedAmountsDescriptions() {
        return localFixedAmountsDescriptions;
    }

    public void setLocalFixedAmountsDescriptions(Object localFixedAmountsDescriptions) {
        this.localFixedAmountsDescriptions = localFixedAmountsDescriptions;
    }

    public ArrayList<String> getSuggestedAmounts() {
        return suggestedAmounts;
    }

    public void setSuggestedAmounts(ArrayList<String> suggestedAmounts) {
        this.suggestedAmounts = suggestedAmounts;
    }

    public Object getSuggestedAmountsMap() {
        return suggestedAmountsMap;
    }

    public void setSuggestedAmountsMap(Object suggestedAmountsMap) {
        this.suggestedAmountsMap = suggestedAmountsMap;
    }

    public Object getPromotions() {
        return promotions;
    }

    public void setPromotions(Object promotions) {
        this.promotions = promotions;
    }
}
