package com.applitools.eyes.selenium.universal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodedRegionReference {
    private TRegion region;
    private String regionId;
    // padding will be here

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
}
