package com.applitools.eyes.locators;

import java.util.List;

public class VisualLocator {

    public static VisualLocatorSettings name(String name) {
        return new VisualLocatorSettings(name);
    }

    public static VisualLocatorSettings names(List<String> names) {
        return new VisualLocatorSettings(names);
    }
}
