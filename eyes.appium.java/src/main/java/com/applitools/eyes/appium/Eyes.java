package com.applitools.eyes.appium;

import com.applitools.ICheckSettings;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.appium.capture.ImageProviderFactory;
import com.applitools.eyes.appium.capture.MobileImageProvider;
import com.applitools.eyes.appium.capture.MobileScreenshotProvider;
import com.applitools.eyes.appium.locators.AndroidVisualLocatorProvider;
import com.applitools.eyes.appium.locators.IOSVisualLocatorProvider;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.capture.ScreenshotProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.events.ValidationInfo;
import com.applitools.eyes.events.ValidationResult;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.fluent.SimpleRegionByRectangle;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.locators.OcrRegion;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.locators.VisualLocatorsProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
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
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.eyes.visualgrid.services.IEyes;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridRunningTest;
import com.applitools.utils.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Eyes extends RunningTest implements IEyes {
    private static final int USE_DEFAULT_MATCH_TIMEOUT = -1;
    private static final int DEFAULT_STITCH_OVERLAP = 50;
    private static final int IOS_STITCH_OVERLAP = 0;

    private static final int UFG_TRIGGER_AREA_WAIT_TIMEOUT_SECONDS = 30; // Time to wait for UFG_Labels to appear
    private static final int UFG_LABEL_1ST_WAIT_TIMEOUT_SECONDS = 10; // Time to wait for UFG_Labels to appear
    private static final int UFG_LABEL_2ND_WAIT_TIMEOUT_SECONDS = 20; // Time to wait for UFG_Labels to appear
    private static final int UFG_LABEL_WAIT_SLEEP_MS = 250; // Time to sleep between searching for UFG_Label
    private static final String UFG_LABEL_IDENTIFIER = "UFG_Label";
    private static final String APIKEY_LABEL_IDENTIFIER = "UFG_Apikey";
    private static final String APIKEY_READY_LABEL_IDENTIFIER = "UFG_ApikeyReady";
    private static final String UFG_TRIGGER_AREA_LABEL = "UFG_TriggerArea";

    private long timerStartMS; // Used for profiling.
    private long totalTimeMS;

    public static final double UNKNOWN_DEVICE_PIXEL_RATIO = 0;
    public static final double DEFAULT_DEVICE_PIXEL_RATIO = 1;

    private final boolean isVisualGrid;
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
    private WebElement scrollRootElement = null;
    private boolean apiKeyWasSent = false;

    final Map<String, RunningTest> visualGridTestList = new HashMap<>();


    public Eyes() {
        this(new ClassicRunner());
    }

    public Eyes(EyesRunner runner) {
        super(runner);
        isVisualGrid = runner instanceof VisualGridRunner;
        regionVisibilityStrategyHandler = new SimplePropertyHandler<>();
        regionVisibilityStrategyHandler.set(new MoveToRegionVisibilityStrategy());
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
        logger.log(TraceLevel.Info, Collections.singleton(getTestId()), Stage.OPEN, Type.CALLED,
                Pair.of("appName", appName),
                Pair.of("testName", testName),
                Pair.of("viewportSize", viewportSize == null ? "default" : viewportSize));
        configuration.setAppName(appName);
        configuration.setTestName(testName);
        configuration.setViewportSize(viewportSize);
        return open(driver);
    }

    public WebDriver open(WebDriver driver, String appName, String testName) {
        logger.log(TraceLevel.Info, Collections.singleton(getTestId()), Stage.OPEN, Type.CALLED,
                Pair.of("appName", appName),
                Pair.of("testName", testName));
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
        logger.log(TraceLevel.Info, Collections.singleton(getTestId()), Stage.OPEN, Type.CALLED,
                Pair.of("appName", appName),
                Pair.of("testName", testName),
                Pair.of("viewportSize", viewportSize == null ? "default" : viewportSize),
                Pair.of("sessionType", sessionType));
        configuration.setAppName(appName);
        configuration.setTestName(testName);
        configuration.setViewportSize(viewportSize);
        configuration.setSessionType(sessionType);
        return open(driver);
    }

    private WebDriver open(WebDriver webDriver) {
        if (getIsDisabled()) {
            return webDriver;
        }

        ArgumentGuard.notNull(webDriver, "driver");

        initDriver(webDriver);

        tryUpdateDevicePixelRatio();

        ensureViewportSize();
        if (isVisualGrid) {
            openVisualGrid();
            return this.driver;
        }

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
            return;
        }

        ArgumentGuard.notNull(checkSettings, "checkSettings");
        checkSettings = checkSettings.withName(name);
        this.check(checkSettings);
    }

    @Override
    public IBatchCloser getBatchCloser() {
        return this;
    }

    @Override
    public String getBatchId() {
        return getConfiguration().getBatch().getId();
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
            regionVisibilityStrategyHandler = new ReadOnlyPropertyHandler<RegionVisibilityStrategy>(new MoveToRegionVisibilityStrategy());
        } else {
            regionVisibilityStrategyHandler = new ReadOnlyPropertyHandler<RegionVisibilityStrategy>(new NopRegionVisibilityStrategy(logger));
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
        setPositionProvider(new AppiumScrollPositionProviderFactory(logger, driver).getScrollPositionProvider());
    }

    private void initImageProvider() {
        imageProvider = ImageProviderFactory.getImageProvider(logger, driver, true);
    }

    private void initDriver(WebDriver driver) {
        if (driver instanceof AppiumDriver) {
            this.driver = new EyesAppiumDriver(logger, this, (AppiumDriver) driver);
            regionVisibilityStrategyHandler.set(new NopRegionVisibilityStrategy(logger));
            adjustStitchOverlap(driver);
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

        String platformName = null;
        if (EyesDriverUtils.isAndroid(underlyingDriver)) {
            platformName = "Android";
        } else if (EyesDriverUtils.isIOS(underlyingDriver)) {
            platformName = "iOS";
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
            appEnv.setOs(os);
        }

        if (appEnv.getDeviceInfo() == null) {
            appEnv.setDeviceInfo(EyesDriverUtils.getMobileDeviceName(underlyingDriver));
        }
        return appEnv;
    }

    public double getDevicePixelRatio() {
        return devicePixelRatio;
    }

    protected void tryUpdateDevicePixelRatio() {
        try {
            devicePixelRatio = driver.getDevicePixelRatio();
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e, getTestId());
            devicePixelRatio = DEFAULT_DEVICE_PIXEL_RATIO;
        }
        logger.log(getTestId(), Stage.GENERAL, Pair.of("devicePixelRatio", devicePixelRatio));
    }

    private void updateCutElement(AppiumCheckSettings checkSettings) {
        try {
            if (checkSettings.getCutElementSelector() == null) {
                return;
            }
            cutElement = getDriver().findElement(checkSettings.getCutElementSelector());
        } catch (NoSuchElementException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e, getTestId());
        }
    }

    public void check(ICheckSettings... checkSettings) {
        if (getIsDisabled()) {
            return;
        }

        if (isVisualGrid) {
            for (ICheckSettings checkSetting : checkSettings) {
                this.check(checkSetting);
            }
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
            r = regionPositionCompensation.compensateRegionPosition(r, getDevicePixelRatio());
            EyesScreenshot subScreenshot = screenshot.getSubScreenshot(r, false);
            subScreenshots.add(subScreenshot);
        }
        return subScreenshots;
    }

    private void matchRegion(ICheckSettingsInternal checkSettingsInternal, List<EyesScreenshot> subScreenshots) {
        String name = checkSettingsInternal.getName();
        for (EyesScreenshot subScreenshot : subScreenshots) {
            debugScreenshotsProvider.save(subScreenshot.getImage(), String.format("subscreenshot_%s", name));
            Location location = subScreenshot.getLocationInScreenshot(Location.ZERO, CoordinatesType.SCREENSHOT_AS_IS);
            AppOutput appOutput = new AppOutput(name, subScreenshot, null, null, location);
            if (isAsync) {
                CheckTask checkTask = issueCheck((ICheckSettings) checkSettingsInternal, null, getAppName());
                checkTask.setAppOutput(appOutput);
                performMatchAsync(checkTask);
                continue;
            }

            ImageMatchSettings ims = MatchWindowTask.createImageMatchSettings(checkSettingsInternal, subScreenshot, this);
            MatchWindowData data = prepareForMatch(checkSettingsInternal, new ArrayList<Trigger>(), appOutput, name,
                    false, ims, null, getAppName());
            performMatch(data);
        }
    }

    public void check(ICheckSettings checkSettings) {
        logger.log(TraceLevel.Info, Collections.singleton(getTestId()), Stage.CHECK, Type.CALLED,
                Pair.of("configuration", getConfiguration()),
                Pair.of("checkSettings", checkSettings));

        if (isVisualGrid) {
            checkVisualGrid(checkSettings);
            return;
        }

        if (checkSettings instanceof AppiumCheckSettings) {
            updateCutElement((AppiumCheckSettings) checkSettings);
            this.scrollRootElement = getScrollRootElement((AppiumCheckSettings) checkSettings);
        }

        if (getIsDisabled()) {
            return;
        }

        ArgumentGuard.notNull(checkSettings, "checkSettings");

        boolean tmpCaptureStatusBar = configuration.isCaptureStatusBar();
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
        AppiumCheckSettings appiumCheckTarget = (checkSettings instanceof AppiumCheckSettings) ? (AppiumCheckSettings) checkSettings : null;
        if (appiumCheckTarget != null) {
            appiumCheckTarget.init(logger, driver);
            if (appiumCheckTarget.getCaptureStatusBar() != null && appiumCheckTarget.isNotRegion()) {
                configuration.setCaptureStatusBar(appiumCheckTarget.getCaptureStatusBar());
            }
            if (!appiumCheckTarget.isNotRegion()) {
                configuration.setCaptureStatusBar(false);
            }
        }
        String name = checkSettingsInternal.getName();

        ValidationInfo validationInfo = this.fireValidationWillStartEvent(name);

        this.stitchContent = checkSettingsInternal.getStitchContent() == null ? false : checkSettingsInternal.getStitchContent();

        final Region targetRegion = checkSettingsInternal.getTargetRegion();

        MatchResult result = null;

        if (targetRegion != null) {
            Region region = new Region(targetRegion.getLocation(), targetRegion.getSize(), CoordinatesType.CONTEXT_RELATIVE);
            result = this.checkWindowBase(region, name, checkSettings);
        } else if (appiumCheckTarget != null) {
            WebElement targetElement = getTargetElement(appiumCheckTarget);
            if (targetElement != null) {
                this.targetElement = targetElement;
                if (this.stitchContent) {
                    result = this.checkElement(targetElement, name, checkSettings);
                } else {
                    result = this.checkRegion(name, checkSettings);
                }
                this.targetElement = null;
            } else {
                result = this.checkWindowBase(null, name, checkSettings);
            }
        }

        if (result == null) {
            result = new MatchResult();
        }

        this.stitchContent = false;
        configuration.setCaptureStatusBar(tmpCaptureStatusBar);

        ValidationResult validationResult = new ValidationResult();
        validationResult.setAsExpected(result.getAsExpected());
        ((AppiumScrollPositionProvider) getPositionProvider()).cleanupCachedData();
    }

    @Override
    protected void getAppOutputForOcr(BaseOcrRegion ocrRegion) {
        OcrRegion appiumOcrRegion = (OcrRegion) ocrRegion;
        AppiumCheckSettings checkSettings = null;
        if (appiumOcrRegion.getRegion() != null) {
            checkSettings = Target.region(appiumOcrRegion.getRegion());
        }
        if (appiumOcrRegion.getElement() != null) {
            checkSettings = Target.region(appiumOcrRegion.getElement()).fully();
        }
        if (appiumOcrRegion.getSelector() != null) {
            checkSettings = Target.region(appiumOcrRegion.getSelector()).fully();
        }

        if (checkSettings == null) {
            throw new IllegalStateException("Got uninitialized ocr region");
        }

        check(checkSettings.ocrRegion(ocrRegion));
    }

    @Override
    protected ScreenshotProvider getScreenshotProvider() {
        return new MobileScreenshotProvider(driver, getDevicePixelRatio());
    }

    public void openVisualGrid() {
        runner.setApiKey(getApiKey());
        runner.setServerUrl(getServerUrl().toString());
        runner.setProxy(getProxy());

        if (isOpen) {
            return;
        }

        isOpen = true;
        if (this.getConfiguration().getBrowsersInfo().isEmpty()) {
            throw new EyesException("Device wasn't set in the configuration");
        }

        if (runner.getAgentId() == null ) {
            runner.setAgentId(getFullAgentId());
        }

        VisualGridRunner visualGridRunner = (VisualGridRunner) runner;
        visualGridRunner.setLogger(logger);

        List<RenderBrowserInfo> deviceInfoList = getConfiguration().getBrowsersInfo();
        List<RunningTest> newTests = new ArrayList<>();
        ServerConnector serverConnector = runner.getServerConnector();
        for (RenderBrowserInfo deviceInfo : deviceInfoList) {
            if (deviceInfo.getIosDeviceInfo() == null && deviceInfo.getAndroidDeviceInfo() == null) {
                throw new EyesException("All browser infos must be ios device info or android device info");
            }

            String agentRunId = String.format("%s_%s", getConfiguration().getTestName(), UUID.randomUUID());
            RunningTest test = new VisualGridRunningTest(logger, false, getTestId(), getConfiguration(), deviceInfo, this.properties, serverConnector, agentRunId);
            this.visualGridTestList.put(test.getTestId(), test);
            newTests.add(test);
        }

        try {
            visualGridRunner.open(this, newTests);
        } catch (Throwable t) {
            for (RunningTest runningTest : visualGridTestList.values()) {
                runningTest.openFailed(t);
            }
            throw t;
        }
    }

    private long timerStart() {
        timerStartMS = System.currentTimeMillis();
        return timerStartMS;
    }

    private long timerTakeTimeAndLog(Stage stage, Set<String> testIds, String timedEventName) {
        long currentTimeMS = System.currentTimeMillis();
        long measuredTime = currentTimeMS-timerStartMS;
        logger.log(TraceLevel.Info, testIds, stage, Type.PROFILING,Pair.of(timedEventName, String.valueOf(measuredTime / 1000.0)));

        timerStartMS = currentTimeMS;
        totalTimeMS += currentTimeMS;
        return measuredTime;
    }

    private long timerGetTotalTime() {
        return totalTimeMS;
    }

    private void checkVisualGrid(ICheckSettings checkSettings) {

        timerStart(); // Profiling related

        checkSettings = updateCheckSettings(checkSettings);
        Set<String> testIds = new HashSet<>();
        for (RunningTest runningTest : visualGridTestList.values()) {
            testIds.add(runningTest.getTestId());
        }
        try {
            byte[] vhs;
            String vhsContentType;
            String vhsType = null;
            VhsCompatibilityParams vhsCompatibilityParams = null;
            Map<String, FrameData> resources = new HashMap<>();
            VHSCaptureMode vhsCaptureMode = VHSCaptureMode.Labels;
            String vhsHash = null;
            if (EyesDriverUtils.isAndroid(driver)) {
                AndroidVHSCaptureResult result = getVHSAndroid(testIds);
                vhs = result.getVhs();
                vhsType = result.getVcrType();
                resources = result.getResources();
                vhsContentType = String.format("x-applitools-vhs/%s", vhsType);
                vhsCaptureMode = result.getVhsCaptureMode();
                vhsHash = result.getVhsHash().toLowerCase();
            } else if (EyesDriverUtils.isIOS(driver)) {
                timerTakeTimeAndLog(Stage.CHECK, testIds, "prepare to take VHS"); // Profiling
                try {
                    vhs = getVHSIos(testIds);
                    timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"got VHS");
                    vhsCompatibilityParams = getVhsCompatibilityParams();
                    timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"got compatibility params");
                } catch (Throwable e) { // for logging purposes
                    GeneralUtils.logExceptionStackTrace(logger, Stage.RESOURCE_COLLECTION, Type.DOWNLOAD_RESOURCE, e, testIds);
                    throw e;
                } finally {
                    try {
                        // Always try and clear the current state.
                        driver.findElementByAccessibilityId("UFG_ClearArea").click();
                        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"clicked clear area");
                    } catch (NoSuchElementException e) {
                        logger.log(TraceLevel.Error, testIds, Stage.RESOURCE_COLLECTION, Type.CLEANUP, Pair.of("message", "Could not find 'UFG_ClearArea', possibly due to a previous failure."));
                    }
                }
                vhsContentType = "x-applitools-vhs/ios";
            } else {
                throw new EyesException("Unknown platform");
            }

            List<CheckTask> checkTasks = new ArrayList<>();
            for (RunningTest runningTest : visualGridTestList.values()) {
                if (runningTest.isCloseTaskIssued()) {
                    continue;
                }

                if (runningTest.getBrowserInfo().getAndroidDeviceInfo() != null) {
                    runningTest.getBrowserInfo().getAndroidDeviceInfo().setVhsType(vhsType);
                }
                checkTasks.add(runningTest.issueCheck(checkSettings, null, null));
            }

            if (checkTasks.isEmpty()) {
                logger.log(TraceLevel.Warn, testIds, Stage.CHECK, null, Pair.of("message", "No check tasks created. Tests were probably aborted"));
                return;
            }
            VisualGridRunner visualGridRunner = (VisualGridRunner) runner;
            visualGridRunner.check(vhs, resources, vhsContentType, checkTasks, vhsCompatibilityParams, vhsCaptureMode, vhsHash);
        } catch (Throwable e) {
            Error error = new Error(e);
            for (RunningTest runningTest : visualGridTestList.values()) {
                runningTest.setTestInExceptionMode(error);
            }
        }
    }

    private AndroidVHSCaptureResult getVHSAndroid(Set<String> testIds) throws Exception {
        sendApiKeyToVhsLib();
        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"sent API Key to UFG lib");

        EyesTriggerAreaElement triggerAreaElement = new EyesTriggerAreaElement(driver, UFG_TRIGGER_AREA_LABEL,
                UFG_TRIGGER_AREA_WAIT_TIMEOUT_SECONDS, UFG_LABEL_WAIT_SLEEP_MS);
        triggerAreaElement.searchForTriggerArea();
        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"TriggerArea found");
        int labelsAmount = Integer.parseInt(triggerAreaElement.getText());
        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"TriggerArea parsed");
        triggerAreaElement.click();
        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"TriggerArea clicked");

        WebElement secondaryLabel = waitForDataAvailability();
        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"UFGSecondary_Label found");

        try {
            Map<String, FrameData> resourcesMap;
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String jsonText = secondaryLabel.getText();
            timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"UFGSecondary_Label data read");
            Map<String, Object> captureResult = mapper.readValue(jsonText, new TypeReference<Map<String, Object>>() {});
            VHSCaptureMode captureResultType;
            String error = (String) captureResult.get("error");
            if (!error.isEmpty()) {
                throw new EyesException(error);
            }
            if (!captureResult.containsKey("mode")) {
                throw new EyesException("UFGSecondary_Label does not contain 'mode'. Please update UFG library to the latest version");
            }
            String vhsResultType = (String) captureResult.get("mode");
            String vcrType = ((String) captureResult.get("flavorName"));
            if (vhsResultType.equals("network")) {
                captureResultType = VHSCaptureMode.Network;
            } else {
                captureResultType = VHSCaptureMode.Labels;
            }
            String vhsExpectedHash = (String) captureResult.get("vhsHash");

            byte[] vhs;
            String resourcesJson;
            if (captureResultType == VHSCaptureMode.Labels) {
                int vhsParts = (Integer) captureResult.get("partsCount");
                logger.log(testIds, Stage.CHECK, Pair.of("vhsParts", vhsParts));
                StringBuilder builder = new StringBuilder();
                int vhsPartsDone = 0;
                while (vhsPartsDone < vhsParts) {
                    if (vhsPartsDone % labelsAmount == 0) {
                        triggerAreaElement.click();
                    }
                    WebElement label = driver.findElementByAccessibilityId(String.format("UFG_Label_%s", vhsPartsDone % labelsAmount));
                    builder.append(label.getText());
                    vhsPartsDone++;
                    logger.log(TraceLevel.Debug, testIds, Stage.CHECK, null, Pair.of("vhsPartsDone", vhsPartsDone));
                }
                resourcesJson = builder.toString();
                String vhsActualHash = GeneralUtils.getSha256hash(resourcesJson.getBytes(StandardCharsets.UTF_8));
                if (!vhsExpectedHash.equalsIgnoreCase(vhsActualHash)) {
                    throw new EyesException(String.format("Didn't get vhs byte correctly. Expected Hash: %s. Actual Hash: %s", vhsExpectedHash, vhsActualHash));
                }


                Map<String, Object> resources = mapper.readValue(resourcesJson, new TypeReference<Map<String, Object>>() {});
                if (!resources.containsKey("vhs")) {
                    throw new EyesException("vhs not found");
                }

                String base64VHS = (String) resources.remove("vhs");
                vhs = Base64.decodeBase64(base64VHS);
                resourcesJson = mapper.writeValueAsString(resources);
                resourcesMap = mapper.readValue(resourcesJson, new TypeReference<Map<String, FrameData>>() {});
            } else {
                vhs = null;
                resourcesMap = new HashMap<>();
            }

            return new AndroidVHSCaptureResult(
                    vhs,
                    vcrType,
                    resourcesMap,
                    vhsExpectedHash,
                    captureResultType
            );
        } finally {
            try {
                WebElement clearButton = driver.findElementByAccessibilityId("UFG_ClearArea");
                timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"UFG_ClearArea found");
                clearButton.click();
                timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"UFG_ClearArea clicked");
            } catch (NoSuchElementException e) {
                logger.log(TraceLevel.Error, testIds, Stage.RESOURCE_COLLECTION, Type.CLEANUP, Pair.of("message", "Could not find 'UFG_ClearArea'"));
            }
        }
    }

    private WebElement waitForDataAvailability() throws InterruptedException {
        long startTimeMS = System.currentTimeMillis();
        long currentTimeMS = System.currentTimeMillis();
        // FIXME - make this code better (constants missing, code duplication)
        do {

            List<WebElement> elementList = driver.findElementsByAccessibilityId("UFG_SecondaryLabel");
            if (!elementList.isEmpty()) {
                return elementList.get(0);
            }
            Thread.sleep(250);
        } while(((int)Math.floor((currentTimeMS - startTimeMS) / 1000.0)) < 30);

        throw new EyesException("Timed-out trying to find UFG_SecondaryLabel. Screen might be overloaded");
    }

    private void sendApiKeyToVhsLib() throws InterruptedException {
        if (apiKeyWasSent) {
            return;
        }
        try {
            WebElement apiKeyLabel = new WebDriverWait(driver, UFG_TRIGGER_AREA_WAIT_TIMEOUT_SECONDS, UFG_LABEL_WAIT_SLEEP_MS)
                    .until(ExpectedConditions.presenceOfElementLocated(By.name(APIKEY_LABEL_IDENTIFIER)));
            apiKeyLabel.sendKeys(getApiKey());

            try {
                driver.findElementByAccessibilityId(APIKEY_READY_LABEL_IDENTIFIER).click();
            } catch (NoSuchElementException e) {
                throw new EyesException("Failed to find " + APIKEY_READY_LABEL_IDENTIFIER + " label, something is wrong.");
            }
        } catch (TimeoutException e) {
            throw new EyesException("Timed-out waiting for API Key label" + APIKEY_LABEL_IDENTIFIER + " label. Please check integration of UFG lib in the application");
        }
        Thread.sleep(100);
        apiKeyWasSent = true;
    }

    public byte[] getVHSIos(Set<String> testIds) throws InterruptedException {
        // First (and hopefully only) click on the trigger area.
        doClickOnTriggerAreaIOS(true, UFG_TRIGGER_AREA_WAIT_TIMEOUT_SECONDS);
        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"trigger area clicked");

        WebElement ufgLabel = null;
        try {
            ufgLabel = new WebDriverWait(driver, UFG_LABEL_1ST_WAIT_TIMEOUT_SECONDS, UFG_LABEL_WAIT_SLEEP_MS)
                    .until(ExpectedConditions.presenceOfElementLocated(By.name(UFG_LABEL_IDENTIFIER)));
        } catch(TimeoutException e) {
            // Workaround for iOS - sometimes an additional "click" is required for something to actually work ( :facepalm: ).
            // This time there's no point in *waiting* for the TriggerArea. If it's not there - it's a good sign.
            doClickOnTriggerAreaIOS(false, 0);
            try {
                ufgLabel = new WebDriverWait(driver, UFG_LABEL_2ND_WAIT_TIMEOUT_SECONDS, UFG_LABEL_WAIT_SLEEP_MS)
                        .until(ExpectedConditions.presenceOfElementLocated(By.name(UFG_LABEL_IDENTIFIER)));
            } catch (TimeoutException e2) {
                throw new EyesException("Could not find UFG_Label after timeout.");
            }
        }
        timerTakeTimeAndLog(Stage.RESOURCE_COLLECTION, testIds,"UFG_Label found");

        // We have a UFG_Label, yay!
        String base64 = ufgLabel.getAttribute("value");
        return Base64.decodeBase64(base64);
    }

    private void doClickOnTriggerAreaIOS(boolean throwIfNoTriggerArea, int waitTimeoutSeconds ) {
        try {
            WebElement triggerArea = new WebDriverWait(driver, waitTimeoutSeconds, UFG_LABEL_WAIT_SLEEP_MS).until(ExpectedConditions.presenceOfElementLocated(By.name(UFG_TRIGGER_AREA_LABEL)));
            triggerArea.click();
        } catch(TimeoutException e) {
            // Due to some issue with iOS, we sometimes check for the existence of the TriggerArea again. This time if it's not available - we DON'T want
            // to throw an exception.
            if (throwIfNoTriggerArea) {
                throw new EyesException("Timed-out waiting for TriggerArea. Please check integration of UFG lib in the application");
            }
        } catch (Exception e) {
            throw new EyesException("Unknown error occurred trying to find and click TriggerArea", e);
        }
    }

    private VhsCompatibilityParams getVhsCompatibilityParams() {
        String jsonValue = driver.findElementByName("UFG_SecondaryLabel").getAttribute("value");
        VhsCompatibilityParams vhsCompatibilityParams = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            vhsCompatibilityParams = mapper.readValue(jsonValue, VhsCompatibilityParams.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return vhsCompatibilityParams;
    }

    private ICheckSettings updateCheckSettings(ICheckSettings checkSettings) {
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;

        MatchLevel matchLevel = checkSettingsInternal.getMatchLevel();
        Boolean fully = checkSettingsInternal.isStitchContent();
        Boolean ignoreDisplacements = checkSettingsInternal.isIgnoreDisplacements();

        if (matchLevel == null) {
            checkSettings = checkSettings.matchLevel(getConfiguration().getMatchLevel());
        }

        if (fully == null) {
            Boolean isForceFullPageScreenshot = getConfiguration().isForceFullPageScreenshot();
            boolean stitchContent = isForceFullPageScreenshot == null ? checkSettings.isCheckWindow() : isForceFullPageScreenshot;
            checkSettings = checkSettings.fully(stitchContent);
        }

        if (ignoreDisplacements == null) {
            checkSettings = checkSettings.ignoreDisplacements(getConfiguration().getIgnoreDisplacements());
        }

        List<VisualGridOption> options = new ArrayList<>();
        options.addAll(getConfiguration().getVisualGridOptions());
        options.addAll(checkSettingsInternal.getVisualGridOptions());
        checkSettings = checkSettings.visualGridOptions(options.size() > 0 ? options.toArray(new VisualGridOption[]{}) : null);
        return checkSettings;
    }

    @Override
    public TestResults close(boolean throwEx) {
        if (isVisualGrid) {
            closeAsync();
            return waitForEyesToFinish(throwEx);
        }

        return super.close(throwEx);
    }

    @Override
    public TestResults abortIfNotClosed() {
        if (isVisualGrid) {
            abortAsync();
            return waitForEyesToFinish(false);
        }

        return super.abortIfNotClosed();
    }

    public TestResults waitForEyesToFinish(boolean throwException) {
        if (!isVisualGrid) {
            return super.waitForEyesToFinish(throwException);
        }

        while (!isCompleted()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        List<TestResultContainer> allResults = getAllTestResults();
        TestResultContainer errorResult = null;
        TestResults firstResult = null;
        for (TestResultContainer result : allResults) {
            if (firstResult == null) {
                firstResult = result.getTestResults();
            }
            if (result.getException() != null) {
                errorResult = result;
                break;
            }
        }
        TestResultsSummary testResultsSummary = new TestResultsSummary(allResults);
        if (errorResult != null) {
            if (throwException) {
                throw new TestFailedException(testResultsSummary, errorResult.getException());
            }
            return errorResult.getTestResults();
        }

        return firstResult;
    }

    @Override
    public void closeAsync() {
        logger.log(getTestId(), Stage.CLOSE, Type.CALLED);
        if (isVisualGrid) {
            isOpen = false;
            for (RunningTest runningTest : visualGridTestList.values()) {
                runningTest.issueClose();
            }
        } else {
            super.closeAsync();
        }
    }

    @Override
    public void abortAsync() {
        logger.log(getTestId(), Stage.CLOSE, Type.CALLED);
        if (isVisualGrid) {
            for (RunningTest runningTest : visualGridTestList.values()) {
                runningTest.issueAbort(new EyesException("eyes.close wasn't called. Aborted the test"), false);
            }
        } else {
            super.abortAsync();
        }
    }

    @Override
    public Map<String, RunningTest> getAllRunningTests() {
        return visualGridTestList;
    }

    @Override
    public List<TestResultContainer> getAllTestResults() {
        if (!isVisualGrid) {
            return super.getAllTestResults();
        }

        VisualGridRunner visualGridRunner = (VisualGridRunner) runner;
        List<TestResultContainer> allResults = new ArrayList<>();
        for (RunningTest runningTest : visualGridTestList.values()) {
            if (!runningTest.isCompleted()) {
                if (visualGridRunner.getError() != null) {
                    throw new EyesException("Execution crashed", visualGridRunner.getError());
                }
                return null;
            }

            allResults.add(runningTest.getTestResultContainer());
        }

        return allResults;
    }

    @Override
    public boolean isCompleted() {
        if (!isVisualGrid) {
            return super.isCompleted();
        }

        return getAllTestResults() != null;
    }

    /**
     * Sets the maximum time (in ms) a match operation tries to perform a match.
     * @param ms Total number of ms to wait for a match.
     */
    public void setMatchTimeout(int ms) {
        final int MIN_MATCH_TIMEOUT = 500;
        if (getIsDisabled()) {
            return;
        }

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
        EyesScreenshot result;

        if (imageProvider instanceof MobileImageProvider) {
            ((MobileImageProvider) imageProvider).setCaptureStatusBar(configuration.isCaptureStatusBar());
        }
        // Extra pause to wait for application actions will be finished.
        // Moving from one screen to another for example or wait until scrollbars becomes invisible
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        if (getForceFullPageScreenshot() || stitchContent) {
            result = getFullPageScreenshot();
        } else {
            result = getSimpleScreenshot();
        }

        if (targetRegion != null && !targetRegion.isEmpty()) {
            result = getSubScreenshot(result, targetRegion);
            debugScreenshotsProvider.save(result.getImage(), "SUB_SCREENSHOT");
        }

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
        EyesScreenshotFactory screenshotFactory = new EyesAppiumScreenshotFactory(logger, driver);
        ScaleProviderFactory scaleProviderFactory = updateScalingParams();

        AppiumScrollPositionProvider scrollPositionProvider = (AppiumScrollPositionProvider) getPositionProvider();

        AppiumCaptureAlgorithmFactory algoFactory = new AppiumCaptureAlgorithmFactory(driver, logger, getTestId(),
                scrollPositionProvider, imageProvider, debugScreenshotsProvider, scaleProviderFactory,
                cutProviderHandler.get(), screenshotFactory, getConfigurationInstance().getWaitBeforeScreenshots(), cutElement,
                getStitchOverlap(), scrollRootElement);

        AppiumFullPageCaptureAlgorithm algo = algoFactory.getAlgorithm();

        BufferedImage fullPageImage = algo.getStitchedRegion(Region.EMPTY, regionPositionCompensation);
        return new EyesAppiumScreenshot(logger, driver, fullPageImage);
    }

    protected EyesAppiumScreenshot getSimpleScreenshot() {
        ScaleProviderFactory scaleProviderFactory = updateScalingParams();
        BufferedImage screenshotImage = imageProvider.getImage();
        debugScreenshotsProvider.save(screenshotImage, "original");

        ScaleProvider scaleProvider = scaleProviderFactory.getScaleProvider(screenshotImage.getWidth());
        if (scaleProvider.getScaleRatio() != 1.0) {
            screenshotImage = ImageUtils.scaleImage(screenshotImage, scaleProvider.getScaleRatio(), true);
            debugScreenshotsProvider.save(screenshotImage, "scaled");
        }

        CutProvider cutProvider = cutProviderHandler.get();
        if (!(cutProvider instanceof NullCutProvider)) {
            screenshotImage = cutProvider.cut(screenshotImage);
            debugScreenshotsProvider.save(screenshotImage, "cut");
        }

        return new EyesAppiumScreenshot(logger, driver, screenshotImage);
    }

    protected MatchResult checkRegion(String name, ICheckSettings checkSettings) {
        Point p = targetElement.getLocation();
        Dimension d = targetElement.getSize();
        Region region = new Region(p.getX(), p.getY(), d.getWidth(), d.getHeight(), CoordinatesType.CONTEXT_RELATIVE);
        return checkWindowBase(region, name, checkSettings);
    }

    public void checkRegion(Region region) {
        checkRegion(region, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    public void checkRegion(final Region region, int matchTimeout, String tag) throws TestFailedException {
        if (getIsDisabled()) {
            return;
        }

        ArgumentGuard.notNull(region, "region");
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
            return;
        }

        ArgumentGuard.notNull(element, "element");

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

    private EyesScreenshot getSubScreenshot(EyesScreenshot screenshot, Region region) {
        ArgumentGuard.notNull(region, "region");
        if ((EyesDriverUtils.isAndroid(driver) || EyesDriverUtils.isIOS(driver))
                && region.getCoordinatesType() != CoordinatesType.CONTEXT_RELATIVE) {
            BufferedImage image = screenshot.getImage();
            if (image.getWidth() < driver.getViewportRect().get("width")) {
                image = ImageUtils.scaleImage(image, driver.getDevicePixelRatio(), true);
            }
            BufferedImage subScreenshotImage = ImageUtils.scaleImage(ImageUtils.getImagePart(image, region),
                    1 / driver.getDevicePixelRatio(), true);

            EyesAppiumScreenshot result = new EyesAppiumScreenshot(logger, driver, subScreenshotImage);
            return result;
        } else {
            return screenshot.getSubScreenshot(region, false);
        }
    }

    private void initVisualLocatorProvider() {
        if (EyesDriverUtils.isAndroid(driver)) {
            visualLocatorsProvider = new AndroidVisualLocatorProvider(logger, getTestId(), driver, getServerConnector(), getDevicePixelRatio(), configuration.getAppName(), debugScreenshotsProvider);
        } else if (EyesDriverUtils.isIOS(driver)) {
            visualLocatorsProvider = new IOSVisualLocatorProvider(logger, getTestId(), driver, getServerConnector(), getDevicePixelRatio(), configuration.getAppName(), debugScreenshotsProvider);
        } else {
            throw new Error("Could not find driver type for getting visual locator provider");
        }
    }

    public Map<String, List<Region>> locate(VisualLocatorSettings visualLocatorSettings) {
        ArgumentGuard.notNull(visualLocatorSettings, "visualLocatorSettings");
        return visualLocatorsProvider.getLocators(visualLocatorSettings);
    }

    private Region getElementRegion(WebElement element, ICheckSettings checkSettings) {
        Boolean statusBarExists = null;
        if (checkSettings instanceof AppiumCheckSettings) {
            statusBarExists = ((AppiumCheckSettings) checkSettings).getStatusBarExists();
        }
        return ((AppiumScrollPositionProvider) getPositionProvider()).getElementRegion(element, shouldStitchContent(), statusBarExists);
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

    private WebElement getScrollRootElement(AppiumCheckSettings checkSettings) {
        scrollRootElement = checkSettings.getScrollRootElement();
        if (scrollRootElement == null) {
            if (checkSettings.getScrollRootElementSelector() != null) {
                scrollRootElement = driver.findElement(checkSettings.getScrollRootElementSelector());
            }
            if (scrollRootElement == null && checkSettings.getScrollRootElementId() != null) {
                scrollRootElement = driver.findElement(MobileBy.id(checkSettings.getScrollRootElementId()));
            }
        }
        return scrollRootElement;
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
            return EyesAppiumUtils.getHelperLibraryVersion(driver, logger);
        }
    }
}
