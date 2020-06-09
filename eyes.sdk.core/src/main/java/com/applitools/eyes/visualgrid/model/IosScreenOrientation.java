package com.applitools.eyes.visualgrid.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IosScreenOrientation {
    PORTRAIT("portrait"),
    LANDSCAPE_LEFT("landscapeLeft"),
    LANDSCAPE_RIGHT("landscapeRight");

    private final String name;

    IosScreenOrientation(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "IosScreenOrientation{" +
                "name='" + name + '\'' +
                '}';
    }
}
