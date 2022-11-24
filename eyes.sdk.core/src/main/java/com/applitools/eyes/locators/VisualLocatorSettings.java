package com.applitools.eyes.locators;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class VisualLocatorSettings {

    @JsonIgnore
    BufferedImage image;

    private List<String> names = new ArrayList<>();
    private Boolean firstOnly;

    public VisualLocatorSettings() {
    }

    protected VisualLocatorSettings(String name) {
        this.names.add(name);
    }

    protected VisualLocatorSettings(List<String> names) {
        this.names.addAll(names);
    }

    public VisualLocatorSettings first() {
        VisualLocatorSettings clone = clone();
        clone.firstOnly = true;
        return clone;
    }

    public VisualLocatorSettings all() {
        VisualLocatorSettings clone = clone();
        clone.firstOnly = false;
        return clone;
    }

    public VisualLocatorSettings name(String name) {
        VisualLocatorSettings clone = clone();
        clone.names.add(name);
        return clone;
    }

    public VisualLocatorSettings names(List<String> names) {
        VisualLocatorSettings clone = clone();
        clone.names.addAll(names);
        return clone;
    }

    public VisualLocatorSettings image(BufferedImage image) {
        VisualLocatorSettings clone = clone();
        clone.image = image;
        return clone;
    }

    protected VisualLocatorSettings clone() {
        VisualLocatorSettings settings = new VisualLocatorSettings();
        populateClone(settings);
        return settings;
    }

    private void populateClone(VisualLocatorSettings clone) {
        clone.names = this.names;
        clone.firstOnly = this.firstOnly;
        clone.image = this.image;
    }

    public List<String> getNames() {
        return names;
    }

    public Boolean isFirstOnly() {
        return firstOnly;
    }

    public BufferedImage getImage() {
        return image;
    }

}
