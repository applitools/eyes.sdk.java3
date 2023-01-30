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
    private String selector;

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

    public OcrRegion(String selector) {
        this.selector = selector;
    }

    public ElementHandle getElement() {
        return element;
    }

    public Locator getLocator() {
        return locator;
    }

    public String getSelector() {
        return selector;
    }

    public Region getRegion() {
        return region;
    }
}
