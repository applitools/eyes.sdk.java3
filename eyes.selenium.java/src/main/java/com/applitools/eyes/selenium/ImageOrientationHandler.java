package com.applitools.eyes.selenium;

import com.applitools.eyes.Logger;
import org.openqa.selenium.WebDriver;

import java.awt.image.BufferedImage;

public interface ImageOrientationHandler {
    boolean isLandscapeOrientation(WebDriver driver);
    int tryAutomaticRotation(Logger logger, WebDriver driver, BufferedImage image);
}
