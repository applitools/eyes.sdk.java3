package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.locators.VisualLocatorsProvider;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.locators.SeleniumVisualLocatorsProvider;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.visualgrid.model.IDebugResourceWriter;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.ArgumentGuard;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * The type Eyes.
 */
public class Eyes implements IEyesBase {

    private static final int USE_DEFAULT_MATCH_TIMEOUT = -1;

    private boolean isVisualGridEyes = false;
    private VisualGridEyes visualGridEyes = null;
    private SeleniumEyes seleniumEyes;
    private ISeleniumEyes activeEyes;
    private EyesRunner runner = null;
    private Configuration configuration = new Configuration();
    private ImageRotation rotation;
    VisualLocatorsProvider visualLocatorsProvider;
    ConfigurationProvider configurationProvider = new ConfigurationProvider() {
        @Override
        public Configuration get() {
            return configuration;
        }
    };

    /**
     * Instantiates a new Eyes.
     */
    public Eyes() {
        seleniumEyes = new SeleniumEyes(configurationProvider, new ClassicRunner());
        activeEyes = seleniumEyes;
    }

    /**
     * Instantiates a new Eyes.
     * @param runner the runner
     */
    public Eyes(EyesRunner runner) {
        this();
        this.runner = runner == null ? new ClassicRunner() : runner;
        if (this.runner instanceof VisualGridRunner) {
            visualGridEyes = new VisualGridEyes((VisualGridRunner) this.runner, configurationProvider);
            activeEyes = visualGridEyes;
            isVisualGridEyes = true;
        } else {
            seleniumEyes = new SeleniumEyes(configurationProvider, (ClassicRunner) runner);
            activeEyes = seleniumEyes;
        }
    }

    private void initLocatorProvider(WebDriver webDriver) {
        if (!(webDriver instanceof EyesSeleniumDriver)) {
            webDriver = new EyesSeleniumDriver(getLogger(), seleniumEyes, (RemoteWebDriver) webDriver);
        }

        visualLocatorsProvider = new SeleniumVisualLocatorsProvider(
                seleniumEyes,
                (EyesSeleniumDriver) webDriver,
                getLogger(),
                getDebugScreenshotsProvider());
    }

    /**
     * Open web driver.
     * @param webDriver the web driver
     * @return the web driver
     */
    public WebDriver open(WebDriver webDriver) {
        if (activeEyes != seleniumEyes) {
            configuration.setIsVisualGrid(true);
            seleniumEyes.open(webDriver);
        }

        webDriver = activeEyes.open(webDriver);
        initLocatorProvider(webDriver);
        return webDriver;
    }

    /**
     * Starts a test.
     * @param driver   The web driver that controls the browser hosting the application under test.
     * @param appName  The name of the application under test.
     * @param testName The test name. (i.e., the visible part of the document's body) or {@code null} to use the current window's viewport.
     * @return A wrapped WebDriver which enables SeleniumEyes trigger recording and frame handling.
     */
    public WebDriver open(WebDriver driver, String appName, String testName) {
        if (activeEyes != seleniumEyes) {
            configuration.setIsVisualGrid(true);
            seleniumEyes.open(driver);
        }

        driver = activeEyes.open(driver, appName, testName, null);
        initLocatorProvider(driver);
        return driver;
    }

    /**
     * Starts a test.
     * @param driver       The web driver that controls the browser hosting the application under test.
     * @param appName      The name of the application under test.
     * @param testName     The test name.
     * @param viewportSize The required browser's viewport size (i.e., the visible part of the document's body) or {@code null} to use the current window's viewport.
     * @return A wrapped WebDriver which enables SeleniumEyes trigger recording and frame handling. {@code sessionType} defaults to {@code null}.
     */
    public WebDriver open(WebDriver driver, String appName, String testName,
                          RectangleSize viewportSize) {
        if (activeEyes != seleniumEyes) {
            configuration.setIsVisualGrid(true);
            seleniumEyes.open(driver);
        }

        driver = activeEyes.open(driver, appName, testName, viewportSize);
        initLocatorProvider(driver);
        return driver;
    }

    /**
     * Sets server url.
     * @param serverUrl the server url
     */
    public void setServerUrl(String serverUrl) {
        configuration.setServerUrl(serverUrl);
        activeEyes.serverUrl(serverUrl);
    }

    /**
     * Sets server url.
     * @param serverUri the server URI
     */
    public void setServerUrl(URI serverUri) {
        activeEyes.serverUrl(serverUri.toString());
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     * @param proxySettings The proxy settings to be used by the rest client.
     *                      If {@code null} then no proxy is set.
     */
    public void setProxy(AbstractProxySettings proxySettings) {
        configuration.setProxy(proxySettings);
        seleniumEyes.setProxy(proxySettings);
    }

    /**
     * Sets is disabled.
     * @param isDisabled If true, all interactions with this API will be silently ignored.
     */
    public void setIsDisabled(Boolean isDisabled) {
        activeEyes.setIsDisabled(isDisabled);
    }

    /**
     * Check.
     * @param checkSettings the check settings
     */
    public void check(ICheckSettings checkSettings) {
        check(null, checkSettings);
    }

    /**
     * See {@link #close(boolean)}.
     * {@code throwEx} defaults to {@code true}.
     * @return The test results.
     */
    public TestResults close() {
        return this.close(true);
    }

    /**
     * If a test is running, aborts it. Otherwise, does nothing.
     */
    public TestResults abortIfNotClosed() {
        return abort();
    }

    public TestResults abort() {
        return activeEyes.abort();
    }

    public void abortAsync() {
        activeEyes.abortAsync();
    }

    /**
     * Gets is disabled.
     * @return Whether eyes is disabled.
     */
    public boolean getIsDisabled() {
        return activeEyes.getIsDisabled();
    }

    /**
     * Gets api key.
     * @return the api key
     */
    public String getApiKey() {
        return activeEyes.getApiKey();
    }

    /**
     * Sets api key.
     * @param apiKey the api key
     */
    public void setApiKey(String apiKey) {
        // EyesBase sets the configuration
        if (seleniumEyes != activeEyes) {
            seleniumEyes.apiKey(apiKey);
        }
        activeEyes.apiKey(apiKey);
    }

    /**
     * Sets branch name.
     * @param branchName the branch name
     */
    public void setBranchName(String branchName) {
        configuration.setBranchName(branchName);
    }


    /**
     * Sets parent branch name.
     * @param branchName the branch name
     */
    public void setParentBranchName(String branchName) {
        configuration.setParentBranchName(branchName);
    }


    /**
     * Sets hide caret.
     * @param hideCaret the hide caret
     */
    public void setHideCaret(boolean hideCaret) {
        configuration.setHideCaret(hideCaret);
    }

    /**
     * Sets the maximum time (in ms) a match operation tries to perform a match.
     * @param ms Total number of ms to wait for a match.
     */
    public void setMatchTimeout(int ms) {
        this.configuration.setMatchTimeout(ms);
    }

    /**
     * Gets match timeout.
     * @return The maximum time in ms waits for a match.
     */
    public int getMatchTimeout() {
        return this.configuration.getMatchTimeout();
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

    /**
     * Set whether or not failed tests are saved by default.
     * @param saveFailedTests True if failed tests should be saved by default, false otherwise.
     */
    public void setSaveFailedTests(boolean saveFailedTests) {
        this.configuration.setSaveFailedTests(saveFailedTests);
    }

    /**
     * Gets save failed tests.
     * @return True if failed tests are saved by default.
     */
    public boolean getSaveFailedTests() {
        return this.configuration.getSaveNewTests();
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     * @param batch The batch info to set.
     */
    public void setBatch(BatchInfo batch) {
        this.configuration.setBatch(batch);
    }

    /**
     * Gets batch.
     * @return The currently set batch info.
     */
    public BatchInfo getBatch() {
        return configuration.getBatch();
    }


    /**
     * Sets failure reports.
     * @param failureReports The failure reports setting.
     * @see FailureReports
     */
    public void setFailureReports(FailureReports failureReports) {
        configuration.setFailureReports(failureReports);
    }


    /**
     * Gets failure reports.
     * @return the failure reports setting.
     */
    public FailureReports getFailureReports() {
        return this.configuration.getFailureReports();
    }

    /**
     * Updates the match settings to be used for the session.
     * @param defaultMatchSettings The match settings to be used for the session.
     */
    public void setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        configuration.setDefaultMatchSettings(defaultMatchSettings);
    }

    /**
     * Gets default match settings.
     * @return The match settings used for the session.
     */
    public ImageMatchSettings getDefaultMatchSettings() {
        return this.configuration.getDefaultMatchSettings();
    }

    /**
     * This function is deprecated. Please use {@link #setDefaultMatchSettings} instead.
     * <p>
     * The test-wide match level to use when checking application screenshot
     * with the expected output.
     * @param matchLevel The match level setting.
     * @see com.applitools.eyes.MatchLevel
     */
    public void setMatchLevel(MatchLevel matchLevel) {
        this.configuration.getDefaultMatchSettings().setMatchLevel(matchLevel);
    }

    /**
     * Gets match level.
     * @return The test-wide match level.
     * @deprecated Please use{@link #getDefaultMatchSettings} instead.
     */
    public MatchLevel getMatchLevel() {
        return this.configuration.getDefaultMatchSettings().getMatchLevel();
    }

    /**
     * Gets full agent id.
     * @return The full agent id composed of both the base agent id and the user given agent id.
     */
    public String getFullAgentId() {
        return activeEyes.getFullAgentId();
    }

    /**
     * Gets is open.
     * @return Whether a session is open.
     */
    public boolean isOpen() {
        return activeEyes.isOpen();
    }

    /**
     * Gets default server url.
     * @return the default server url
     */
    public static URI getDefaultServerUrl() {
        return SeleniumEyes.getDefaultServerUrl();
    }

    /**
     * Sets a handler of log messages generated by this API.
     * @param logHandler Handles log messages generated by this API.
     */
    public void setLogHandler(LogHandler logHandler) {
        activeEyes.setLogHandler(logHandler);
    }

    /**
     * Gets log handler.
     * @return The currently set log handler.
     */
    public LogHandler getLogHandler() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getLogHandler();
        } else {
            if (this.visualGridEyes.getLogger() != null) {
                return this.visualGridEyes.getLogger().getLogHandler();
            }
        }
        return null;
    }

    /**
     * Gets logger.
     * @return the logger
     */
    public Logger getLogger() {
        return activeEyes.getLogger();
    }

    /**
     * Manually set the the sizes to cut from an image before it's validated.
     * @param cutProvider the provider doing the cut.
     */
    public void setImageCut(CutProvider cutProvider) {
        this.seleniumEyes.setImageCut(cutProvider);
    }

    /**
     * Gets is cut provider explicitly set.
     * @return the is cut provider explicitly set
     */
    public boolean getIsCutProviderExplicitlySet() {
        return this.seleniumEyes.getIsCutProviderExplicitlySet();
    }

    /**
     * Check.
     * @param tag           the tag
     * @param checkSettings the check settings
     */
    public void check(String tag, ICheckSettings checkSettings) {
        activeEyes.check(tag, checkSettings);
    }

    /**
     * Close test results.
     * @param shouldThrowException the should throw exception
     * @return the test results
     */
    public TestResults close(boolean shouldThrowException) {
        return activeEyes.close(shouldThrowException);
    }

    /**
     * Manually set the scale ratio for the images being validated.
     * @param scaleRatio The scale ratio to use, or {@code null} to reset                   back to automatic scaling.
     */
    public void setScaleRatio(Double scaleRatio) {
        this.seleniumEyes.setScaleRatio(scaleRatio);
    }

    /**
     * Gets scale ratio.
     * @return The ratio used to scale the images being validated.
     */
    public double getScaleRatio() {
        return this.seleniumEyes.getScaleRatio();
    }

    /**
     * Adds a property to be sent to the server.
     * @param name  The property name.
     * @param value The property value.
     */
    public void addProperty(String name, String value) {
        activeEyes.addProperty(name, value);
    }

    /**
     * Clears the list of custom properties.
     */
    public void clearProperties() {
        activeEyes.clearProperties();
    }

    /**
     * Sets save debug screenshots.
     * @param saveDebugScreenshots If true, will save all screenshots to local directory.
     */
    public void setSaveDebugScreenshots(boolean saveDebugScreenshots) {
        this.seleniumEyes.setSaveDebugScreenshots(saveDebugScreenshots);
    }

    /**
     * Gets save debug screenshots.
     * @return True if screenshots saving enabled.
     */
    public boolean getSaveDebugScreenshots() {
        return seleniumEyes.getSaveDebugScreenshots();
    }

    /**
     * Sets debug screenshots path.
     * @param pathToSave Path where you want to save the debug screenshots.
     */
    public void setDebugScreenshotsPath(String pathToSave) {
        this.seleniumEyes.setDebugScreenshotsPath(pathToSave);
    }

    /**
     * Gets debug screenshots path.
     * @return The path where you want to save the debug screenshots.
     */
    public String getDebugScreenshotsPath() {
        return this.seleniumEyes.getDebugScreenshotsPath();
    }

    /**
     * Sets debug screenshots prefix.
     * @param prefix The prefix for the screenshots' names.
     */
    public void setDebugScreenshotsPrefix(String prefix) {
        this.seleniumEyes.setDebugScreenshotsPrefix(prefix);
    }

    /**
     * Gets debug screenshots prefix.
     * @return The prefix for the screenshots' names.
     */
    public String getDebugScreenshotsPrefix() {
        return this.seleniumEyes.getDebugScreenshotsPrefix();
    }

    /**
     * Gets debug screenshots provider.
     * @return the debug screenshots provider
     */
    public DebugScreenshotsProvider getDebugScreenshotsProvider() {
        return this.seleniumEyes.getDebugScreenshotsProvider();
    }

    /**
     * Gets ignore caret.
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    public boolean getIgnoreCaret() {
        return this.configuration.getIgnoreCaret();
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */
    public void setIgnoreCaret(boolean value) {
        this.configuration.setIgnoreCaret(value);
    }

    /**
     * Gets stitch overlap.
     * @return Returns the stitching overlap in pixels.
     */
    public int getStitchOverlap() {
        return this.configuration.getStitchOverlap();
    }

    /**
     * Sets the stitching overlap in pixels.
     * @param pixels The width (in pixels) of the overlap.
     */
    public void setStitchOverlap(int pixels) {
        this.configuration.setStitchOverlap(pixels);
    }

    /**
     * Check region.
     * @param region the region the check. See {@link #checkRegion(Region, int, String)}. {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkRegion(Region region) {
        checkRegion(region, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    /**
     * Takes a snapshot of the application under test and matches a specific region within it with the expected output.
     * @param region       A non empty region representing the screen region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    public void checkRegion(final Region region, int matchTimeout, String tag) throws TestFailedException {
        if (getIsDisabled()) {
            return;
        }

        ArgumentGuard.notNull(region, "region");
        check(Target.region(region).timeout(matchTimeout).withName(tag));
    }

    /**
     * See {@link #checkRegion(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     * @param element The element which represents the region to check.
     */
    public void checkRegion(WebElement element) {
        checkRegion(element, USE_DEFAULT_MATCH_TIMEOUT, null, true);
    }

    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(WebElement)}, otherwise
     * behaves the same as {@link #checkElement(WebElement)}.
     * @param element       The element which represents the region to check.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, boolean stitchContent) {
        checkRegion(element, USE_DEFAULT_MATCH_TIMEOUT, null, stitchContent);
    }

    /**
     * See {@link #checkRegion(WebElement, int, String)}.
     * Default match timeout is used.
     * @param element The element which represents the region to check.
     * @param tag     An optional tag to be associated with the snapshot.
     */
    public void checkRegion(WebElement element, String tag) {
        checkRegion(element, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(WebElement, String)}***. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     * @param element       The element which represents the region to check.
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, String tag, boolean stitchContent) {
        checkRegion(element, USE_DEFAULT_MATCH_TIMEOUT, tag, stitchContent);
    }

    /**
     * Takes a snapshot of the application under test and matches a region of
     * a specific element with the expected region output.
     * @param element      The element which represents the region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException if a mismatch is detected and                             immediate failure reports are enabled
     */
    public void checkRegion(final WebElement element, int matchTimeout, String tag) {
        checkRegion(element, matchTimeout, tag, true);
    }

    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(WebElement, int, String)}***. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     * @param element       The element which represents the region to check.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, int matchTimeout, String tag, boolean stitchContent) {
        if (getIsDisabled()) {
            return;
        }

        ArgumentGuard.notNull(element, "element");
        check(Target.region(element).timeout(matchTimeout).withName(tag).fully(stitchContent));
    }

    /**
     * See {@link #checkRegion(By, String)}.
     * {@code tag} defaults to {@code null}.
     * @param selector The selector by which to specify which region to check.
     */
    public void checkRegion(By selector) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, null, false);
    }

    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(By)}. Otherwise, behaves the
     * same as {@code #checkElement(org.openqa.selenium.By)}
     * @param selector      The selector by which to specify which region to check.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(By selector, boolean stitchContent) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, null, stitchContent);
    }

    /**
     * See {@link #checkRegion(By, int, String)}.
     * Default match timeout is used.
     * @param selector The selector by which to specify which region to check.
     * @param tag      An optional tag to be associated with the screenshot.
     */
    public void checkRegion(By selector, String tag) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, tag, true);
    }

    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(By, String)}. Otherwise,
     * behaves the same as {@link #checkElement(By, String)}.
     * @param selector      The selector by which to specify which region to check.
     * @param tag           An optional tag to be associated with the screenshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(By selector, String tag, boolean stitchContent) {
        checkRegion(selector, USE_DEFAULT_MATCH_TIMEOUT, tag, stitchContent);
    }

    /**
     * Takes a snapshot of the application under test and matches a region
     * specified by the given selector with the expected region output.
     * @param selector     The selector by which to specify which region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and                             immediate failure reports are enabled
     */
    public void checkRegion(By selector, int matchTimeout, String tag) {
        checkRegion(selector, matchTimeout, tag, true);
    }

    /**
     * If {@code stitchContent} is {@code true} then behaves the same as
     * {@link #checkRegion(By, int, String)}. Otherwise,
     * behaves the same as {@link #checkElement(By, int, String)}.
     * @param selector      The selector by which to specify which region to check.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the screenshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(By selector, int matchTimeout, String tag, boolean stitchContent) {
        check(tag, Target.region(selector).timeout(matchTimeout).fully(stitchContent));
    }

    /**
     * See {@link #checkRegionInFrame(int, By, String)}.
     * {@code tag} defaults to {@code null}.
     * @param frameIndex The index of the frame to switch to. (The same index                   as would be used in a call to                   driver.switchTo().frame()).
     * @param selector   The selector by which to specify which region to check inside the frame.
     */
    public void checkRegionInFrame(int frameIndex, By selector) {
        checkRegionInFrame(frameIndex, selector, null);
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     * @param framePath     The path to the frame to check. This is a list of                      frame names/IDs (where each frame is nested in the previous frame).
     * @param selector      A Selector specifying the region to check.
     * @param matchTimeout  The amount of time to retry matching (milliseconds).
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether or not to stitch the internal content of the                      region (i.e., perform {@link #checkElement(By, int, String)} on the region.
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
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     * @param frameIndex    The index of the frame to switch to. (The same index                      as would be used in a call to                      driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of                      the region (i.e., perform                      {@link #checkElement(By, int, String)} on the                      region.
     */
    public void checkRegionInFrame(int frameIndex, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        check(tag, Target.frame(frameIndex).region(selector).timeout(matchTimeout).fully(stitchContent));
    }

    /**
     * See {@link #checkRegionInFrame(int, By, String)}.
     * {@code tag} defaults to {@code null}.
     * @param frameIndex    The index of the frame to switch to. (The same index
     *                      as would be used in a call to
     *                      driver.switchTo().frame()).
     * @param selector      The selector by which to specify which region to check inside the frame.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(int frameIndex, By selector, boolean stitchContent) {
        checkRegionInFrame(frameIndex, selector, null, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(int, By, String, boolean)}.
     * {@code stitchContent} defaults to {@code true}.
     * @param frameIndex The index of the frame to switch to. (The same index
     *                   as would be used in a call to
     *                   driver.switchTo().frame()).
     * @param selector   The selector by which to specify which region to check inside the frame.
     * @param tag        An optional tag to be associated with the screenshot.
     */
    public void checkRegionInFrame(int frameIndex, By selector, String tag) {
        checkRegionInFrame(frameIndex, selector, USE_DEFAULT_MATCH_TIMEOUT, tag);

    }

    /**
     * See {@link #checkRegionInFrame(int, By, int, String, boolean)}.
     * Default match timeout is used.
     * @param frameIndex    The index of the frame to switch to. (The same index
     *                      as would be used in a call to
     *                      driver.switchTo().frame()).
     * @param selector      The selector by which to specify which region to check inside the frame.
     * @param tag           An optional tag to be associated with the screenshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(int frameIndex, By selector, String tag, boolean stitchContent) {
        checkRegionInFrame(frameIndex, selector, USE_DEFAULT_MATCH_TIMEOUT, tag, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(int, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code true}.
     * @param frameIndex   The index of the frame to switch to. (The same index
     *                     as would be used in a call to
     *                     driver.switchTo().frame()).
     * @param selector     The selector by which to specify which region to check inside the frame.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     */
    public void checkRegionInFrame(int frameIndex, By selector, int matchTimeout, String tag) {
        checkRegionInFrame(frameIndex, selector, matchTimeout, tag, true);
    }


    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector) {
        checkRegionInFrame(frameNameOrId, selector, true);
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector, boolean stitchContent) {
        checkRegionInFrame(frameNameOrId, selector, USE_DEFAULT_MATCH_TIMEOUT, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector, int matchTimeout, boolean stitchContent) {
        checkRegionInFrame(frameNameOrId, selector, matchTimeout, null, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param tag           An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector, String tag) {
        checkRegionInFrame(frameNameOrId, selector, USE_DEFAULT_MATCH_TIMEOUT, tag, true);
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * Default match timeout is used
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector, String tag, boolean stitchContent) {
        checkRegionInFrame(frameNameOrId, selector, USE_DEFAULT_MATCH_TIMEOUT, tag, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code true}.
     * @param frameNameOrId The name or id of the frame to switch to. (as would                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check inside the frame.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   int matchTimeout, String tag) {
        checkRegionInFrame(frameNameOrId, selector, matchTimeout, tag, true);
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     * @param frameNameOrId The name or id of the frame to switch to. (as would                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check inside the frame.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of                      the region (i.e., perform                      {@link #checkElement(By, int, String)} on the region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        check(tag, Target.frame(frameNameOrId).region(selector).timeout(matchTimeout).fully(stitchContent));
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector) {
        checkRegionInFrame(frameReference, selector, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param stitchContent  If {@code true}, stitch the internal content of                       the region (i.e., perform                       {@link #checkElement(By, int, String)} on the                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector, boolean stitchContent) {
        checkRegionInFrame(frameReference, selector, USE_DEFAULT_MATCH_TIMEOUT, null, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code stitchContent} defaults to {@code true}.
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param tag            An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector, String tag) {
        checkRegionInFrame(frameReference, selector, USE_DEFAULT_MATCH_TIMEOUT, tag, true);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * Default match timeout is used.
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param tag            An optional tag to be associated with the snapshot.
     * @param stitchContent  If {@code true}, stitch the internal content of                       the region (i.e., perform                       {@link #checkElement(By, int, String)} on the                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   String tag, boolean stitchContent) {
        checkRegionInFrame(frameReference, selector, USE_DEFAULT_MATCH_TIMEOUT, tag, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code true}.
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param matchTimeout   The amount of time to retry matching. (Milliseconds)
     * @param tag            An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag) {
        checkRegionInFrame(frameReference, selector, matchTimeout, tag, true);
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check.
     * @param matchTimeout   The amount of time to retry matching. (Milliseconds)
     * @param tag            An optional tag to be associated with the snapshot.
     * @param stitchContent  If {@code true}, stitch the internal content of                       the region (i.e., perform                       {@link #checkElement(By, int, String)} on the                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        check(tag, Target.frame(frameReference).region(selector).timeout(matchTimeout).fully(stitchContent));
    }

    /**
     * See {@link #checkElement(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     * @param element the element
     */
    public void checkElement(WebElement element) {
        checkElement(element, null);
    }

    /**
     * Check element.
     * @param element the element to check
     * @param tag     See {@link #checkElement(WebElement, int, String)}.                Default match timeout is used.
     */
    public void checkElement(WebElement element, String tag) {
        checkElement(element, USE_DEFAULT_MATCH_TIMEOUT, tag);
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
     * Check element.
     * @param selector the selector                 See {@link #checkElement(By, String)}.                 {@code tag} defaults to {@code null}.
     */
    public void checkElement(By selector) {
        checkElement(selector, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    /**
     * Check element.
     * @param selector selector
     * @param tag      tg                 See {@link #checkElement(By, int, String)}.                 Default match timeout is used.
     */
    public void checkElement(By selector, String tag) {
        checkElement(selector, USE_DEFAULT_MATCH_TIMEOUT, tag);
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
        this.seleniumEyes.addMouseTrigger(action, control, cursor);
    }

    /**
     * Adds a mouse trigger.
     * @param action  Mouse action.
     * @param element The WebElement on which the click was called.
     */
    public void addMouseTrigger(MouseAction action, WebElement element) {
        this.seleniumEyes.addMouseTrigger(action, element);
    }

    /**
     * Adds a keyboard trigger.
     * @param control The control's context-relative region.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(Region control, String text) {
        this.seleniumEyes.addTextTrigger(control, text);
    }

    /**
     * Adds a keyboard trigger.
     * @param element The element for which we sent keys.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(WebElement element, String text) {
        this.seleniumEyes.addTextTrigger(element, text);
    }

    /**
     * Use this method only if you made a previous call to {@link #open
     * (WebDriver, String, String)}*** or one of its variants.
     * <p>
     * {@inheritDoc}
     * @return the viewport size
     */
    public RectangleSize getViewportSize() {
        return this.seleniumEyes.getViewportSize();
    }

    /**
     * Call this method if for some
     * reason you don't want to call {@link #open(WebDriver, String, String)}
     * (or one of its variants) yet.
     * @param driver The driver to use for getting the viewport.
     * @return The viewport size of the current context.
     */
    public static RectangleSize getViewportSize(WebDriver driver) {
        return SeleniumEyes.getViewportSize(driver);
    }

    /**
     * Set the viewport size using the driver. Call this method if for some
     * reason you don't want to call {@link #open(WebDriver, String, String)}
     * (or one of its variants) yet.
     * @param driver The driver to use for setting the viewport.
     * @param size   The required viewport size.
     */
    public static void setViewportSize(WebDriver driver, RectangleSize size) {
        ArgumentGuard.notNull(driver, "driver");
        EyesDriverUtils.setViewportSize(new Logger(), driver, size);
    }

    /**
     * Gets hide caret.
     * @return gets the hide caret flag
     */
    public boolean getHideCaret() {
        return configuration.getHideCaret();
    }

    /**
     * Should stitch content boolean.
     * @return the should stitch flag
     */
    public boolean shouldStitchContent() {
        return seleniumEyes.shouldStitchContent();
    }

    /**
     * ﻿Forces a full page screenshot (by scrolling and stitching) if the
     * browser only ﻿supports viewport screenshots).
     * @param shouldForce Whether to force a full page screenshot or not.
     */
    public void setForceFullPageScreenshot(boolean shouldForce) {
        configuration.setForceFullPageScreenshot(shouldForce);
    }

    /**
     * Gets force full page screenshot.
     * @return Whether SeleniumEyes should force a full page screenshot.
     */
    public boolean getForceFullPageScreenshot() {
        Boolean forceFullPageScreenshot = configuration.getForceFullPageScreenshot();
        if (forceFullPageScreenshot == null) {
            return isVisualGridEyes;
        }
        return forceFullPageScreenshot;
    }

    /**
     * Sets the time to wait just before taking a screenshot (e.g., to allow
     * positioning to stabilize when performing a full page stitching).
     * @param waitBeforeScreenshots The time to wait (Milliseconds). Values                              smaller or equal to 0, will cause the                              default value to be used.
     */
    public void setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        this.configuration.setWaitBeforeScreenshots(waitBeforeScreenshots);
    }

    /**
     * Gets wait before screenshots.
     * @return The time to wait just before taking a screenshot.
     */
    public int getWaitBeforeScreenshots() {
        return this.configuration.getWaitBeforeScreenshots();
    }


    /**
     * Turns on/off the automatic scrolling to a region being checked by
     * {@code checkRegion}.
     * @param shouldScroll Whether to automatically scroll to a region being validated.
     */
    public void setScrollToRegion(boolean shouldScroll) {
        this.seleniumEyes.setScrollToRegion(shouldScroll);
    }

    /**
     * Gets scroll to region.
     * @return Whether to automatically scroll to a region being validated.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean getScrollToRegion() {
        return this.seleniumEyes.getScrollToRegion();
    }

    /**
     * Set the type of stitching used for full page screenshots. When the
     * page includes fixed position header/sidebar, use {@link StitchMode#CSS}.
     * Default is {@link StitchMode#SCROLL}.
     * @param mode The stitch mode to set.
     */
    public void setStitchMode(StitchMode mode) {
        this.configuration.setStitchMode(mode);
    }

    /**
     * Gets stitch mode.
     * @return The current stitch mode settings.
     */
    public StitchMode getStitchMode() {
        return this.configuration.getStitchMode();
    }

    /**
     * Hide the scrollbars when taking screenshots.
     * @param shouldHide Whether to hide the scrollbars or not.
     */
    public void setHideScrollbars(boolean shouldHide) {
        this.configuration.setHideScrollbars(shouldHide);
    }

    /**
     * Gets hide scrollbars.
     * @return Whether or not scrollbars are hidden when taking screenshots.
     */
    public boolean getHideScrollbars() {
        return this.configuration.getHideScrollbars();
    }

    /**
     * Gets rotation.
     * @return The image rotation model.
     */
    public ImageRotation getRotation() {
        return this.seleniumEyes.getRotation();
    }

    /**
     * Sets rotation.
     * @param rotation The image rotation model.
     */
    public void setRotation(ImageRotation rotation) {
        this.rotation = rotation;
        WebDriver driver = getDriver();
        if (driver != null) {
            ((EyesSeleniumDriver) driver).setRotation(rotation);
        }
    }

    /**
     * Gets device pixel ratio.
     * @return The device pixel ratio, or if the DPR is not known yet or if it wasn't possible to extract it.
     */
    public double getDevicePixelRatio() {
        return this.seleniumEyes.getDevicePixelRatio();
    }

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
     * @throws TestFailedException Thrown if a mismatch is detected and                             immediate failure reports are enabled.
     */
    public void checkWindow(int matchTimeout, String tag) {
        check(tag, Target.window().timeout(matchTimeout));
    }

    public void checkWindow(String tag, boolean fully) {
        check(tag, Target.window().fully(fully));

    }

    /**
     * Takes multiple screenshots at once (given all <code>ICheckSettings</code> objects are on the same level).
     * @param checkSettings Multiple <code>ICheckSettings</code> object representing different regions in the viewport.
     */
    public void check(ICheckSettings... checkSettings) {
        activeEyes.check(checkSettings);
    }

    /**
     * Check frame.
     * @param frameNameOrId frame to check(name or id) See {@link #checkFrame(String, int, String)}.
     *                      {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId) {
        checkFrame(frameNameOrId, null);
    }

    /**
     * Check frame.
     * @param frameNameOrId frame to check(name or id)
     * @param tag           See {@link #checkFrame(String, int, String)}.                      Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId, String tag) {
        checkFrame(frameNameOrId, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     * @param frameNameOrId The name or id of the frame to check. (The same                      name/id as would be used in a call to                      driver.switchTo().frame()).
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the match.
     */
    public void checkFrame(String frameNameOrId, int matchTimeout, String tag) {
        check(tag, Target.frame(frameNameOrId).fully().timeout(matchTimeout));
    }

    /**
     * Check frame.
     * @param frameIndex index of frame                   See {@link #checkFrame(int, int, String)}.                   {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(int frameIndex) {
        checkFrame(frameIndex, null);
    }

    /**
     * Check frame.
     * @param frameIndex index of frame
     * @param tag        See {@link #checkFrame(int, int, String)}.                   Default match timeout is used.
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
        check(tag, Target.frame(frameIndex).timeout(matchTimeout).fully());
    }

    /**
     * Check frame.
     * @param frameReference web element to check                       See {@link #checkFrame(WebElement, int, String)}.                       {@code tag} defaults to {@code null}.                       Default match timeout is used.
     */
    public void checkFrame(WebElement frameReference) {
        checkFrame(frameReference, USE_DEFAULT_MATCH_TIMEOUT, null);
    }

    /**
     * Check frame.
     * @param frameReference web element to check
     * @param tag            tag                       See {@link #checkFrame(WebElement, int, String)}.                       Default match timeout is used.
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
            settings = settings.frame(framePath[i]);
        }
        check(tag, settings.timeout(matchTimeout));
    }

    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     * @param framesPath the frames path
     * @param tag        the tag
     */
    public void checkFrame(String[] framesPath, String tag) {
        this.checkFrame(framesPath, USE_DEFAULT_MATCH_TIMEOUT, tag);
    }

    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     * {@code tag} defaults to {@code null}.
     * @param framesPath the frames path
     */
    public void checkFrame(String[] framesPath) {
        this.checkFrame(framesPath, null);
    }

    /**
     * Gets server url.
     * @return The URI of the eyes server.
     */
    public URI getServerUrl() {
        return activeEyes.getServerUrl();
    }

    /**
     * Sets the user given agent id of the SDK. {@code null} is referred to
     * as no id.
     * @param agentId The agent ID to set.
     */
    public void setAgentId(String agentId) {
        this.configuration.setAgentId(agentId);
    }

    /**
     * Gets agent id.
     * @return The user given agent id of the SDK.
     */
    public String getAgentId() {
        return this.configuration.getAgentId();
    }

    /**
     * Sets the server connector to use. MUST BE SET IN ORDER FOR THE EYES OBJECT TO WORK!
     * @param serverConnector The server connector object to use.
     */
    public void setServerConnector(ServerConnector serverConnector) {
        this.seleniumEyes.setServerConnector(serverConnector);
        if (this.isVisualGridEyes) {
            this.visualGridEyes.setServerConnector(serverConnector);
        }
    }


    /**
     * Gets proxy.
     * @return The current proxy settings used by the server connector, or {@code null} if no proxy is set.
     */
    public AbstractProxySettings getProxy() {
        return this.configuration.getProxy();
    }


    /**
     * Sets app name.
     * @param appName The name of the application under test.
     */
    public void setAppName(String appName) {
        this.configuration.setAppName(appName);
    }

    /**
     * Gets app name.
     * @return The name of the application under test.
     */
    public String getAppName() {
        return configuration.getAppName();
    }


    /**
     * Gets host os.
     * @return the host os
     */
    public String getHostOS() {
        return this.configuration.getHostOS();
    }

    /**
     * Gets host app.
     * @return The application name running the AUT.
     */
    public String getHostApp() {
        return this.configuration.getHostApp();
    }

    /**
     * Sets baseline name.
     * @param baselineName If specified, determines the baseline to compare                     with and disables automatic baseline inference.
     * @deprecated Only available for backward compatibility. See {@link #setBaselineEnvName(String)}.
     */
    public void setBaselineName(String baselineName) {
        setBaselineEnvName(baselineName);
    }

    /**
     * Gets baseline name.
     * @return The baseline name, if specified.
     * @deprecated Only available for backward compatibility. See {@link #getBaselineEnvName()}.
     */
    @SuppressWarnings("UnusedDeclaration")
    public String getBaselineName() {
        return getBaselineEnvName();
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     * @param baselineEnvName The name of the baseline's environment.
     */
    public void setBaselineEnvName(String baselineEnvName) {
        this.configuration.setBaselineEnvName(baselineEnvName);
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     * @return The name of the baseline's environment, or {@code null} if no such name was set.
     */
    public String getBaselineEnvName() {
        return configuration.getBaselineEnvName();
    }


    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @param envName The name of the environment of the baseline.
     */
    public void setEnvName(String envName) {
        this.configuration.setEnvironmentName(envName);
    }

    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @return The name of the environment of the baseline, or {@code null} if no such name was set.
     */
    public String getEnvName() {
        return this.configuration.getEnvironmentName();
    }


    /**
     * Gets position provider.
     * @return The currently set position provider.
     */
    public PositionProvider getPositionProvider() {
        return this.seleniumEyes.getPositionProvider();
    }

    /**
     * Sets position provider.
     * @param positionProvider The position provider to be used.
     */
    public void setPositionProvider(PositionProvider positionProvider) {
        this.seleniumEyes.setPositionProvider(positionProvider);
    }

    /**
     * Sets explicit viewport size.
     * @param explicitViewportSize sets the viewport
     */
    public void setExplicitViewportSize(RectangleSize explicitViewportSize) {
        this.seleniumEyes.setExplicitViewportSize(explicitViewportSize);
    }

    /**
     * Gets agent setup.
     * @return the agent setup.
     */
    public Object getAgentSetup() {
        return this.seleniumEyes.getAgentSetup();
    }

    /**
     * Log.
     * @param message the massage to log
     */
    public void log(String message) {
        activeEyes.getLogger().log(new HashSet<String>(), Stage.GENERAL, Pair.of("message", message));
    }

    /**
     * Is send dom boolean.
     * @return sendDom flag
     */
    public boolean isSendDom() {
        return this.configuration.isSendDom();
    }

    /**
     * Sets send dom.
     * @param isSendDom should send dom flag
     */
    public void setSendDom(boolean isSendDom) {
        this.configuration.setSendDom(isSendDom);
    }

    /**
     * Sets host os.
     * @param hostOS the hosting host
     */
    public void setHostOS(String hostOS) {
        this.configuration.setHostOS(hostOS);
    }

    /**
     * Sets host app.
     * @param hostApp The application running the AUT (e.g., Chrome).
     */
    public void setHostApp(String hostApp) {
        this.configuration.setHostApp(hostApp);
    }

    /**
     * for internal usage
     * @return rendering info
     */
    public RenderingInfo getRenderingInfo() {
        return null;
    }

    /**
     * Gets branch name.
     * @return The current branch (see {@link #setBranchName(String)}).
     */
    public String getBranchName() {
        return configuration.getBranchName();
    }

    /**
     * Gets parent branch name.
     * @return The name of the current parent branch under which new branches will be created. (see {@link #setParentBranchName(String)}).
     */
    public String getParentBranchName() {
        return configuration.getParentBranchName();
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}***.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public void setBaselineBranchName(String branchName) {
        this.configuration.setBaselineBranchName(branchName);
    }

    /**
     * Gets baseline branch name.
     * @return The name of the current parent branch under which new branches will be created. (see {@link #setBaselineBranchName(String)}).
     */
    public String getBaselineBranchName() {
        return configuration.getBaselineBranchName();
    }

    /**
     * Automatically save differences as a baseline.
     * @param saveDiffs Sets whether to automatically save differences as baseline.
     */
    public void setSaveDiffs(Boolean saveDiffs) {
        this.configuration.setSaveDiffs(saveDiffs);
    }

    /**
     * Returns whether to automatically save differences as a baseline.
     * @return Whether to automatically save differences as baseline.
     */
    public Boolean getSaveDiffs() {
        return this.configuration.getSaveDiffs();
    }

    public void setIgnoreDisplacements(boolean isIgnoreDisplacements) {
        this.configuration.setIgnoreDisplacements(isIgnoreDisplacements);
    }

    public boolean getIgnoreDisplacements() {
        return this.configuration.getIgnoreDisplacements();
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.configuration.setDebugResourceWriter(debugResourceWriter);
    }

    /**
     * Superseded by {@link #setHostOS(String)} and {@link #setHostApp(String)}.
     * Sets the OS (e.g., Windows) and application (e.g., Chrome) that host the application under test.
     * @param hostOS  The name of the OS hosting the application under test or {@code null} to auto-detect.
     * @param hostApp The name of the application hosting the application under test or {@code null} to auto-detect.
     */
    @Deprecated
    public void setAppEnvironment(String hostOS, String hostApp) {
        setHostOS(hostOS);
        setHostApp(hostApp);
    }

    /**
     * Gets driver.
     * @return the driver
     */
    public WebDriver getDriver() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getDriver();
        }
        return visualGridEyes.getDriver();
    }

    /**
     * Gets original fc.
     * @return Original frame chain
     */
    public FrameChain getOriginalFC() {
        return this.seleniumEyes.getOriginalFC();
    }

    /**
     * Gets current frame position provider.
     * @return get Current Frame Position Provider
     */
    public PositionProvider getCurrentFramePositionProvider() {
        return this.seleniumEyes.getCurrentFramePositionProvider();
    }

    /**
     * Gets region to check.
     * @return the region to check
     */
    public Region getRegionToCheck() {
        return this.seleniumEyes.getRegionToCheck();
    }

    /**
     * Sets region to check.
     * @param regionToCheck the region to check
     */
    public void setRegionToCheck(Region regionToCheck) {
        this.seleniumEyes.setRegionToCheck(regionToCheck);
    }

    /**
     * Gets current frame scroll root element.
     * @return the current scroll root web element
     */
    public WebElement getCurrentFrameScrollRootElement() {
        return this.seleniumEyes.getCurrentFrameScrollRootElement();
    }

    /**
     * Gets server connector.
     * @return the server connector
     */
    public ServerConnector getServerConnector() {
        return this.seleniumEyes.getServerConnector();
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

    public void closeAsync() {
        if (isVisualGridEyes) {
            visualGridEyes.closeAsync();
        } else {
            seleniumEyes.close(false);
        }
    }

    public Map<String, List<Region>> locate(VisualLocatorSettings visualLocatorSettings) {
        ArgumentGuard.notNull(visualLocatorSettings, "visualLocatorSettings");
        return visualLocatorsProvider.getLocators(visualLocatorSettings);
    }
}
