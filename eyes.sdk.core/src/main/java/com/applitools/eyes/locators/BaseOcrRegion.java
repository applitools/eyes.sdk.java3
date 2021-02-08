package com.applitools.eyes.locators;

import com.applitools.eyes.AppOutput;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseOcrRegion {
    private AppOutput appOutput;
    @JsonIgnore
    private String hint = null;
    private Float minMatch;
    private String language;

    public BaseOcrRegion hint(String hint) {
        if (hint == null || !hint.isEmpty()) {
            this.hint = hint;
        }
        return this;
    }

    public BaseOcrRegion minMatch(float minMatch) {
        this.minMatch = minMatch;
        return this;
    }

    public BaseOcrRegion language(String language) {
        this.language = language;
        return this;
    }

    public String getHint() {
        return hint;
    }

    public Float getMinMatch() {
        return minMatch;
    }

    public String getLanguage() {
        return language;
    }

    public AppOutput getAppOutput() {
        return appOutput;
    }

    public void setAppOutput(AppOutput appOutput) {
        this.appOutput = appOutput;
    }

    @JsonProperty("regions")
    public List<Map<String, Object>> getRegions() {
        if (appOutput == null) {
            return null;
        }

        List<Map<String, Object>> regions = new ArrayList<>();
        Map<String, Object> props = new HashMap<>();
        props.put("left", 0);
        props.put("top", 0);
        props.put("width", appOutput.getScreenshot().getImage().getWidth());
        props.put("height", appOutput.getScreenshot().getImage().getHeight());
        if (hint != null) {
            props.put("expected", hint);
        }
        regions.add(props);
        return regions;
    }
}
