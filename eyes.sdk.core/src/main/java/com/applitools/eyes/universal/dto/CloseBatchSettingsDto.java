package com.applitools.eyes.universal.dto;

/**
 * close batch settings dto
 */
public class CloseBatchSettingsDto {

    /**
     * the batch ID
     */
    private String batchId;

    public CloseBatchSettingsDto(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
