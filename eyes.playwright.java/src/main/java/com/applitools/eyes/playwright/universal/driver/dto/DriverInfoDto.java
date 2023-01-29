package com.applitools.eyes.playwright.universal.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriverInfoDto that = (DriverInfoDto) o;
        return Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(features);
    }

    @Override
    public String toString() {
        return "DriverInfoDto{" +
                "features=" + features +
                '}';
    }
}
