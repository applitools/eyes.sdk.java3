package com.applitools.eyes.locators;

import com.applitools.eyes.AppOutput;
import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextRegionSettings {
    private AppOutput appOutput;
    @JsonIgnore
    private BufferedImage image;
    private final List<String> patterns;
    private Boolean ignoreCase;
    private Boolean firstOnly;
    private String language;

    public TextRegionSettings(String pattern, String... patterns) {
        ArgumentGuard.notNull(pattern, "pattern");
        if (patterns != null && patterns.length != 0) {
            ArgumentGuard.notContainsNull(patterns, "patterns");
            List<String> list = new ArrayList<>();
            list.add(pattern);
            Collections.addAll(list, patterns);
            this.patterns = list;
        } else {
            this.patterns = Collections.singletonList(pattern);
        }
    }

    public TextRegionSettings ignoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        return this;
    }

    public TextRegionSettings firstOnly(boolean firstOnly) {
        this.firstOnly = firstOnly;
        return this;
    }

    public TextRegionSettings language(String language) {
        this.language = language;
        return this;
    }

    public TextRegionSettings image(BufferedImage image) {
        this.image = image;
        return this;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public Boolean getIgnoreCase() {
        return ignoreCase;
    }

    public Boolean getFirstOnly() {
        return firstOnly;
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

    public BufferedImage getImage() {
        return image;
    }
}
