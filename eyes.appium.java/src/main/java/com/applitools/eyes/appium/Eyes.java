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
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.locators.VisualLocatorsProvider;
import com.applitools.eyes.scaling.FixedScaleProviderFactory;
import com.applitools.eyes.scaling.NullScaleProvider;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.EyesDriverUtils;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.SimpleRegionByElement;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.selenium.positioning.NullRegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.RegionPositionCompensation;
import com.applitools.eyes.selenium.regionVisibility.MoveToRegionVisibilityStrategy;
import com.applitools.eyes.selenium.regionVisibility.NopRegionVisibilityStrategy;
import com.applitools.eyes.selenium.regionVisibility.RegionVisibilityStrategy;
import com.applitools.utils.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.*;

public class Eyes extends EyesBase {
    private static final int USE_DEFAULT_MATCH_TIMEOUT = -1;
    private static final int DEFAULT_STITCH_OVERLAP = 50;
    private static final int IOS_STITCH_OVERLAP = 0;

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
    private ImageRotation rotation;
    protected WebElement targetElement = null;
    private PropertyHandler<RegionVisibilityStrategy> regionVisibilityStrategyHandler;
    private String scrollRootElementId = null;

    public Eyes() {
        super(new ClassicRunner());
        regionVisibilityStrategyHandler = new SimplePropertyHandler<>();
        regionVisibilityStrategyHandler.set(new MoveToRegionVisibilityStrategy(logger));
        configuration.setStitchOverlap(DEFAULT_STITCH_OVERLAP);
    }

    public WebDriver open(WebDriver driver, Configuration configuration) {
        this.configuration = new Configuration(configuration);
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
        this.configuration.setViewportSize(driver.getDefaultContentViewportSize());
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

    public boolean shouldStitchContent() {
        return stitchContent;
    }

    @Override
    public String getBaseAgentId() {
        return "eyes.appium.java/" + ClassVersionGetter.CURRENT_VERSION;
    }

    @Override
    protected String tryCaptureDom() {
        return null;
    }

    @Override
    protected RectangleSize getViewportSize() {
        ArgumentGuard.notNull(driver, "driver");
        return driver.getDefaultContentViewportSize();
    }

    @Override
    protected Configuration setViewportSize(RectangleSize size) {
        return configuration;
    }

    @Override
    protected String getInferredEnvironment() {
        return "";
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
            this.driver = new EyesAppiumDriver(logger, this, (AppiumDriver) driver);
            regionVisibilityStrategyHandler.set(new NopRegionVisibilityStrategy(logger));
            adjustStitchOverlap(driver);
        } else {
            logger.verbose("Did not find an instance of AppiumDriver, using regular logic");
        }
    }

    private void adjustStitchOverlap(WebDriver driver) {
        if (EyesDriverUtils.isIOS(driver)) {
            configuration.setStitchOverlap(IOS_STITCH_OVERLAP);
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This override also checks for mobile operating system.
     */
    @Override
    protected AppEnvironment getAppEnvironment() {
        AppEnvironment appEnv = (AppEnvironment) super.getAppEnvironment();
        RemoteWebDriver underlyingDriver = driver.getRemoteWebDriver();
        // If hostOs isn't set, we'll try and extract and OS ourselves.
        if (appEnv.getOs() != null) {
            return appEnv;
        }

        logger.log("No OS set, checking for mobile OS...");
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
            String platformVersion = EyesDriverUtils.getPlatformVersion(underlyingDriver);
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

        if (appEnv.getDeviceInfo() == null) {
            appEnv.setDeviceInfo(EyesDriverUtils.getMobileDeviceName(underlyingDriver));
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

    public void check(ICheckSettings... checkSettings) {
        if (getIsDisabled()) {
            logger.log(String.format("check(ICheckSettings[%d]): Ignored", checkSettings.length));
            return;
        }

        boolean originalForceFPS = getConfigurationInstance().getForceFullPageScreenshot() != null && getConfigurationInstance().getForceFullPageScreenshot();

        if (checkSettings.length > 1) {
            getConfigurationInstance().setForceFullPageScreenshot(true);
        }

        Dictionary<Integer, GetSimpleRegion> getRegions = new Hashtable<>();
        Dictionary<Integer, ICheckSettingsInternal> checkSettingsInternalDictionary = new Hashtable<>();

        for (int i = 0; i < checkSettings.length; ++i) {
            ICheckSettings settings = checkSettings[i];
            ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) settings;

            checkSettingsInternalDictionary.put(i, checkSettingsInternal);

            Region targetRegion = checkSettingsInternal.getTargetRegion();

            if (targetRegion != null) {
                getRegions.put(i, new SimpleRegionByRectangle(targetRegion));
            } else {
                AppiumCheckSettings appiumCheckTarget = (settings instanceof AppiumCheckSettings) ? (AppiumCheckSettings) settings : null;
                if (appiumCheckTarget != null) {
                    WebElement targetElement = getTargetElement(appiumCheckTarget);
                    if (targetElement != null) {
                        getRegions.put(i, new SimpleRegionByElement(targetElement));
                    }
                }
            }
        }

        matchRegions(getRegions, checkSettingsInternalDictionary, checkSettings);
        getConfigurationInstance().setForceFullPageScreenshot(originalForceFPS);
    }

    private void matchRegions(Dictionary<Integer, GetSimpleRegion> getRegions,
                              Dictionary<Integer, ICheckSettingsInternal> checkSettingsInternalDictionary,
                              ICheckSettings[] checkSettings) {

        if (getRegions.size() == 0) {
            return;
        }

        EyesScreenshot screenshot = getFullPageScreenshot();
        for (int i = 0; i < checkSettings.length; ++i) {
            if (((Hashtable<Integer, GetSimpleRegion>) getRegions).containsKey(i)) {
                GetSimpleRegion getRegion = getRegions.get(i);
                ICheckSettingsInternal checkSettingsInternal = checkSettingsInternalDictionary.get(i);
                List<EyesScreenshot> subScreenshots = getSubScreenshots(screenshot, getRegion);
                matchRegion(checkSettingsInternal, subScreenshots);
            }
        }
    }

    private List<EyesScreenshot> getSubScreenshots(EyesScreenshot screenshot, GetSimpleRegion getRegion) {
        List<EyesScreenshot> subScreenshots = new ArrayList<>();
        for (Region r : getRegion.getRegions(screenshot)) {
            logger.verbose("original sub-region: " + r);
            r = regionPositionCompensation.compensateRegionPosition(r, getDevicePixelRatio());
            logger.verbose("sub-region after compensation: " + r);
            EyesScreenshot subScreenshot = screenshot.getSubScreenshot(r, false);
            subScreenshots.add(subScreenshot);
        }
        return subScreenshots;
    }

    private void matchRegion(ICheckSettingsInternal checkSettingsInternal, List<EyesScreenshot> subScreenshots) {
        String name = checkSettingsInternal.getName();
        for (EyesScreenshot subScreenshot : subScreenshots) {
            debugScreenshotsProvider.save(subScreenshot.getImage(), String.format("subscreenshot_%s", name));

            ImageMatchSettings ims = MatchWindowTask.createImageMatchSettings(checkSettingsInternal, subScreenshot, this);
            Location location = subScreenshot.getLocationInScreenshot(Location.ZERO, CoordinatesType.SCREENSHOT_AS_IS);
            AppOutput appOutput = new AppOutput(name, subScreenshot, null, null, location);
            MatchWindowData data = prepareForMatch(new ArrayList<Trigger>(), appOutput, name, false,
                    ims, this, null, getAppName());
            MatchResult matchResult = performMatch(data);
            logger.verbose("matchResult.asExcepted: " + matchResult.getAsExpected());
        }
    }

    public void check(ICheckSettings checkSettings) {
        if (checkSettings instanceof AppiumCheckSettings) {
            updateCutElement((AppiumCheckSettings) checkSettings);
            this.scrollRootElementId = getScrollRootElementId((AppiumCheckSettings) checkSettings);
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

        MatchResult result = null;

        if (targetRegion != null) {
            logger.verbose("have target region");
            Region region = new Region(targetRegion.getLocation(), targetRegion.getSize(), CoordinatesType.CONTEXT_RELATIVE);
            result = this.checkWindowBase(region, name, checkSettings);
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
                result = this.checkWindowBase(null, name, checkSettings);
            }
        }

        if (result == null) {
            result = new MatchResult();
        }

        this.stitchContent = false;

        ValidationResult validationResult = new ValidationResult();
        validationResult.setAsExpected(result.getAsExpected());

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
    protected EyesScreenshot getScreenshot(Region targetRegion, ICheckSettingsInternal checkSettingsInternal) {
        logger.verbose("getScreenshot()");

        EyesScreenshot result;

        if (getForceFullPageScreenshot() || stitchContent) {
            result = getFullPageScreenshot();
        } else {
            result = getSimpleScreenshot();
        }

        if (targetRegion != null && !targetRegion.isEmpty()) {
            result = getSubScreenshot(result, targetRegion, checkSettingsInternal);
            debugScreenshotsProvider.save(result.getImage(), "SUB_SCREENSHOT");
        }

        logger.verbose("Done!");
        return result;
    }

    @Override
    protected String getTitle() {
        return "";
    }

    protected MatchResult checkElement(final WebElement element, String name, final ICheckSettings checkSettings) {
        Region region = getElementRegion(element, checkSettings);
        return checkWindowBase(region, name, checkSettings);
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

        if (targetElement != null && !(targetElement instanceof EyesAppiumElement)) {
            targetElement = new EyesAppiumElement(driver, targetElement, 1 / devicePixelRatio);
        }
        return targetElement;
    }

    private ScaleProviderFactory updateScalingParams() {
        // Update the scaling params only if we haven't done so yet, and the user hasn't set anything else manually.
        if (scaleProviderHandler.get() instanceof NullScaleProvider) {
            ScaleProviderFactory factory = new FixedScaleProviderFactory(logger, 1 / getDevicePixelRatio(), scaleProviderHandler);
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

    public void setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        configuration.setDefaultMatchSettings(defaultMatchSettings);
    }

    public ImageMatchSettings getDefaultMatchSettings() {
        return this.configuration.getDefaultMatchSettings();
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
                cutProviderHandler.get(), screenshotFactory, getConfigurationInstance().getWaitBeforeScreenshots(), cutElement,
                getStitchOverlap(), scrollRootElementId);

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
            screenshotImage = ImageUtils.scaleImage(screenshotImage, scaleProvider.getScaleRatio(), true);
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
        Point p = targetElement.getLocation();
        Dimension d = targetElement.getSize();
        Region region = new Region(p.getX(), p.getY(), d.getWidth(), d.getHeight(), CoordinatesType.CONTEXT_RELATIVE);
        MatchResult result = checkWindowBase(region, name, checkSettings);
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

    protected EyesScreenshot getSubScreenshot(EyesScreenshot screenshot, Region region, ICheckSettingsInternal checkSettingsInternal) {
        ArgumentGuard.notNull(region, "region");
        if ((EyesDriverUtils.isAndroid(driver) || EyesDriverUtils.isIOS(driver))
                && region.getCoordinatesType() != CoordinatesType.CONTEXT_RELATIVE) {
            logger.verbose(String.format("getSubScreenshot([%s])", region));

            BufferedImage image = screenshot.getImage();
            if (image.getWidth() < driver.getViewportRect().get("width")) {
                image = ImageUtils.scaleImage(image, driver.getDevicePixelRatio(), true);
            }
            BufferedImage subScreenshotImage = ImageUtils.scaleImage(ImageUtils.getImagePart(image, region),
                    1 / driver.getDevicePixelRatio(), true);

            EyesAppiumScreenshot result = new EyesAppiumScreenshot(logger, driver, subScreenshotImage);

            logger.verbose("Done!");
            return result;
        } else {
            return screenshot.getSubScreenshot(region, false);
        }
    }

    private void initVisualLocatorProvider() {
        if (EyesDriverUtils.isAndroid(driver)) {
            visualLocatorsProvider = new AndroidVisualLocatorProvider(logger, driver, getServerConnector(), getDevicePixelRatio(), configuration.getAppName(), debugScreenshotsProvider);
        } else if (EyesDriverUtils.isIOS(driver)) {
            visualLocatorsProvider = new IOSVisualLocatorProvider(logger, driver, getServerConnector(), getDevicePixelRatio(), configuration.getAppName(), debugScreenshotsProvider);
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

    @Override
    public com.applitools.eyes.selenium.Configuration getConfiguration() {
        return new com.applitools.eyes.selenium.Configuration(configuration);
    }

    public void setBranchName(String branchName) {
        configuration.setBranchName(branchName);
    }

    public String getBranchName() {
        return configuration.getBranchName();
    }

    public void setParentBranchName(String branchName) {
        configuration.setParentBranchName(branchName);
    }

    public String getParentBranchName() {
        return configuration.getParentBranchName();
    }

    @Override
    public Configuration setBatch(BatchInfo batch) {
        return this.configuration.setBatch(batch);
    }

    @Override
    protected Configuration getConfigurationInstance() {
        return configuration;
    }

    public BatchInfo getBatch() {
        return configuration.getBatch();
    }

    public void setAgentId(String agentId) {
        this.configuration.setAgentId(agentId);
    }

    public String getAgentId() {
        return this.configuration.getAgentId();
    }

    public void setHostOS(String hostOS) {
        this.configuration.setHostOS(hostOS);
    }

    public String getHostOS() {
        return this.configuration.getHostOS();
    }

    public void setHostApp(String hostApp) {
        this.configuration.setHostApp(hostApp);
    }

    public String getHostApp() {
        return this.configuration.getHostOS();
    }

    public boolean getIgnoreCaret() {
        return this.configuration.getIgnoreCaret();
    }

    public void setIgnoreCaret(boolean value) {
        this.configuration.setIgnoreCaret(value);
    }

    public void setMatchLevel(MatchLevel matchLevel) {
        this.configuration.getDefaultMatchSettings().setMatchLevel(matchLevel);
    }

    public MatchLevel getMatchLevel() {
        return this.configuration.getDefaultMatchSettings().getMatchLevel();
    }

    public void setEnvName(String envName) {
        this.configuration.setEnvironmentName(envName);
    }

    public String getEnvName() {
        return this.configuration.getEnvironmentName();
    }

    public void setBaselineEnvName(String baselineEnvName) {
        this.configuration.setBaselineEnvName(baselineEnvName);
    }

    public String getBaselineEnvName() {
        return configuration.getBaselineEnvName();
    }

    public void setBaselineBranchName(String branchName) {
        this.configuration.setBaselineBranchName(branchName);
    }

    public String getBaselineBranchName() {
        return configuration.getBaselineBranchName();
    }

    public void setIgnoreDisplacements(boolean isIgnoreDisplacements) {
        this.configuration.setIgnoreDisplacements(isIgnoreDisplacements);
    }

    public boolean getIgnoreDisplacements() {
        return this.configuration.getIgnoreDisplacements();
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

    @Override
    public Object getAgentSetup() {
        return new EyesAppiumAgentSetup();
    }

    private String getScrollRootElementId(AppiumCheckSettings checkSettings) {
        String scrollRootElementId = checkSettings.getScrollRootElementId();
        if (scrollRootElementId == null) {
            WebElement webElement = checkSettings.getScrollRootElement();
            if (webElement == null && checkSettings.getScrollRootElementSelector() != null) {
                webElement = driver.findElement(checkSettings.getScrollRootElementSelector());
            }
            if (webElement != null) {
                scrollRootElementId = webElement.getAttribute("resourceId").split("/")[1];
            }
        }
        return scrollRootElementId;
    }

    private String getHelperLibraryVersion() {
        String version = "";
        if (driver.getRemoteWebDriver() instanceof AndroidDriver) {
            try {
                WebElement hiddenElement = driver.getRemoteWebDriver().findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelper_Version\")"));
                if (hiddenElement != null) {
                    version = hiddenElement.getText();
                }
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            }
            if (version == null) {
                try {
                    WebElement hiddenElement = driver.getRemoteWebDriver().findElement(MobileBy.AndroidUIAutomator("new UiSelector().description(\"EyesAppiumHelper\")"));
                    if (hiddenElement != null) {
                        version = "1.0.0";
                    }
                } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                }
            }
            if (version == null) {
                logger.verbose("Appium Helper library is not used...");
            } else {
                logger.verbose("Appium Helper library version: " + version);
            }
        }
        return version;
    }

    class EyesAppiumAgentSetup {
        class WebDriverInfo {
            /**
             * Gets name.
             * @return the name
             */
            public String getName() {
                return remoteWebDriver.getClass().getName();
            }

            /**
             * Gets capabilities.
             * @return the capabilities
             */
            public Capabilities getCapabilities() {
                return remoteWebDriver.getCapabilities();
            }
        }

        /**
         * Instantiates a new Eyes selenium agent setup.
         */
        public EyesAppiumAgentSetup() {
            remoteWebDriver = driver.getRemoteWebDriver();
        }

        private RemoteWebDriver remoteWebDriver;

        /**
         * Gets selenium session id.
         * @return the selenium session id
         */
        public String getAppiumSessionId() {
            return remoteWebDriver.getSessionId().toString();
        }

        /**
         * Gets web driver.
         * @return the web driver
         */
        public WebDriverInfo getWebDriver() {
            return new WebDriverInfo();
        }

        /**
         * Gets device pixel ratio.
         * @return the device pixel ratio
         */
        public double getDevicePixelRatio() {
            return Eyes.this.getDevicePixelRatio();
        }

        /**
         * Gets cut provider.
         * @return the cut provider
         */
        public String getCutProvider() {
            return Eyes.this.cutProviderHandler.get().getClass().getName();
        }

        /**
         * Gets scale provider.
         * @return the scale provider
         */
        public String getScaleProvider() {
            return Eyes.this.scaleProviderHandler.get().getClass().getName();
        }

        /**
         * Gets stitch mode.
         * @return the stitch mode
         */
        public StitchMode getStitchMode() {
            return Eyes.this.getConfigurationInstance().getStitchMode();
        }

        /**
         * Gets hide scrollbars.
         * @return the hide scrollbars
         */
        public boolean getHideScrollbars() {
            return Eyes.this.getConfigurationInstance().getHideScrollbars();
        }

        /**
         * Gets force full page screenshot.
         * @return the force full page screenshot
         */
        public boolean getForceFullPageScreenshot() {
            Boolean forceFullPageScreenshot = getConfigurationInstance().getForceFullPageScreenshot();
            if (forceFullPageScreenshot == null) return false;
            return forceFullPageScreenshot;
        }

        public String getHelperLibraryVersion() {
            return Eyes.this.getHelperLibraryVersion();
        }
    }
}
