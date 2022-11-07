package com.applitools.eyes.universal.dto;

import com.applitools.eyes.TestResults;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoDto {
    private TestResults result;

    public TestResults getResult() {
        return result;
    }

    public void setResult(TestResults result) {
        this.result = result;
    }
}
