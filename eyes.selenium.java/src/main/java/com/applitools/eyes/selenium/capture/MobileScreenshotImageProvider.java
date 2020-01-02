package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IEyesJsExecutor;
import com.applitools.eyes.Logger;
import com.applitools.eyes.UserAgent;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import org.openqa.selenium.TakesScreenshot;

public class MobileScreenshotImageProvider extends TakesScreenshotImageProvider {

    protected final SeleniumEyes eyes;
    protected final IEyesJsExecutor jsExecutor;
    protected final UserAgent userAgent;

    public MobileScreenshotImageProvider(SeleniumEyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        super(logger, tsInstance);
        this.eyes = eyes;
        this.jsExecutor = new SeleniumJavaScriptExecutor((EyesWebDriver)eyes.getDriver());
        this.userAgent = userAgent;
    }
}
