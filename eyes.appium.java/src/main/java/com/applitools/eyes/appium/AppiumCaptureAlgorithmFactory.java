package com.applitools.eyes.appium;

import com.applitools.eyes.CutProvider;
import com.applitools.eyes.Logger;
import com.applitools.eyes.ScaleProviderFactory;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.config.ContentInset;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.selenium.EyesDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AppiumCaptureAlgorithmFactory {

    private final EyesAppiumDriver driver;
    private final Logger logger;
    private final String testId;
    private final AppiumScrollPositionProvider scrollProvider;
    private final ImageProvider imageProvider;
    private final DebugScreenshotsProvider debugScreenshotsProvider;
    private final ScaleProviderFactory scaleProviderFactory;
    private final CutProvider cutProvider;
    private final EyesScreenshotFactory screenshotFactory;
    private final int waitBeforeScreenshot;
    private final WebElement cutElement;
    private final Integer stitchingAdjustment;
    private final WebElement scrollableElement;
    private final By scrollRootElementSelector;
    private final ContentInset contentInset;
    private final boolean cacheScrollableSize;

    public AppiumCaptureAlgorithmFactory(EyesAppiumDriver driver, Logger logger, String testId,
                                         AppiumScrollPositionProvider scrollProvider,
                                         ImageProvider imageProvider, DebugScreenshotsProvider debugScreenshotsProvider,
                                         ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                         EyesScreenshotFactory screenshotFactory, int waitBeforeScreenshots, WebElement cutElement,
                                         Integer stitchingAdjustment, WebElement scrollableElement, By scrollRootElementSelector,
                                         ContentInset contentInset, boolean cacheScrollableSize) {
        this.driver = driver;
        this.logger = logger;
        this.testId = testId;
        this.scrollProvider = scrollProvider;
        this.imageProvider = imageProvider;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
        this.scaleProviderFactory = scaleProviderFactory;
        this.cutProvider = cutProvider;
        this.screenshotFactory = screenshotFactory;
        this.waitBeforeScreenshot = waitBeforeScreenshots;
        this.cutElement = cutElement;
        this.stitchingAdjustment = stitchingAdjustment;
        this.scrollableElement = scrollableElement;
        this.scrollRootElementSelector = scrollRootElementSelector;
        this.contentInset = contentInset;
        this.cacheScrollableSize = cacheScrollableSize;
    }

    public AppiumFullPageCaptureAlgorithm getAlgorithm() {
        if (EyesDriverUtils.isAndroid(driver.getRemoteWebDriver())) {
            return new AndroidFullPageCaptureAlgorithm(logger, testId, scrollProvider, imageProvider,
                    debugScreenshotsProvider, scaleProviderFactory, cutProvider, screenshotFactory,
                    waitBeforeScreenshot, stitchingAdjustment, scrollableElement, contentInset, cacheScrollableSize);
        } else if (EyesDriverUtils.isIOS(driver.getRemoteWebDriver())) {
            return new AppiumFullPageCaptureAlgorithm(logger, testId, scrollProvider, imageProvider,
                    debugScreenshotsProvider, scaleProviderFactory, cutProvider, screenshotFactory,
                    waitBeforeScreenshot, cutElement, stitchingAdjustment, scrollableElement, scrollRootElementSelector,
                    contentInset, cacheScrollableSize);
        }
        throw new Error("Could not find driver type for getting capture algorithm");
    }
}
