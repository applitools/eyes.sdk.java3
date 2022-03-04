package com.applitools.eyes.selenium.universal.dto;

public class TargetPathLocatorDto extends SelectorRegionDto {
    private TargetPathLocatorDto shadow;


    public TargetPathLocatorDto getShadow() {
        return shadow;
    }

    public void setShadow(TargetPathLocatorDto shadow) {
        this.shadow = shadow;
    }

    public String toJson() {
        String json = "{ type: " + getType() + ", ";
        json += "selector: " + getSelector() + ",";
        if (shadow != null) {
            json += "shadow: " + shadow.toJson();
        }
        json += "}"
    }
}
