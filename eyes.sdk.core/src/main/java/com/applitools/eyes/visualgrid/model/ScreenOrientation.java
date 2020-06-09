package com.applitools.eyes.visualgrid.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ScreenOrientation {

    PORTRAIT("portrait"),
    LANDSCAPE("landscape");

    private String orientation;

    ScreenOrientation(String orientation) {
        this.orientation = orientation;
    }

    @JsonValue
    public String getOrientation() {
        return orientation;
    }
}
