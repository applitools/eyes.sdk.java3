package com.applitools.eyes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TargetPath {

    // Static factories
    public static TargetPath region(By by) {
        return new TargetPathLocator().region(by);
    }

    public static TargetPath region(WebElement element) {
        return new TargetPathLocator().region(element);
    }

    public static TargetPath shadow(By by) {
        return new TargetPathLocator().shadow(by);
    }

    public static TargetPath shadow(WebElement element) {
        return new TargetPathLocator().shadow(element);
    }

    private static class TargetPathLocator {
        TargetPathLocator parent;
        PathNode value;
    }

    private static class RegionLocator extends TargetPathLocator { }

    private static class ShadowDomLocator extends TargetPathLocator{
        public TargetPathLocator region(By by) {

        }

        public TargetPathLocator region(WebElement element) {

        }

        public TargetPathLocator shadow(By by) {

        }

        public TargetPathLocator shadow(WebElement element) {

        }
    }

}
