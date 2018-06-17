package com.applitools.eyes;

import com.applitools.eyes.capture.AppOutputProvider;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.debug.FileDebugScreenshotsProvider;
import com.applitools.eyes.debug.NullDebugScreenshotProvider;
import com.applitools.eyes.diagnostics.ResponseTimeAlgorithm;
import com.applitools.eyes.events.ISessionEventHandler;
import com.applitools.eyes.events.SessionEventHandlers;
import com.applitools.eyes.events.ValidationInfo;
import com.applitools.eyes.events.ValidationResult;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.fluent.*;
import com.applitools.eyes.positioning.*;
import com.applitools.eyes.scaling.FixedScaleProvider;
import com.applitools.eyes.scaling.NullScaleProvider;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.triggers.MouseTrigger;
import com.applitools.eyes.triggers.TextTrigger;
import com.applitools.utils.*;
import org.apache.commons.codec.binary.Base64;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Applitools Eyes Base for Java API .
 */
public abstract class EyesBase {

    private static final int DEFAULT_MATCH_TIMEOUT = 2000; // Milliseconds
    protected static final int USE_DEFAULT_TIMEOUT = -1;

    private boolean shouldMatchWindowRunOnceOnTimeout;

    private MatchWindowTask matchWindowTask;

    protected ServerConnector serverConnector;
    protected RunningSession runningSession;
    private SessionStartInfo sessionStartInfo;
    protected PropertyHandler<RectangleSize> viewportSizeHandler;
    protected EyesScreenshot lastScreenshot;
    protected PropertyHandler<ScaleProvider> scaleProviderHandler;
    protected PropertyHandler<CutProvider> cutProviderHandler;
    protected PositionProvider positionProvider;

    // Will be checked <b>before</b> any argument validation. If true,
    // all method will immediately return without performing any action.
    private boolean isDisabled;

    protected Logger logger;
    private boolean isOpen;
    private String agentId;
    /**
     * Will be set for separately for each test.
     */
    private String currentAppName;

    /**
     * The default app name if no current name was provided. If this is
     * {@code null} then there is no default appName.
     */
    private String appName;

    private SessionType sessionType;
    private String testName;
    private ImageMatchSettings defaultMatchSettings;
    private int matchTimeout;
    private BatchInfo batch;
    private String hostApp;
    private String hostOS;
    private String baselineEnvName;
    private String environmentName;
    private String branchName;
    private String parentBranchName;
    private String baselineBranchName;
    private Boolean saveDiffs;
    private FailureReports failureReports;
    private final Queue<Trigger> userInputs;
    private final List<PropertyData> properties = new ArrayList<>();

    // Used for automatic save of a test run.
    private boolean saveNewTests, saveFailedTests;

    protected DebugScreenshotsProvider debugScreenshotsProvider;
    private boolean isViewportSizeSet;
    private int stitchingOverlap = 50;

    private final SessionEventHandlers sessionEventHandlers = new SessionEventHandlers();
    private int validationId;

    /**
     * Creates a new {@code EyesBase}instance that interacts with the Eyes
     * Server at the specified url.
     * @param serverUrl The Eyes server URL.
     */
    public EyesBase(String serverUrl) throws URISyntaxException {
        this(new URI(serverUrl));
    }

    /**
     * Creates a new {@code EyesBase}instance that interacts with the Eyes
     * Server at the specified url.
     * @param serverUrl The Eyes server URL.
     */
    public EyesBase(URI serverUrl) {

        if (isDisabled) {
            userInputs = null;
            return;
        }

        ArgumentGuard.notNull(serverUrl, "serverUrl");

        logger = new Logger();

        Region.initLogger(logger);
        ImageUtils.initLogger(logger);

        initProviders();

        serverConnector = ServerConnectorFactory.create(logger, getBaseAgentId(), serverUrl);
        matchTimeout = DEFAULT_MATCH_TIMEOUT;
        runningSession = null;
        defaultMatchSettings = new ImageMatchSettings();
        defaultMatchSettings.setIgnoreCaret(true);
        failureReports = FailureReports.ON_CLOSE;
        userInputs = new ArrayDeque<>();

        // New tests are automatically saved by default.
        saveNewTests = true;
        saveFailedTests = false;
        saveDiffs = null;
        agentId = null;
        lastScreenshot = null;
        debugScreenshotsProvider = new NullDebugScreenshotProvider();
    }

    /**
     * @param hardReset If false, init providers only if they're not initialized.
     */
    private void initProviders(boolean hardReset) {
        if (hardReset) {
            scaleProviderHandler = new SimplePropertyHandler<>();
            scaleProviderHandler.set(new NullScaleProvider());
            cutProviderHandler = new SimplePropertyHandler<>();
            cutProviderHandler.set(new NullCutProvider());
            positionProvider = new InvalidPositionProvider();
            viewportSizeHandler = new SimplePropertyHandler<>();
            viewportSizeHandler.set(null);

            return;
        }

        if (scaleProviderHandler == null) {
            scaleProviderHandler = new SimplePropertyHandler<>();
            scaleProviderHandler.set(new NullScaleProvider());
        }

        if (cutProviderHandler == null) {
            cutProviderHandler = new SimplePropertyHandler<>();
            cutProviderHandler.set(new NullCutProvider());
        }

        if (positionProvider == null) {
            positionProvider = new InvalidPositionProvider();
        }

        if (viewportSizeHandler == null) {
            viewportSizeHandler = new SimplePropertyHandler<>();
            viewportSizeHandler.set(null);
        }
    }

    /**
     * Same as {@link #initProviders(boolean)}, setting {@code hardReset} to {@code false}.
     */
    private void initProviders() {
        initProviders(false);
    }

    /**
     * Sets the user given agent id of the SDK. {@code null} is referred to
     * as no id.
     * @param agentId The agent ID to set.
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * @return The user given agent id of the SDK.
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * Sets the API key of your applitools Eyes account.
     * @param apiKey The api key to set.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setApiKey(String apiKey) {
        ArgumentGuard.notNull(apiKey, "apiKey");
        serverConnector.setApiKey(apiKey);
    }

    /**
     * @return The currently set API key or {@code null} if no key is set.
     */
    public String getApiKey() {
        return serverConnector.getApiKey();
    }


    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server, or {@code null} to use
     *                  the default server.
     */
    public void setServerUrl(String serverUrl) throws URISyntaxException {
        setServerUrl(new URI(serverUrl));
    }

    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server, or {@code null} to use
     *                  the default server.
     */
    public void setServerUrl(URI serverUrl) {
        if (serverUrl == null) {
            serverConnector.setServerUrl(getDefaultServerUrl());
        } else {
            serverConnector.setServerUrl(serverUrl);
        }
    }

    /**
     * @return The URI of the eyes server.
     */
    public URI getServerUrl() {
        return serverConnector.getServerUrl();
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     * @param proxySettings The proxy settings to be used by the rest client.
     *                      If {@code null} then no proxy is set.
     */
    public void setProxy(ProxySettings proxySettings) {
        serverConnector.setProxy(proxySettings);
    }

    /**
     * @return The current proxy settings used by the server connector,
     * or {@code null} if no proxy is set.
     */
    public ProxySettings getProxy() {
        return serverConnector.getProxy();
    }

    /**
     * @param isDisabled If true, all interactions with this API will be
     *                   silently ignored.
     */
    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * @return Whether eyes is disabled.
     */
    public boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * @param appName The name of the application under test.
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return The name of the application under test.
     */
    public String getAppName() {
        return currentAppName != null ? currentAppName : appName;
    }

    /**
     * Sets the branch in which the baseline for subsequent test runs resides.
     * If the branch does not already exist it will be created under the
     * specified parent branch (see {@link #setParentBranchName}).
     * Changes to the baseline or model of a branch do not propagate to other
     * branches.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * @return The current branch (see {@link #setBranchName(String)}).
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public void setParentBranchName(String branchName) {
        this.parentBranchName = branchName;
    }

    /**
     * @return The name of the current parent branch under which new branches
     * will be created. (see {@link #setParentBranchName(String)}).
     */
    public String getParentBranchName() {
        return parentBranchName;
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}.
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public void setBaselineBranchName(String branchName) {
        this.baselineBranchName = branchName;
    }

    /**
     * @return The name of the current parent branch under which new branches
     * will be created. (see {@link #setBaselineBranchName(String)}).
     */
    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    /**
     * Automatically save differences as a baseline.
     * @param saveDiffs Sets whether to automatically save differences as baseline.
     */
    public void setSaveDiffs(Boolean saveDiffs) {
        this.saveDiffs = saveDiffs;
    }

    /**
     * Returns whether to automatically save differences as a baseline.
     * @return Whether to automatically save differences as baseline.
     */
    public Boolean getSaveDiffs() {
        return this.saveDiffs;
    }

    /**
     * Clears the user inputs list.
     */
    protected void clearUserInputs() {
        if (isDisabled) {
            return;
        }
        userInputs.clear();
    }

    /**
     * @return User inputs collected between {@code checkWindowBase} invocations.
     */
    protected Trigger[] getUserInputs() {
        if (isDisabled) {
            return null;
        }
        Trigger[] result = new Trigger[userInputs.size()];
        return userInputs.toArray(result);
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

        this.matchTimeout = ms;
    }

    /**
     * @return The maximum time in ms {@link #checkWindowBase
     * (RegionProvider, String, boolean, int)} waits for a match.
     */
    public int getMatchTimeout() {
        return matchTimeout;
    }

    /**
     * Set whether or not new tests are saved by default.
     * @param saveNewTests True if new tests should be saved by default. False otherwise.
     */
    public void setSaveNewTests(boolean saveNewTests) {
        this.saveNewTests = saveNewTests;
    }

    /**
     * @return True if new tests are saved by default.
     */
    public boolean getSaveNewTests() {
        return saveNewTests;
    }

    /**
     * Set whether or not failed tests are saved by default.
     * @param saveFailedTests True if failed tests should be saved by default, false otherwise.
     */
    public void setSaveFailedTests(boolean saveFailedTests) {
        this.saveFailedTests = saveFailedTests;
    }

    /**
     * @return True if failed tests are saved by default.
     */
    public boolean getSaveFailedTests() {
        return saveFailedTests;
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     * @param batch The batch info to set.
     */
    public void setBatch(BatchInfo batch) {
        if (isDisabled) {
            logger.verbose("Ignored");
            return;
        }

        logger.verbose("setBatch(" + batch + ")");

        this.batch = batch;
    }

    /**
     * @return The currently set batch info.
     */
    public BatchInfo getBatch() {
        return batch;
    }

    /**
     * @param failureReports The failure reports setting.
     * @see FailureReports
     */
    public void setFailureReports(FailureReports failureReports) {
        this.failureReports = failureReports;
    }

    /**
     * @return the failure reports setting.
     */
    public FailureReports getFailureReports() {
        return failureReports;
    }

    /**
     * Updates the match settings to be used for the session.
     * @param defaultMatchSettings The match settings to be used for the session.
     */
    public void setDefaultMatchSettings(ImageMatchSettings
                                                defaultMatchSettings) {
        ArgumentGuard.notNull(defaultMatchSettings, "defaultMatchSettings");
        this.defaultMatchSettings = defaultMatchSettings;
    }

    /**
     * @return The match settings used for the session.
     */
    public ImageMatchSettings getDefaultMatchSettings() {
        return defaultMatchSettings;
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
        this.defaultMatchSettings.setMatchLevel(matchLevel);
    }

    /**
     * @return The test-wide match level.
     * @deprecated Please use{@link #getDefaultMatchSettings} instead.
     */
    public MatchLevel getMatchLevel() {
        return defaultMatchSettings.getMatchLevel();
    }

    /**
     * @return The base agent id of the SDK.
     */
    protected abstract String getBaseAgentId();

    /**
     * @return The full agent id composed of both the base agent id and the
     * user given agent id.
     */
    public String getFullAgentId() {
        String agentId = getAgentId();
        if (agentId == null) {
            return getBaseAgentId();
        }
        return String.format("%s [%s]", agentId, getBaseAgentId());
    }

    /**
     * @return Whether a session is open.
     */
    public boolean getIsOpen() {
        return isOpen;
    }

    public static URI getDefaultServerUrl() {
        try {
            return new URI("https://eyesapi.applitools.com");
        } catch (URISyntaxException ex) {
            throw new EyesException(ex.getMessage(), ex);
        }
    }

    /**
     * Sets a handler of log messages generated by this API.
     * @param logHandler Handles log messages generated by this API.
     */
    public void setLogHandler(LogHandler logHandler) {
        logger.setLogHandler(logHandler);
    }

    /**
     * @return The currently set log handler.
     */
    public LogHandler getLogHandler() {
        return logger.getLogHandler();
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Manually set the the sizes to cut from an image before it's validated.
     * @param cutProvider the provider doing the cut.
     */
    public void setImageCut(CutProvider cutProvider) {
        if (cutProvider != null) {
            cutProviderHandler = new ReadOnlyPropertyHandler<>(logger,
                    cutProvider);
        } else {
            cutProviderHandler = new SimplePropertyHandler<>();
            cutProviderHandler.set(new NullCutProvider());
        }
    }

    public boolean getIsCutProviderExplicitlySet() {
        return cutProviderHandler != null && !(cutProviderHandler.get() instanceof NullCutProvider);
    }

    /**
     * Manually set the scale ratio for the images being validated.
     * @param scaleRatio The scale ratio to use, or {@code null} to reset
     *                   back to automatic scaling.
     */
    public void setScaleRatio(Double scaleRatio) {
        if (scaleRatio != null) {
            scaleProviderHandler = new ReadOnlyPropertyHandler<ScaleProvider>(
                    logger, new FixedScaleProvider(scaleRatio));
        } else {
            scaleProviderHandler = new SimplePropertyHandler<>();
            scaleProviderHandler.set(new NullScaleProvider());
        }
    }

    /**
     * @return The ratio used to scale the images being validated.
     */
    public double getScaleRatio() {
        return scaleProviderHandler.get().getScaleRatio();
    }

    /**
     * Adds a property to be sent to the server.
     * @param name  The property name.
     * @param value The property value.
     */
    public void addProperty(String name, String value) {
        PropertyData pd = new PropertyData(name, value);
        properties.add(pd);
    }

    /**
     * Clears the list of custom properties.
     */
    public void clearProperties() {
        properties.clear();
    }

    /**
     * @param saveDebugScreenshots If true, will save all screenshots to local directory.
     */
    public void setSaveDebugScreenshots(boolean saveDebugScreenshots) {
        DebugScreenshotsProvider prev = debugScreenshotsProvider;
        if (saveDebugScreenshots) {
            debugScreenshotsProvider = new FileDebugScreenshotsProvider();
        } else {
            debugScreenshotsProvider = new NullDebugScreenshotProvider();
        }
        debugScreenshotsProvider.setPrefix(prev.getPrefix());
        debugScreenshotsProvider.setPath(prev.getPath());
    }

    /**
     * @return True if screenshots saving enabled.
     */
    public boolean getSaveDebugScreenshots() {
        return !(debugScreenshotsProvider instanceof NullDebugScreenshotProvider);
    }

    /**
     * @param pathToSave Path where you want to save the debug screenshots.
     */
    public void setDebugScreenshotsPath(String pathToSave) {
        debugScreenshotsProvider.setPath(pathToSave);
    }

    /**
     * @return The path where you want to save the debug screenshots.
     */
    public String getDebugScreenshotsPath() {
        return debugScreenshotsProvider.getPath();
    }

    /**
     * @param prefix The prefix for the screenshots' names.
     */
    public void setDebugScreenshotsPrefix(String prefix) {
        debugScreenshotsProvider.setPrefix(prefix);
    }

    /**
     * @return The prefix for the screenshots' names.
     */
    public String getDebugScreenshotsPrefix() {
        return debugScreenshotsProvider.getPrefix();
    }

    public DebugScreenshotsProvider getDebugScreenshotsProvider() {
        return debugScreenshotsProvider;
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    public boolean getIgnoreCaret() {
        Boolean ignoreCaret = defaultMatchSettings.getIgnoreCaret();
        return ignoreCaret == null ? true : ignoreCaret;
    }

    /**
     * Sets the ignore blinking caret value.
     * @param value The ignore value.
     */
    public void setIgnoreCaret(boolean value) {
        defaultMatchSettings.setIgnoreCaret(value);
    }

    /**
     * Returns the stitching overlap in pixels.
     */
    public int getStitchOverlap() {
        return this.stitchingOverlap;
    }

    /**
     * Sets the stitching overlap in pixels.
     * @param pixels The width (in pixels) of the overlap.
     */
    public void setStitchOverlap(int pixels) {
        this.stitchingOverlap = pixels;
    }

    /**
     * See {@link #close(boolean)}.
     * {@code throwEx} defaults to {@code true}.
     * @return The test results.
     */
    public TestResults close() {
        return close(true);
    }

    /**
     * Ends the test.
     * @param throwEx If true, an exception will be thrown for failed/new tests.
     * @return The test results.
     * @throws TestFailedException if a mismatch was found and throwEx is true.
     * @throws NewTestException    if this is a new test was found and throwEx
     *                             is true.
     */
    public TestResults close(boolean throwEx) {
        try {
            if (isDisabled) {
                logger.verbose("Ignored");
                return null;
            }
            logger.verbose(String.format("close(%b)", throwEx));
            ArgumentGuard.isValidState(isOpen, "Eyes not open");

            isOpen = false;

            lastScreenshot = null;
            clearUserInputs();

            initProviders(true);

            if (runningSession == null) {
                logger.verbose("Server session was not started");
                logger.log("--- Empty test ended.");
                return new TestResults();
            }

            boolean isNewSession = runningSession.getIsNewSession();
            String sessionResultsUrl = runningSession.getUrl();

            logger.verbose("Ending server session...");
            boolean save = (isNewSession && saveNewTests)
                    || (!isNewSession && saveFailedTests);
            logger.verbose("Automatically save test? " + String.valueOf(save));
            TestResults results = serverConnector.stopSession(runningSession, false, save);

            results.setNew(isNewSession);
            results.setUrl(sessionResultsUrl);
            logger.verbose(results.toString());

            TestResultsStatus status = results.getStatus();

            sessionEventHandlers.testEnded(getAUTSessionId(), results);

            if (status == TestResultsStatus.Unresolved) {
                if (results.isNew()) {
                    logger.log("--- New test ended. Please approve the new baseline at " + sessionResultsUrl);
                    if (throwEx) {
                        throw new NewTestException(results, sessionStartInfo);
                    }
                } else {
                    logger.log("--- Failed test ended. See details at " + sessionResultsUrl);
                    if (throwEx) {
                        throw new DiffsFoundException(results, sessionStartInfo);
                    }
                }
            } else if (status == TestResultsStatus.Failed) {
                logger.log("--- Failed test ended. See details at " + sessionResultsUrl);
                if (throwEx) {
                    throw new TestFailedException(results, sessionStartInfo);
                }
            } else {
                // Test passed
                logger.log("--- Test passed. See details at " + sessionResultsUrl);
            }

            results.setServerConnector(this.serverConnector);

            return results;
        } finally {
            // Making sure that we reset the running session even if an
            // exception was thrown during close.
            runningSession = null;
            currentAppName = null;
            logger.getLogHandler().close();
        }
    }

    /**
     * Ends the test.
     * @param isDeadlineExceeded If {@code true} the test will fail (unless
     *                           it's a new test).
     * @throws TestFailedException
     * @throws NewTestException
     */
    protected void closeResponseTime(boolean isDeadlineExceeded) {
        try {
            if (isDisabled) {
                logger.verbose("Ignored");
            }

            logger.verbose(String.format("closeResponseTime(%b)",
                    isDeadlineExceeded));
            ArgumentGuard.isValidState(isOpen, "Eyes not open");

            isOpen = false;

            if (runningSession == null) {
                logger.verbose("Server session was not started");
                logger.log("--- Empty test ended.");
                return;
            }

            boolean isNewSession = runningSession.getIsNewSession();
            String sessionResultsUrl = runningSession.getUrl();

            logger.verbose("Ending server session...");
            boolean save = (isNewSession && saveNewTests);

            logger.verbose("Automatically save test? " + String.valueOf(save));
            TestResults results =
                    serverConnector.stopSession(runningSession, false,
                            save);

            results.setNew(isNewSession);
            results.setUrl(sessionResultsUrl);
            logger.verbose(results.toString());

            String instructions;
            if (isDeadlineExceeded && !isNewSession) {

                logger.log("--- Failed test ended. See details at "
                        + sessionResultsUrl);

                String message =
                        "'" + sessionStartInfo.getScenarioIdOrName()
                                + "' of '"
                                + sessionStartInfo.getAppIdOrName()
                                + "'. See details at " + sessionResultsUrl;
                throw new TestFailedException(results, message);
            }

            if (isNewSession) {
                instructions = "Please approve the new baseline at "
                        + sessionResultsUrl;

                logger.log("--- New test ended. " + instructions);

                String message =
                        "'" + sessionStartInfo.getScenarioIdOrName()
                                + "' of '" + sessionStartInfo
                                .getAppIdOrName()
                                + "'. " + instructions;
                throw new NewTestException(results, message);
            }

            // Test passed
            logger.log("--- Test passed. See details at " + sessionResultsUrl);

        } finally {
            // Making sure that we reset the running session even if an
            // exception was thrown during close.
            runningSession = null;
            currentAppName = null;
            logger.getLogHandler().close();
        }
    }

    /**
     * If a test is running, aborts it. Otherwise, does nothing.
     */
    public void abortIfNotClosed() {
        try {
            if (isDisabled) {
                logger.verbose("Ignored");
                return;
            }

            isOpen = false;

            lastScreenshot = null;
            clearUserInputs();

            if (null == runningSession) {
                logger.verbose("Closed");
                return;
            }

            logger.verbose("Aborting server session...");
            try {
                // When aborting we do not save the test.
                serverConnector.stopSession(runningSession, true, false);
                logger.log("--- Test aborted.");
            } catch (EyesException ex) {
                logger.log(
                        "Failed to abort server session: " + ex.getMessage());
            }
        } finally {
            runningSession = null;
            logger.getLogHandler().close();
        }
    }

    /**
     * @param hostOS The host OS running the AUT.
     */
    public void setHostOS(String hostOS) {

        logger.log("Host OS: " + hostOS);

        if (hostOS == null || hostOS.isEmpty()) {
            this.hostOS = null;
        } else {
            this.hostOS = hostOS.trim();
        }
    }

    /**
     * @return get the host OS running the AUT.
     */
    public String getHostOS() {
        return hostOS;
    }

    /**
     * @param hostApp The application running the AUT (e.g., Chrome).
     */
    public void setHostApp(String hostApp) {

        logger.log("Host App: " + hostApp);

        if (hostApp == null || hostApp.isEmpty()) {
            this.hostApp = null;
        } else {
            this.hostApp = hostApp.trim();
        }
    }

    /**
     * @return The application name running the AUT.
     */
    public String getHostApp() {
        return hostApp;
    }

    /**
     * @param baselineName If specified, determines the baseline to compare
     *                     with and disables automatic baseline inference.
     * @deprecated Only available for backward compatibility. See {@link #setBaselineEnvName(String)}.
     */
    public void setBaselineName(String baselineName) {

        logger.log("Baseline environment name: " + baselineName);

        if (baselineName == null || baselineName.isEmpty()) {
            this.baselineEnvName = null;
        } else {
            this.baselineEnvName = baselineName.trim();
        }
    }

    /**
     * @return The baseline name, if specified.
     * @deprecated Only available for backward compatibility. See {@link #getBaselineEnvName()}.
     */
    @SuppressWarnings("UnusedDeclaration")
    public String getBaselineName() {
        return baselineEnvName;
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     * @param baselineEnvName The name of the baseline's environment.
     */
    public void setBaselineEnvName(String baselineEnvName) {

        logger.log("Baseline environment name: " + baselineEnvName);

        if (baselineEnvName == null || baselineEnvName.isEmpty()) {
            this.baselineEnvName = null;
        } else {
            this.baselineEnvName = baselineEnvName.trim();
        }
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     * @return The name of the baseline's environment, or {@code null} if no such name was set.
     */
    public String getBaselineEnvName() {
        return baselineEnvName;
    }


    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @param envName The name of the environment of the baseline.
     */
    public void setEnvName(String envName) {

        logger.log("Environment name: " + envName);

        if (envName == null || envName.isEmpty()) {
            this.environmentName = null;
        } else {
            this.environmentName = envName.trim();
        }
    }

    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     * @return The name of the environment of the baseline, or {@code null} if no such name was set.
     */
    public String getEnvName() {
        return environmentName;
    }


    /**
     * Superseded by {@link #setHostOS(String)} and {@link #setHostApp(String)}.
     * Sets the OS (e.g., Windows) and application (e.g., Chrome) that host the application under test.
     * @param hostOS  The name of the OS hosting the application under test or {@code null} to auto-detect.
     * @param hostApp The name of the application hosting the application under test or {@code null} to auto-detect.
     */
    @Deprecated
    public void setAppEnvironment(String hostOS, String hostApp) {
        if (isDisabled) {
            logger.verbose("Ignored");
            return;
        }

        logger.log("Warning: SetAppEnvironment is deprecated! Please use 'setHostOS' and 'setHostApp'");

        logger.verbose("setAppEnvironment(" + hostOS + ", " + hostApp + ")");
        setHostOS(hostOS);
        setHostApp(hostApp);
    }

    /**
     * @return The currently set position provider.
     */
    public PositionProvider getPositionProvider() {
        return positionProvider;
    }

    /**
     * @param positionProvider The position provider to be used.
     */
    protected void setPositionProvider(PositionProvider positionProvider) {
        this.positionProvider = positionProvider;
    }

    /**
     * See {@link #checkWindowBase(RegionProvider, String, boolean, int)}.
     * {@code retryTimeout} defaults to {@code USE_DEFAULT_TIMEOUT}.
     * @param regionProvider Returns the region to check or the empty rectangle to check the entire window.
     * @param tag            An optional tag to be associated with the snapshot.
     * @param ignoreMismatch Whether to ignore this check if a mismatch is found.
     * @return The result of matching the output with the expected output.
     */
    protected MatchResult checkWindowBase(RegionProvider regionProvider,
                                          String tag, boolean ignoreMismatch) {
        return checkWindowBase(regionProvider, tag, ignoreMismatch,
                USE_DEFAULT_TIMEOUT);
    }

    /**
     * Takes a snapshot of the application under test and matches it with the
     * expected output.
     * @param regionProvider Returns the region to check or the empty rectangle to check the entire window.
     * @param tag            An optional tag to be associated with the snapshot.
     * @param ignoreMismatch Whether to ignore this check if a mismatch is found.
     * @param retryTimeout   The amount of time to retry matching in milliseconds or a negative
     *                       value to use the default retry timeout.
     * @return The result of matching the output with the expected output.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    protected MatchResult checkWindowBase(RegionProvider regionProvider, String tag, boolean ignoreMismatch, int retryTimeout) {
        return this.checkWindowBase(regionProvider, tag, ignoreMismatch, new CheckSettings(retryTimeout));
    }

    protected void beforeMatchWindow() {
    }

    protected void afterMatchWindow() {
    }

    /**
     * Takes a snapshot of the application under test and matches it with the
     * expected output.
     * @param regionProvider Returns the region to check or the empty rectangle to check the entire window.
     * @param tag            An optional tag to be associated with the snapshot.
     * @param ignoreMismatch Whether to ignore this check if a mismatch is found.
     * @param checkSettings  The settings to use.
     * @return The result of matching the output with the expected output.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    protected MatchResult checkWindowBase(RegionProvider regionProvider, String tag,
                                          boolean ignoreMismatch, ICheckSettings checkSettings) {
        MatchResult result;

        if (getIsDisabled()) {
            logger.verbose("Ignored");
            result = new MatchResult();
            result.setAsExpected(true);
            return result;
        }

        if (tag == null) {
            tag = "";
        }

        ArgumentGuard.isValidState(getIsOpen(), "Eyes not open");
        ArgumentGuard.notNull(regionProvider, "regionProvider");

        beforeMatchWindow();

        result = matchWindow(regionProvider, tag, ignoreMismatch, checkSettings, this);

        afterMatchWindow();

        logger.verbose("MatchWindow Done!");

        if (!ignoreMismatch) {
            clearUserInputs();
            lastScreenshot = result.getScreenshot();
        }

        validateResult(tag, result);

        logger.verbose("Done!");
        return result;
    }

    private static MatchResult matchWindow(RegionProvider regionProvider, String tag, boolean ignoreMismatch,
                                           ICheckSettings checkSettings, EyesBase self) {
        MatchResult result;
        ICheckSettingsInternal checkSettingsInternal = (checkSettings instanceof ICheckSettingsInternal) ? (ICheckSettingsInternal) checkSettings : null;

        String autSessionId = self.getAUTSessionId();

        ValidationInfo validationInfo = new ValidationInfo();
        validationInfo.setValidationId("" + (++self.validationId));
        validationInfo.setTag(tag);

        self.getSessionEventHandlers().validationWillStart(autSessionId, validationInfo);

        // Update retry timeout if it wasn't specified.
        int retryTimeout = -1;
        if (checkSettingsInternal != null) {
            retryTimeout = checkSettingsInternal.getTimeout();
        }

        ImageMatchSettings defaultMatchSettings = self.getDefaultMatchSettings();

        // Set defaults if necessary
        if (checkSettingsInternal != null) {
            if (checkSettingsInternal.getMatchLevel() == null) {
                checkSettings = checkSettings.matchLevel(defaultMatchSettings.getMatchLevel());
            }

            if (checkSettingsInternal.getIgnoreCaret() == null) {
                checkSettings = checkSettings.ignoreCaret(defaultMatchSettings.getIgnoreCaret());
            }

            checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
        }

        self.logger.verbose(String.format("CheckWindowBase(%s, '%s', %b, %d)",
                regionProvider.getClass(), tag, ignoreMismatch, retryTimeout));

        self.ensureRunningSession();

        self.logger.verbose("Calling match window...");

        result = self.matchWindowTask.matchWindow(self.getUserInputs(), regionProvider.getRegion(), tag,
                self.shouldMatchWindowRunOnceOnTimeout, ignoreMismatch, checkSettingsInternal, retryTimeout, self);

        ValidationResult validationResult = new ValidationResult();
        self.getSessionEventHandlers().validationEnded(autSessionId, validationInfo.getValidationId(), validationResult);

        return result;
    }

    private void validateResult(String tag, MatchResult result) {
        if (result.getAsExpected()) {
            return;
        }

        shouldMatchWindowRunOnceOnTimeout = true;

        if (!runningSession.getIsNewSession()) {
            logger.log(String.format("Mismatch! (%s)", tag));
        }

        if (getFailureReports() == FailureReports.IMMEDIATE) {
            throw new TestFailedException(String.format(
                    "Mismatch found in '%s' of '%s'",
                    sessionStartInfo.getScenarioIdOrName(),
                    sessionStartInfo.getAppIdOrName()));
        }
    }

    /**
     * Runs a timing test.
     * @param regionProvider Returns the region to check or the empty rectangle to check the entire window.
     * @param action         An action to run in parallel to starting the test, or {@code null} if no such action is required.
     * @param deadline       The expected amount of time until finding a match. (Seconds)
     * @param timeout        The maximum amount of time to retry matching. (Seconds)
     * @param matchInterval  The interval for testing for a match. (Milliseconds)
     * @return The earliest match found, or {@code null} if no match was found.
     */
    protected MatchWindowDataWithScreenshot testResponseTimeBase(
            RegionProvider regionProvider, Runnable action, int deadline,
            int timeout, long matchInterval) {

        if (getIsDisabled()) {
            logger.verbose("Ignored");
            return null;
        }

        ArgumentGuard.isValidState(getIsOpen(), "Eyes not open");
        ArgumentGuard.notNull(regionProvider, "regionProvider");
        ArgumentGuard.greaterThanZero(deadline, "deadline");
        ArgumentGuard.greaterThanZero(timeout, "timeout");
        ArgumentGuard.greaterThanZero(matchInterval, "matchInterval");

        logger.verbose(String.format("testResponseTimeBase(regionProvider, %d, %d, %d)",
                deadline, timeout, matchInterval));

        if (runningSession == null) {
            logger.verbose("No running session, calling start session..");
            startSession();
            logger.verbose("Done!");
        }

        //If there's an action to do
        Thread actionThread = null;
        if (action != null) {
            logger.verbose("Starting webdriver action.");
            actionThread = new Thread(action);
            actionThread.start();
        }

        long startTime = System.currentTimeMillis();

        // A callback which will call getAppOutput
        AppOutputProvider appOutputProvider = new AppOutputProvider() {
            public AppOutputWithScreenshot getAppOutput(
                    Region region,
                    EyesScreenshot lastScreenshot,
                    ICheckSettingsInternal checkSettingsInternal) {
                // FIXME - If we use compression here it hurts us later (because of another screenshot order).
                return getAppOutputWithScreenshot(region, null, null);
            }
        };

        MatchWindowDataWithScreenshot result;
        if (runningSession.getIsNewSession()) {
            ResponseTimeAlgorithm.runNewProgressionSession(logger,
                    serverConnector, runningSession, appOutputProvider,
                    regionProvider, startTime, deadline);
            // Since there's never a match for a new session..
            result = null;
        } else {
            result = ResponseTimeAlgorithm.runProgressionSessionForExistingBaseline(
                    logger, serverConnector, runningSession, appOutputProvider, regionProvider, startTime,
                    deadline, timeout, matchInterval);
        }

        if (actionThread != null) {
            // FIXME - Replace join with wait to according to the parameters
            logger.verbose("Making sure 'action' thread had finished...");
            try {
                actionThread.join(30000);
            } catch (InterruptedException e) {
                logger.verbose(
                        "Got interrupted while waiting for 'action' to finish!");
            }
        }

        logger.verbose("Done!");
        return result;
    }

    protected void beforeOpen() {
    }

    protected void afterOpen() {
    }

    /**
     * Starts a test.
     * @param appName      The name of the application under test.
     * @param testName     The test name.
     * @param viewportSize The client's viewport size (i.e., the visible part
     *                     of the document's body) or {@code null} to allow
     *                     any viewport size.
     * @param sessionType  The type of test (e.g., Progression for timing
     *                     tests), or {@code null} to use the default.
     */
    protected void openBase(String appName, String testName,
                            RectangleSize viewportSize, SessionType sessionType) {

        logger.getLogHandler().open();

        try {
            if (isDisabled) {
                logger.verbose("Ignored");
                return;
            }

            // If there's no default application name, one must be provided
            // for the current test.
            if (this.appName == null) {
                ArgumentGuard.notNull(appName, "appName");
            }

            ArgumentGuard.notNull(testName, "testName");

            logger.log("Agent = " + getFullAgentId());
            logger.verbose(String.format("openBase('%s', '%s', '%s')", appName,
                    testName, viewportSize));

            validateApiKey();
            logOpenBase();
            validateSessionOpen();

            initProviders();

            this.isViewportSizeSet = false;

            beforeOpen();

            this.currentAppName = appName != null ? appName : this.appName;
            this.testName = testName;
            viewportSizeHandler.set(viewportSize);
            this.sessionType = sessionType != null ? sessionType : SessionType.SEQUENTIAL;

            if (viewportSize != null) {
                ensureRunningSession();
            }

            this.validationId = -1;

            isOpen = true;
            afterOpen();

        } catch (EyesException e) {
            logger.log(e.getMessage());
            logger.getLogHandler().close();
            throw e;
        }
    }

    private void ensureRunningSession() {
        if (runningSession != null) {
            return;
        }

        logger.log("No running session, calling start session...");
        startSession();
        logger.setSessionId(runningSession.getSessionId());
        logger.log("Done!");

        matchWindowTask = new MatchWindowTask(
                logger,
                serverConnector,
                runningSession,
                matchTimeout,
                // A callback which will call getAppOutput
                new AppOutputProvider() {
                    @Override
                    public AppOutputWithScreenshot getAppOutput(Region region, EyesScreenshot lastScreenshot,
                                                                ICheckSettingsInternal checkSettingsInternal) {
                        return getAppOutputWithScreenshot(region, lastScreenshot, checkSettingsInternal);
                    }
                }
        );
    }

    private void validateApiKey() {
        if (getApiKey() == null) {
            String errMsg =
                    "API key is missing! Please set it using setApiKey()";
            logger.log(errMsg);
            throw new EyesException(errMsg);
        }
    }

    private void logOpenBase() {
        logger.log(String.format("Eyes server URL is '%s'", serverConnector.getServerUrl()));
        logger.verbose(String.format("Timeout = '%d'", serverConnector.getTimeout()));
        logger.log(String.format("matchTimeout = '%d' ", matchTimeout));
        logger.log(String.format("Default match settings = '%s' ", defaultMatchSettings));
        logger.log(String.format("FailureReports = '%s' ", failureReports));
    }

    private void validateSessionOpen() {
        if (isOpen) {
            abortIfNotClosed();
            String errMsg = "A test is already running";
            logger.log(errMsg);
            throw new EyesException(errMsg);
        }
    }

    /**
     * @return The viewport size of the AUT.
     */
    protected abstract RectangleSize getViewportSize();

    /**
     * @param size The required viewport size.
     */
    protected abstract void setViewportSize(RectangleSize size);

    /**
     * Define the viewport size as {@code size} without doing any actual action on the
     * @param explicitViewportSize The size of the viewport. {@code null} disables the explicit size.
     */
    public void setExplicitViewportSize(RectangleSize explicitViewportSize) {
        if (explicitViewportSize == null) {
            viewportSizeHandler = new SimplePropertyHandler<>();
            viewportSizeHandler.set(null);
            this.isViewportSizeSet = false;

            return;
        }

        logger.verbose("Viewport size explicitly set to " + explicitViewportSize);
        viewportSizeHandler = new ReadOnlyPropertyHandler<>(logger,
                new RectangleSize(explicitViewportSize.getWidth(), explicitViewportSize.getHeight()));
        this.isViewportSizeSet = true;
    }

    /**
     * @return The inferred environment string
     * or {@code null} if none is available. The inferred string is in the
     * format "source:info" where source is either "useragent" or "pos".
     * Information associated with a "useragent" source is a valid browser user
     * agent string. Information associated with a "pos" source is a string of
     * the format "process-name;os-name" where "process-name" is the name of the
     * main module of the executed process and "os-name" is the OS name.
     */
    protected abstract String getInferredEnvironment();

    /**
     * @return An updated screenshot.
     */
    protected abstract EyesScreenshot getScreenshot();

    /**
     * @return The current title of of the AUT.
     */
    protected abstract String getTitle();

    // FIXME add "GetScreenshotUrl"
    // FIXME add CloseOrAbort ??

    /**
     * Adds a trigger to the current list of user inputs.
     * @param trigger The trigger to add to the user inputs list.
     */
    protected void addUserInput(Trigger trigger) {
        if (isDisabled) {
            return;
        }
        ArgumentGuard.notNull(trigger, "trigger");
        userInputs.add(trigger);
    }

    /**
     * Adds a text trigger.
     * @param control The control's position relative to the window.
     * @param text    The trigger's text.
     */
    protected void addTextTriggerBase(Region control, String text) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring '%s' (disabled)", text));
            return;
        }

        ArgumentGuard.notNull(control, "control");
        ArgumentGuard.notNull(text, "text");

        // We don't want to change the objects we received.
        control = new Region(control);

        if (lastScreenshot == null) {
            logger.verbose(String.format("Ignoring '%s' (no screenshot)",
                    text));
            return;
        }

        control = lastScreenshot.getIntersectedRegion(control, CoordinatesType.SCREENSHOT_AS_IS);

        if (control.isSizeEmpty()) {
            logger.verbose(String.format("Ignoring '%s' (out of bounds)",
                    text));
            return;
        }

        Trigger trigger = new TextTrigger(control, text);
        addUserInput(trigger);

        logger.verbose(String.format("Added %s", trigger));
    }

    /**
     * Adds a mouse trigger.
     * @param action  Mouse action.
     * @param control The control on which the trigger is activated
     *                (location is relative to the window).
     * @param cursor  The cursor's position relative to the control.
     */
    protected void addMouseTriggerBase(MouseAction action, Region control,
                                       Location cursor) {
        if (getIsDisabled()) {
            logger.verbose(String.format("Ignoring %s (disabled)", action));
            return;
        }

        ArgumentGuard.notNull(action, "action");
        ArgumentGuard.notNull(control, "control");
        ArgumentGuard.notNull(cursor, "cursor");

        // Triggers are actually performed on the previous window.
        if (lastScreenshot == null) {
            logger.verbose(String.format("Ignoring %s (no screenshot)",
                    action));
            return;
        }

        // Getting the location of the cursor in the screenshot
        Location cursorInScreenshot = new Location(cursor);
        // First we need to getting the cursor's coordinates relative to the
        // context (and not to the control).
        cursorInScreenshot.offset(control.getLocation());
        try {
            cursorInScreenshot = lastScreenshot.getLocationInScreenshot(
                    cursorInScreenshot, CoordinatesType.CONTEXT_RELATIVE);
        } catch (OutOfBoundsException e) {
            logger.verbose(String.format("Ignoring %s (out of bounds)",
                    action));
            return;
        }

        Region controlScreenshotIntersect =
                lastScreenshot.getIntersectedRegion(control, CoordinatesType.SCREENSHOT_AS_IS);

        // If the region is NOT empty, we'll give the coordinates relative to
        // the control.
        if (!controlScreenshotIntersect.isSizeEmpty()) {
            Location l = controlScreenshotIntersect.getLocation();
            cursorInScreenshot.offset(-l.getX(), -l.getY());
        }

        Trigger trigger = new MouseTrigger(action, controlScreenshotIntersect, cursorInScreenshot);
        addUserInput(trigger);

        logger.verbose(String.format("Added %s", trigger));
    }

    // FIXME add getScreenshot (Wrapper) ?? (Check EyesBase in .NET)

    /**
     * Application environment is the environment (e.g., the host OS) which
     * runs the application under test.
     * @return The current application environment.
     */
    protected AppEnvironment getAppEnvironment() {

        AppEnvironment appEnv = new AppEnvironment();

        // If hostOS isn't set, we'll try and extract and OS ourselves.
        if (hostOS != null) {
            appEnv.setOs(hostOS);
        }

        if (hostApp != null) {
            appEnv.setHostingApp(hostApp);
        }

        appEnv.setInferred(getInferredEnvironment());
        appEnv.setDisplaySize(viewportSizeHandler.get());
        return appEnv;
    }

    /**
     * Start eyes session on the eyes server.
     */
    protected void startSession() {
        logger.verbose("startSession()");

        sessionEventHandlers.testStarted(getAUTSessionId());

        ensureViewportSize();

        BatchInfo testBatch;
        if (batch == null) {
            logger.verbose("No batch set");
            testBatch = new BatchInfo(null);
        } else {
            logger.verbose("Batch is " + batch);
            testBatch = batch;
        }

        sessionEventHandlers.initStarted();

        AppEnvironment appEnv = getAppEnvironment();

        sessionEventHandlers.initEnded();

        logger.verbose("Application environment is " + appEnv);

        sessionStartInfo = new SessionStartInfo(getBaseAgentId(), sessionType,
                getAppName(), null, testName, testBatch, baselineEnvName, environmentName, appEnv,
                defaultMatchSettings,
                branchName != null ? branchName : System.getenv("APPLITOOLS_BRANCH"),
                parentBranchName != null ? parentBranchName : System.getenv("APPLITOOLS_PARENT_BRANCH"),
                baselineBranchName != null ? baselineBranchName : System.getenv("APPLITOOLS_BASELINE_BRANCH"),
                saveDiffs,
                properties);

        logger.verbose("Starting server session...");
        runningSession = serverConnector.startSession(sessionStartInfo);

        logger.verbose("Server session ID is " + runningSession.getId());

        String testInfo = "'" + testName + "' of '" + getAppName() + "' " + appEnv;
        if (runningSession.getIsNewSession()) {
            logger.log("--- New test started - " + testInfo);
            shouldMatchWindowRunOnceOnTimeout = true;
        } else {
            logger.log("--- Test started - " + testInfo);
            shouldMatchWindowRunOnceOnTimeout = false;
        }
    }

    private void ensureViewportSize() {
        if (!isViewportSizeSet) {

            try {
                if (viewportSizeHandler.get() == null) {
                    // If it's read-only, no point in making the getViewportSize() call.
                    if (!(viewportSizeHandler instanceof ReadOnlyPropertyHandler)) {
                        RectangleSize targetSize = getViewportSize();
                        sessionEventHandlers.setSizeWillStart(targetSize);
                        viewportSizeHandler.set(targetSize);
                    }
                } else {
                    RectangleSize targetSize = viewportSizeHandler.get();
                    sessionEventHandlers.setSizeWillStart(targetSize);
                    setViewportSize(targetSize);
                }
                isViewportSizeSet = true;
                sessionEventHandlers.setSizeEnded();
            } catch (NullPointerException e) {
                isViewportSizeSet = false;
            }
        }
    }

    protected EyesScreenshot getSubScreenshot(EyesScreenshot screenshot, Region region, ICheckSettingsInternal checkSettingsInternal) {
        return screenshot.getSubScreenshot(region, false);
    }

    /**
     * @param region         The region of the screenshot which will be set in the application output.
     * @param lastScreenshot Previous application screenshot (used for compression) or {@code null} if not available.
     * @return The updated app output and screenshot.
     */
    private AppOutputWithScreenshot getAppOutputWithScreenshot(
            Region region, EyesScreenshot lastScreenshot, ICheckSettingsInternal checkSettingsInternal) {

        logger.verbose("getting screenshot...");
        // Getting the screenshot (abstract function implemented by each SDK).
        EyesScreenshot screenshot = getScreenshot();
        logger.verbose("Done getting screenshot!");

        // Cropping by region if necessary
        if (!region.isSizeEmpty()) {
            screenshot = getSubScreenshot(screenshot, region, checkSettingsInternal);
            debugScreenshotsProvider.save(screenshot.getImage(), "SUB_SCREENSHOT");
        }

        logger.verbose("Compressing screenshot...");
        String compressResult = compressScreenshot64(screenshot, lastScreenshot);
        logger.verbose("Done! Getting title...");
        String title = getTitle();
        logger.verbose("Done!");
        AppOutputWithScreenshot result = new AppOutputWithScreenshot(new AppOutput(title, compressResult), screenshot);
        logger.verbose("Done!");
        return result;
    }

    /**
     * Compresses a given screenshot.
     * @param screenshot     The screenshot to compress.
     * @param lastScreenshot The previous screenshot, or null.
     * @return A base64 encoded compressed screenshot.
     */
    private String compressScreenshot64(EyesScreenshot screenshot,
                                        EyesScreenshot lastScreenshot) {

        ArgumentGuard.notNull(screenshot, "screenshot");

        BufferedImage screenshotImage = screenshot.getImage();
        byte[] uncompressed = ImageUtils.encodeAsPng(screenshotImage);

        BufferedImage source = (lastScreenshot != null) ?
                lastScreenshot.getImage() : null;

        // Compressing the screenshot
        byte[] compressedScreenshot;
        try {
            compressedScreenshot = ImageDeltaCompressor.compressByRawBlocks(
                    screenshotImage, uncompressed, source);
        } catch (IOException e) {
            throw new EyesException("Failed to compress screenshot!", e);
        }

        return Base64.encodeBase64String(compressedScreenshot);
    }

    public void log(String message) {
        logger.log(message);
    }

    protected SessionEventHandlers getSessionEventHandlers() {
        return sessionEventHandlers;
    }

    public void addSessionEventHandler(ISessionEventHandler eventHandler) {
        this.sessionEventHandlers.addEventHandler(eventHandler);
    }

    protected abstract String getAUTSessionId();
}
