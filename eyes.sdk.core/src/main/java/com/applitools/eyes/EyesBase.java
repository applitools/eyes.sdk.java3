package com.applitools.eyes;

import com.applitools.ICheckSettings;
import com.applitools.eyes.config.IConfigurationGetter;
import com.applitools.eyes.config.IConfigurationSetter;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.eyes.capture.AppOutputProvider;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.debug.FileDebugScreenshotsProvider;
import com.applitools.eyes.debug.NullDebugScreenshotProvider;
import com.applitools.eyes.diagnostics.ResponseTimeAlgorithm;
import com.applitools.eyes.events.ISessionEventHandler;
import com.applitools.eyes.events.SessionEventHandlers;
import com.applitools.eyes.events.ValidationInfo;
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
public abstract class EyesBase implements IEyesBase{

    protected static final int USE_DEFAULT_TIMEOUT = -1;
    private static final int MAX_ITERATION = 10;

    private boolean shouldMatchWindowRunOnceOnTimeout;

    private MatchWindowTask matchWindowTask;

    protected IServerConnector serverConnector;
    protected RunningSession runningSession;
    protected SessionStartInfo sessionStartInfo;
    protected PropertyHandler<RectangleSize> viewportSizeHandler;
    protected EyesScreenshot lastScreenshot;
    protected PropertyHandler<ScaleProvider> scaleProviderHandler;
    protected PropertyHandler<CutProvider> cutProviderHandler;
    protected PropertyHandler<PositionProvider> positionProviderHandler;

    // Will be checked <b>before</b> any argument validation. If true,
    // all method will immediately return without performing any action.
    private boolean isDisabled;
    protected Logger logger;

    protected boolean isOpen;

    private final Queue<Trigger> userInputs;
    private final List<PropertyData> properties = new ArrayList<>();

    private boolean isViewportSizeSet;
    protected RenderingInfo renderInfo;

    private int validationId;
    private final SessionEventHandlers sessionEventHandlers = new SessionEventHandlers();
    protected DebugScreenshotsProvider debugScreenshotsProvider;

    public EyesBase() {

        if (isDisabled) {
            userInputs = null;
            return;
        }

        logger = new Logger();

        initProviders();

        setServerConnector(new ServerConnector());

        runningSession = null;
        userInputs = new ArrayDeque<>();

        lastScreenshot = null;
        debugScreenshotsProvider = new NullDebugScreenshotProvider();
    }


    /**
     * @param hardReset If false, init providers only if they're not initialized.
     */
    private void initProviders(boolean hardReset) {
        if (hardReset) {
            scaleProviderHandler = new SimplePropertyHandler<>();
            scaleProviderHandler.set(new NullScaleProvider(logger));
            cutProviderHandler = new SimplePropertyHandler<>();
            cutProviderHandler.set(new NullCutProvider());
            positionProviderHandler = new SimplePropertyHandler<>();
            positionProviderHandler.set(new InvalidPositionProvider());
            viewportSizeHandler = new SimplePropertyHandler<>();
            viewportSizeHandler.set(null);

            return;
        }

        if (scaleProviderHandler == null) {
            scaleProviderHandler = new SimplePropertyHandler<>();
            scaleProviderHandler.set(new NullScaleProvider(logger));
        }

        if (cutProviderHandler == null) {
            cutProviderHandler = new SimplePropertyHandler<>();
            cutProviderHandler.set(new NullCutProvider());
        }

        if (positionProviderHandler == null) {
            positionProviderHandler = new SimplePropertyHandler<>();
            positionProviderHandler.set(new InvalidPositionProvider());
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
     * Sets the server connector to use. MUST BE SET IN ORDER FOR THE EYES OBJECT TO WORK!
     * @param serverConnector The server connector object to use.
     */
    public void setServerConnector(IServerConnector serverConnector) {
        ArgumentGuard.notNull(serverConnector, "serverConnector");
        if (serverConnector.getLogger() == null) {
            serverConnector.setLogger(this.logger);
        }
        this.serverConnector = serverConnector;
    }

    /**
     * Sets the API key of your applitools Eyes account.
     * @param apiKey The api key to set.
     */
    public IConfigurationSetter setApiKey(String apiKey) {
        ArgumentGuard.notNull(apiKey, "apiKey");
        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }
        serverConnector.setApiKey(apiKey);
        return this.getConfigSetter();
    }

    /**
     * @return The currently set API key or {@code null} if no key is set.
     */
    public String getApiKey() {
        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }
        return serverConnector.getApiKey();
    }


    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server, or {@code null} to use
     *                  the default server.
     */
    public IConfigurationSetter setServerUrl(String serverUrl) {
        setServerUrl(URI.create(serverUrl));
        return this.getConfigSetter();
    }

    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server, or {@code null} to use
     *                  the default server.
     */
    public IConfigurationSetter setServerUrl(URI serverUrl) {
        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }
        if (serverUrl == null) {
            serverConnector.setServerUrl(getDefaultServerUrl());
        } else {
            serverConnector.setServerUrl(serverUrl);
        }
        return this.getConfigSetter();
    }

    /**
     * @return The URI of the eyes server.
     */
    public URI getServerUrl() {
        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }
        return serverConnector.getServerUrl();
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     * @param abstractProxySettings The proxy settings to be used by the rest client.
     *                              If {@code null} then no proxy is set.
     */
    public IConfigurationSetter setProxy(AbstractProxySettings abstractProxySettings) {
        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }
        getConfigSetter().setProxy(abstractProxySettings);
        serverConnector.setProxy(abstractProxySettings);
        return getConfigSetter();
    }

    /**
     * @return The current proxy settings used by the server connector,
     * or {@code null} if no proxy is set.
     */
    public AbstractProxySettings getProxy() {
        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }
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
     * @return The base agent id of the SDK.
     */
    protected abstract String getBaseAgentId();

    /**
     * @return The full agent id composed of both the base agent id and the
     * user given agent id.
     */
    public String getFullAgentId() {
        String agentId = getConfigGetter().getAgentId();
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
            cutProvider.setLogger(logger);
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
            FixedScaleProvider scaleProvider = new FixedScaleProvider(logger, scaleRatio);
            scaleProviderHandler = new ReadOnlyPropertyHandler<ScaleProvider>(
                    logger, scaleProvider);
        } else {
            scaleProviderHandler = new SimplePropertyHandler<>();
            scaleProviderHandler.set(new NullScaleProvider(logger));
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

    protected void addProperty(PropertyData propery) {
        properties.add(propery);
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
            debugScreenshotsProvider = new FileDebugScreenshotsProvider(logger);
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
                logger.log("Server session was not started --- Empty test ended.");
                return new TestResults();
            }

            boolean isNewSession = runningSession.getIsNewSession();

            logger.verbose("Ending server session...");
            boolean save = (isNewSession && getConfigGetter().getSaveNewTests())
                    || (!isNewSession && getConfigGetter().getSaveFailedTests());
            logger.verbose("Automatically save test? " + String.valueOf(save));
            TestResults results = serverConnector.stopSession(runningSession, false, save);

            results.setNew(isNewSession);
            results.setUrl(runningSession.getUrl());
            logger.verbose(results.toString());

            sessionEventHandlers.testEnded(getAUTSessionId(), results);

            logSessionResultsAndThrowException(logger, throwEx, results);

            results.setServerConnector(this.serverConnector);

            return results;
        } finally {
            // Making sure that we reset the running session even if an
            // exception was thrown during close.
            runningSession = null;
//            logger.getLogHandler().close();
        }
    }

    public static void logSessionResultsAndThrowException(Logger logger, boolean throwEx, TestResults results) {
        TestResultsStatus status = results.getStatus();
        String sessionResultsUrl = results.getUrl();
        String scenarioIdOrName = results.getName();
        String appIdOrName = results.getAppName();
        if (status == TestResultsStatus.Unresolved) {
            if (results.isNew()) {
                logger.log("--- New test ended. Please approve the new baseline at " + sessionResultsUrl);
                if (throwEx) {
                    throw new NewTestException(results, scenarioIdOrName, appIdOrName);
                }
            } else {
                logger.log("--- Failed test ended. See details at " + sessionResultsUrl);
                if (throwEx) {
                    throw new DiffsFoundException(results, scenarioIdOrName, appIdOrName);
                }
            }
        } else if (status == TestResultsStatus.Failed) {
            logger.log("--- Failed test ended. See details at " + sessionResultsUrl);
            if (throwEx) {
                throw new TestFailedException(results, scenarioIdOrName, appIdOrName);
            }
        } else {
            // Test passed
            logger.log("--- Test passed. See details at " + sessionResultsUrl);
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
            boolean save = (isNewSession && getConfigGetter().getSaveNewTests());

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
            logger.getLogHandler().close();
        }
    }

    /**
     * If a test is running, aborts it. Otherwise, does nothing.
     */
    public TestResults abortIfNotClosed() {
        try {
            if (isDisabled) {
                logger.verbose("Ignored");
                return null;
            }

            isOpen = false;

            lastScreenshot = null;
            clearUserInputs();

            if (null == runningSession) {
                logger.verbose("Closed");
                return null;
            }

            logger.verbose("Aborting server session...");
            try {
                // When aborting we do not save the test.
                boolean isNewSession = runningSession.getIsNewSession();
                TestResults results = serverConnector.stopSession(runningSession, true, false);
                results.setNew(isNewSession);
                results.setUrl(runningSession.getUrl());
                logger.log("--- Test aborted.");
                return results;
            } catch (EyesException ex) {
                logger.log(
                        "Failed to abort server session: " + ex.getMessage());
            }
        } finally {
            runningSession = null;
            closeLogger();
        }
        return null;
    }

    public TestResults abort() {
        return abortIfNotClosed();
    }

    protected void openLogger() {
        logger.getLogHandler().open();
    }

    protected void closeLogger() {
        logger.getLogHandler().close();
    }

    /**
     * @return The currently set position provider.
     */
    public PositionProvider getPositionProvider() {
        return positionProviderHandler.get();
    }

    /**
     * @param positionProvider The position provider to be used.
     */
    public void setPositionProvider(PositionProvider positionProvider) {
        if (positionProvider != null) {
            positionProviderHandler = new ReadOnlyPropertyHandler<>(logger,
                    positionProvider);
        } else {
            positionProviderHandler = new SimplePropertyHandler<>();
            positionProviderHandler.set(new InvalidPositionProvider());
        }
    }

    /**
     * See {@link #checkWindowBase(RegionProvider, String, boolean, int, String)}.
     * {@code retryTimeout} defaults to {@code USE_DEFAULT_TIMEOUT}.
     * @param regionProvider Returns the region to check or the empty rectangle to check the entire window.
     * @param tag            An optional tag to be associated with the snapshot.
     * @param ignoreMismatch Whether to ignore this check if a mismatch is found.
     * @return The result of matching the output with the expected output.
     */
    protected MatchResult checkWindowBase(RegionProvider regionProvider,
                                          String tag, boolean ignoreMismatch, String source) {
        return checkWindowBase(regionProvider, tag, ignoreMismatch,
                USE_DEFAULT_TIMEOUT, source);
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
    protected MatchResult checkWindowBase(RegionProvider regionProvider, String tag, boolean ignoreMismatch, int retryTimeout, String source) {
        return this.checkWindowBase(regionProvider, tag, ignoreMismatch, new CheckSettings(retryTimeout), source);
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
                                          boolean ignoreMismatch, ICheckSettings checkSettings, String source) {
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

        ensureRunningSession();

        beforeMatchWindow();

        result = matchWindow(regionProvider, tag, ignoreMismatch, checkSettings, source);

        afterMatchWindow();

        logger.verbose("MatchWindow Done!");

        if (!ignoreMismatch) {
            clearUserInputs();
            lastScreenshot = result.getScreenshot(checkSettings);
        }

        validateResult(tag, result);

        logger.verbose("Done!");
        return result;
    }

    protected abstract String tryCaptureDom();

    protected String tryCaptureAndPostDom(ICheckSettingsInternal checkSettingsInternal) {
        String domUrl = null;
        if (GeneralUtils.configureSendDom(checkSettingsInternal, getConfigGetter())) {
            try {
                String domJson = tryCaptureDom();
                domUrl = tryPostDomSnapshot(domJson);
                logger.verbose("domUrl: " + domUrl);
            } catch (Exception ex) {
                logger.log("Error: " + ex);
            }
        }

        return domUrl;
    }

    protected ValidationInfo fireValidationWillStartEvent(String tag) {
        String autSessionId = getAUTSessionId();

        ValidationInfo validationInfo = new ValidationInfo();
        validationInfo.setValidationId("" + (++validationId));
        validationInfo.setTag(tag);

        getSessionEventHandlers().validationWillStart(autSessionId, validationInfo);

        return validationInfo;
    }

    private MatchResult matchWindow(RegionProvider regionProvider, String tag, boolean ignoreMismatch,
                                    ICheckSettings checkSettings, String source) {
        MatchResult result;
        ICheckSettingsInternal checkSettingsInternal = (checkSettings instanceof ICheckSettingsInternal) ? (ICheckSettingsInternal) checkSettings : null;

        ImageMatchSettings defaultMatchSettings = getConfigGetter().getDefaultMatchSettings();

        // Update retry timeout if it wasn't specified.
        int retryTimeout = -1;

        // Set defaults if necessary
        if (checkSettingsInternal != null) {
            retryTimeout = checkSettingsInternal.getTimeout();
            if (checkSettingsInternal.getMatchLevel() == null) {
                checkSettings = checkSettings.matchLevel(defaultMatchSettings.getMatchLevel());
            }

            if (checkSettingsInternal.getIgnoreCaret() == null) {
                checkSettings = checkSettings.ignoreCaret(defaultMatchSettings.getIgnoreCaret());
            }

            checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
        }

        Region region = regionProvider.getRegion(checkSettingsInternal);
        logger.verbose("params: ([" + region + "], " + tag + ", " + retryTimeout + ")");

        result = matchWindowTask.matchWindow(
                getUserInputs(), region, tag, shouldMatchWindowRunOnceOnTimeout, ignoreMismatch,
                checkSettingsInternal, retryTimeout, source);

        return result;
    }

    private String tryPostDomSnapshot(String domJson) {
        if (domJson != null) {
            return serverConnector.postDomSnapshot(domJson);
        }
        return null;
    }

    private void validateResult(String tag, MatchResult result) {
        if (result.getAsExpected()) {
            return;
        }

        shouldMatchWindowRunOnceOnTimeout = true;

        if (!runningSession.getIsNewSession()) {
            logger.log(String.format("Mismatch! (%s)", tag));
        }

        if (getConfigGetter().getFailureReports() == FailureReports.IMMEDIATE) {
            throw new TestFailedException(String.format(
                    "Mismatch found in '%s' of '%s'",
                    sessionStartInfo.getScenarioIdOrName(),
                    sessionStartInfo.getAppIdOrName()));
        }
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
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
            logger.verbose("Starting WebDriver action.");
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
        if (isDisabled) {
            logger.verbose("Ignored");
            return;
        }

        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }

        // If there's no default application name, one must be provided for the current test.
        if (getAppName() == null) {
            ArgumentGuard.notNull(appName, "appName");
            this.getConfigSetter().setAppName(appName);
        }

        ArgumentGuard.notNull(testName, "testName");
        this.getConfigSetter().setTestName(testName);

        logger.log("Agent = " + getFullAgentId());
        logger.verbose(String.format("openBase('%s', '%s', '%s')", appName,
                testName, viewportSize));

        getConfigSetter().setSessionType(sessionType != null ? sessionType : SessionType.SEQUENTIAL);
        getConfigSetter().setViewportSize(viewportSize);

        openBase();
    }

    protected void openBase() throws EyesException {
        openLogger();
        int retry = 0;
        do {
            try {
                if (isDisabled) {
                    logger.verbose("Ignored");
                    return;
                }

                sessionEventHandlers.testStarted(getAUTSessionId());

                validateApiKey();
                logOpenBase();
                validateSessionOpen();

                initProviders();

                this.isViewportSizeSet = false;

                sessionEventHandlers.initStarted();

                beforeOpen();

                RectangleSize viewportSize = getViewportSizeForOpen();
                viewportSizeHandler.set(viewportSize);

                try {
                    if (viewportSize != null) {
                        ensureRunningSession();
                    }
                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    retry++;
                    continue;
                }

                this.validationId = -1;

                isOpen = true;
                afterOpen();
                return;
            } catch (EyesException e) {
                logger.log(e.getMessage());
                logger.getLogHandler().close();
                throw e;
            }


        } while (MAX_ITERATION > retry);

        throw new EyesException("eyes.openBase() failed");
    }

    protected RectangleSize getViewportSizeForOpen() {
        return getConfigGetter().getViewportSize();
    }

    protected void ensureRunningSession() {
        if (runningSession != null) {
            logger.log("session already running.");
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
                getConfigGetter().getMatchTimeout(),
                this,
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
        logger.log(String.format("matchTimeout = '%d' ", getConfigGetter().getMatchTimeout()));
        logger.log(String.format("Default match settings = '%s' ", getConfigGetter().getDefaultMatchSettings()));
        logger.log(String.format("FailureReports = '%s' ", getConfigGetter().getFailureReports()));
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
    protected abstract IConfigurationSetter setViewportSize(RectangleSize size);

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
    protected abstract EyesScreenshot getScreenshot(ICheckSettingsInternal checkSettingsInternal);

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
        if (getConfigGetter().getHostOS() != null) {
            appEnv.setOs(getConfigGetter().getHostOS());
        }

        if (getConfigGetter().getHostApp() != null) {
            appEnv.setHostingApp(getConfigGetter().getHostApp());
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
        if (serverConnector == null) {
            throw new EyesException("server connector not set.");
        }
        ensureViewportSize();

        IConfigurationGetter configGetter = getConfigGetter();
        BatchInfo testBatch = configGetter.getBatch();
        if (testBatch == null) {
            logger.verbose("No batch set");
            getConfigSetter().setBatch(new BatchInfo(null));
        } else {
            logger.verbose("Batch is " + testBatch);
        }

        AppEnvironment appEnv = getAppEnvironment();

        sessionEventHandlers.initEnded();

        logger.verbose("Application environment is " + appEnv);

        String appName = getAppName();
        sessionStartInfo = new SessionStartInfo(getFullAgentId(), configGetter.getSessionType(), appName,
                null, getTestName(), configGetter.getBatch(), getBaselineEnvName(),
                configGetter.getEnvironmentName(), getAppEnvironment(), configGetter.getDefaultMatchSettings(),
                configGetter.getBranchName(),
                configGetter.getParentBranchName(), configGetter.getBaselineBranchName(), configGetter.getSaveDiffs(), properties);

        logger.verbose("Starting server session...");
        runningSession = serverConnector.startSession(sessionStartInfo);

        logger.verbose("Server session ID is " + runningSession.getId());

        String testInfo = "'" + getTestName() + "' of '" + appName + "' " + appEnv;
        if (runningSession.getIsNewSession()) {
            logger.log("--- New test started - " + testInfo);
            shouldMatchWindowRunOnceOnTimeout = true;
        } else {
            logger.log("--- Test started - " + testInfo);
            shouldMatchWindowRunOnceOnTimeout = false;
        }
    }

    protected String getTestName() {
        return getConfigGetter().getTestName();
    }

    protected String getAppName() {
        return getConfigGetter().getAppName();
    }

    protected String getBaselineEnvName() {
        return getConfigGetter().getBaselineEnvName();
    }

    public Object getAgentSetup() {
        return null;
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
        EyesScreenshot screenshot = getScreenshot(checkSettingsInternal);
        logger.verbose("Done getting screenshot!");

        // Cropping by region if necessary
        Location location = null;
        if (!region.isSizeEmpty()) {
            location = region.getLocation();
            screenshot = screenshot.getSubScreenshot(region, false);
            debugScreenshotsProvider.save(screenshot.getImage(), "SUB_SCREENSHOT");
        }

        logger.verbose("Compressing screenshot...");
        String compressResult = compressScreenshot64(screenshot, lastScreenshot);
        logger.verbose("Done! Getting title...");
        String title = getTitle();
        logger.verbose("Done!");

        AppOutputWithScreenshot result = new AppOutputWithScreenshot(new AppOutput(title, compressResult, screenshot.domUrl, null), screenshot, location);
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

    public void removeSessionEventHandler(ISessionEventHandler eventHandler) {
        this.sessionEventHandlers.removeEventHandler(eventHandler);
    }

    public void clearSessionEventHandlers() {
        this.sessionEventHandlers.clearEventHandlers();
    }

    protected abstract String getAUTSessionId();

    public Boolean isSendDom() {
        return getConfigGetter().isSendDom();
    }

    public IConfigurationSetter setSendDom(boolean isSendDom) {
        this.getConfigSetter().setSendDom(isSendDom);
        return getConfigSetter();
    }

    public RenderingInfo getRenderingInfo() {
        if (this.renderInfo != null) {
            return this.renderInfo;
        }
        this.renderInfo = this.serverConnector.getRenderInfo();
        return this.renderInfo;
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     * @param batch The batch info to set.
     */
    public IConfigurationSetter setBatch(BatchInfo batch) {
        if (isDisabled) {
            logger.verbose("Ignored");
            return getConfigSetter();
        }

        logger.verbose("setBatch(" + batch + ")");

        this.getConfigSetter().setBatch(batch);
        return getConfigSetter();
    }

    protected abstract <T extends IConfigurationGetter> T getConfigGetter();

    protected abstract <T extends IConfigurationSetter> T getConfigSetter();


}
