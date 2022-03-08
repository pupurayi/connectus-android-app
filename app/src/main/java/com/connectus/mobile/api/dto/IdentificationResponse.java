package com.connectus.mobile.api.dto;

import com.connectus.mobile.common.DocumentStatus;
import com.connectus.mobile.common.IdType;

import java.util.UUID;

public class IdentificationResponse {
    private UUID documentId;
    private IdType type;
    private String number;
    private String countryCode;
    private DocumentStatus status;
    private String statusMessage;
    private String fileName;

    public IdentificationResponse(UUID documentId, IdType type, String number, String countryCode, DocumentStatus status, String statusMessage, String fileName) {
        this.documentId = documentId;
        this.type = type;
        this.number = number;
        this.countryCode = countryCode;
        this.status = status;
        this.statusMessage = statusMessage;
        this.fileName = fileName;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public IdType getType() {
        return type;
    }

    public void setType(IdType type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
