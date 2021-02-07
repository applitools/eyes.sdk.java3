package com.applitools.eyes.locators;

import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class OcrRegion extends BaseOcrRegion {
    @JsonIgnore
    private WebElement element;
    @JsonIgnore
    private By selector;
    @JsonIgnore
    private Region region;

    public OcrRegion(WebElement element) {
        this.element = element;
    }

    public OcrRegion(By selector) {
        this.selector = selector;
    }

    public OcrRegion(Region region) {
        this.region = region;
    }

    public WebElement getElement() {
        return element;
    }

    public By getSelector() {
        return selector;
    }

    public Region getRegion() {
        return region;
    }
}
