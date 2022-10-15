package com.applitools.eyes.selenium.universal.dto;

public class CloseSettingsDto {

    private Boolean throwErr;
    private Boolean updateBaselineIfNew;
    private Boolean updateBaselineIfDifferent;

    public Boolean getThrowErr() {
        return throwErr;
    }

    public void setThrowErr(Boolean throwErr) {
        this.throwErr = throwErr;
    }

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
