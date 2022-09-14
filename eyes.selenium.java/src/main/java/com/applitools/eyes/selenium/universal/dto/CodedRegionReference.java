package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.Padding;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodedRegionReference {
    private TRegion region;
    private String regionId;
    // padding will be here
    private Padding padding;

    public TRegion getRegion() {
        return region;
    }

    public void setRegion(TRegion region) {
        this.region = region;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public Padding getPadding() { return padding; }

    public void setPadding(Padding padding) { this.padding = padding; }
}