package com.applitools.eyes.locators;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisualLocatorsData {

    private String appName;

    private String imageUrl;

    private boolean firstOnly;

    private List<String> locatorNames;

    public VisualLocatorsData() {
    }

    public VisualLocatorsData(String appName, String imageUrl, boolean firstOnly, List<String> locatorNames) {
        this.appName = appName;
        this.imageUrl = imageUrl;
        this.firstOnly = firstOnly;
        this.locatorNames = locatorNames;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFirstOnly() {
        return firstOnly;
    }

    public void setFirstOnly(boolean firstOnly) {
        this.firstOnly = firstOnly;
    }

    public List<String> getLocatorNames() {
        return locatorNames;
    }

    public void setLocatorNames(List<String> locatorNames) {
        this.locatorNames = locatorNames;
    }

    @Override
    public String toString() {
        return "VisualLocatorsData{" +
                "appName='" + appName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", firstOnly=" + firstOnly +
                ", locatorNames=" + locatorNames +
                '}';
    }
}
