/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.utils.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Calendar;

/**
 * The main API gateway for the SDK.
 */
public class Eyes extends EyesBase {

    public interface WebDriverAction {
        void drive(WebDriver driver);
    }

    public static final double UNKNOWN_DEVICE_PIXEL_RATIO = 0;
    public static final double DEFAULT_DEVICE_PIXEL_RATIO = 1;


    private static final int USE_DEFAULT_MATCH_TIMEOUT = -1;

    // Seconds
    private static final int RESPONSE_TIME_DEFAULT_DEADLINE = 10;
    // Seconds
    private static final int RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE = 20;

    // Milliseconds
    private static final int DEFAULT_WAIT_BEFORE_SCREENSHOTS = 100;

    private EyesWebDriver driver;
    private boolean dontGetTitle;


    private boolean forceFullPageScreenshot;
    private boolean checkFrameOrElement;
    private RegionProvider regionToCheck;
    private boolean hideScrollbars;
	private ImageRotation rotation;
    private double devicePixelRatio;
    private StitchMode stitchMode;
    private int waitBeforeScreenshots;
    private RegionVisibilityStrategy regionVisibilityStrategy;

    /**
     * Creates a new (possibly disabled) Eyes instance that interacts with the
     * Eyes Server at the specified url.
     *
     * @param serverUrl  The Eyes server URL.
     */
    public Eyes(URI serverUrl) {
        super(serverUrl);

        checkFrameOrElement = false;
        regionToCheck = null;
        forceFullPageScreenshot = false;
        dontGetTitle = false;
        hideScrollbars = false;
        devicePixelRatio = UNKNOWN_DEVICE_PIXEL_RATIO;
        stitchMode = StitchMode.SCROLL;
        waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
        regionVisibilityStrategy = new MoveToRegionVisibilityStrategy(logger);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Creates a new Eyes instance that interacts with the Eyes cloud
     * service.
     */
    public Eyes() {
        this(getDefaultServerUrl());
    }

    @Override
    public String getBaseAgentId() {
        return "eyes.selenium.java/3.11";
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * ﻿Forces a full page screenshot (by scrolling and stitching) if the
     * browser only ﻿supports viewport screenshots).
     *
     * @param shouldForce Whether to force a full page screenshot or not.
     */
    public void setForceFullPageScreenshot(boolean shouldForce) {
        forceFullPageScreenshot = shouldForce;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * @return Whether Eyes should force a full page screenshot.
     */
    public boolean getForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    @SuppressWarnings("unused")
    /**
     * Sets the time to wait just before taking a screenshot (e.g., to allow
     * positioning to stabilize when performing a full page stitching).
     *
     * @param waitBeforeScreenshots The time to wait (Milliseconds). Values
     *                              smaller or equal to 0, will cause the
     *                              default value to be used.
     */
    public void setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        if (waitBeforeScreenshots <= 0) {
            this.waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
        } else {
            this.waitBeforeScreenshots = waitBeforeScreenshots;
        }
    }

    @SuppressWarnings("unused")
    /**
     *
     * @return The time to wait just before taking a screenshot.
     */
    public int getWaitBeforeScreenshots() {
        return waitBeforeScreenshots;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Turns on/off the automatic scrolling to a region being checked by
     * {@code checkRegion}.
     *
     * @param shouldScroll Whether to automatically scroll to a region being
     *                     validated.
     */
    public void setScrollToRegion(boolean shouldScroll) {
        if (shouldScroll) {
            regionVisibilityStrategy =
                    new MoveToRegionVisibilityStrategy(logger);
        } else {
            regionVisibilityStrategy = new NopRegionVisibilityStrategy(logger);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * @return Whether to automatically scroll to a region being validated.
     */
    public boolean getScrollToRegion() {
        return !(regionVisibilityStrategy instanceof
                NopRegionVisibilityStrategy);
    }

    @SuppressWarnings("unused")
    /**
     * Set the type of stitching used for full page screenshots. When the
     * page includes fixed position header/sidebar, use {@link StitchMode#CSS}.
     * Default is {@link StitchMode#SCROLL}.
     *
     * @param mode The stitch mode to set.
     */
    public void setStitchMode(StitchMode mode) {
        stitchMode = mode;
        if (driver != null) {
            switch (mode) {
                case CSS: setPositionProvider(
                        new CssTranslatePositionProvider(logger, this.driver));
                    break;
                default: setPositionProvider(
                        new ScrollPositionProvider(logger, this.driver));
            }
        }
    }

    /**
     *
     * @return The current stitch mode settings.
     */
    public StitchMode getStitchMode() {
        return stitchMode;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Hide the scrollbars when taking screenshots.
     * @param shouldHide Whether to hide the scrollbars or not.
     */
    public void setHideScrollbars(boolean shouldHide) {
        hideScrollbars = shouldHide;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return Whether or not scrollbars are hidden when taking screenshots.
     */
    public boolean getHideScrollbars() {
        return hideScrollbars;
    }

    @SuppressWarnings("unused")
    /**
     *
     * @return The image rotation data.
     */
    public ImageRotation getRotation() {
        return rotation;
    }

    @SuppressWarnings("unused")
    /**
     *
     * @param rotation The image rotation data.
     */
    public void setRotation(ImageRotation rotation) {
		this.rotation = rotation;
		if (driver != null) {
			driver.setRotation(rotation);
		}
    }

    @SuppressWarnings("unused")
    /**
     *
     * @return The device pixel ratio, or {@link #UNKNOWN_DEVICE_PIXEL_RATIO}
     * if the DPR is not known yet or if it wasn't possible to extract it.
     */
    public double getDevicePixelRatio() {
        return devicePixelRatio;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #open(WebDriver, String, String, RectangleSize, SessionType)}.
     * {@code sessionType} defaults to {@code null}.
     */
    public WebDriver open(WebDriver driver, String appName, String testName,
                          RectangleSize viewportSize) {
        return open(driver, appName, testName, viewportSize, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #open(WebDriver, String, String, SessionType)}.
     * {@code viewportSize} defaults to {@code null}.
     * {@code sessionType} defaults to {@code null}.
     */
    public WebDriver open(WebDriver driver, String appName, String testName) {
        return open(driver, appName, testName, null, null);
    }


    /**
     * Starts a test.
     *
     * @param driver         The web driver that controls the browser hosting
     *                       the application under test.
     * @param appName        The name of the application under test.
     * @param testName       The test name.
     * @param viewportSize   The required browser's viewport size
     *                       (i.e., the visible part of the document's body) or
     *                       {@code null} to use the current window's viewport.
     * @param sessionType    The type of test (e.g.,  standard test / visual
     *                       performance test).
     * @return A wrapped WebDriver which enables Eyes trigger recording and
     * frame handling.
     */
    protected WebDriver open(WebDriver driver, String appName, String testName,
                          RectangleSize viewportSize, SessionType sessionType) {

        if (getIsDisabled()) {
            logger.verbose("Ignored");
            return driver;
        }

        openBase(appName, testName, viewportSize, sessionType);

        ArgumentGuard.notNull(driver, "driver");

        if (driver instanceof RemoteWebDriver) {
            this.driver = new EyesWebDriver(logger, this,
                    (RemoteWebDriver) driver);
        } else if (driver instanceof EyesWebDriver) {
            this.driver = (EyesWebDriver) driver;
        } else {
            String errMsg = "Driver is not a RemoteWebDriver (" +
                    driver.getClass().getName() + ")";
            logger.log(errMsg);
            throw new EyesException(errMsg);
        }
        devicePixelRatio = UNKNOWN_DEVICE_PIXEL_RATIO;

        // Setting the correct position provider.
        switch (getStitchMode()) {
            case CSS: setPositionProvider(
                    new CssTranslatePositionProvider(logger, this.driver));
                break;
            default: setPositionProvider(
                    new ScrollPositionProvider(logger, this.driver));
        }

        this.driver.setRotation(rotation);
        return this.driver;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #open(WebDriver, String, String, RectangleSize)}.
     * {@code viewportSize} defaults to {@code null}.
     */
    protected WebDriver open(WebDriver driver, String appName, String testName,
                          SessionType sessionType) {
        return open(driver, appName, testName, null, sessionType);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkWindow(String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkWindow() {
        checkWindow(null);
    }

    /**
     * See {@link #checkWindow(int, String)}.
     * Default match timeout is used.
     *
     * @param tag An optional tag to be associated with the snapshot.
     */
    public void checkWindow(String tag) {
        checkWindow(USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Takes a snapshot of the application under test and matches it with
     * the expected output.
     *
     * @param matchTimeout The amount of time to retry matching
     *                     (Milliseconds).
     * @param tag An optional tag to be associated with the snapshot.
     * @throws TestFailedException Thrown if a mismatch is detected and
     *                             immediate failure reports are enabled.
     */
    public void checkWindow(int matchTimeout, String tag) {

        if (getIsDisabled()) {
            logger.log(String.format("CheckWindow(%d, '%s'): Ignored",
                    matchTimeout, tag));
            return;
        }

        logger.log(String.format("CheckWindow(%d, '%s')", matchTimeout,
                tag));

        super.checkWindowBase(
                new RegionProvider() {
                    public Region getRegion() {
                        return Region.EMPTY;
                    }

                    public CoordinatesType getCoordinatesType() {
                        return null;
                    }
                },
                tag,
                false,
                matchTimeout
        );
    }

    /**
     * Runs a test on the current window.
     *
     * @param driver         The web driver that controls the browser hosting
     *                       the application under test.
     * @param appName        The name of the application under test.
     * @param testName       The test name (will also be used as the tag name
     *                       for the step).
     * @param viewportSize   The required browser's viewport size
     *                       (i.e., the visible part of the document's body) or
     *                       {@code null} to use the current window's viewport.
     */
    public void testWindow(WebDriver driver, String appName, String testName,
                           RectangleSize viewportSize) {
        open(driver, appName, testName, viewportSize);
        try {
            checkWindow(testName);
            close();
        } finally {
            abortIfNotClosed();
        }
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testWindow(WebDriver, String, String, RectangleSize)}.
     * {@code viewportSize} defaults to {@code null}.
     */
    public void testWindow(WebDriver driver, String appName, String testName) {
        testWindow(driver, appName, testName, null);
    }

    /**
     * See {@link #testWindow(WebDriver, String, String, RectangleSize)}.
     * {@code appName} defaults to {@code null} (which means the name set in
     * {@link #setAppName(String)} would be used.
     */
    public void testWindow(WebDriver driver, String testName,
                           RectangleSize viewportSize) {
        testWindow(driver, null, testName, viewportSize);
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testWindow(WebDriver, String, RectangleSize)}.
     * {@code viewportSize} defaults to {@code null}.
     */
    public void testWindow(WebDriver driver, String testName) {
        testWindow(driver, testName, (RectangleSize) null);
    }

    @SuppressWarnings("unused")
    /**
     * Run a visual performance test.
     * @param driver The driver to use.
     * @param appName The name of the application being tested.
     * @param testName The test name.
     * @param action Actions to be performed in parallel to starting the test.
     * @param deadline The expected time until the application
     *                        should have been loaded. (Seconds)
     * @param timeout The maximum time until the application should have been
     *                   loaded. (Seconds)
     */
    public void testResponseTime(final WebDriver driver, String appName,
                                 String testName, final WebDriverAction action,
                                 int deadline, int timeout) {
        open(driver, appName, testName, SessionType.PROGRESSION);
        Runnable runnableAction = null;
        if (action != null) {
            runnableAction = new Runnable() {
                public void run() {
                    action.drive(driver);
                }
            };
        }

        MatchWindowDataWithScreenshot result =
            super.testResponseTimeBase(
                new RegionProvider() {
                    public Region getRegion() {
                        return Region.EMPTY;
                    }

                    public CoordinatesType getCoordinatesType() {
                        return null;
                    }
                },
                runnableAction,
                deadline,
                timeout,
                5000);

        logger.verbose("Checking if deadline was exceeded...");
        boolean deadlineExceeded = true;
        if (result != null) {
            TimedAppOutput tao =
                    (TimedAppOutput) result.getMatchWindowData().getAppOutput();
            long resultElapsed = tao.getElapsed();
            long deadlineMs = deadline * 1000;
            logger.verbose(String.format(
                    "Deadline: %d, Elapsed time for match: %d",
                    deadlineMs, resultElapsed));
            deadlineExceeded = resultElapsed > deadlineMs;
        }
        logger.verbose("Deadline exceeded? "+ deadlineExceeded);

        closeResponseTime(deadlineExceeded);
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int)}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, WebDriverAction action,
                                 int deadline) {
        testResponseTime(driver, appName, testName, action, deadline,
                (deadline + RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE));
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int)}.
     * {@code deadline} defaults to {@link #RESPONSE_TIME_DEFAULT_DEADLINE}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     *
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, WebDriverAction action) {
        testResponseTime(driver, appName, testName, action,
                RESPONSE_TIME_DEFAULT_DEADLINE,
                (RESPONSE_TIME_DEFAULT_DEADLINE +
                        RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE));
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int)}.
     * {@code action} defaults to {@code null}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline) {
        testResponseTime(driver, appName, testName, null, deadline,
                (deadline + RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE));
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int)}.
     * {@code deadline} defaults to {@link #RESPONSE_TIME_DEFAULT_DEADLINE}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     * {@code action} defaults to {@code null}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName) {
        testResponseTime(driver, appName, testName, null,
                RESPONSE_TIME_DEFAULT_DEADLINE,
                (RESPONSE_TIME_DEFAULT_DEADLINE +
                        RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE));
    }

    @SuppressWarnings("unused")
    /**
     * Similar to {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int)},
     * except this method sets the viewport size before starting the
     * performance test.
     *
     * @param viewportSize The required viewport size.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, WebDriverAction action,
                                 int deadline, int timeout,
                                 RectangleSize viewportSize) {
        // Notice we specifically use the setViewportSize overload which does
        // not handle frames (as we want to make sure this is as fast as
        // possible).
        setViewportSize(driver, viewportSize);

        testResponseTime(driver, appName, testName, action, deadline, timeout);
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int, RectangleSize)}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, WebDriverAction action,
                                 int deadline, RectangleSize viewportSize) {
        testResponseTime(driver, appName, testName, action, deadline,
                (deadline + RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE),
                viewportSize);
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int, RectangleSize)}.
     * {@code deadline} defaults to {@link #RESPONSE_TIME_DEFAULT_DEADLINE}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, WebDriverAction action,
                                 RectangleSize viewportSize) {
        testResponseTime(driver, appName, testName, action,
                RESPONSE_TIME_DEFAULT_DEADLINE,
                (RESPONSE_TIME_DEFAULT_DEADLINE +
                        RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE),
                viewportSize);
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, WebDriverAction, int, int, RectangleSize)}.
     * {@code action} defaults to {@code null}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline, int timeout,
                                 RectangleSize viewportSize) {
        testResponseTime(driver, appName, testName, null, deadline, timeout,
                viewportSize);
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, int, int, RectangleSize)}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline,
                                 RectangleSize viewportSize) {
        testResponseTime(driver, appName, testName, deadline,
                (deadline + RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE),
                viewportSize);
    }

    @SuppressWarnings("unused")
    /**
     * See {@link #testResponseTime(WebDriver, String, String, int, int, RectangleSize)}.
     * {@code deadline} defaults to {@link #RESPONSE_TIME_DEFAULT_DEADLINE}.
     * {@code timeout} defaults to {@link #RESPONSE_TIME_DEFAULT_TIMEOUT}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, RectangleSize viewportSize) {
        testResponseTime(driver, appName, testName,
                RESPONSE_TIME_DEFAULT_DEADLINE,
                (RESPONSE_TIME_DEFAULT_DEADLINE +
                        RESPONSE_TIME_DEFAULT_DIFF_FROM_DEADLINE),
                viewportSize);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegion(Region, int, String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkRegion(Region region) {
        checkRegion(region, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Takes a snapshot of the application under test and matches a specific
     * region within it with the expected output.
     *
     * @param region       A non empty region representing the screen region to
     *                     check.
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException Thrown if a mismatch is detected and
     *                             immediate failure reports are enabled.
     */
    public void checkRegion(final Region region, int matchTimeout, String tag) {

        if (getIsDisabled()) {
            logger.log(String.format("CheckRegion([%s], %d, '%s'): Ignored",
                    region, matchTimeout, tag));
            return;
        }

        ArgumentGuard.notNull(region, "region");

        logger.verbose(String.format("CheckRegion([%s], %d, '%s')", region,
                matchTimeout, tag));

        super.checkWindowBase(
                new RegionProvider() {

                    public Region getRegion() {
                        return region;
                    }

                    public CoordinatesType getCoordinatesType() {
                        // If we're given a region, it is relative to the
                        // frame's viewport.
                        return CoordinatesType.CONTEXT_AS_IS;
                    }
                },
                tag,
                false,
                matchTimeout
        );
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegion(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkRegion(WebElement element) {
        checkRegion(element, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(org.openqa.selenium.WebElement)}, otherwise
     * behaves the same as {@link #checkElement(WebElement)}.
     */
    public void checkRegion(WebElement element, boolean stitchContent) {
        if (stitchContent) {
            checkElement(element);
        } else {
            checkRegion(element);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegion(WebElement, int, String)}.
     * Default match timeout is used.
     */
    public void checkRegion(WebElement element, String tag) {
        checkRegion(element, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(org.openqa.selenium.WebElement, String)}. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     */
    public void checkRegion(WebElement element, String tag,
                            boolean stitchContent) {
        if (stitchContent) {
            checkElement(element, tag);
        } else {
            checkRegion(element, tag);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Takes a snapshot of the application under test and matches a region of
     * a specific element with the expected region output.
     *
     * @param element      The element which represents the region to check.
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException if a mismatch is detected and
     *                             immediate failure reports are enabled
     */
    public void checkRegion(final WebElement element, int matchTimeout,
                            String tag) {
        if (getIsDisabled()) {
            logger.log(String.format("CheckRegion(element, %d, '%s'): Ignored",
                    matchTimeout, tag));
            return;
        }

        ArgumentGuard.notNull(element, "element");

        logger.log(String.format("CheckRegion(element, %d, '%s')",
                matchTimeout, tag));

        // If needed, scroll to the top/left of the element (additional help
        // to make sure it's visible).
        Point locationAsPoint = element.getLocation();
        regionVisibilityStrategy.moveToRegion(positionProvider,
                new Location(locationAsPoint.getX(), locationAsPoint.getY()));

        super.checkWindowBase(
                new RegionProvider() {

                    public Region getRegion() {
                        Point p = element.getLocation();
                        Dimension d = element.getSize();
                        return new Region(p.getX(), p.getY(), d.getWidth(),
                                d.getHeight());
                    }

                    public CoordinatesType getCoordinatesType() {
                        // If we're given a region, it is relative to the
                        // frame's viewport.
                        return CoordinatesType.CONTEXT_RELATIVE;
                    }
                },
                tag,
                false,
                matchTimeout
        );
        logger.verbose("Done! trying to scroll back to original position..");
        regionVisibilityStrategy.returnToOriginalPosition(positionProvider);
        logger.verbose("Done!");
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(org.openqa.selenium.WebElement, int, String)}. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     */
    public void checkRegion(WebElement element, int matchTimeout,
                            String tag, boolean stitchContent) {
        if (stitchContent) {
            checkElement(element, matchTimeout, tag);
        } else {
            checkRegion(element, matchTimeout, tag);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegion(By, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkRegion(By selector) {
        checkRegion(selector, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(org.openqa.selenium.By)}. Otherwise, behaves the
     * same as {@code #checkElement(org.openqa.selenium.By)}
     */
    public void checkRegion(By selector, boolean stitchContent) {
        if (stitchContent) {
            checkElement(selector);
        } else {
            checkRegion(selector);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegion(By, int, String)}.
     * Default match timeout is used.
     */
    public void checkRegion(By selector, String tag) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(org.openqa.selenium.By, String)}. Otherwise,
     * behaves the same as {@link #checkElement(By, String)}.
     */
    public void checkRegion(By selector, String tag, boolean stitchContent) {
        if (stitchContent) {
            checkElement(selector, tag);
        } else {
            checkRegion(selector, tag);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Takes a snapshot of the application under test and matches a region
     * specified by the given selector with the expected region output.
     *
     * @param selector     Selects the region to check.
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and
     *                             immediate failure reports are enabled
     */
    public void checkRegion(By selector, int matchTimeout, String tag) {

        if (getIsDisabled()) {
            logger.log(String.format("CheckRegion(selector, %d, '%s'): Ignored",
                    matchTimeout, tag));
            return;
        }

        checkRegion(driver.findElement(selector), matchTimeout, tag);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(org.openqa.selenium.By, int, String)}. Otherwise,
     * behaves the same as {@link #checkElement(By, int, String)}.
     */
    public void checkRegion(By selector, int matchTimeout, String tag,
                            boolean stitchContent) {
        if (stitchContent) {
            checkElement(selector, matchTimeout, tag);
        } else {
            checkRegion(selector, matchTimeout, tag);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(int, By, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkRegionInFrame(int frameIndex, By selector) {
        checkRegionInFrame(frameIndex, selector, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(int, By, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkRegionInFrame(int frameIndex, By selector, boolean
            stitchContent) {
        checkRegionInFrame(frameIndex, selector, null, stitchContent);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(int, By, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     */
    public void checkRegionInFrame(int frameIndex, By selector, String tag) {
        checkRegionInFrame(frameIndex, selector, tag, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(int, By, int, String, boolean)}.
     * Default match timeout is used.
     */
    public void checkRegionInFrame(int frameIndex, By selector, String tag,
                                   boolean stitchContent) {
        checkRegionInFrame(frameIndex, selector, USE_DEFAULT_MATCH_TIMEOUT,
                tag, stitchContent);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(int, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     */
    public void checkRegionInFrame(int frameIndex, By selector,
                                   int matchTimeout, String tag) {
        checkRegionInFrame(frameIndex, selector, matchTimeout, tag, false);
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param frameIndex   The index of the frame to switch to. (The same index
     *                     as would be used in a call to
     *                     driver.switchTo().frame()).
     * @param selector     A Selector specifying the region to check.
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(int frameIndex, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        if (getIsDisabled()) {
            logger.log(String.format(
                    "CheckRegionInFrame(%d, selector, %d, '%s'): Ignored",
                    frameIndex, matchTimeout, tag));
            return;
        }
        driver.switchTo().frame(frameIndex);
        if (stitchContent) {
            checkElement(selector, matchTimeout, tag);
        } else {
            checkRegion(selector, matchTimeout, tag);
        }
        driver.switchTo().parentFrame();
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(String, By)}.
     * {@code stitchContent} defaults to {@code null}.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector) {
        checkRegionInFrame(frameNameOrId, selector, false);
    }

    /**
     * See {@link #checkRegionInFrame(String, By, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   boolean stitchContent) {
        checkRegionInFrame(frameNameOrId, selector, null, stitchContent);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   String tag) {
        checkRegionInFrame(frameNameOrId, selector, USE_DEFAULT_MATCH_TIMEOUT,
                tag, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * Default match timeout is used
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   String tag, boolean stitchContent) {
        checkRegionInFrame(frameNameOrId, selector, USE_DEFAULT_MATCH_TIMEOUT,
                tag, stitchContent);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   int matchTimeout, String tag) {
        checkRegionInFrame(frameNameOrId, selector, matchTimeout, tag, false);
    }


    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param matchTimeout  The amount of time to retry matching.
     *                      (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        if (getIsDisabled()) {
            logger.log(String.format(
                    "CheckRegionInFrame('%s', selector, %d, '%s'): Ignored",
                    frameNameOrId, matchTimeout, tag));
            return;
        }
        driver.switchTo().frame(frameNameOrId);
        if (stitchContent) {
            checkElement(selector, matchTimeout, tag);
        } else {
            checkRegion(selector, matchTimeout, tag);
        }
        driver.switchTo().parentFrame();
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(WebElement, By, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector) {
        checkRegionInFrame(frameReference, selector, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   boolean stitchContent) {
        checkRegionInFrame(frameReference, selector, null, stitchContent);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   String tag) {
        checkRegionInFrame(frameReference, selector, tag, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * Default match timeout is used.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   String tag, boolean stitchContent) {
        checkRegionInFrame(frameReference, selector, USE_DEFAULT_MATCH_TIMEOUT,
                tag, stitchContent);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag) {
        checkRegionInFrame(frameReference, selector, matchTimeout, tag, false);
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param frameReference    The element which is the frame to switch to. (as
     *                          would be used in a call to
     *                          driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param matchTimeout  The amount of time to retry matching.
     *                      (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        if (getIsDisabled()) {
            logger.log(String.format(
                    "CheckRegionInFrame(frame, selector, %d, '%s'): Ignored",
                    matchTimeout, tag));
            return;
        }
        driver.switchTo().frame(frameReference);
        if (stitchContent) {
            checkElement(selector, matchTimeout, tag);
        } else {
            checkRegion(selector, matchTimeout, tag);
        }
        driver.switchTo().parentFrame();
    }


    /**
     * Updates the state of scaling related parameters.
     */
    protected ScaleProviderFactory updateScalingParams() {
        // Update the scaling params only if we haven't done so yet, and the user hasn't set anything else manually.
        if (devicePixelRatio == UNKNOWN_DEVICE_PIXEL_RATIO &&
                scaleProviderHandler.get() instanceof NullScaleProvider) {
            ScaleProviderFactory factory;
            logger.verbose("Trying to extract device pixel ratio...");
            try {
                devicePixelRatio =
                        EyesSeleniumUtils.getDevicePixelRatio(driver);
            } catch (Exception e) {
                logger.verbose(
                        "Failed to extract device pixel ratio! Using default.");
                devicePixelRatio = DEFAULT_DEVICE_PIXEL_RATIO;
            }
            logger.verbose(String.format("Device pixel ratio: %f",
                    devicePixelRatio));

            logger.verbose("Setting scale provider..");
            try {
                factory = new ContextBasedScaleProviderFactory(positionProvider.getEntireSize(), getViewportSize(),
                        devicePixelRatio, scaleProviderHandler);
            } catch (Exception e) {
                // This can happen in Appium for example.
                logger.verbose("Failed to set ContextBasedScaleProvider.");
                logger.verbose("Using FixedScaleProvider instead...");
                factory = new FixedScaleProviderFactory(1/devicePixelRatio,
                        scaleProviderHandler);
            }
            logger.verbose("Done!");
            return factory;
        }
        // If we already have a scale provider set, we'll just use it, and pass a mock as provider handler.
        PropertyHandler<ScaleProvider> nullProvider = new SimplePropertyHandler<>();
        return new ScaleProviderIdentityFactory(scaleProviderHandler.get(), nullProvider);
    }

    /**
     * Verifies the current frame.
     *
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag An optional tag to be associated with the snapshot.
     */
    protected void checkCurrentFrame(int matchTimeout, String tag) {
        try {
            logger.verbose(String.format("CheckCurrentFrame(%d, '%s')",
                    matchTimeout, tag));

            checkFrameOrElement = true;

            logger.verbose("Getting screenshot as base64..");
            String screenshot64 = driver.getScreenshotAs(OutputType.BASE64);
            logger.verbose("Done! Creating image object...");
            BufferedImage screenshotImage =
                    ImageUtils.imageFromBase64(screenshot64);

            // FIXME - Scaling should be handled in a single place instead
            ScaleProvider scaleProvider = updateScalingParams().getScaleProvider(screenshotImage.getWidth());

            screenshotImage = ImageUtils.scaleImage(screenshotImage, scaleProvider);
            logger.verbose("Done! Building required object...");
            final EyesWebDriverScreenshot screenshot =
                    new EyesWebDriverScreenshot(logger, driver,
                            screenshotImage);
            logger.verbose("Done!");

            regionToCheck = new RegionProvider() {
                public Region getRegion() {
                    return screenshot.getFrameWindow();
                }

                public CoordinatesType getCoordinatesType() {
                    return CoordinatesType.SCREENSHOT_AS_IS;
                }
            };

            super.checkWindowBase(
                    new RegionProvider() {
                        public Region getRegion() {
                            return Region.EMPTY;
                        }

                        public CoordinatesType getCoordinatesType() {
                            return null;
                        }
                    },
                    tag,
                    false,
                    matchTimeout
            );
        } finally {
            checkFrameOrElement = false;
            regionToCheck = null;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(String, int, String)}.
     * {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId) {
        checkFrame(frameNameOrId, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(String, int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId, String tag) {
        checkFrame(frameNameOrId, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     *
     * @param frameNameOrId The name or id of the frame to check. (The same
     *                      name/id as would be used in a call to
     *                   driver.switchTo().frame()).
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag An optional tag to be associated with the match.
     */
    public void checkFrame(String frameNameOrId, int matchTimeout, String tag) {
        if (getIsDisabled()) {
            logger.log(String.format("CheckFrame(%s, %d, '%s'): Ignored",
                    frameNameOrId, matchTimeout, tag));
            return;
        }

        ArgumentGuard.notNull(frameNameOrId, "frameNameOrId");

        logger.log(String.format("CheckFrame(%s, %d, '%s')",
                frameNameOrId, matchTimeout, tag));

        logger.verbose("Switching to frame with name/id: " + frameNameOrId +
                " ...");
        driver.switchTo().frame(frameNameOrId);
        logger.verbose("Done.");

        checkCurrentFrame(matchTimeout, tag);

        logger.verbose("Switching back to parent frame");
        driver.switchTo().parentFrame();
        logger.verbose("Done!");
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(int, int, String)}.
     * {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(int frameIndex) {
        checkFrame(frameIndex, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(int, int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(int frameIndex, String tag) {
        checkFrame(frameIndex, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     *
     * @param frameIndex   The index of the frame to switch to. (The same index
     *                     as would be used in a call to
     *                     driver.switchTo().frame()).
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag An optional tag to be associated with the match.
     */
    public void checkFrame(int frameIndex, int matchTimeout, String tag) {
        if (getIsDisabled()) {
            logger.log(String.format("CheckFrame(%d, %d, '%s'): Ignored",
                    frameIndex, matchTimeout, tag));
            return;
        }

        ArgumentGuard.greaterThanOrEqualToZero(frameIndex, "frameIndex");

        logger.log(String.format("CheckFrame(%d, %d, '%s')",
                frameIndex, matchTimeout, tag));

        logger.verbose("Switching to frame with index: " + frameIndex + " ...");
        driver.switchTo().frame(frameIndex);
        logger.verbose("Done!");

        checkCurrentFrame(matchTimeout, tag);

        logger.verbose("Switching back to parent frame...");
        driver.switchTo().parentFrame();
        logger.verbose("Done!");

    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(WebElement, int, String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkFrame(WebElement frameReference) {
        checkFrame(frameReference, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(WebElement, int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(WebElement frameReference, String tag) {
        checkFrame(frameReference, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     *
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame() ).
     * @param matchTimeout The amount of time to retry matching (milliseconds).
     * @param tag An optional tag to be associated with the match.
     */
    public void checkFrame(WebElement frameReference, int matchTimeout,
                           String tag) {
        if (getIsDisabled()) {
            logger.log(String.format("checkFrame(element, %d, '%s'): Ignored",
                    matchTimeout, tag));
            return;
        }

        ArgumentGuard.notNull(frameReference, "frameReference");

        logger.log(String.format("CheckFrame(element, %d, '%s')",
                matchTimeout, tag));

        logger.verbose("Switching to frame based on element reference...");
        driver.switchTo().frame(frameReference);
        logger.verbose("Done!");

        checkCurrentFrame(matchTimeout, tag);

        logger.verbose("Switching back to parent frame...");
        driver.switchTo().parentFrame();
        logger.verbose("Done!");
    }

    /**
     * Matches the frame given by the frames path, by switching into the frame
     * and using stitching to get an image of the frame.
     * @param framePath The path to the frame to check. This is a list of
     *                  frame names/IDs (where each frame is nested in the
     *                  previous frame).
     * @param matchTimeout The amount of time to retry matching (milliseconds).
     * @param tag An optional tag to be associated with the match.
     */
    public void checkFrame(String[] framePath, int matchTimeout, String tag) {
        if (getIsDisabled()) {
            logger.log(String.format(
                    "checkFrame(framePath, %d, '%s'): Ignored",
                    matchTimeout,
                    tag));
            return;
        }
        ArgumentGuard.notNull(framePath, "framePath");
        ArgumentGuard.greaterThanZero(framePath.length, "framePath.length");
        logger.log(String.format(
                "checkFrame(framePath, %d, '%s')", matchTimeout, tag));
        FrameChain originalFrameChain = driver.getFrameChain();
        // We'll switch into the PARENT frame of the frame we want to check,
        // and call check frame.
        logger.verbose("Switching to parent frame according to frames path..");
        String[] parentFramePath = new String[framePath.length-1];
        System.arraycopy(framePath, 0, parentFramePath, 0,
                parentFramePath.length);
        ((EyesTargetLocator)(driver.switchTo())).frames(parentFramePath);
        logger.verbose("Done! Calling checkFrame..");
        checkFrame(framePath[framePath.length - 1], matchTimeout, tag);
        logger.verbose("Done! switching to default content..");
        driver.switchTo().defaultContent();
        logger.verbose("Done! Switching back into the original frame..");
        ((EyesTargetLocator)(driver.switchTo())).frames(originalFrameChain);
        logger.verbose("Done!");
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(String[] framesPath, String tag) {
        checkFrame(framesPath, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     * {@code tag} defaults to {@code null}.
     */
    public void checkFrame(String[] framesPath) {
        checkFrame(framesPath, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param framePath The path to the frame to check. This is a list of
     *                  frame names/IDs (where each frame is nested in the
     *                  previous frame).
     * @param selector A Selector specifying the region to check.
     * @param matchTimeout The amount of time to retry matching (milliseconds).
     * @param tag An optional tag to be associated with the snapshot.
     * @param stitchContent Whether or not to stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(String[] framePath, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        if (getIsDisabled()) {
            logger.log(String.format(
                "checkRegionInFrame(framePath, selector, %d, '%s'): Ignored",
                matchTimeout, tag));
            return;
        }
        ArgumentGuard.notNull(framePath, "framePath");
        ArgumentGuard.greaterThanZero(framePath.length, "framePath.length");
        logger.log(String.format(
                "checkFrame(framePath, %d, '%s')", matchTimeout, tag));
        FrameChain originalFrameChain = driver.getFrameChain();
        // We'll switch into the PARENT frame of the frame we want to check,
        // and call check frame.
        logger.verbose("Switching to parent frame according to frames path..");
        String[] parentFramePath = new String[framePath.length-1];
        System.arraycopy(framePath, 0, parentFramePath, 0,
                parentFramePath.length);
        ((EyesTargetLocator)(driver.switchTo())).frames(parentFramePath);
        logger.verbose("Done! Calling checkRegionInFrame..");
        checkRegionInFrame(framePath[framePath.length - 1], selector,
                matchTimeout, tag, stitchContent);
        logger.verbose("Done! switching back to default content..");
        driver.switchTo().defaultContent();
        logger.verbose("Done! Switching into the original frame..");
        ((EyesTargetLocator)(driver.switchTo())).frames(originalFrameChain);
        logger.verbose("Done!");
    }

    /**
     * See {@link #checkRegionInFrame(String[], By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     */
    public void checkRegionInFrame(String[] framePath, By selector,
                                   int matchTimeout, String tag) {
        checkRegionInFrame(framePath, selector, matchTimeout, tag, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(String[], By, int, String)}.
     * Default match timeout is used.
     */
    public void checkRegionInFrame(String[] framePath, By selector,
                                   String tag) {
        checkRegionInFrame(framePath, selector, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * See {@link #checkRegionInFrame(String[], By, int, String)}.
     * Default match timeout is used.
     * {@code tag} defaults to {@code null}.
     */
    public void checkRegionInFrame(String[] framePath, By selector) {
        checkRegionInFrame(framePath, selector, USE_DEFAULT_MATCH_TIMEOUT,
                null);
    }

    /**
     * See {@link #checkElement(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     */
    protected void checkElement(WebElement element) {
        checkElement(element, null);
    }

    /**
     * See {@link #checkElement(WebElement, int, String)}.
     * Default match timeout is used.
     */
    protected void checkElement(WebElement element, String tag) {
        checkElement(element, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Takes a snapshot of the application under test and matches a specific
     * element with the expected region output.
     *
     * @param element      The element to check.
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException if a mismatch is detected and
     *                             immediate failure reports are enabled
     */
    protected void checkElement(final WebElement element, int matchTimeout,
                             String tag) {
        String originalOverflow = null;
        EyesRemoteWebElement eyesElement;

        // Since the element might already have been found using EyesWebDriver.
        if (element instanceof EyesRemoteWebElement) {
            eyesElement = (EyesRemoteWebElement) element;
        } else {
            eyesElement = new EyesRemoteWebElement(logger,
                    driver, (RemoteWebElement) element);
        }

        PositionProvider originalPositionProvider = getPositionProvider();
        try {
            checkFrameOrElement = true;
            setPositionProvider(new ElementPositionProvider(logger, driver,
                    element));

            // Set overflow to "hidden".
            originalOverflow = eyesElement.getOverflow();
            eyesElement.setOverflow("hidden");

            Point p = eyesElement.getLocation();
            Dimension d = element.getSize();

            int borderLeftWidth = eyesElement.getBorderLeftWidth();
            int borderRightWidth = eyesElement.getBorderRightWidth();
            int borderTopWidth = eyesElement.getBorderTopWidth();
            int borderBottomWidth = eyesElement.getBorderBottomWidth();

             final Region elementRegion = new Region(
                    p.getX() + borderLeftWidth,
                    p.getY() + borderTopWidth,
                    d.getWidth() - borderLeftWidth - borderRightWidth,
                    d.getHeight() - borderTopWidth - borderBottomWidth);

            logger.verbose("Element region: " + elementRegion);

            regionToCheck = new RegionProvider() {
                public Region getRegion() {
                    return elementRegion;
                }

                public CoordinatesType getCoordinatesType() {
                    return CoordinatesType.CONTEXT_RELATIVE;
                }
            };
            super.checkWindowBase(
                    new RegionProvider() {
                        public Region getRegion() {
                            return Region.EMPTY;
                        }

                        public CoordinatesType getCoordinatesType() {
                            return null;
                        }
                    },
                    tag,
                    false,
                    matchTimeout
            );
        } finally {
            if (originalOverflow != null) {
                eyesElement.setOverflow(originalOverflow);
            }

            checkFrameOrElement = false;
            setPositionProvider(originalPositionProvider);
            regionToCheck = null;
        }
    }

    /**
     * See {@link #checkElement(By, String)}.
     * {@code tag} defaults to {@code null}.
     */
    protected void checkElement(By selector) {
        checkElement(selector, null);
    }

    /**
     * See {@link #checkElement(By, int, String)}.
     * Default match timeout is used.
     */
    protected void checkElement(By selector, String tag) {
        checkElement(selector, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Takes a snapshot of the application under test and matches an element
     * specified by the given selector with the expected region output.
     *
     * @param selector     Selects the element to check.
     * @param matchTimeout The amount of time to retry matching.
     *                     (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and
     *                             immediate failure reports are enabled
     */
    protected void checkElement(By selector, int matchTimeout, String tag) {

        if (getIsDisabled()) {
            logger.log(String.format(
                    "CheckElement(selector, %d, '%s'): Ignored",
                    matchTimeout, tag));
            return;
        }

        checkElement(driver.findElement(selector), matchTimeout, tag);
    }

    /**
     * Adds a mouse trigger.
     *
     * @param action  Mouse action.
     * @param control The control on which the trigger is activated (context
     *                relative coordinates).
     * @param cursor  The cursor's position relative to the control.
     */
    protected void addMouseTrigger(MouseAction action, Region control,
                                   Location cursor) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring %s (disabled)", action));
            return;
        }

        // Triggers are actually performed on the previous window.
        if (lastScreenshot == null) {
            logger.verbose(String.format("Ignoring %s (no screenshot)",
                    action));
            return;
        }

        if (!FrameChain.isSameFrameChain(driver.getFrameChain(),
                ((EyesWebDriverScreenshot) lastScreenshot).getFrameChain())) {
            logger.verbose(String.format("Ignoring %s (different frame)",
                    action));
            return;
        }

        addMouseTriggerBase(action, control, cursor);
    }

    /**
     * Adds a mouse trigger.
     *
     * @param action  Mouse action.
     * @param element The WebElement on which the click was called.
     */
    protected void addMouseTrigger(MouseAction action, WebElement element) {
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
            logger.verbose(String.format("Ignoring %s (no screenshot)",
                    action));
            return;
        }

        if (!FrameChain.isSameFrameChain(driver.getFrameChain(),
                ((EyesWebDriverScreenshot) lastScreenshot).getFrameChain())) {
            logger.verbose(String.format("Ignoring %s (different frame)",
                    action));
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
     *
     * @param control The control's context-relative region.
     * @param text    The trigger's text.
     */
    protected void addTextTrigger(Region control, String text) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring '%s' (disabled)", text));
            return;
        }

        if (lastScreenshot == null) {
            logger.verbose(String.format("Ignoring '%s' (no screenshot)",
                    text));
            return;
        }

        if (!FrameChain.isSameFrameChain(driver.getFrameChain(),
                ((EyesWebDriverScreenshot) lastScreenshot).getFrameChain())) {
            logger.verbose(String.format("Ignoring '%s' (different frame)",
                    text));
            return;
        }

        addTextTriggerBase(control, text);
    }

    /**
     * Adds a keyboard trigger.
     *
     * @param element The element for which we sent keys.
     * @param text    The trigger's text.
     */
    protected void addTextTrigger(WebElement element, String text) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring '%s' (disabled)", text));
            return;
        }

        ArgumentGuard.notNull(element, "element");

        Point pl = element.getLocation();
        Dimension ds = element.getSize();

        Region elementRegion = new Region(pl.getX(), pl.getY(), ds.getWidth(),
                ds.getHeight());

        addTextTrigger(elementRegion, text);
    }

    @Override
    /**
     * Use this method only if you made a previous call to {@link #open
     * (WebDriver, String, String)} or one of its variants.
     *
     * {@inheritDoc}
     */
    protected RectangleSize getViewportSize() {
        ArgumentGuard.isValidState(getIsOpen(), "Eyes not open");

        return driver.getDefaultContentViewportSize();
    }

    @SuppressWarnings("unused")
    /**
     * Call this method if for some
     * reason you don't want to call {@link #open(WebDriver, String, String)}
     * (or one of its variants) yet.
     *
     * @param driver The driver to use for getting the viewport.
     * @return The viewport size of the current context.
     */
    public static RectangleSize getViewportSize(WebDriver driver) {
        ArgumentGuard.notNull(driver, "driver");
        return EyesSeleniumUtils.getViewportSizeOrDisplaySize(new Logger(), driver);
    }

    @Override
    /**
     * Use this method only if you made a previous call to {@link #open
     * (WebDriver, String, String)} or one of its variants.
     *
     * {@inheritDoc}
     */
    protected void setViewportSize(RectangleSize size) {
        ArgumentGuard.isValidState(getIsOpen(), "Eyes not open");

        FrameChain originalFrame = driver.getFrameChain();
        driver.switchTo().defaultContent();

        try {
          EyesSeleniumUtils.setViewportSize(logger, driver, size);
        } catch (EyesException e) {
            // Just in case the user catches this error
            ((EyesTargetLocator) driver.switchTo()).frames(originalFrame);

            throw new TestFailedException("Failed to set the viewport size", e);
        }
        ((EyesTargetLocator) driver.switchTo()).frames(originalFrame);
        this.viewportSize = new RectangleSize(size.getWidth(),
                size.getHeight());
    }

    /**
     * Set the viewport size using the driver. Call this method if for some
     * reason you don't want to call {@link #open(WebDriver, String, String)}
     * (or one of its variants) yet.
     *
     * @param driver The driver to use for setting the viewport.
     * @param size The required viewport size.
     */
    public static void setViewportSize(WebDriver driver, RectangleSize size) {
        ArgumentGuard.notNull(driver, "driver");
        EyesSeleniumUtils.setViewportSize(new Logger(), driver, size);
    }

    @Override
    protected EyesScreenshot getScreenshot() {

        logger.verbose("getScreenshot()");
        EyesWebDriverScreenshot result;

        ScaleProviderFactory scaleProviderFactory = updateScalingParams();

        String originalOverflow = null;
        if (hideScrollbars) {
            try {
                originalOverflow =
                        EyesSeleniumUtils.hideScrollbars(driver, 200);
            } catch (EyesDriverOperationException e) {
                logger.log("WARNING: Failed to hide scrollbars! Error: "
                        + e.getMessage());
            }
        }
        try {
            ImageProvider imageProvider =
                    new TakesScreenshotImageProvider(logger, driver);
            EyesScreenshotFactory screenshotFactory =
                    new EyesWebDriverScreenshotFactory(logger, driver);
            if (checkFrameOrElement) {
                logger.verbose("Check frame/element requested");
                FullPageCaptureAlgorithm algo =
                        new FullPageCaptureAlgorithm(logger);
                BufferedImage entireFrameOrElement =
                        algo.getStitchedRegion(imageProvider, regionToCheck,
                                positionProvider, positionProvider,
                                scaleProviderFactory,
                                cutProviderHandler.get(),
                                getWaitBeforeScreenshots(), debugScreenshotsProvider, screenshotFactory);
                logger.verbose("Building screenshot object...");
                result = new EyesWebDriverScreenshot(logger, driver,
                        entireFrameOrElement,
                        new RectangleSize(entireFrameOrElement.getWidth(),
                                entireFrameOrElement.getHeight()));
            } else if (forceFullPageScreenshot) {
                logger.verbose("Full page screenshot requested.");
                // Save the current frame path.
                FrameChain originalFrame = driver.getFrameChain();
                driver.switchTo().defaultContent();
                FullPageCaptureAlgorithm algo =
                        new FullPageCaptureAlgorithm(logger);
                BufferedImage fullPageImage = algo.getStitchedRegion
                        (imageProvider,
                        new RegionProvider() {
                            public Region getRegion() {
                                return Region.EMPTY;
                            }

                            public CoordinatesType getCoordinatesType() {
                                return null;
                            }
                        },
                        new ScrollPositionProvider(logger, this.driver),
                        positionProvider, scaleProviderFactory,
                                cutProviderHandler.get(),
                        getWaitBeforeScreenshots(), debugScreenshotsProvider, screenshotFactory);

                ((EyesTargetLocator) driver.switchTo()).frames(originalFrame);
                result = new EyesWebDriverScreenshot(logger, driver,
                        fullPageImage);
            } else {
                logger.verbose("Screenshot requested...");
                String screenshot64 = driver.getScreenshotAs(OutputType.BASE64);
                logger.verbose("Done! Creating image object...");
                BufferedImage screenshotImage = ImageUtils.imageFromBase64(screenshot64);

                debugScreenshotsProvider.save(screenshotImage, "original");

                ScaleProvider scaleProvider = scaleProviderFactory.getScaleProvider(screenshotImage.getWidth());
                logger.verbose("Done!");
                screenshotImage = ImageUtils.scaleImage(screenshotImage, scaleProvider);

                debugScreenshotsProvider.save(screenshotImage, "scaled");

                screenshotImage = cutProviderHandler.get().cut(screenshotImage);

                debugScreenshotsProvider.save(screenshotImage, "cut");

                logger.verbose("Creating screenshot object...");
                result = new EyesWebDriverScreenshot(logger, driver, screenshotImage);
            }
            logger.verbose("Done!");
            return result;
        } finally {
            if (hideScrollbars) {
                try {
                    EyesSeleniumUtils.setOverflow(driver, originalOverflow);
                } catch (EyesDriverOperationException e) {
                    // Bummer, but we'll continue with the screenshot anyway :)
                    logger.log("WARNING: Failed to revert overflow! Error: "
                            + e.getMessage());
                }
            }
        }
    }

    @Override
    protected String getTitle() {
        if (!dontGetTitle) {
            try {
                return driver.getTitle();
            } catch (Exception ex) {
                logger.verbose("failed (" + ex.getMessage() + ")");
                dontGetTitle = true;
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
     *
     * This override also checks for mobile operating system.
     */
    @Override
    protected AppEnvironment getAppEnvironment() {

        AppEnvironment appEnv = super.getAppEnvironment();
        RemoteWebDriver underlyingDriver = driver.getRemoteWebDriver();
        // If hostOs isn't set, we'll try and extract and OS ourselves.
        if (appEnv.getOs() == null) {
            logger.log("No OS set, checking for mobile OS...");
            if (EyesSeleniumUtils.isMobileDevice(underlyingDriver)) {
                String platformName = null;
                logger.log("Mobile device detected! Checking device type..");
                if (EyesSeleniumUtils.isAndroid(underlyingDriver)) {
                    logger.log("Android detected.");
                    platformName = "Android";
                } else if (EyesSeleniumUtils.isIOS(underlyingDriver)) {
                    logger.log("iOS detected.");
                    platformName = "iOS";
                } else {
                    logger.log("Unknown device type.");
                }
                // We only set the OS if we identified the device type.
                if (platformName != null) {
                    String os = platformName;
                    String platformVersion =
                        EyesSeleniumUtils.getPlatformVersion(underlyingDriver);
                    if (platformVersion != null) {
                        String majorVersion =
                                platformVersion.split("\\.", 2)[0];

                        if (!majorVersion.isEmpty()) {
                            os += " " + majorVersion;
                        }
                    }

                    logger.verbose("Setting OS: " + os);
                    appEnv.setOs(os);
                    logger.verbose("Setting scale method for mobile.");
                }
            } else {
                logger.log("No mobile OS detected.");
            }
        }
        logger.log("Done!");
        return appEnv;
    }
}
