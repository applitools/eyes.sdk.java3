package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import org.openqa.selenium.TakesScreenshot;

public class MobileScreenshotImageProvider extends TakesScreenshotImageProvider {

    protected final SeleniumEyes eyes;
    protected final IEyesJsExecutor jsExecutor;
    protected final UserAgent userAgent;
    private RectangleSize cachedViewportSize = null;
    private String cachedUrl;

    public MobileScreenshotImageProvider(SeleniumEyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        super(logger, tsInstance);
        this.eyes = eyes;
        this.jsExecutor = new SeleniumJavaScriptExecutor((EyesSeleniumDriver)eyes.getDriver());
        this.userAgent = userAgent;
    }

    protected RectangleSize getViewportSize() {
        EyesSeleniumDriver driver = (EyesSeleniumDriver)eyes.getDriver();
        if (cachedViewportSize == null || !driver.getCurrentUrl().equals(cachedUrl)) {
            cachedUrl = driver.getCurrentUrl();
            cachedViewportSize = EyesDriverUtils.getViewportSize(driver);
        }
        return cachedViewportSize;
    }
}
