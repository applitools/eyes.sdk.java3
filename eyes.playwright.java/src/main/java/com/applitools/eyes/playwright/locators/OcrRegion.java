package com.applitools.eyes.playwright.locators;

import com.applitools.eyes.Region;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;

public class OcrRegion extends BaseOcrRegion {
    @JsonIgnore
    private ElementHandle element;
    @JsonIgnore
    private Locator locator;
    @JsonIgnore
    private Region region;

    public OcrRegion(ElementHandle element) {
        this.element = element;
    }

    public OcrRegion(Locator locator) {
        this.locator = locator;
    }

    public OcrRegion(Region region) {
        this.region = region;
    }

    public ElementHandle getElement() {
        return element;
    }

    public Locator getLocator() {
        return locator;
    }

    public Region getRegion() {
        return region;
    }
}
