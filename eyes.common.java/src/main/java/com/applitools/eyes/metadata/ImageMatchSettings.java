package com.applitools.eyes.metadata;

import com.applitools.eyes.AccessibilityLevel;
import com.applitools.eyes.AccessibilityRegionByRectangle;
import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "matchLevel",
        "ignore",
        "strict",
        "content",
        "layout",
        "floating",
        "splitTopHeight",
        "splitBottomHeight",
        "ignoreCaret",
        "scale",
        "remainder"
})
public class ImageMatchSettings {

    @JsonProperty("matchLevel")
    private MatchLevel matchLevel;
    @JsonProperty("ignore")
    private Region[] ignore = null;
    @JsonProperty("strict")
    private Region[] strict = null;
    @JsonProperty("content")
    private Region[] content = null;
    @JsonProperty("layout")
    private Region[] layout = null;
    @JsonProperty("floating")
    private FloatingMatchSettings[] floating = null;
    @JsonProperty("splitTopHeight")
    private Integer splitTopHeight;
    @JsonProperty("splitBottomHeight")
    private Integer splitBottomHeight;
    @JsonProperty("ignoreCaret")
    private Boolean ignoreCaret;
    @JsonProperty("scale")
    private Integer scale;
    @JsonProperty("remainder")
    private Integer remainder;
    @JsonProperty("ignoreDisplacements")
    private Boolean ignoreDisplacements;
    @JsonProperty("accessibility")
    private AccessibilityRegionByRectangle[] accessibility = new AccessibilityRegionByRectangle[0];
    @JsonProperty("accessibilityLevel")
    private AccessibilityLevel accessibilityLevel = AccessibilityLevel.None;

    @JsonProperty("matchLevel")
    public MatchLevel getMatchLevel() {
        return matchLevel;
    }

    @JsonProperty("matchLevel")
    public void setMatchLevel(MatchLevel matchLevel) {
        this.matchLevel = matchLevel;
    }

    @JsonProperty("ignore")
    public Region[] getIgnore() {
        return ignore;
    }

    @JsonProperty("ignore")
    public void setIgnore(Region[] ignore) {
        this.ignore = ignore;
    }

    @JsonProperty("strict")
    public Region[] getStrict() {
        return strict;
    }

    @JsonProperty("strict")
    public void setStrict(Region[] strict) {
        this.strict = strict;
    }

    @JsonProperty("content")
    public Region[] getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(Region[] content) {
        this.content = content;
    }

    @JsonProperty("layout")
    public Region[] getLayout() {
        return layout;
    }

    @JsonProperty("layout")
    public void setLayout(Region[] layout) {
        this.layout = layout;
    }

    @JsonProperty("floating")
    public FloatingMatchSettings[] getFloating() {
        return floating;
    }

    @JsonProperty("floating")
    public void setFloating(FloatingMatchSettings[] floating) {
        this.floating = floating;
    }

    @JsonProperty("splitTopHeight")
    public Integer getSplitTopHeight() {
        return splitTopHeight;
    }

    @JsonProperty("splitTopHeight")
    public void setSplitTopHeight(Integer splitTopHeight) {
        this.splitTopHeight = splitTopHeight;
    }

    @JsonProperty("splitBottomHeight")
    public Integer getSplitBottomHeight() {
        return splitBottomHeight;
    }

    @JsonProperty("splitBottomHeight")
    public void setSplitBottomHeight(Integer splitBottomHeight) {
        this.splitBottomHeight = splitBottomHeight;
    }

    @JsonProperty("ignoreCaret")
    public Boolean getIgnoreCaret() {
        return ignoreCaret;
    }

    @JsonProperty("ignoreCaret")
    public void setIgnoreCaret(Boolean ignoreCaret) {
        this.ignoreCaret = ignoreCaret;
    }

    @JsonProperty("scale")
    public Integer getScale() {
        return scale;
    }

    @JsonProperty("scale")
    public void setScale(Integer scale) {
        this.scale = scale;
    }

    @JsonProperty("remainder")
    public Integer getRemainder() {
        return remainder;
    }

    @JsonProperty("remainder")
    public void setRemainder(Integer remainder) {
        this.remainder = remainder;
    }

    @JsonProperty("ignoreDisplacements")
    public Boolean getIgnoreDisplacements() {
        return ignoreDisplacements;
    }

    @JsonProperty("ignoreDisplacements")
    public void setIgnoreDisplacements(Boolean ignoreDisplacements) {
        this.ignoreDisplacements = ignoreDisplacements;
    }

    @JsonProperty("accessibilityLevel")
    public AccessibilityLevel getAccessibilityLevel() {
        return accessibilityLevel;
    }
    @JsonProperty("accessibilityLevel")
    public void setAccessibilityLevel(AccessibilityLevel accessibilityLevel) {
        this.accessibilityLevel = accessibilityLevel;
    }
    @JsonProperty("accessibility")
    public AccessibilityRegionByRectangle[] getAccessibility() {
        return accessibility;
    }
    @JsonProperty("accessibility")
    public void setAccessibility(AccessibilityRegionByRectangle[] accessibilityValidation) {
        this.accessibility = accessibilityValidation;
    }

}
