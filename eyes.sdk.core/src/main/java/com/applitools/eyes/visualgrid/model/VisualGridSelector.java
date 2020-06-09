package com.applitools.eyes.visualgrid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VisualGridSelector {

    private String selector;

    @JsonIgnore
    private Object category;

    public VisualGridSelector(String selector, Object category) {
        this.selector = selector;
        this.category = category;
    }

    public String getSelector() {
        return selector;
    }

    @JsonProperty("type")
    public String getType(){
        return "xpath";
    }

    @JsonIgnore
    public Object getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "VisualGridSelector{" +
                "selector='" + selector + '\'' +
                ", category=" + category +
                '}';
    }
}
