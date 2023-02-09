package com.applitools.eyes.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloseSettingsDto {
    private Boolean updateBaselineIfNew;
    private Boolean updateBaselineIfDifferent;

    public Boolean getUpdateBaselineIfNew() {
        return updateBaselineIfNew;
    }

    public void setUpdateBaselineIfNew(Boolean updateBaselineIfNew) {
        this.updateBaselineIfNew = updateBaselineIfNew;
    }

    public Boolean getUpdateBaselineIfDifferent() {
        return updateBaselineIfDifferent;
    }

    public void setUpdateBaselineIfDifferent(Boolean updateBaselineIfDifferent) {
        this.updateBaselineIfDifferent = updateBaselineIfDifferent;
    }
}
