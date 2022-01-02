package com.applitools.eyes.appium;

import com.applitools.eyes.selenium.EyesDriverUtils;
import org.openqa.selenium.WebDriver;

public class AppiumElementScaleProvider {
    private final double pixelRatio;

    public AppiumElementScaleProvider(WebDriver driver, double pixelRatio) {
        if (EyesDriverUtils.isIOS(driver)) {
            this.pixelRatio = 1.0;
        } else {
            this.pixelRatio = pixelRatio;
        }
    }

    public double scale(double value) {
        return Math.ceil(value * pixelRatio);
    }

    public int scale(int value) {
        return (int) Math.ceil(value * pixelRatio);
    }
}
