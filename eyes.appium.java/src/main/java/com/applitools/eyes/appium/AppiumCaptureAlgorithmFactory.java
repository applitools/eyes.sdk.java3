package com.applitools.eyes.appium;

import com.applitools.eyes.CutProvider;
import com.applitools.eyes.Logger;
import com.applitools.eyes.ScaleProviderFactory;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.selenium.EyesDriverUtils;
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

    public AppiumCaptureAlgorithmFactory(EyesAppiumDriver driver, Logger logger, String testId,
                                         AppiumScrollPositionProvider scrollProvider,
                                         ImageProvider imageProvider, DebugScreenshotsProvider debugScreenshotsProvider,
                                         ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                         EyesScreenshotFactory screenshotFactory, int waitBeforeScreenshots, WebElement cutElement,
                                         Integer stitchingAdjustment, WebElement scrollableElement) {
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
    }

    public AppiumFullPageCaptureAlgorithm getAlgorithm() {
        if (EyesDriverUtils.isAndroid(driver.getRemoteWebDriver())) {
            return new AndroidFullPageCaptureAlgorithm(logger, testId, scrollProvider, imageProvider,
                    debugScreenshotsProvider, scaleProviderFactory, cutProvider, screenshotFactory,
                    waitBeforeScreenshot, stitchingAdjustment, scrollableElement);
        } else if (EyesDriverUtils.isIOS(driver.getRemoteWebDriver())) {
            return new AppiumFullPageCaptureAlgorithm(logger, testId, scrollProvider, imageProvider,
                    debugScreenshotsProvider, scaleProviderFactory, cutProvider, screenshotFactory,
                    waitBeforeScreenshot, cutElement, stitchingAdjustment, scrollableElement);
        }
        throw new Error("Could not find driver type for getting capture algorithm");
    }
}
