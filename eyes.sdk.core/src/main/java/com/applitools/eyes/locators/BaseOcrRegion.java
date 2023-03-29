package com.applitools.eyes.locators;

import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseOcrRegion {
    @JsonIgnore
    private String hint = null;
    private Float minMatch;
    private String language;
    private Region region = null;

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

    public BaseOcrRegion region(Region region) {
        this.region = region;
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

    public Region getRegion() {
        return region;
    }

}
