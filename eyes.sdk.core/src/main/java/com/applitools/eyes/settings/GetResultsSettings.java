package com.applitools.eyes.settings;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetResultsSettings {

    private Boolean throwErr;

    public GetResultsSettings(Boolean throwErr) {
        this.throwErr = throwErr;
    }

    public Boolean getThrowErr() {
        return throwErr;
    }

    public void setThrowErr(Boolean throwErr) {
        this.throwErr = throwErr;
    }
}
