package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Batch config returned from the server
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchConfig {
    @JsonProperty("id")
    private String id;
    @JsonProperty("storedBatchInfoId")
    private String storedBatchInfoId;
    @JsonProperty("scmSourceBranch")
    private String scmSourceBranch;
    @JsonProperty("scmTargetBranch")
    private String scmTargetBranch;
    @JsonProperty("scmMergeBaseTime")
    private String scmMergeBaseTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoredBatchInfoId() {
        return storedBatchInfoId;
    }

    public void setStoredBatchInfoId(String storedBatchInfoId) {
        this.storedBatchInfoId = storedBatchInfoId;
    }

    public String getScmSourceBranch() {
        return scmSourceBranch;
    }

    public void setScmSourceBranch(String scmSourceBranch) {
        this.scmSourceBranch = scmSourceBranch;
    }

    public String getScmTargetBranch() {
        return scmTargetBranch;
    }

    public void setScmTargetBranch(String scmTargetBranch) {
        this.scmTargetBranch = scmTargetBranch;
    }

    public String getScmMergeBaseTime() {
        return scmMergeBaseTime;
    }

    public void setScmMergeBaseTime(String scmMergeBaseTime) {
        this.scmMergeBaseTime = scmMergeBaseTime;
    }
}
