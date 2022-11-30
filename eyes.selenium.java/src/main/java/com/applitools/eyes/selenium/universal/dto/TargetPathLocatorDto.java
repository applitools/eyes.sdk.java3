package com.applitools.eyes.selenium.universal.dto;

import com.applitools.eyes.universal.dto.TRegion;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetPathLocatorDto extends TRegion {
    private String selector;
    private String type;
    private String elementId;
    private TargetPathLocatorDto shadow;
    private TargetPathLocatorDto fallback;
    private TargetPathLocatorDto child;

    public TargetPathLocatorDto getShadow() {
        return shadow;
    }

    public void setShadow(TargetPathLocatorDto shadow) {
        this.shadow = shadow;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public TargetPathLocatorDto getFallback() {
        return fallback;
    }

    public void setFallback(TargetPathLocatorDto fallback) {
        this.fallback = fallback;
    }

    public TargetPathLocatorDto getChild() {
        return child;
    }

    public void setChild(TargetPathLocatorDto child) {
        this.child = child;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}