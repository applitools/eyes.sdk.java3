package com.applitools.eyes.playwright.universal.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverInfoDto {

    private DriverInfoFeaturesDto features;

    public DriverInfoDto() {
        this.features = new DriverInfoFeaturesDto();
    }

    public DriverInfoFeaturesDto getFeatures() {
        return features;
    }

    public void setFeatures(DriverInfoFeaturesDto features) {
        this.features = features;
    }

    @Override
    public String toString() {
        return "DriverInfoDto{" +
                "features=" + features +
                '}';
    }
}
