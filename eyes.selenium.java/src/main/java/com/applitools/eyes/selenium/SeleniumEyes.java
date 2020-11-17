package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.scaling.FixedScaleProviderFactory;
import com.applitools.eyes.scaling.NullScaleProvider;
import com.applitools.eyes.selenium.capture.*;
import com.applitools.eyes.selenium.fluent.*;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.*;
import com.applitools.eyes.selenium.regionVisibility.MoveToRegionVisibilityStrategy;
import com.applitools.eyes.selenium.regionVisibility.NopRegionVisibilityStrategy;
import com.applitools.eyes.selenium.regionVisibility.RegionVisibilityStrategy;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.utils.*;
import org.apache.http.annotation.Obsolete;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.*;

/**
 * The main API gateway for the SDK.
 */
@SuppressWarnings("WeakerAccess")
public class SeleniumEyes extends RunningTest implements ISeleniumEyes {

    private static final String SCROLL_ELEMENT_ATTRIBUTE = "data-applitools-scroll";
    private static final String ACTIVE_FRAME_ATTRIBUTE = "data-applitools-active-frame";

    /**
     * The constant UNKNOWN_DEVICE_PIXEL_RATIO.
     */
    public static final double UNKNOWN_DEVICE_PIXEL_RATIO = 0;
    /**
     * The constant DEFAULT_DEVICE_PIXEL_RATIO.
     */
    public static final double DEFAULT_DEVICE_PIXEL_RATIO = 1;

    private static final int USE_DEFAULT_MATCH_TIMEOUT = -1;

    private FrameChain originalFC;
    private WebElement userDefinedSRE;
    private PositionProvider currentFramePositionProvider;

    private EyesSeleniumDriver driver;
    private boolean doNotGetTitle;

    public boolean checkFrameOrElement;
    private Region regionToCheck;

    private ImageRotation rotation;
    private double devicePixelRatio;
    private PropertyHandler<RegionVisibilityStrategy> regionVisibilityStrategyHandler;
    private SeleniumJavaScriptExecutor jsExecutor;

    private UserAgent userAgent;
    private ImageProvider imageProvider;
    private RegionPositionCompensation regionPositionCompensation;
    private Region effectiveViewport;

    private EyesScreenshotFactory screenshotFactory;
    private final ConfigurationProvider configurationProvider;

    /**
     * Should stitch content boolean.
     * @return the boolean
     */
    @Obsolete
    public boolean shouldStitchContent() {
        return false;
    }

    /**
     * The interface Web driver action.
     */
    @SuppressWarnings("UnusedDeclaration")
    public interface WebDriverAction {
        /**
         * Drive.
         * @param driver the driver
         */
        void drive(WebDriver driver);
    }

    /**
     * Creates a new SeleniumEyes instance that interacts with the SeleniumEyes cloud
     * service.
     */
    public SeleniumEyes(ConfigurationProvider configurationProvider, ClassicRunner runner) {
        super(runner);
        this.configurationProvider = configurationProvider;
        checkFrameOrElement = false;
        doNotGetTitle = false;
        devicePixelRatio = UNKNOWN_DEVICE_PIXEL_RATIO;
        regionVisibilityStrategyHandler = new SimplePropertyHandler<>();
        regionVisibilityStrategyHandler.set(new MoveToRegionVisibilityStrategy(logger));
    }

    @Override
    public String getBaseAgentId() {
        return "eyes.selenium.java/" + ClassVersionGetter.CURRENT_VERSION;
    }

    public void apiKey(String apiKey) {
        setApiKey(apiKey);
    }

    @Override
    public boolean isEyesClosed() {
        return isCompleted();
    }

    @Override
    public IBatchCloser getBatchCloser() {
        return this;
    }

    @Override
    public String getBatchId() {
        return getConfiguration().getBatch().getId();
    }

    @Override
    public Map<String, RunningTest> getAllRunningTests() {
        Map<String, RunningTest> map = new HashMap<>();
        map.put(getTestId(), this);
        return map;
    }

    @Override
    public List<TestResultContainer> getAllTestResults() {
        if (!isCompleted()) {
            return null;
        }

        return Collections.singletonList(testResultContainer);
    }

    @Override
    public boolean isCompleted() {
        return testResultContainer != null;
    }

    public void serverUrl(String serverUrl) {
        setServerUrl(serverUrl);
    }

    public void serverUrl(URI serverUrl) {
        setServerUrl(serverUrl);
    }

    /**
     * Gets driver.
     * @return the driver
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Gets original fc.
     * @return the original fc
     */
    public FrameChain getOriginalFC() {
        return originalFC;
    }

    /**
     * Gets current frame position provider.
     * @return the current frame position provider
     */
    public PositionProvider getCurrentFramePositionProvider() {
        return currentFramePositionProvider;
    }

    /**
     * Gets region to check.
     * @return the region to check
     */
    public Region getRegionToCheck() {
        return regionToCheck;
    }

    /**
     * Sets region to check.
     * @param regionToCheck the region to check
     */
    public void setRegionToCheck(Region regionToCheck) {
        this.regionToCheck = regionToCheck;
    }

    /**
     * Turns on/off the automatic scrolling to a region being checked by
     * {@code checkRegion}.
     * @param shouldScroll Whether to automatically scroll to a region being validated.
     */
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

    /**
     * Gets rotation.
     * @return The image rotation model.
     */
    public ImageRotation getRotation() {
        return rotation;
    }

    /**
     * Sets rotation.
     * @param rotation The image rotation model.
     */
    public void setRotation(ImageRotation rotation) {
        this.rotation = rotation;
        if (driver != null) {
            driver.setRotation(rotation);
        }
    }

    /**
     * @return The device pixel ratio, or {@link #UNKNOWN_DEVICE_PIXEL_RATIO}
     * if the DPR is not known yet or if it wasn't possible to extract it.
     */
    public double getDevicePixelRatio() {
        return devicePixelRatio;
    }

    @Override
    public WebDriver open(WebDriver driver, String appName, String testName, RectangleSize viewportSize) throws EyesException {
        getConfigurationInstance().setAppName(appName).setTestName(testName);
        if (viewportSize != null && !viewportSize.isEmpty()) {
            getConfigurationInstance().setViewportSize(new RectangleSize(viewportSize));
        }
        return open(driver);
    }

    /**
     * Open web driver.
     * @param driver the driver
     * @return the web driver
     * @throws EyesException the eyes exception
     */
    public WebDriver open(WebDriver driver) throws EyesException {

        openLogger();

        if (getIsDisabled()) {
            logger.verbose("Ignored");
            return driver;
        }

        initDriver(driver);

        this.jsExecutor = new SeleniumJavaScriptExecutor(this.driver);

        String uaString = this.driver.getUserAgent();
        if (uaString != null) {
            if (uaString.startsWith("useragent:")) {
                uaString = uaString.substring(10);
            }
            userAgent = UserAgent.parseUserAgentString(uaString, true);
        }

        initDevicePixelRatio();

        screenshotFactory = new EyesWebDriverScreenshotFactory(logger, this.driver);
        imageProvider = ImageProviderFactory.getImageProvider(userAgent, this, logger, this.driver);
        regionPositionCompensation = RegionPositionCompensationFactory.getRegionPositionCompensation(userAgent, this, logger);

        if (!getConfigurationInstance().isVisualGrid()) {
            openBase();
        }

        //updateScalingParams();

        this.driver.setRotation(rotation);
        this.runner.addBatch(this.getConfigurationInstance().getBatch().getId(), this);
        return this.driver;
    }

    private void initDevicePixelRatio() {
        logger.verbose("Trying to extract device pixel ratio...");
        try {
            devicePixelRatio = driver.getDevicePixelRatio();
        } catch (Exception ex) {
            logger.verbose("Failed to extract device pixel ratio! Using default.");
            devicePixelRatio = DEFAULT_DEVICE_PIXEL_RATIO;
        }
        logger.verbose("Device pixel ratio: " + devicePixelRatio);
    }

    private void initDriver(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            this.driver = new EyesSeleniumDriver(logger, this, (RemoteWebDriver) driver);
        } else if (driver instanceof EyesSeleniumDriver) {
            this.driver = (EyesSeleniumDriver) driver;
        } else {
            String errMsg = "Driver is not a RemoteWebDriver (" +
                    driver.getClass().getName() + ")";
            logger.log(errMsg);
            throw new EyesException(errMsg);
        }
        if (EyesDriverUtils.isMobileDevice(driver)) {
            regionVisibilityStrategyHandler.set(new NopRegionVisibilityStrategy(logger));
        }
    }

    /**
     * Gets scroll root element.
     * @return the scroll root element
     */
    public WebElement getScrollRootElement() {
        if (this.userDefinedSRE == null) {
            this.userDefinedSRE = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
        }
        return this.userDefinedSRE;
    }

    private PositionProvider createPositionProvider() {
        return createPositionProvider(this.userDefinedSRE);
    }

    private PositionProvider createPositionProvider(WebElement scrollRootElement) {
        // Setting the correct position provider.
        StitchMode stitchMode = getConfigurationInstance().getStitchMode();
        logger.verbose("initializing position provider. stitchMode: " + stitchMode);
        switch (stitchMode) {
            case CSS:
                return new CssTranslatePositionProvider(logger, this.jsExecutor, scrollRootElement);
            default:
                return ScrollPositionProviderFactory.getScrollPositionProvider(userAgent, logger, this.jsExecutor, scrollRootElement);
        }
    }


    /**
     * See {@link #checkWindow(String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkWindow() {
        checkWindow((String) null);
    }

    /**
     * See {@link #checkWindow(int, String)}.
     * Default match timeout is used.
     * @param tag An optional tag to be associated with the snapshot.
     */
    public void checkWindow(String tag) {
        check(tag, Target.window());
    }

    /**
     * Takes a snapshot of the application under test and matches it with
     * the expected output.
     * @param matchTimeout The amount of time to retry matching (Milliseconds).
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException Thrown if a mismatch is detected and
     *                             immediate failure reports are enabled.
     */
    public void checkWindow(int matchTimeout, String tag) {
        check(tag, Target.window().timeout(matchTimeout));
    }


    /**
     * Takes multiple screenshots at once (given all <code>ICheckSettings</code> objects are on the same level).
     * @param checkSettings Multiple <code>ICheckSettings</code> object representing different regions in the viewport.
     */
    public void check(ICheckSettings... checkSettings) {
        if (getIsDisabled()) {
            logger.log(String.format("check(ICheckSettings[%d]): Ignored", checkSettings.length));
            return;
        }

        Boolean forceFullPageScreenshot = getConfigurationInstance().getForceFullPageScreenshot();
        boolean originalForceFPS = forceFullPageScreenshot == null ? false : forceFullPageScreenshot;

        if (checkSettings.length > 1) {
            getConfigurationInstance().setForceFullPageScreenshot(true);
        }

        logger.verbose(getConfigurationInstance().toString());

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
                ISeleniumCheckTarget seleniumCheckTarget =
                        (settings instanceof ISeleniumCheckTarget) ? (ISeleniumCheckTarget) settings : null;

                if (seleniumCheckTarget != null) {
                    seleniumCheckTarget.init(logger, driver);
                    WebElement targetElement = getTargetElement(seleniumCheckTarget);
                    if (targetElement == null && seleniumCheckTarget.getFrameChain().size() == 1) {
                        targetElement = EyesSeleniumUtils.findFrameByFrameCheckTarget(seleniumCheckTarget.getFrameChain().get(0), driver);
                    }

                    if (targetElement != null) {
                        getRegions.put(i, new SimpleRegionByElement(targetElement));
                    }
                }
            }
            //check(settings);
        }

        this.userDefinedSRE = EyesSeleniumUtils.getScrollRootElement(logger, driver, (IScrollRootElementContainer) checkSettings[0]);
        this.currentFramePositionProvider = null;
        setPositionProvider(createPositionProvider());

        matchRegions(getRegions, checkSettingsInternalDictionary, checkSettings);
        getConfigurationInstance().setForceFullPageScreenshot(originalForceFPS);
    }

    private void matchRegions(Dictionary<Integer, GetSimpleRegion> getRegions,
                              Dictionary<Integer, ICheckSettingsInternal> checkSettingsInternalDictionary,
                              ICheckSettings[] checkSettings) {

        if (getRegions.size() == 0) return;

        this.originalFC = driver.getFrameChain().clone();

        Region bBox = findBoundingBox(getRegions, checkSettings);

        ScaleProviderFactory scaleProviderFactory = updateScalingParams();
        FullPageCaptureAlgorithm algo = createFullPageCaptureAlgorithm(scaleProviderFactory, new RenderingInfo());

        Object activeElement = null;
        if (getConfigurationInstance().getHideCaret()) {
            try {
                activeElement = driver.executeScript("var activeElement = document.activeElement; activeElement && activeElement.blur(); return activeElement;");
            } catch (WebDriverException e) {
                logger.verbose("WARNING: Cannot hide caret! " + e.getMessage());
            }
        }

        Region region = Region.EMPTY;
        boolean hasFrames = driver.getFrameChain().size() > 0;
        if (hasFrames) {
            region = new Region(bBox.getLocation(), ((EyesRemoteWebElement) userDefinedSRE).getClientSize());
        } else {
            WebElement defaultRootElement = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
            if (!userDefinedSRE.equals(defaultRootElement)) {
                EyesRemoteWebElement eyesScrollRootElement;
                if (userDefinedSRE instanceof EyesRemoteWebElement) {
                    eyesScrollRootElement = (EyesRemoteWebElement) userDefinedSRE;
                } else {
                    eyesScrollRootElement = new EyesRemoteWebElement(logger, driver, userDefinedSRE);
                }

                Point location = eyesScrollRootElement.getLocation();
                SizeAndBorders sizeAndBorders = eyesScrollRootElement.getSizeAndBorders();

                region = new Region(
                        location.getX() + sizeAndBorders.getBorders().getLeft(),
                        location.getY() + sizeAndBorders.getBorders().getTop(),
                        sizeAndBorders.getSize().getWidth(),
                        sizeAndBorders.getSize().getHeight());
            }
        }
        region.intersect(effectiveViewport);

        BufferedImage screenshotImage = algo.getStitchedRegion(
                region, bBox, positionProviderHandler.get(), positionProviderHandler.get(), RectangleSize.EMPTY);

        debugScreenshotsProvider.save(screenshotImage, "original");
        EyesWebDriverScreenshot screenshot = new EyesWebDriverScreenshot(logger, driver, screenshotImage, EyesWebDriverScreenshot.ScreenshotType.VIEWPORT, Location.ZERO);

        for (int i = 0; i < checkSettings.length; ++i) {
            if (((Hashtable<Integer, GetSimpleRegion>) getRegions).containsKey(i)) {
                GetSimpleRegion simpleRegion = getRegions.get(i);
                ICheckSettingsInternal checkSettingsInternal = checkSettingsInternalDictionary.get(i);
                List<EyesScreenshot> subScreenshots = getSubScreenshots(hasFrames ? Region.EMPTY : bBox, screenshot, simpleRegion);
                matchRegion(checkSettingsInternal, subScreenshots);
            }
        }

        if (getConfigurationInstance().getHideCaret() && activeElement != null) {
            try {
                driver.executeScript("arguments[0].focus();", activeElement);
            } catch (WebDriverException e) {
                logger.verbose("WARNING: Could not return focus to active element! " + e.getMessage());
            }
        }

        ((EyesTargetLocator) driver.switchTo()).frames(this.originalFC);
    }

    private List<EyesScreenshot> getSubScreenshots(Region bBox, EyesWebDriverScreenshot screenshot, GetSimpleRegion getSimpleRegion) {
        List<EyesScreenshot> subScreenshots = new ArrayList<>();
        for (Region r : getSimpleRegion.getRegions(screenshot)) {
            logger.verbose("original sub-region: " + r);
            r = r.offset(-bBox.getLeft(), -bBox.getTop());
            //r = regionPositionCompensation.compensateRegionPosition(r, devicePixelRatio);
            //logger.verbose("sub-region after compensation: " + r);
            EyesScreenshot subScreenshot = screenshot.getSubScreenshotForRegion(r, false);
            subScreenshots.add(subScreenshot);
        }
        return subScreenshots;
    }

    private void matchRegion(ICheckSettingsInternal checkSettingsInternal, List<EyesScreenshot> subScreenshots) {

        String name = checkSettingsInternal.getName();
        String source = EyesDriverUtils.isMobileDevice(driver) ? null : driver.getCurrentUrl();
        for (EyesScreenshot subScreenshot : subScreenshots) {

            debugScreenshotsProvider.save(subScreenshot.getImage(), String.format("subscreenshot_%s", name));

            ImageMatchSettings ims = MatchWindowTask.createImageMatchSettings(checkSettingsInternal, subScreenshot, this);
            Location location = subScreenshot.getLocationInScreenshot(Location.ZERO, CoordinatesType.SCREENSHOT_AS_IS);
            AppOutput appOutput = new AppOutput(name, subScreenshot, null, null, location);
            MatchWindowData data = prepareForMatch(new ArrayList<Trigger>(), appOutput, name, false,
                    ims, this, null, source);
            MatchResult matchResult = performMatch(data);

            logger.verbose("matchResult.asExcepted: " + matchResult.getAsExpected());
        }
    }

    private Region findBoundingBox(Dictionary<Integer, GetSimpleRegion> getRegions, ICheckSettings[] checkSettings) {
        RectangleSize rectSize = getViewportSize();
        logger.verbose("rectSize: " + rectSize);
        EyesScreenshot screenshot = new EyesWebDriverScreenshot(logger, driver,
                new BufferedImage(rectSize.getWidth(), rectSize.getHeight(), BufferedImage.TYPE_INT_RGB));

        return findBoundingBox(getRegions, checkSettings, screenshot);
    }

    private Region findBoundingBox(Dictionary<Integer, GetSimpleRegion> getRegions, ICheckSettings[] checkSettings, EyesScreenshot screenshot) {
        Region bBox = null;
        for (int i = 0; i < checkSettings.length; ++i) {
            GetSimpleRegion simpleRegion = getRegions.get(i);
            if (simpleRegion != null) {
                List<Region> regions = simpleRegion.getRegions(screenshot);
                for (Region region : regions) {
                    if (bBox == null) {
                        bBox = new Region(region);
                    } else {
                        bBox = bBox.expandToContain(region);
                    }
                }
            }
        }
        Location offset = screenshot.getLocationInScreenshot(Location.ZERO, CoordinatesType.CONTEXT_AS_IS);
        return bBox.offset(offset);
    }

    private WebElement getTargetElement(ISeleniumCheckTarget seleniumCheckTarget) {
        assert seleniumCheckTarget != null;
        By targetSelector = seleniumCheckTarget.getTargetSelector();
        WebElement targetElement = seleniumCheckTarget.getTargetElement();
        if (targetElement == null && targetSelector != null) {
            targetElement = this.driver.findElement(targetSelector);
        } else if (targetElement != null && !(targetElement instanceof EyesRemoteWebElement)) {
            targetElement = new EyesRemoteWebElement(logger, driver, targetElement);
        }
        return targetElement;
    }

    /**
     * Check.
     * @param name          the name
     * @param checkSettings the check settings
     */
    public void check(String name, ICheckSettings checkSettings) {
        if (getIsDisabled()) {
            logger.log(String.format("check('%s', %s): Ignored", name, checkSettings));
            return;
        }
        ArgumentGuard.isValidState(isOpen, "Eyes not open");
        ArgumentGuard.notNull(checkSettings, "checkSettings");
        if (name != null) {
            checkSettings = checkSettings.withName(name);
        }
        this.check(checkSettings);
    }

    @Override
    public void setIsDisabled(boolean disabled) {
        super.setIsDisabled(disabled);
    }

    @Override
    public String tryCaptureDom() {
        String fullWindowDom = "";
        FrameChain fc = driver.getFrameChain().clone();
        try {
            Frame frame = fc.peek();
            WebElement scrollRootElement = null;
            if (frame != null) {
                scrollRootElement = frame.getScrollRootElement();
            }
            if (scrollRootElement == null) {
                scrollRootElement = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
            }
            PositionProvider positionProvider = ScrollPositionProviderFactory.getScrollPositionProvider(userAgent, logger, jsExecutor, scrollRootElement);

            DomCapture domCapture = new DomCapture(this);
            fullWindowDom = domCapture.getPageDom(positionProvider);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            ((EyesTargetLocator) driver.switchTo()).frames(fc);
        }
        return fullWindowDom;
    }

    @Override
    protected void setEffectiveViewportSize(RectangleSize size) {
        this.effectiveViewport = new Region(Location.ZERO, size);
        logger.verbose("setting effective viewport size to " + size);
    }

    public void check(ICheckSettings checkSettings) {
        if (getIsDisabled()) {
            logger.log(String.format("check(%s): Ignored", checkSettings));
            return;
        }

        try {
            ArgumentGuard.isValidState(isOpen, "Eyes not open");
            ArgumentGuard.notNull(checkSettings, "checkSettings");
            ArgumentGuard.notOfType(checkSettings, ISeleniumCheckTarget.class, "checkSettings");

            boolean isMobileDevice = EyesDriverUtils.isMobileDevice(driver);

            String source = null;
            if (!isMobileDevice) {
                source = driver.getCurrentUrl();
                logger.verbose("URL: " + source);
            }

            ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
            ISeleniumCheckTarget seleniumCheckTarget = (ISeleniumCheckTarget) checkSettings;

            CheckState state = new CheckState();
            seleniumCheckTarget.setState(state);
            Boolean forceFullPageScreenshot = getConfigurationInstance().getForceFullPageScreenshot();
            Boolean fully = checkSettingsInternal.getStitchContent();
            state.setStitchContent((fully != null && fully) || (forceFullPageScreenshot != null && forceFullPageScreenshot));

            // Ensure frame is not used as a region
            ((SeleniumCheckSettings) checkSettings).sanitizeSettings(logger, driver, state.isStitchContent());
            seleniumCheckTarget.init(logger, driver);

            Region targetRegion = checkSettingsInternal.getTargetRegion();

            logger.verbose("setting userDefinedSRE ...");
            this.userDefinedSRE = tryGetUserDefinedSREFromSREContainer(seleniumCheckTarget, driver);
            WebElement scrollRootElement = this.userDefinedSRE;
            if (scrollRootElement == null && !isMobileDevice) {
                scrollRootElement = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
            }

            logger.verbose("userDefinedSRE set to " + ((this.userDefinedSRE != null) ? userDefinedSRE.toString() : "null"));
            logger.verbose("scrollRootElement set to " + scrollRootElement);

            currentFramePositionProvider = null;
            super.positionProviderHandler.set(PositionProviderFactory.getPositionProvider(logger, getConfigurationInstance().getStitchMode(), jsExecutor, scrollRootElement, userAgent));
            CaretVisibilityProvider caretVisibilityProvider = new CaretVisibilityProvider(logger, driver, getConfigurationInstance());

            PageState pageState = new PageState(logger, driver, getConfigurationInstance().getStitchMode(), userAgent);
            pageState.preparePage(seleniumCheckTarget, getConfigurationInstance(), scrollRootElement);

            FrameChain frameChainAfterSwitchToTarget = driver.getFrameChain().clone();

            RectangleSize viewportSize = this.effectiveViewport.getSize();
            Region effectiveViewport = computeEffectiveViewport(frameChainAfterSwitchToTarget, viewportSize);
            state.setEffectiveViewport(effectiveViewport);
            // new Rectangle(Point.Empty, viewportSize_);
            WebElement targetElement = getTargetElementFromSettings(seleniumCheckTarget);

            caretVisibilityProvider.hideCaret();

            //////////////////////////////////////////////////////////////////

            // Cases:
            // Target.Region(x,y,w,h).Fully(true) - TODO - NOT TESTED!
            // Target.Region(x,y,w,h).Fully(false)
            // Target.Region(element).Fully(true)
            // Target.Region(element).Fully(false)
            // Target.Frame(frame).Fully(true)
            // Target.Frame(frame).Region(x,y,w,h).Fully(true)
            // Target.Frame(frame).Region(x,y,w,h).Fully(false) - TODO - NOT TESTED!
            // Target.Frame(frame).Region(element).Fully(true)
            // Target.Frame(frame).Region(element).Fully(false) - TODO - NOT TESTED!
            // Target.Window().Fully(true)
            // Target.Window().Fully(false)

            // Algorithm:
            // 1. Save current page state
            // 2. Switch to frame
            // 3. Maximize desired region or element visibility
            // 4. Capture desired region of element
            // 5. Go back to original frame
            // 6. Restore page state

            if (targetElement != null) {
                if (isMobileDevice) {
                    checkNativeElement(checkSettingsInternal, targetElement);
                } else if (state.isStitchContent()) {
                    checkFullElement(checkSettingsInternal, targetElement, targetRegion, state, source);
                } else {
                    // TODO Verify: if element is outside the viewport, we should still capture entire (outer) bounds
                    checkElement(checkSettingsInternal, targetElement, targetRegion, state, source);
                }
            } else if (targetRegion != null) {
                // Coordinates should always be treated as "Fully" in case they get out of the viewport.
                boolean originalFully = state.isStitchContent();
                state.setStitchContent(true);
                checkFullRegion(checkSettingsInternal, targetRegion, state, source);
                state.setStitchContent(originalFully);
            } else if (!isMobileDevice && seleniumCheckTarget.getFrameChain().size() > 0) {
                if (state.isStitchContent()) {
                    checkFullFrame(checkSettingsInternal, state, source);
                } else {
                    logger.log("Target.Frame(frame).Fully(false)");
                    logger.log("WARNING: This shouldn't have been called, as it is covered by `CheckElement_(...)`");
                }
            } else {
                if (state.isStitchContent()) {
                    checkFullWindow(checkSettingsInternal, state, scrollRootElement, source);
                } else {
                    checkWindow(checkSettingsInternal, state, scrollRootElement, source);
                }
            }

            caretVisibilityProvider.restoreCaret();

            pageState.restorePageState();
        } catch (Exception ex) {
            GeneralUtils.logExceptionStackTrace(logger, ex);
            throw ex;
        }
    }

    private void checkFullFrame(ICheckSettingsInternal checkSettingsInternal, CheckState state, String source) {
        logger.verbose("Target.Frame(frame).Fully(true)");
        FrameChain currentFrameChain = driver.getFrameChain().clone();
        Location visualOffset = getFrameChainOffset(currentFrameChain);
        Frame currentFrame = currentFrameChain.peek();
        state.setEffectiveViewport(state.getEffectiveViewport().getIntersected(new Region(visualOffset, currentFrame.getInnerSize())));

        WebElement currentFrameSRE = getCurrentFrameScrollRootElement();
        RectangleSize currentSREScrollSize = EyesRemoteWebElement.getScrollSize(currentFrameSRE, driver, logger);
        state.setFullRegion(new Region(state.getEffectiveViewport().getLocation(), currentSREScrollSize));
        state.setOriginalLocation(Location.ZERO);
        checkWindowBase(null, checkSettingsInternal, source);
    }

    private void checkFullRegion(ICheckSettingsInternal checkSettingsInternal, Region targetRegion, CheckState state, String source) {
        if (((ISeleniumCheckTarget) checkSettingsInternal).getFrameChain().size() > 0) {
            logger.verbose("Target.Frame(frame).Region(x,y,w,h).Fully(true)");
        } else {
            logger.verbose("Target.Region(x,y,w,h).Fully(true)");
        }
        FrameChain currentFrameChain = driver.getFrameChain().clone();
        Frame currentFrame = currentFrameChain.peek();
        if (currentFrame != null) {
            Region currentFrameBoundsWithoutBorders = removeBorders(currentFrame.getBounds(), currentFrame.getBorderWidths());
            state.setEffectiveViewport(state.getEffectiveViewport().getIntersected(currentFrameBoundsWithoutBorders));
            WebElement currentFrameSRE = getCurrentFrameScrollRootElement();
            RectangleSize currentSREScrollSize = EyesRemoteWebElement.getScrollSize(currentFrameSRE, driver, logger);
            state.setFullRegion(new Region(state.getEffectiveViewport().getLocation(), currentSREScrollSize));
        } else {
            Location visualOffset = getFrameChainOffset(currentFrameChain);
            targetRegion = targetRegion.offset(visualOffset);
        }
        checkWindowBase(targetRegion, checkSettingsInternal, source);
    }

    private static Region removeBorders(Region region, Borders borders) {
        return new Region(
                region.getLeft() + borders.getLeft(),
                region.getTop() + borders.getTop(),
                region.getWidth() - borders.getHorizontal(),
                region.getHeight() - borders.getVertical(),
                region.getCoordinatesType());
    }

    private void checkElement(ICheckSettingsInternal checkSettingsInternal, WebElement targetElement,
                              Region targetRegion, CheckState state, String source) {
        List<FrameLocator> frameLocators = ((ISeleniumCheckTarget) checkSettingsInternal).getFrameChain();
        if (frameLocators.size() > 0) {
            logger.verbose("Target.Frame(frame).Region(element).Fully(false)");
        } else {
            logger.verbose("Target.Region(element).Fully(false)");
        }
        FrameChain currentFrameChain = driver.getFrameChain().clone();
        Region bounds = EyesRemoteWebElement.getClientBounds(targetElement, driver, logger);

        WebElement defaultSRE = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
        PositionProvider defaultSREPositionProvider = getPositionProviderForScrollRootElement(defaultSRE);
        state.setOriginalLocation(bounds.offset(defaultSREPositionProvider.getCurrentPosition()).getLocation());

        WebElement currentFrameSRE = getCurrentFrameScrollRootElement();
        PositionProvider currentFramePositionProvider = getPositionProviderForScrollRootElement(currentFrameSRE);
        Location currentFramePosition = currentFramePositionProvider.getCurrentPosition();
        Location visualOffset = getFrameChainOffset(currentFrameChain);
        bounds = bounds.offset(visualOffset);
        currentFramePositionProvider.setPosition(bounds.offset(currentFramePosition).getLocation());
        Region actualElementBounds = EyesRemoteWebElement.getClientBounds(targetElement, driver, logger);
        actualElementBounds = actualElementBounds.offset(visualOffset);
        Location actualFramePosition = new Location(bounds.getLeft() - actualElementBounds.getLeft(),
                bounds.getTop() - actualElementBounds.getTop());
        bounds = bounds.offset(-actualFramePosition.getX(), -actualFramePosition.getY());

        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();
        FrameChain fcClone = currentFrameChain.clone();

        while (!state.getEffectiveViewport().isIntersected(bounds) && fcClone.size() > 0) {
            fcClone.pop();
            switchTo.parentFrame();
            currentFrameSRE = getCurrentFrameScrollRootElement();
            currentFramePositionProvider = getPositionProviderForScrollRootElement(currentFrameSRE);
            currentFramePosition = currentFramePositionProvider.getCurrentPosition();
            bounds = bounds.offset(currentFramePosition);
            actualFramePosition = currentFramePositionProvider.setPosition(bounds.getLocation());
            bounds = bounds.offset(-actualFramePosition.getX(), -actualFramePosition.getY());
        }

        switchTo.frames(currentFrameChain);

        Region crop = computeCropRectangle(bounds, targetRegion);
        if (crop == null) {
            crop = bounds;
        }
        checkWindowBase(crop, checkSettingsInternal, source);
    }

    private void checkFullElement(ICheckSettingsInternal checkSettingsInternal, WebElement targetElement,
                                  Region targetRegion, CheckState state, String source) {
        if (((ISeleniumCheckTarget) checkSettingsInternal).getFrameChain().size() > 0) {
            logger.verbose("Target.Frame(frame)...Region(element).Fully(true)");
        } else {
            logger.verbose("Target.Region(element).Fully(true)");
        }

        // Hide scrollbars
        String originalOverflow = EyesDriverUtils.setOverflow(driver, "hidden", targetElement);

        // Get element's scroll size and bounds
        RectangleSize scrollSize = EyesRemoteWebElement.getScrollSize(targetElement, driver, logger);
        Region elementBounds = EyesRemoteWebElement.getClientBounds(targetElement, driver, logger);
        Region elementInnerBounds = EyesRemoteWebElement.getClientBoundsWithoutBorders(targetElement, driver, logger);

        boolean isScrollableElement = scrollSize.getHeight() > elementInnerBounds.getHeight() || scrollSize.getWidth() > elementInnerBounds.getWidth();

        if (isScrollableElement) {
            elementBounds = elementInnerBounds;
        }
        initPositionProvidersForCheckElement(isScrollableElement, targetElement, state);

        Location location = SeleniumScrollPositionProvider.getCurrentPosition(driver, EyesSeleniumUtils.getDefaultRootElement(logger, driver));
        state.setOriginalLocation(elementBounds.offset(location).getLocation());
        Location originalElementLocation = elementBounds.getLocation();

        String positionStyle = ((EyesRemoteWebElement) targetElement).getComputedStyle("position");
        if (!positionStyle.equalsIgnoreCase("fixed")) {
            if (getConfiguration().getStitchMode().equals(StitchMode.CSS)) {
                bringRegionToViewCss(elementBounds, state.getEffectiveViewport().getLocation());
                if (isScrollableElement) {
                    elementBounds = EyesRemoteWebElement.getClientBoundsWithoutBorders(targetElement, driver, logger);
                } else {
                    elementBounds = EyesRemoteWebElement.getClientBounds(targetElement, driver, logger);
                }
                state.setEffectiveViewport(computeEffectiveViewport(driver.getFrameChain().clone(), effectiveViewport.getSize()));
            } else {
                elementBounds = bringRegionToView(elementBounds, state.getEffectiveViewport().getLocation());
            }
        }

        Region fullElementBounds = new Region(elementBounds);
        fullElementBounds.setWidth(Math.max(fullElementBounds.getWidth(), scrollSize.getWidth()));
        fullElementBounds.setHeight(Math.max(fullElementBounds.getHeight(), scrollSize.getHeight()));
        FrameChain currentFrameChain = driver.getFrameChain().clone();
        Location screenshotOffset = getFrameChainOffset(currentFrameChain);
        logger.verbose("screenshotOffset: " + screenshotOffset);
        fullElementBounds = fullElementBounds.offset(screenshotOffset);

        state.setFullRegion(fullElementBounds);

        // Now we have a 2-step part:
        // 1. Intersect the SRE and the effective viewport.
        if (getConfigurationInstance().getStitchMode() == StitchMode.CSS && userDefinedSRE != null) {
            Region viewportInScreenshot = state.getEffectiveViewport();
            int elementTranslationWidth = originalElementLocation.getX() - elementBounds.getLeft();
            int elementTranslationHeight = originalElementLocation.getY() - elementBounds.getTop();
            state.setEffectiveViewport(new Region(
                    viewportInScreenshot.getLeft(),
                    viewportInScreenshot.getTop(),
                    viewportInScreenshot.getWidth() - elementTranslationWidth,
                    viewportInScreenshot.getHeight() - elementTranslationHeight));
        }

        // In CSS stitch mode, if the element is not scrollable but the SRE is (i.e., "Modal" case),
        // we move the SRE to (0,0) but then we translate the element itself to get the full contents.
        // However, in Scroll stitch mode, we scroll the SRE itself to the get full contents, and it
        // already has an offset caused by "BringRegionToView", so we should consider this offset.
        if (getConfigurationInstance().getStitchMode() == StitchMode.SCROLL && !isScrollableElement) {
            EyesRemoteWebElement sre = (EyesRemoteWebElement) getCurrentFrameScrollRootElement();
            state.setStitchOffset(new RectangleSize(sre.getScrollLeft(), sre.getScrollTop()));
        }

        // 2. Intersect the element and the effective viewport
        Region elementBoundsInScreenshotCoordinates = elementBounds.offset(screenshotOffset);
        Region intersectedViewport = state.getEffectiveViewport().getIntersected(elementBoundsInScreenshotCoordinates);
        state.setEffectiveViewport(intersectedViewport);

        Region crop = computeCropRectangle(fullElementBounds, targetRegion);
        checkWindowBase(crop, checkSettingsInternal, source);

        EyesDriverUtils.setOverflow(driver, originalOverflow, targetElement);
    }

    private void checkNativeElement(ICheckSettingsInternal checkSettingsInternal, WebElement targetElement) {
        final Rectangle rect = targetElement.getRect();
        Region region = checkSettingsInternal.getTargetRegion();
        if (region == null) {
            region = new Region(rect.x, rect.y, rect.width, rect.height);
        }

        checkWindowBase(region, checkSettingsInternal, null);
    }

    private void checkWindow(ICheckSettingsInternal checkSettingsInternal, CheckState state, WebElement scrollRootElement, String source) {
        logger.verbose("Target.Window()");
        Location location = Location.ZERO;
        if (!EyesDriverUtils.isMobileDevice(driver)) {
            location = SeleniumScrollPositionProvider.getCurrentPosition(driver, scrollRootElement);
        }
        state.setOriginalLocation(location);
        checkWindowBase(null, checkSettingsInternal, source);
    }

    private void checkFullWindow(ICheckSettingsInternal checkSettingsInternal, CheckState state, WebElement scrollRootElement, String source) {
        logger.verbose("Target.Window().Fully(true)");
        initPositionProvidersForCheckWindow(state, scrollRootElement);
        state.setOriginalLocation(Location.ZERO);
        checkWindowBase(null, checkSettingsInternal, source);
    }

    private void initPositionProvidersForCheckWindow(CheckState state, WebElement scrollRootElement) {
        if (getConfigurationInstance().getStitchMode() == StitchMode.SCROLL) {
            state.setStitchPositionProvider(new SeleniumScrollPositionProvider(logger, driver, scrollRootElement));
        } else {
            // Stitch mode == CSS
            if (userDefinedSRE != null) {
                state.setStitchPositionProvider(new ElementPositionProvider(logger, driver, userDefinedSRE));
            } else {
                state.setStitchPositionProvider(new CssTranslatePositionProvider(logger, driver, scrollRootElement));
                state.setOriginPositionProvider(new SeleniumScrollPositionProvider(logger, driver, scrollRootElement));
            }
        }
    }

    private static Region computeCropRectangle(Region fullRect, Region cropRect) {
        if (cropRect == null) {
            return null;
        }
        Region crop = new Region(fullRect);
        Location cropLocation = crop.getLocation();
        Region cropRectClone = cropRect.offset(cropLocation);
        crop.intersect(cropRectClone);
        return crop;
    }

    private Location getFrameChainOffset(FrameChain frameChain) {
        Location offset = Location.ZERO;
        for (Frame frame : frameChain) {
            offset = offset.offset(frame.getLocation());
        }
        return offset;
    }

    private Region bringRegionToViewCss(Region bounds, Location viewportLocation) {
        FrameChain frames = driver.getFrameChain().clone();
        if (frames.size() <= 0) {
            return bringRegionToView(bounds, viewportLocation);
        }

        Location currentFrameOffset = frames.getCurrentFrameOffset();
        EyesTargetLocator locator = (EyesTargetLocator) driver.switchTo();
        locator.defaultContent();
        try {
            EyesRemoteWebElement currentFrameSRE = (EyesRemoteWebElement) getCurrentFrameScrollRootElement();
            PositionProvider currentFramePositionProvider = PositionProviderFactory.getPositionProvider(
                    logger, StitchMode.CSS, jsExecutor, currentFrameSRE, userAgent);
            Location currentFramePosition = currentFramePositionProvider.getCurrentPosition();
            Location boundsPosition = bounds.getLocation();
            Location newFramePosition = boundsPosition.offset(-viewportLocation.getX(), -viewportLocation.getY());
            newFramePosition = newFramePosition.offset(currentFrameOffset);
            Location currentCssLocation = currentFrameSRE.getCurrentCssStitchingLocation();
            if (currentCssLocation != null) {
                newFramePosition = newFramePosition.offset(currentCssLocation);
            }
            Location actualFramePosition = currentFramePositionProvider.setPosition(newFramePosition);
            bounds = bounds.offset(-actualFramePosition.getX(), -actualFramePosition.getY());
            bounds = bounds.offset(currentFramePosition);
            return bounds;
        } finally {
            locator.frames(frames);
        }
    }

    private Region bringRegionToView(Region bounds, Location viewportLocation) {
        WebElement currentFrameSRE = getCurrentFrameScrollRootElement();
        StitchMode stitchMode = getConfigurationInstance().getStitchMode();
        PositionProvider currentFramePositionProvider = PositionProviderFactory.getPositionProvider(
                logger, stitchMode, jsExecutor, currentFrameSRE, userAgent);
        Location currentFramePosition = currentFramePositionProvider.getCurrentPosition();
        Location boundsPosition = bounds.getLocation();
        Location newFramePosition = boundsPosition.offset(-viewportLocation.getX(), -viewportLocation.getY());
        if (stitchMode.equals(StitchMode.SCROLL)) {
            newFramePosition = newFramePosition.offset(currentFramePosition);
        }
        Location actualFramePosition = currentFramePositionProvider.setPosition(newFramePosition);
        bounds = bounds.offset(-actualFramePosition.getX(), -actualFramePosition.getY());
        bounds = bounds.offset(currentFramePosition);
        return bounds;
    }

    private void initPositionProvidersForCheckElement(boolean isScrollableElement, WebElement targetElement, CheckState state) {
        // User might still call "fully" on a non-scrollable element, adjust the position provider accordingly.
        if (isScrollableElement) {
            state.setStitchPositionProvider(new ElementPositionProvider(logger, driver, targetElement));
        } else { // Not a scrollable element but an element enclosed within a scroll-root-element
            WebElement scrollRootElement = getCurrentFrameScrollRootElement();
            if (getConfigurationInstance().getStitchMode() == StitchMode.CSS) {
                state.setStitchPositionProvider(new CssTranslatePositionProvider(logger, driver, targetElement));
                state.setOriginPositionProvider(new NullPositionProvider());
            } else {
                state.setStitchPositionProvider(new SeleniumScrollPositionProvider(logger, driver, scrollRootElement));
            }
        }
        logger.verbose("isScrollableElement ?" + isScrollableElement);
    }

    public static WebElement tryGetUserDefinedSREFromSREContainer(IScrollRootElementContainer scrollRootElementContainer, EyesSeleniumDriver driver) {
        WebElement scrollRootElement = scrollRootElementContainer.getScrollRootElement();
        if (scrollRootElement == null) {
            By scrollRootSelector = scrollRootElementContainer.getScrollRootSelector();
            if (scrollRootSelector != null) {
                scrollRootElement = driver.findElement(scrollRootSelector);
            }
        }
        return scrollRootElement;
    }

    public static WebElement getScrollRootElementFromSREContainer(Logger logger, IScrollRootElementContainer scrollRootElementContainer, EyesSeleniumDriver driver) {
        WebElement scrollRootElement = tryGetUserDefinedSREFromSREContainer(scrollRootElementContainer, driver);
        if (scrollRootElement == null) {
            scrollRootElement = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
        }
        return scrollRootElement;
    }

    private PositionProvider getPositionProviderForScrollRootElement(WebElement scrollRootElement) {
        return getPositionProviderForScrollRootElement(logger, driver, getConfigurationInstance().getStitchMode(), userAgent, scrollRootElement);
    }

    public static PositionProvider getPositionProviderForScrollRootElement(Logger logger, EyesSeleniumDriver driver, StitchMode stitchMode, UserAgent ua, WebElement scrollRootElement) {
        PositionProvider positionProvider = PositionProviderFactory.tryGetPositionProviderForElement(scrollRootElement);
        if (positionProvider == null) {
            logger.verbose("creating a new position provider.");
            WebElement defaultSRE = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
            if (scrollRootElement.equals(defaultSRE)) {
                positionProvider = PositionProviderFactory.getPositionProvider(logger, stitchMode, driver, scrollRootElement, ua);
            } else {
                positionProvider = new ElementPositionProvider(logger, driver, scrollRootElement);
            }
        }
        logger.verbose("position provider: " + positionProvider);
        return positionProvider;
    }

    private Region computeEffectiveViewport(FrameChain frameChain, RectangleSize initialSize) {
        Region viewport = new Region(Location.ZERO, initialSize);
        if (userDefinedSRE != null) {
            Region sreInnerBounds = EyesRemoteWebElement.getClientBoundsWithoutBorders(userDefinedSRE, driver, logger);
            viewport.intersect(sreInnerBounds);
        }
        Location offset = Location.ZERO;
        for (Frame frame : frameChain) {
            offset = offset.offset(frame.getLocation());
            Region frameViewport = new Region(offset, frame.getInnerSize());
            viewport.intersect(frameViewport);
            Region frameSreInnerBounds = frame.getScrollRootElementInnerBounds();
            if (frameSreInnerBounds.isSizeEmpty()) {
                continue;
            }
            frameSreInnerBounds = frameSreInnerBounds.offset(offset);
            viewport.intersect(frameSreInnerBounds);
        }
        return viewport;
    }

    private WebElement getTargetElementFromSettings(ISeleniumCheckTarget seleniumCheckTarget) {
        By targetSelector = seleniumCheckTarget.getTargetSelector();
        WebElement targetElement = seleniumCheckTarget.getTargetElement();
        if (targetElement == null && targetSelector != null) {
            targetElement = driver.findElement(targetSelector);
        } else if (targetElement != null && !(targetElement instanceof EyesRemoteWebElement)) {
            targetElement = new EyesRemoteWebElement(logger, driver, targetElement);
        }
        return targetElement;
    }

    /**
     * Updates the state of scaling related parameters.
     * @return the scale provider factory
     */
    protected ScaleProviderFactory updateScalingParams() {
        // Update the scaling params only if we haven't done so yet, and the user hasn't set anything else manually.
        if (scaleProviderHandler.get() instanceof NullScaleProvider) {
            ScaleProviderFactory factory;
            logger.verbose("Trying to extract device pixel ratio...");
            try {
                devicePixelRatio = driver.getDevicePixelRatio();
            } catch (Exception e) {
                logger.verbose(
                        "Failed to extract device pixel ratio! Using default.");
                devicePixelRatio = DEFAULT_DEVICE_PIXEL_RATIO;
            }

            logger.verbose(String.format("Device pixel ratio: %f", devicePixelRatio));
            if (!EyesDriverUtils.isMobileDevice(driver)) {
                logger.verbose("Setting web scale provider...");
                factory = getScaleProviderFactory();
            } else {
                logger.verbose("Setting native app scale provider...");
                factory = new FixedScaleProviderFactory(logger, 1 / devicePixelRatio, scaleProviderHandler);
            }

            logger.verbose("Done!");
            return factory;
        }
        // If we already have a scale provider set, we'll just use it, and pass a mock as provider handler.
        PropertyHandler<ScaleProvider> nullProvider = new SimplePropertyHandler<>();
        return new ScaleProviderIdentityFactory(logger, scaleProviderHandler.get(), nullProvider);
    }

    private ScaleProviderFactory getScaleProviderFactory() {
        WebElement element = EyesSeleniumUtils.getDefaultRootElement(logger, driver);
        RectangleSize entireSize = EyesDriverUtils.getEntireElementSize(logger, jsExecutor, element);
        return new ContextBasedScaleProviderFactory(logger, entireSize, getConfigurationInstance().getViewportSize(),
                devicePixelRatio, false, scaleProviderHandler);
    }

    /**
     * Gets current frame scroll root element.
     * @return the current frame scroll root element
     */
    public WebElement getCurrentFrameScrollRootElement() {
        return EyesSeleniumUtils.getCurrentFrameScrollRootElement(logger, driver, userDefinedSRE);
    }

    /**
     * See {@link #checkFrame(String, int, String)}.
     * {@code tag} defaults to {@code null}. Default match timeout is used.
     * @param frameNameOrId the frame name or id
     */
    public void checkFrame(String frameNameOrId) {
        check(null, Target.frame(frameNameOrId));
    }

    /**
     * See {@link #checkFrame(String, int, String)}.
     * Default match timeout is used.
     * @param frameNameOrId the frame name or id
     * @param tag           the tag
     */
    public void checkFrame(String frameNameOrId, String tag) {
        check(tag, Target.frame(frameNameOrId).fully());
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     * @param frameNameOrId The name or id of the frame to check. (The same                      name/id as would be used in a call to                      driver.switchTo().frame()).
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the match.
     */
    public void checkFrame(String frameNameOrId, int matchTimeout, String tag) {
        check(tag, Target.frame(frameNameOrId).timeout(matchTimeout).fully());
    }

    /**
     * See {@link #checkFrame(int, int, String)}.
     * {@code tag} defaults to {@code null}. Default match timeout is used.
     * @param frameIndex the frame index
     */
    public void checkFrame(int frameIndex) {
        checkFrame(frameIndex, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    /**
     * See {@link #checkFrame(int, int, String)}.
     * Default match timeout is used.
     * @param frameIndex the frame index
     * @param tag        the tag
     */
    public void checkFrame(int frameIndex, String tag) {
        checkFrame(frameIndex, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     * @param frameIndex   The index of the frame to switch to. (The same index                     as would be used in a call to                     driver.switchTo().frame()).
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the match.
     */
    public void checkFrame(int frameIndex, int matchTimeout, String tag) {
        if (getIsDisabled()) {
            logger.log(String.format("CheckFrame(%d, %d, '%s'): Ignored", frameIndex, matchTimeout, tag));
            return;
        }

        ArgumentGuard.greaterThanOrEqualToZero(frameIndex, "frameIndex");

        logger.log(String.format("CheckFrame(%d, %d, '%s')", frameIndex, matchTimeout, tag));

        check(tag, Target.frame(frameIndex).timeout(matchTimeout).fully());
    }

    /**
     * See {@link #checkFrame(WebElement, int, String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     * @param frameReference the frame reference
     */
    public void checkFrame(WebElement frameReference) {
        checkFrame(frameReference, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    /**
     * See {@link #checkFrame(WebElement, int, String)}.
     * Default match timeout is used.
     * @param frameReference the frame reference
     * @param tag            the tag
     */
    public void checkFrame(WebElement frameReference, String tag) {
        checkFrame(frameReference, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame() ).
     * @param matchTimeout   The amount of time to retry matching (milliseconds).
     * @param tag            An optional tag to be associated with the match.
     */
    public void checkFrame(WebElement frameReference, int matchTimeout, String tag) {
        check(tag, Target.frame(frameReference).timeout(matchTimeout));
    }

    /**
     * Matches the frame given by the frames path, by switching into the frame
     * and using stitching to get an image of the frame.
     * @param framePath    The path to the frame to check. This is a list of                     frame names/IDs (where each frame is nested in the                     previous frame).
     * @param matchTimeout The amount of time to retry matching (milliseconds).
     * @param tag          An optional tag to be associated with the match.
     */
    public void checkFrame(String[] framePath, int matchTimeout, String tag) {

        SeleniumCheckSettings settings = Target.frame(framePath[0]);
        for (int i = 1; i < framePath.length; i++) {
            settings.frame(framePath[i]);
        }
        check(tag, settings.timeout(matchTimeout).fully());
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     * @param framePath     The path to the frame to check. This is a list of
     *                      frame names/IDs (where each frame is nested in the previous frame).
     * @param selector      A Selector specifying the region to check.
     * @param matchTimeout  The amount of time to retry matching (milliseconds).
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether or not to stitch the internal content of the
     *                      region (i.e., perform {@link #checkElement(By, int, String)} on the region.
     */
    public void checkRegionInFrame(String[] framePath, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {

        SeleniumCheckSettings settings = Target.frame(framePath[0]);
        for (int i = 1; i < framePath.length; i++) {
            settings = settings.frame(framePath[i]);
        }
        check(tag, settings.region(selector).timeout(matchTimeout).fully(stitchContent));
    }

    /**
     * Takes a snapshot of the application under test and matches a specific
     * element with the expected region output.
     * @param element      The element to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException if a mismatch is detected and immediate failure reports are enabled
     */
    public void checkElement(WebElement element, int matchTimeout, String tag) {
        check(tag, Target.region(element).timeout(matchTimeout).fully());
    }

    /**
     * See {@link #checkElement(By, String)}.
     * {@code tag} defaults to {@code null}.
     * @param selector the selector
     */
    public void checkElement(By selector) {
        check(null, Target.region(selector).fully());
    }

    /**
     * See {@link #checkElement(By, int, String)}.
     * Default match timeout is used.
     * @param selector the selector
     * @param tag      the tag
     */
    public void checkElement(By selector, String tag) {
        check(tag, Target.region(selector).fully());
    }

    /**
     * Takes a snapshot of the application under test and matches an element
     * specified by the given selector with the expected region output.
     * @param selector     Selects the element to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and                             immediate failure reports are enabled
     */
    public void checkElement(By selector, int matchTimeout, String tag) {
        check(tag, Target.region(selector).timeout(matchTimeout).fully());
    }

    /**
     * Adds a mouse trigger.
     * @param action  Mouse action.
     * @param control The control on which the trigger is activated (context relative coordinates).
     * @param cursor  The cursor's position relative to the control.
     */
    public void addMouseTrigger(MouseAction action, Region control, Location cursor) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring %s (disabled)", action));
            return;
        }

        // Triggers are actually performed on the previous window.
        if (lastScreenshot == null) {
            logger.verbose(String.format("Ignoring %s (no screenshot)", action));
            return;
        }

        if (!FrameChain.isSameFrameChain(driver.getFrameChain(),
                ((EyesWebDriverScreenshot) lastScreenshot).getFrameChain())) {
            logger.verbose(String.format("Ignoring %s (different frame)", action));
            return;
        }

        addMouseTriggerBase(action, control, cursor);
    }

    /**
     * Adds a mouse trigger.
     * @param action  Mouse action.
     * @param element The WebElement on which the click was called.
     */
    public void addMouseTrigger(MouseAction action, WebElement element) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring %s (disabled)", action));
            return;
        }

        ArgumentGuard.notNull(element, "element");

        Point pl = element.getLocation();
        Dimension ds = element.getSize();

        Region elementRegion = new Region(pl.getX(), pl.getY(), ds.getWidth(),
                ds.getHeight());

        // Triggers are actually performed on the previous window.
        if (lastScreenshot == null) {
            logger.verbose(String.format("Ignoring %s (no screenshot)", action));
            return;
        }

        if (!FrameChain.isSameFrameChain(driver.getFrameChain(),
                ((EyesWebDriverScreenshot) lastScreenshot).getFrameChain())) {
            logger.verbose(String.format("Ignoring %s (different frame)", action));
            return;
        }

        // Get the element region which is intersected with the screenshot,
        // so we can calculate the correct cursor position.
        elementRegion = lastScreenshot.getIntersectedRegion
                (elementRegion, CoordinatesType.CONTEXT_RELATIVE);

        addMouseTriggerBase(action, elementRegion,
                elementRegion.getMiddleOffset());
    }

    /**
     * Adds a keyboard trigger.
     * @param control The control's context-relative region.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(Region control, String text) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring '%s' (disabled)", text));
            return;
        }

        if (lastScreenshot == null) {
            logger.verbose(String.format("Ignoring '%s' (no screenshot)", text));
            return;
        }

        if (!FrameChain.isSameFrameChain(driver.getFrameChain(),
                ((EyesWebDriverScreenshot) lastScreenshot).getFrameChain())) {
            logger.verbose(String.format("Ignoring '%s' (different frame)", text));
            return;
        }

        addTextTriggerBase(control, text);
    }

    /**
     * Adds a keyboard trigger.
     * @param element The element for which we sent keys.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(WebElement element, String text) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring '%s' (disabled)", text));
            return;
        }

        ArgumentGuard.notNull(element, "element");

        Point pl = element.getLocation();
        Dimension ds = element.getSize();

        Region elementRegion = new Region(pl.getX(), pl.getY(), ds.getWidth(), ds.getHeight());

        addTextTrigger(elementRegion, text);
    }

    @Override
    public MatchWindowData prepareForMatch(CheckTask checkTask) {
        return null;
    }

    @Override
    public CheckTask issueCheck(ICheckSettings checkSettings, List<VisualGridSelector[]> regionSelectors, String source) {
        return null;
    }

    @Override
    public void checkCompleted(CheckTask checkTask, MatchResult matchResult) {

    }

    /**
     * Use this method only if you made a previous call to {@link #open
     * (WebDriver, String, String)} or one of its variants.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public RectangleSize getViewportSize() {
        if (!isOpen && driver == null) {
            logger.log("Called getViewportSize before calling open");
            return null;
        }

        RectangleSize vpSize;
        if (!EyesDriverUtils.isMobileDevice(driver)) {
            if (imageProvider instanceof MobileScreenshotImageProvider) {
                BufferedImage image = imageProvider.getImage();
                vpSize = new RectangleSize((int) Math.round(image.getWidth() / devicePixelRatio), (int) Math.round(image.getHeight() / devicePixelRatio));
            } else {
                vpSize = EyesDriverUtils.getViewportSize(driver);
            }
        } else {
            vpSize = getViewportSize(driver);
        }
        return vpSize;
    }

    /**
     * @param driver The driver to use for getting the viewport.
     * @return The viewport size of the current context.
     */
    static RectangleSize getViewportSize(WebDriver driver) {
        ArgumentGuard.notNull(driver, "driver");
        return EyesDriverUtils.getViewportSizeOrDisplaySize(new Logger(), driver);
    }

    /**
     * Use this method only if you made a previous call to {@link #open
     * (WebDriver, String, String)} or one of its variants.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected Configuration setViewportSize(RectangleSize size) {
        if (!EyesDriverUtils.isMobileDevice(driver)) {
            FrameChain originalFrame = driver.getFrameChain();
            driver.switchTo().defaultContent();

            try {
                EyesDriverUtils.setViewportSize(logger, driver, size);
                effectiveViewport = new Region(Location.ZERO, size);
            } catch (EyesException e1) {
                // Just in case the user catches this error
                ((EyesTargetLocator) driver.switchTo()).frames(originalFrame);
                GeneralUtils.logExceptionStackTrace(logger, e1);
                throw new TestFailedException("Failed to set the viewport size", e1);
            }
            ((EyesTargetLocator) driver.switchTo()).frames(originalFrame);
        }

        getConfigurationInstance().setViewportSize(new RectangleSize(size.getWidth(), size.getHeight()));
        return getConfigurationInstance();
    }

    @Override
    protected EyesScreenshot getScreenshot(Region targetRegion, ICheckSettingsInternal checkSettingsInternal) {
        if (!EyesDriverUtils.isMobileDevice(driver)) {
            setElementAttribute(driver.findElement(By.tagName("html")), ACTIVE_FRAME_ATTRIBUTE, "true");
        }

        ScaleProviderFactory scaleProviderFactory = updateScalingParams();

        EyesWebDriverScreenshot result;
        CheckState state = ((ISeleniumCheckTarget) checkSettingsInternal).getState();
        WebElement targetElement = state.getTargetElementInternal();
        boolean stitchContent = state.isStitchContent();

        Region effectiveViewport = state.getEffectiveViewport();
        Region fullRegion = state.getFullRegion();
        if (effectiveViewport.contains(fullRegion) && !fullRegion.isEmpty()) {
            logger.verbose("effectiveViewport: " + effectiveViewport + " ; fullRegion: " + fullRegion);
            result = getViewportScreenshot(scaleProviderFactory);
            result = result.getSubScreenshot(fullRegion, true);
        } else if (targetElement != null || stitchContent) {
            result = getFrameOrElementScreenshot(scaleProviderFactory, state);
        } else {
            result = getViewportScreenshot(scaleProviderFactory);
        }

        if (targetRegion != null && !targetRegion.isSizeEmpty()) {
            result = result.getSubScreenshot(targetRegion, false);
            debugScreenshotsProvider.save(result.getImage(), "SUB_SCREENSHOT");
        }

        if (!EyesDriverUtils.isMobileDevice(driver)) {
            result.setDomUrl(tryCaptureAndPostDom(checkSettingsInternal));
        }

        result.setOriginalLocation(state.getOriginalLocation());
        return result;
    }

    private EyesWebDriverScreenshot getViewportScreenshot(ScaleProviderFactory scaleProviderFactory) {
        try {
            Thread.sleep(getWaitBeforeScreenshots());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return getScaledAndCroppedScreenshot(scaleProviderFactory);
    }

    boolean shouldTakeFullPageScreenshot(ICheckSettingsInternal checkSettingsInternal) {
        Boolean isFully = checkSettingsInternal.getStitchContent();
        if (isFully != null) {
            return isFully;
        }

        Boolean isForceFullPage = getConfigurationInstance().getForceFullPageScreenshot();
        if (isForceFullPage == null) {
            return false;
        }

        return isForceFullPage;
    }

    private EyesWebDriverScreenshot getFrameOrElementScreenshot(ScaleProviderFactory scaleProviderFactory, CheckState state) {
        RenderingInfo renderingInfo = serverConnector.getRenderInfo();
        FullPageCaptureAlgorithm algo = createFullPageCaptureAlgorithm(scaleProviderFactory, renderingInfo);

        EyesWebDriverScreenshot result;
        logger.verbose("Check frame/element requested");

        PositionProvider positionProvider = state.getStitchPositionProvider();
        PositionProvider originPositionProvider = state.getOriginPositionProvider();

        if (positionProvider == null) {
            logger.verbose("positionProvider is null, updating it.");
            WebElement scrollRootElement = getCurrentFrameScrollRootElement();
            positionProvider = getPositionProviderForScrollRootElement(logger, driver, getConfigurationInstance().getStitchMode(), userAgent, scrollRootElement);
        }

        if (originPositionProvider == null) {
            originPositionProvider = new NullPositionProvider();
        }

        if (state.isStitchContent()) {
            setElementAttribute(((ISeleniumPositionProvider) positionProvider).getScrolledElement(), SCROLL_ELEMENT_ATTRIBUTE, "true");
        }
        BufferedImage entireFrameOrElement = algo.getStitchedRegion(state.getEffectiveViewport(), state.getFullRegion(), positionProvider, originPositionProvider, state.getStitchOffset());

        logger.verbose("Building screenshot object...");
        Location frameLocationInScreenshot = new Location(-state.getFullRegion().getLeft(), -state.getFullRegion().getTop());
        result = new EyesWebDriverScreenshot(logger, driver, entireFrameOrElement,
                new RectangleSize(entireFrameOrElement.getWidth(), entireFrameOrElement.getHeight()),
                frameLocationInScreenshot);

        return result;
    }

    private EyesWebDriverScreenshot getScaledAndCroppedScreenshot(ScaleProviderFactory scaleProviderFactory) {
        BufferedImage screenshotImage = this.imageProvider.getImage();
        debugScreenshotsProvider.save(screenshotImage, "original");

        ScaleProvider scaleProvider = scaleProviderFactory.getScaleProvider(screenshotImage.getWidth());
        CutProvider cutProvider = cutProviderHandler.get();
        if (scaleProvider.getScaleRatio() != 1.0) {
            screenshotImage = ImageUtils.scaleImage(screenshotImage, scaleProvider.getScaleRatio());
            debugScreenshotsProvider.save(screenshotImage, "scaled");
            cutProvider.scale(scaleProvider.getScaleRatio());
        }

        if (!(cutProvider instanceof NullCutProvider)) {
            screenshotImage = cutProvider.cut(screenshotImage);
            debugScreenshotsProvider.save(screenshotImage, "cut");
        }

        return new EyesWebDriverScreenshot(logger, driver, screenshotImage);
    }

    private long getWaitBeforeScreenshots() {
        return getConfigurationInstance().getWaitBeforeScreenshots();
    }

    private void setElementAttribute(WebElement element, String key, String value) {
        if (EyesDriverUtils.isMobileDevice(driver)) {
            return;
        }
        if (element != null) {
            try {
                jsExecutor.executeScript(String.format("var e = arguments[0]; if (e != null) e.setAttribute('%s','%s');", key, value), element);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private FullPageCaptureAlgorithm createFullPageCaptureAlgorithm(ScaleProviderFactory scaleProviderFactory, RenderingInfo renderingInfo) {
        ISizeAdjuster sizeAdjuster = ImageProviderFactory.getImageSizeAdjuster(userAgent, jsExecutor);
        return new FullPageCaptureAlgorithm(logger, regionPositionCompensation,
                getConfigurationInstance().getWaitBeforeScreenshots(), debugScreenshotsProvider, screenshotFactory,
                scaleProviderFactory, cutProviderHandler.get(), getConfigurationInstance().getStitchOverlap(),
                imageProvider, renderingInfo.getMaxImageHeight(), renderingInfo.getMaxImageArea(), sizeAdjuster);
    }

    @Override
    protected String getTitle() {
        if (!doNotGetTitle && !EyesDriverUtils.isMobileDevice(driver)) {
            try {
                return driver.getTitle();
            } catch (Exception ex) {
                logger.verbose("failed (" + ex.getMessage() + ")");
                doNotGetTitle = true;
            }
        }

        return "";
    }

    @Override
    protected String getInferredEnvironment() {
        String userAgent = driver.getUserAgent();
        if (userAgent != null) {
            return "useragent:" + userAgent;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This override also checks for mobile operating system.
     */
    @Override
    protected Object getAppEnvironment() {
        AppEnvironment appEnv = (AppEnvironment) super.getAppEnvironment();
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
                    String platformVersion =
                            EyesDriverUtils.getPlatformVersion(underlyingDriver);
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
                    appEnv.setDeviceInfo(EyesDriverUtils.getMobileDeviceName(driver.getRemoteWebDriver()));
                }
            } else {
                logger.log("No mobile OS detected.");
            }
        }
        logger.log("Done!");
        return appEnv;
    }

    @Override
    public void setIsDisabled(Boolean disabled) {
        super.setIsDisabled(disabled);
    }

    @Override
    public TestResults close(boolean throwEx) {
        TestResults results;
        try {
            results = stopSession(false);
            closeCompleted(results);
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            closeFailed(e);
            throw e;
        }

        if (error != null && throwEx) {
            throw new Error(error);
        }

        if (runner != null) {
            this.runner.aggregateResult(testResultContainer);
        }
        return results;
    }

    /**
     * The type Eyes selenium agent setup.
     */
    @SuppressWarnings("UnusedDeclaration")
    class EyesSeleniumAgentSetup {
        /**
         * The type Web driver info.
         */
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
        public EyesSeleniumAgentSetup() {
            remoteWebDriver = driver.getRemoteWebDriver();
        }

        private RemoteWebDriver remoteWebDriver;

        /**
         * Gets selenium session id.
         * @return the selenium session id
         */
        public String getSeleniumSessionId() {
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
         * Gets stitch mode.
         * @return the stitch mode
         */
        public StitchMode getStitchMode() {
            return SeleniumEyes.this.getConfigurationInstance().getStitchMode();
        }

        /**
         * Gets hide scrollbars.
         * @return the hide scrollbars
         */
        public boolean getHideScrollbars() {
            return SeleniumEyes.this.getConfigurationInstance().getHideScrollbars();
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

    }
    @Override
    public Object getAgentSetup() {
        return new EyesSeleniumAgentSetup();
    }

    @Override
    public Boolean isSendDom() {
        return !EyesDriverUtils.isMobileDevice(driver) && super.isSendDom();
    }

    @Override
    protected Configuration getConfigurationInstance() {
        return configurationProvider.get();
    }

    /**
     * For test purposes only.
     */
    void setDebugScreenshotProvider(DebugScreenshotsProvider debugScreenshotProvider) {
        this.debugScreenshotsProvider = debugScreenshotProvider;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    /**
     * Gets scale provider.
     * @return the scale provider
     */
    public ScaleProvider getScaleProvider() {
        return scaleProviderHandler.get();
    }

    public CutProvider getCutProvider() {
        return cutProviderHandler.get();
    }
}
