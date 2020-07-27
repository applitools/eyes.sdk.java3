package com.applitools.eyes.appium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.appium.capture.ImageProviderFactory;
import com.applitools.eyes.appium.locators.AndroidVisualLocatorProvider;
import com.applitools.eyes.appium.locators.IOSVisualLocatorProvider;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.events.ValidationInfo;
import com.applitools.eyes.events.ValidationResult;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.locators.VisualLocatorsProvider;
import com.applitools.eyes.positioning.RegionProvider;
import com.applitools.eyes.scaling.FixedScaleProviderFactory;
import com.applitools.eyes.scaling.NullScaleProvider;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.selenium.positioning.NullRegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.RegionPositionCompensation;
import com.applitools.eyes.selenium.regionVisibility.MoveToRegionVisibilityStrategy;
import com.applitools.eyes.selenium.regionVisibility.NopRegionVisibilityStrategy;
import com.applitools.eyes.selenium.regionVisibility.RegionVisibilityStrategy;
import com.applitools.utils.*;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class Eyes extends EyesBase {
    private static final int USE_DEFAULT_MATCH_TIMEOUT = -1;
    private static final int DEFAULT_STITCH_OVERLAP = 50;

    public static final double UNKNOWN_DEVICE_PIXEL_RATIO = 0;
    public static final double DEFAULT_DEVICE_PIXEL_RATIO = 1;

    private Configuration configuration = new Configuration();
    private EyesAppiumDriver driver;
    private VisualLocatorsProvider visualLocatorsProvider;

    private WebElement cutElement;
    private ImageProvider imageProvider;
    private double devicePixelRatio = UNKNOWN_DEVICE_PIXEL_RATIO;
    private boolean stitchContent;
    private RegionPositionCompensation regionPositionCompensation;
    private UserAgent userAgent;
    private ImageRotation rotation;
    private Region regionToCheck = null;
    protected WebElement targetElement = null;
    private PropertyHandler<RegionVisibilityStrategy> regionVisibilityStrategyHandler;

    public Eyes() {
        super();
        regionVisibilityStrategyHandler = new SimplePropertyHandler<>();
        regionVisibilityStrategyHandler.set(new MoveToRegionVisibilityStrategy(logger));
        configuration.setStitchOverlap(DEFAULT_STITCH_OVERLAP);
    }

    public WebDriver open(WebDriver driver, Configuration configuration) {
        this.configuration = configuration;
        return open(driver);
    }

    /**
     * See {@link #open(WebDriver, String, String, RectangleSize, SessionType)}.
     * {@code sessionType} defaults to {@code null}.
     */
    public WebDriver open(WebDriver driver, String appName, String testName,
                          RectangleSize viewportSize) {
        configuration.setAppName(appName);
        configuration.setTestName(testName);
        configuration.setViewportSize(viewportSize);
        return open(driver);
    }

    public WebDriver open(WebDriver driver, String appName, String testName) {
        configuration.setAppName(appName);
        configuration.setTestName(testName);
        return open(driver);
    }


    /**
     * Starts a test.
     * @param driver       The web driver that controls the browser hosting
     *                     the application under test.
     * @param appName      The name of the application under test.
     * @param testName     The test name.
     * @param viewportSize The required browser's viewport size
     *                     (i.e., the visible part of the document's body) or
     *                     {@code null} to use the current window's viewport.
     * @param sessionType  The type of test (e.g.,  standard test / visual
     *                     performance test).
     * @return A wrapped WebDriver which enables Eyes trigger recording and
     * frame handling.
     */
    protected WebDriver open(WebDriver driver, String appName, String testName,
                             RectangleSize viewportSize, SessionType sessionType) {
        configuration.setAppName(appName);
        configuration.setTestName(testName);
        configuration.setViewportSize(viewportSize);
        configuration.setSessionType(sessionType);
        return open(driver);
    }

    private WebDriver open(WebDriver webDriver) {
        if (getIsDisabled()) {
            logger.verbose("Ignored");
            return webDriver;
        }

        ArgumentGuard.notNull(webDriver, "driver");

        initDriver(webDriver);

        tryUpdateDevicePixelRatio();

        ensureViewportSize();
        openBase();

        setUserAgent(UserAgent.parseUserAgentString(sessionStartInfo.getEnvironment().getInferred(), true));

        initImageProvider();
        regionPositionCompensation = new NullRegionPositionCompensation();

        initDriverBasedPositionProviders();

        initVisualLocatorProvider();

        this.driver.setRotation(getRotation());
        return this.driver;
    }

    public void checkWindow() {
        checkWindow(null);
    }

    public void checkWindow(String tag) {
        check(tag, Target.window());
    }

    public void checkWindow(int matchTimeout, String tag) {
        check(tag, Target.window().timeout(matchTimeout));
    }

    public void checkWindow(String tag, boolean fully) {
        check(tag, Target.window().fully(fully));

    }

    public void check(String name, ICheckSettings checkSettings) {
        if (getIsDisabled()) {
            logger.log(String.format("check('%s', %s): Ignored", name, checkSettings));
            return;
        }

        ArgumentGuard.notNull(checkSettings, "checkSettings");
        checkSettings = checkSettings.withName(name);
        this.check(checkSettings);
    }

    private void ensureViewportSize() {
        if (this.configuration.getViewportSize() == null) {
            this.configuration.setViewportSize(driver.getDefaultContentViewportSize());
        }
    }

    /**
     * @return The image rotation data.
     */
    public ImageRotation getRotation() {
        return rotation;
    }

    /**
     * @param rotation The image rotation data.
     */
    public void setRotation(ImageRotation rotation) {
        this.rotation = rotation;
        if (driver != null) {
            driver.setRotation(rotation);
        }
    }

    public void setForceFullPageScreenshot(boolean shouldForce) {
        configuration.setForceFullPageScreenshot(shouldForce);
    }

    public boolean getForceFullPageScreenshot() {
        Boolean forceFullPageScreenshot = configuration.getForceFullPageScreenshot();
        if (forceFullPageScreenshot == null) {
            return false;
        }
        return forceFullPageScreenshot;
    }

    public void setScrollToRegion(boolean shouldScroll) {
        if (shouldScroll) {
            regionVisibilityStrategyHandler = new ReadOnlyPropertyHandler<RegionVisibilityStrategy>(logger, new MoveToRegionVisibilityStrategy(logger));
        } else {
            regionVisibilityStrategyHandler = new ReadOnlyPropertyHandler<RegionVisibilityStrategy>(logger, new NopRegionVisibilityStrategy(logger));
        }
    }

    /**
     * Gets scroll to region.
     * @return Whether to automatically scroll to a region being validated.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean getScrollToRegion() {
        return !(regionVisibilityStrategyHandler.get() instanceof NopRegionVisibilityStrategy);
    }

    protected UserAgent getUserAgent() {
        return userAgent;
    }

    protected void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public Region getRegionToCheck() {
        return regionToCheck;
    }

    public void setRegionToCheck(Region regionToCheck) {
        this.regionToCheck = regionToCheck;
    }

    public boolean shouldStitchContent() {
        return stitchContent;
    }

    @Override
    public String getBaseAgentId() {
        return "eyes.appium.java/"  + ClassVersionGetter.CURRENT_VERSION;
    }

    @Override
    protected String tryCaptureDom() {
        return null;
    }

    @Override
    protected RectangleSize getViewportSize() {
        ArgumentGuard.notNull(driver, "driver");
        return EyesDriverUtils.getViewportSizeOrDisplaySize(new Logger(), driver);
    }

    @Override
    protected Configuration setViewportSize(RectangleSize size) {
        viewportSize = new RectangleSize(size.getWidth(), size.getHeight());
        return configuration;
    }

    @Override
    protected String getInferredEnvironment() {
        if (userAgent != null) {
            return "useragent:" + userAgent.toString();
        }

        return null;
    }

    private void initDriverBasedPositionProviders() {
        logger.verbose("Initializing Appium position provider");
        setPositionProvider(new AppiumScrollPositionProviderFactory(logger, driver).getScrollPositionProvider());
    }

    private void initImageProvider() {
        imageProvider = ImageProviderFactory.getImageProvider(logger, driver, true);
    }

    private void initDriver(WebDriver driver) {
        if (driver instanceof AppiumDriver) {
            logger.verbose("Found an instance of AppiumDriver, so using EyesAppiumDriver instead");
            this.driver = new EyesAppiumDriver(logger, (AppiumDriver) driver);
            regionVisibilityStrategyHandler.set(new NopRegionVisibilityStrategy(logger));
        } else {
            logger.verbose("Did not find an instance of AppiumDriver, using regular logic");
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected ScaleProviderFactory getScaleProviderFactory() {
        // in the context of appium, we know the pixel ratio by getting it directly from the appium
        // server, so there's no need to figure anything out on the fly. just return a Fixed one
        return new FixedScaleProviderFactory(logger, 1 / driver.getDevicePixelRatio(), scaleProviderHandler);
    }

    /**
     * @param driver The driver for which to check if it represents a mobile
     *               device.
     * @return {@code true} if the platform running the test is a mobile
     * platform. {@code false} otherwise.
     */
    protected boolean isMobileDevice(WebDriver driver) {
        return EyesDriverUtils.isMobileDevice(driver);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This override also checks for mobile operating system.
     */
    @Override
    protected AppEnvironment getAppEnvironment() {

        AppEnvironment appEnv = super.getAppEnvironment();
        RemoteWebDriver underlyingDriver = driver.getRemoteWebDriver();
        // If hostOs isn't set, we'll try and extract and OS ourselves.
        if (appEnv.getOs() == null) {
            logger.log("No OS set, checking for mobile OS...");
            if (EyesDriverUtils.isMobileDevice(underlyingDriver)) {
                String platformName = null;
                logger.log("Mobile device detected! Checking device type..");
                if (EyesDriverUtils.isAndroid(underlyingDriver)) {
                    logger.log("Android detected.");
                    platformName = "Android";
                } else if (EyesDriverUtils.isIOS(underlyingDriver)) {
                    logger.log("iOS detected.");
                    platformName = "iOS";
                } else {
                    logger.log("Unknown device type.");
                }
                // We only set the OS if we identified the device type.
                if (platformName != null) {
                    String os = platformName;
                    String platformVersion = EyesAppiumUtils.getPlatformVersion(underlyingDriver);
                    if (platformVersion != null) {
                        String majorVersion =
                            platformVersion.split("\\.", 2)[0];

                        if (!majorVersion.isEmpty()) {
                            os += " " + majorVersion;
                        }
                    }

                    logger.verbose("Setting OS: " + os);
                    appEnv.setOs(os);
                }
                Object deviceNameCapability = underlyingDriver.getCapabilities().getCapability("deviceName");
                String deviceName = deviceNameCapability != null ? deviceNameCapability.toString() : "Unknown";
                appEnv.setDeviceInfo(deviceName);
            } else {
                logger.log("No mobile OS detected.");
            }
        }
        logger.log("Done!");
        return appEnv;
    }

    public double getDevicePixelRatio() {
        return devicePixelRatio;
    }

    protected void tryUpdateDevicePixelRatio() {
        logger.verbose("Trying to update device pixel ratio...");
        try {
            devicePixelRatio = driver.getDevicePixelRatio();
        } catch (Exception e) {
            logger.verbose(
                    "Failed to extract device pixel ratio! Using default.");
            devicePixelRatio = DEFAULT_DEVICE_PIXEL_RATIO;
        }
        logger.verbose(String.format("Device pixel ratio: %f", devicePixelRatio));
    }

    private void updateCutElement(AppiumCheckSettings checkSettings) {
        try {
            if (checkSettings.getCutElementSelector() == null) {
                return;
            }
            cutElement = getDriver().findElement(checkSettings.getCutElementSelector());
        } catch (NoSuchElementException ignored) {
            logger.verbose("Element to cut is not found with selector: " + checkSettings.getCutElementSelector());
        }
    }

    public void check(ICheckSettings checkSettings) {
        if (checkSettings instanceof AppiumCheckSettings) {
            updateCutElement((AppiumCheckSettings) checkSettings);
        }

        if (getIsDisabled()) {
            logger.log(String.format("check(%s): Ignored", checkSettings));
            return;
        }

        ArgumentGuard.notNull(checkSettings, "checkSettings");

        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
        AppiumCheckSettings appiumCheckTarget = (checkSettings instanceof AppiumCheckSettings) ? (AppiumCheckSettings) checkSettings : null;
        if (appiumCheckTarget != null) {
            appiumCheckTarget.init(logger, driver);
        }
        String name = checkSettingsInternal.getName();

        logger.verbose(String.format("check(\"%s\", checkSettings) - begin", name));

        ValidationInfo validationInfo = this.fireValidationWillStartEvent(name);

        this.stitchContent = checkSettingsInternal.getStitchContent() == null ? false : checkSettingsInternal.getStitchContent();

        final Region targetRegion = checkSettingsInternal.getTargetRegion();

        this.regionToCheck = null;

        MatchResult result = null;

        if (targetRegion != null) {
            logger.verbose("have target region");
            result = this.checkWindowBase(new RegionProvider() {
                @Override
                public Region getRegion() {
                    return new Region(targetRegion.getLocation(), targetRegion.getSize(), CoordinatesType.CONTEXT_RELATIVE);
                }
            }, name, false, checkSettings);
        } else if (appiumCheckTarget != null) {
            WebElement targetElement = getTargetElement(appiumCheckTarget);
            if (targetElement != null) {
                logger.verbose("have target element");
                this.targetElement = targetElement;
                if (this.stitchContent) {
                    result = this.checkElement(targetElement, name, checkSettings);
                } else {
                    result = this.checkRegion(name, checkSettings);
                }
                this.targetElement = null;
            } else {
                logger.verbose("default case");
                result = this.checkWindowBase(RegionProvider.NULL_INSTANCE, name, false, checkSettings);
            }
        }

        if (result == null) {
            result = new MatchResult();
        }

        this.stitchContent = false;

        ValidationResult validationResult = new ValidationResult();
        validationResult.setAsExpected(result.getAsExpected());
        getSessionEventHandlers().validationEnded(getAUTSessionId(), validationInfo.getValidationId(), validationResult);

        logger.verbose("check - done!");
    }

    @Override
    public TestResults close(boolean throwEx) {
        TestResults results = null;
        try {
            results = super.close(throwEx);
        } catch (Throwable e) {
            logger.log(e.getMessage());
            if (throwEx) {
                throw e;
            }
        }
        this.serverConnector.closeConnector();
        return results;
    }

    /**
     * Sets the maximum time (in ms) a match operation tries to perform a match.
     * @param ms Total number of ms to wait for a match.
     */
    public void setMatchTimeout(int ms) {
        final int MIN_MATCH_TIMEOUT = 500;
        if (getIsDisabled()) {
            logger.verbose("Ignored");
            return;
        }

        logger.verbose("Setting match timeout to: " + ms);
        if ((ms != 0) && (MIN_MATCH_TIMEOUT > ms)) {
            throw new IllegalArgumentException("Match timeout must be set in milliseconds, and must be > " +
                    MIN_MATCH_TIMEOUT);
        }

        configuration.setMatchTimeout(ms);
    }

    /**
     * @return The maximum time in ms {@link #checkWindowBase
     * (RegionProvider, String, boolean, int)} waits for a match.
     */
    public int getMatchTimeout() {
        return configuration.getMatchTimeout();
    }

    @Override
    protected EyesScreenshot getScreenshot(ICheckSettingsInternal checkSettingsInternal) {

        logger.verbose("getScreenshot()");

        EyesScreenshot result;

        if (getForceFullPageScreenshot() || stitchContent) {
            result = getFullPageScreenshot();
        } else {
            result = getSimpleScreenshot();
        }

        logger.verbose("Done!");
        return result;
    }

    @Override
    protected String getTitle() {
        return "";
    }

    protected MatchResult checkElement(final WebElement element, String name, final ICheckSettings checkSettings) {
        return checkWindowBase(new RegionProvider() {
            @Override
            public Region getRegion() {
                return getElementRegion(element, checkSettings);
            }
        }, name, false, checkSettings);
    }

    public void checkElement(WebElement element) {
        checkElement(element, null);
    }

    public void checkElement(WebElement element, String tag) {
        checkElement(element, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    public void checkElement(WebElement element, int matchTimeout, String tag) {
        check(tag, Target.region(element).timeout(matchTimeout).fully());
    }

    public void checkElement(By selector) {
        checkElement(selector, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    public void checkElement(By selector, String tag) {
        checkElement(selector, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    public void checkElement(By selector, int matchTimeout, String tag) {
        check(tag, Target.region(selector).timeout(matchTimeout).fully());
    }

    private WebElement getTargetElement(AppiumCheckSettings seleniumCheckTarget) {
        assert seleniumCheckTarget != null;
        By targetSelector = seleniumCheckTarget.getTargetSelector();
        WebElement targetElement = seleniumCheckTarget.getTargetElement();
        if (targetElement == null && targetSelector != null) {
            targetElement = this.driver.findElement(targetSelector);
        }
        return targetElement;
    }

    private ScaleProviderFactory updateScalingParams() {
        // Update the scaling params only if we haven't done so yet, and the user hasn't set anything else manually.
        if (scaleProviderHandler.get() instanceof NullScaleProvider) {
            ScaleProviderFactory factory;
            logger.verbose("Setting scale provider...");
            try {
                factory = getScaleProviderFactory();
            } catch (Exception e) {
                // This can happen in Appium for example.
                logger.verbose("Failed to set ContextBasedScaleProvider.");
                logger.verbose("Using FixedScaleProvider instead...");
                factory = new FixedScaleProviderFactory(logger, 1 / getDevicePixelRatio(), scaleProviderHandler);
            }
            logger.verbose("Done!");
            return factory;
        }
        // If we already have a scale provider set, we'll just use it, and pass a mock as provider handler.
        PropertyHandler<ScaleProvider> nullProvider = new SimplePropertyHandler<>();
        return new ScaleProviderIdentityFactory(logger, scaleProviderHandler.get(), nullProvider);
    }

    /**
     * Set whether or not new tests are saved by default.
     * @param saveNewTests True if new tests should be saved by default. False otherwise.
     */
    public void setSaveNewTests(boolean saveNewTests) {
        this.configuration.setSaveNewTests(saveNewTests);
    }

    /**
     * Gets save new tests.
     * @return True if new tests are saved by default.
     */
    public boolean getSaveNewTests() {
        return this.configuration.getSaveNewTests();
    }

    public void setSaveDiffs(Boolean saveDiffs) {
        this.configuration.setSaveDiffs(saveDiffs);
    }

    public Boolean getSaveDiffs() {
        return this.configuration.getSaveDiffs();
    }

    /**
     * Gets stitch overlap.
     * @return Returns the stitching overlap in pixels.
     */
    public int getStitchOverlap() {
        return configuration.getStitchOverlap();
    }

    /**
     * Sets the stitching overlap in pixels.
     * @param pixels The width (in pixels) of the overlap.
     */
    public void setStitchOverlap(int pixels) {
        configuration.setStitchOverlap(pixels);
    }

    protected EyesAppiumScreenshot getFullPageScreenshot() {

        logger.verbose("Full page Appium screenshot requested.");

        EyesScreenshotFactory screenshotFactory = new EyesAppiumScreenshotFactory(logger, driver);
        ScaleProviderFactory scaleProviderFactory = updateScalingParams();

        AppiumScrollPositionProvider scrollPositionProvider = (AppiumScrollPositionProvider) getPositionProvider();

        AppiumCaptureAlgorithmFactory algoFactory = new AppiumCaptureAlgorithmFactory(driver, logger,
                scrollPositionProvider, imageProvider, debugScreenshotsProvider, scaleProviderFactory,
                cutProviderHandler.get(), screenshotFactory, getConfiguration().getWaitBeforeScreenshots(), cutElement, getStitchOverlap());

        AppiumFullPageCaptureAlgorithm algo = algoFactory.getAlgorithm();

        BufferedImage fullPageImage = algo.getStitchedRegion(Region.EMPTY, regionPositionCompensation);
        return new EyesAppiumScreenshot(logger, driver, fullPageImage);
    }

    protected EyesAppiumScreenshot getSimpleScreenshot() {
        ScaleProviderFactory scaleProviderFactory = updateScalingParams();
//        ensureElementVisible(this.targetElement);

        logger.verbose("Screenshot requested...");
        BufferedImage screenshotImage = imageProvider.getImage();
        debugScreenshotsProvider.save(screenshotImage, "original");

        ScaleProvider scaleProvider = scaleProviderFactory.getScaleProvider(screenshotImage.getWidth());
        if (scaleProvider.getScaleRatio() != 1.0) {
            logger.verbose("scaling...");
            screenshotImage = ImageUtils.scaleImage(screenshotImage, scaleProvider);
            debugScreenshotsProvider.save(screenshotImage, "scaled");
        }

        CutProvider cutProvider = cutProviderHandler.get();
        if (!(cutProvider instanceof NullCutProvider)) {
            logger.verbose("cutting...");
            screenshotImage = cutProvider.cut(screenshotImage);
            debugScreenshotsProvider.save(screenshotImage, "cut");
        }

        logger.verbose("Creating screenshot object...");
        return new EyesAppiumScreenshot(logger, driver, screenshotImage);
    }

    protected MatchResult checkRegion(String name, ICheckSettings checkSettings) {
        MatchResult result = checkWindowBase(new RegionProvider() {
            @Override
            public Region getRegion() {
                Point p = targetElement.getLocation();
                Dimension d = targetElement.getSize();
                return new Region(p.getX(), p.getY(), d.getWidth(), d.getHeight(), CoordinatesType.CONTEXT_RELATIVE);
            }
        }, name, false, checkSettings);
        logger.verbose("Done! trying to scroll back to original position.");

        return result;
    }

    public void checkRegion(Region region) {
        checkRegion(region, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    public void checkRegion(final Region region, int matchTimeout, String tag) throws TestFailedException {
        if (getIsDisabled()) {
            getLogger().log(String.format("checkRegion([%s], %d, '%s'): Ignored", region, matchTimeout, tag));
            return;
        }

        ArgumentGuard.notNull(region, "region");

        getLogger().verbose(String.format("checkRegion([%s], %d, '%s')", region, matchTimeout, tag));

        check(Target.region(region).timeout(matchTimeout).withName(tag));
    }

    public void checkRegion(WebElement element) {
        checkRegion(element, USE_DEFAULT_MATCH_TIMEOUT, null, true);
    }

    public void checkRegion(WebElement element, String tag, boolean stitchContent) {
        checkRegion(element, USE_DEFAULT_MATCH_TIMEOUT, tag, stitchContent);
    }

    public void checkRegion(final WebElement element, int matchTimeout, String tag) {
        checkRegion(element, matchTimeout, tag, true);
    }

    public void checkRegion(WebElement element, int matchTimeout, String tag, boolean stitchContent) {
        if (getIsDisabled()) {
            getLogger().log(String.format("checkRegion([%s], %d, '%s'): Ignored", element, matchTimeout, tag));
            return;
        }

        ArgumentGuard.notNull(element, "element");

        getLogger().verbose(String.format("checkRegion([%s], %d, '%s')", element, matchTimeout, tag));

        check(Target.region(element).timeout(matchTimeout).withName(tag).fully(stitchContent));
    }

    public void checkRegion(By selector) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, null, false);
    }

    public void checkRegion(By selector, boolean stitchContent) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, null, stitchContent);
    }

    public void checkRegion(By selector, String tag) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, tag, true);
    }

    public void checkRegion(By selector, String tag, boolean stitchContent) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, tag, stitchContent);
    }

    public void checkRegion(By selector, int matchTimeout, String tag) {
        checkRegion(selector, matchTimeout, tag, true);
    }

    public void checkRegion(By selector, int matchTimeout, String tag, boolean stitchContent) {
        check(tag, Target.region(selector).timeout(matchTimeout).fully(stitchContent));
    }

    @Override
    protected EyesScreenshot getSubScreenshot(EyesScreenshot screenshot, Region region, ICheckSettingsInternal checkSettingsInternal) {
        ArgumentGuard.notNull(region, "region");
        if ((EyesDriverUtils.isAndroid(driver) || EyesDriverUtils.isIOS(driver))
                && region.getCoordinatesType() != CoordinatesType.CONTEXT_RELATIVE) {
            logger.verbose(String.format("getSubScreenshot([%s])", region));

            BufferedImage image = screenshot.getImage();
            if (image.getWidth() < driver.getViewportRect().get("width")) {
                image = ImageUtils.scaleImage(image, driver.getDevicePixelRatio());
            }
            BufferedImage subScreenshotImage = ImageUtils.scaleImage(ImageUtils.getImagePart(image, region),
                    1/driver.getDevicePixelRatio());

            EyesAppiumScreenshot result = new EyesAppiumScreenshot(logger, driver, subScreenshotImage);

            logger.verbose("Done!");
            return result;
        } else {
            return screenshot.getSubScreenshot(region, false);
        }
    }

    @Override
    protected String getAUTSessionId() {
        try {
            return driver.getRemoteWebDriver().getSessionId().toString();
        } catch (Exception e) {
            logger.log("WARNING: Failed to get AUT session ID! (maybe driver is not available?). Error: "
                    + e.getMessage());
            return "";
        }
    }

    public Location getElementOriginalLocation(WebElement element) {
        try {
            ContentSize contentSize = EyesAppiumUtils.getContentSize(driver.getRemoteWebDriver(), element);
            return new Location(contentSize.left, contentSize.top);
        } catch (IOException ignored) {
            return new Location(element.getLocation().getX(), element.getLocation().getY());
        }
    }

    private void initVisualLocatorProvider() {
        if (EyesDriverUtils.isAndroid(driver)) {
            visualLocatorsProvider = new AndroidVisualLocatorProvider(logger, driver, serverConnector, getDevicePixelRatio(), configuration.getAppName(), debugScreenshotsProvider);
        } else if (EyesDriverUtils.isIOS(driver)) {
            visualLocatorsProvider = new IOSVisualLocatorProvider(logger, driver, serverConnector, getDevicePixelRatio(), configuration.getAppName(), debugScreenshotsProvider);
        } else {
            throw new Error("Could not find driver type for getting visual locator provider");
        }
    }

    public Map<String, List<Region>> locate(VisualLocatorSettings visualLocatorSettings) {
        ArgumentGuard.notNull(visualLocatorSettings, "visualLocatorSettings");
        return visualLocatorsProvider.getLocators(visualLocatorSettings);
    }

    private Region getElementRegion(WebElement element, ICheckSettings checkSettings) {
        logger.verbose("Get element region...");
        Boolean statusBarExists = null;
        if (checkSettings instanceof AppiumCheckSettings) {
            statusBarExists = ((AppiumCheckSettings) checkSettings).getStatusBarExists();
        }
        Region region = ((AppiumScrollPositionProvider) getPositionProvider()).getElementRegion(element, shouldStitchContent(), statusBarExists);
        logger.verbose("Element region: " + region.toString());
        return region;
    }

    public com.applitools.eyes.selenium.Configuration getConfiguration() {
        return new com.applitools.eyes.selenium.Configuration(configuration);
    }

    public void setConfiguration(Configuration configuration) {
        ArgumentGuard.notNull(configuration, "configuration");
        String apiKey = configuration.getApiKey();
        if (apiKey != null) {
            this.setApiKey(apiKey);
        }
        URI serverUrl = configuration.getServerUrl();
        if (serverUrl != null) {
            this.setServerUrl(serverUrl.toString());
        }
        AbstractProxySettings proxy = configuration.getProxy();
        if (proxy != null) {
            this.setProxy(proxy);
        }
        this.configuration = new Configuration(configuration);
    }
}
