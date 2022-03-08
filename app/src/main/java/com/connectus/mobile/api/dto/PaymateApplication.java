package com.connectus.mobile.api.dto;

import java.util.Date;

public class PaymateApplication {
    private long applicationId;
    private double lat;
    private double lng;
    private String selfie;
    private String certifiedId;
    private String businessLicense;
    private String taxClearance;
    private String academicCertificate;
    private String recommendationLetter;
    private Date createdAt;
    private Date updatedAt;

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getSelfie() {
        return selfie;
    }

    public void setSelfie(String selfie) {
        this.selfie = selfie;
    }

    public String getCertifiedId() {
        return certifiedId;
    }

    public void setCertifiedId(String certifiedId) {
        this.certifiedId = certifiedId;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getTaxClearance() {
        return taxClearance;
    }

    public void setTaxClearance(String taxClearance) {
        this.taxClearance = taxClearance;
    }

    public String getAcademicCertificate() {
        return academicCertificate;
    }

    public void setAcademicCertificate(String academicCertificate) {
        this.academicCertificate = academicCertificate;
    }

    public String getRecommendationLetter() {
        return recommendationLetter;
    }

    public void setRecommendationLetter(String recommendationLetter) {
        this.recommendationLetter = recommendationLetter;
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
