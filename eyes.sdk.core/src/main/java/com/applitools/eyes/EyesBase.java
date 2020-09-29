package com.applitools.eyes;

import com.applitools.ICheckSettings;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.capture.AppOutputProvider;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.debug.FileDebugScreenshotsProvider;
import com.applitools.eyes.debug.NullDebugScreenshotProvider;
import com.applitools.eyes.events.ISessionEventHandler;
import com.applitools.eyes.events.SessionEventHandlers;
import com.applitools.eyes.events.ValidationInfo;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.positioning.InvalidPositionProvider;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.scaling.FixedScaleProvider;
import com.applitools.eyes.scaling.NullScaleProvider;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.triggers.MouseTrigger;
import com.applitools.eyes.triggers.TextTrigger;
import com.applitools.eyes.visualgrid.model.DeviceSize;
import com.applitools.eyes.visualgrid.model.RenderingInfo;
import com.applitools.utils.*;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Applitools Eyes Base for Java API .
 */
public abstract class EyesBase implements IEyesBase {

    protected static final int USE_DEFAULT_TIMEOUT = -1;
    private static final int MAX_ITERATION = 10;

    private boolean shouldMatchWindowRunOnceOnTimeout;

    private MatchWindowTask matchWindowTask;

    protected ServerConnector serverConnector;
    protected RunningSession runningSession;
    protected SessionStartInfo sessionStartInfo;
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
    public void setServerConnector(ServerConnector serverConnector) {
        ArgumentGuard.notNull(serverConnector, "serverConnector");
        this.serverConnector = serverConnector;
        serverConnector.setLogger(logger);
    }

    public ServerConnector getServerConnector() {
        if (serverConnector != null && serverConnector.getAgentId() == null) {
            serverConnector.setAgentId(getFullAgentId());
        }

        return serverConnector;
    }

    /**
     * Sets the API key of your applitools Eyes account.
     * @param apiKey The api key to set.
     */
    public Configuration setApiKey(String apiKey) {
        ArgumentGuard.notNull(apiKey, "apiKey");
        getConfigurationInstance().setApiKey(apiKey);
        if (getServerConnector() == null) {
            throw new EyesException("server connector not set.");
        }
        getServerConnector().setApiKey(apiKey);
        return this.getConfigurationInstance();
    }

    /**
     * @return The currently set API key or {@code null} if no key is set.
     */
    public String getApiKey() {
        if (getServerConnector() == null) {
            throw new EyesException("server connector not set.");
        }
        return getServerConnector().getApiKey();
    }


    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server, or {@code null} to use
     *                  the default server.
     */
    public Configuration setServerUrl(String serverUrl) {
        setServerUrl(URI.create(serverUrl));
        return this.getConfigurationInstance();
    }

    /**
     * Sets the current server URL used by the rest client.
     * @param serverUrl The URI of the rest server, or {@code null} to use
     *                  the default server.
     */
    public Configuration setServerUrl(URI serverUrl) {
        if (getServerConnector() == null) {
            throw new EyesException("server connector not set.");
        }
        if (serverUrl == null) {
            getServerConnector().setServerUrl(getDefaultServerUrl());
        } else {
            getServerConnector().setServerUrl(serverUrl);
        }
        return this.getConfigurationInstance();
    }

    /**
     * @return The URI of the eyes server.
     */
    public URI getServerUrl() {
        if (getServerConnector() == null) {
            throw new EyesException("server connector not set.");
        }
        return getServerConnector().getServerUrl();
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     * @param abstractProxySettings The proxy settings to be used by the rest client.
     *                              If {@code null} then no proxy is set.
     */
    public Configuration setProxy(AbstractProxySettings abstractProxySettings) {
        if (getServerConnector() == null) {
            throw new EyesException("server connector not set.");
        }

        getServerConnector().setProxy(abstractProxySettings);
        return getConfigurationInstance();
    }

    /**
     * @return The current proxy settings used by the server connector,
     * or {@code null} if no proxy is set.
     */
    public AbstractProxySettings getProxy() {
        if (getServerConnector() == null) {
            throw new EyesException("server connector not set.");
        }
        return getServerConnector().getProxy();
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
        String agentId = getConfigurationInstance().getAgentId();
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

    public boolean getIsScaleProviderExplicitlySet() {
        return scaleProviderHandler != null && !(scaleProviderHandler.get() instanceof NullScaleProvider);
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

    protected void addProperty(PropertyData property) {
        properties.add(property);
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
        AtomicReference<TestResults> reference = new AtomicReference<>();
        final AtomicReference<EyesSyncObject> lock = new AtomicReference<>(new EyesSyncObject(logger, "close"));
        close(new SyncTaskListener<>(lock, reference), throwEx);
        synchronized (lock.get()) {
            try {
                if (reference.get() == null) {
                    lock.get().waitForNotify();
                }
            } catch (InterruptedException e) {
                throw new EyesException("Failed waiting for close", e);
            }
        }

        TestResults testResults = reference.get();
        if (testResults == null) {
            throw new EyesException("Failed closing test");
        }
        logSessionResultsAndThrowException(logger, throwEx, testResults);
        return testResults;
    }

    public void close(final TaskListener<TestResults> listener, boolean throwEx) {
        if (isDisabled) {
            logger.verbose("Ignored");
            listener.onComplete(new TestResults());
            return;
        }
        logger.verbose(String.format("close(%b)", throwEx));
        if (!isOpen) {
            logger.log("WARNING: Eyes not open");
            listener.onComplete(new TestResults());
            return;
        }

        isOpen = false;

        lastScreenshot = null;
        clearUserInputs();

        initProviders(true);

        if (runningSession == null) {
            logger.log("Server session was not started --- Empty test ended.");
            listener.onComplete(new TestResults());
            return;
        }

        final boolean isNewSession = runningSession.getIsNew();

        logger.verbose("Ending server session...");
        boolean save = (isNewSession && getConfigurationInstance().getSaveNewTests())
                || (!isNewSession && getConfigurationInstance().getSaveFailedTests());
        logger.verbose("Automatically save test? " + save);
        getServerConnector().stopSession(new TaskListener<TestResults>() {
            @Override
            public void onComplete(TestResults testResults) {
                testResults.setNew(isNewSession);
                testResults.setUrl(runningSession.getUrl());
                logger.verbose(testResults.toString());

                sessionEventHandlers.testEnded(getAUTSessionId(), testResults);
                testResults.setServerConnector(getServerConnector());
                runningSession = null;
                listener.onComplete(testResults);
            }

            @Override
            public void onFail() {
                runningSession = null;
                listener.onFail();
            }
        }, runningSession, false, save);
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

    public TestResults abortIfNotClosed() {
        AtomicReference<TestResults> reference = new AtomicReference<>();
        final AtomicReference<EyesSyncObject> lock = new AtomicReference<>(new EyesSyncObject(logger, "abortIfNotClosed"));
        abortIfNotClosed(new SyncTaskListener<>(lock, reference));
        synchronized (lock.get()) {
            try {
                if (reference.get() == null) {
                    lock.get().waitForNotify();
                }
            } catch (InterruptedException e) {
                throw new EyesException("Failed waiting for abort", e);
            }
        }

        TestResults testResults = reference.get();
        if (testResults == null) {
            throw new EyesException("Failed stopping session");
        }
        return testResults;
    }

    /**
     * If a test is running, aborts it. Otherwise, does nothing.
     */
    public void abortIfNotClosed(final TaskListener<TestResults> listener) {
        if (isDisabled) {
            logger.verbose("Ignored");
            listener.onComplete(new TestResults());
            return;
        }

        isOpen = false;

        lastScreenshot = null;
        clearUserInputs();

        if (null == runningSession) {
            logger.verbose("Closed");
            listener.onComplete(new TestResults());
            return;
        }

        logger.verbose("Aborting server session...");
        // When aborting we do not save the test.
        getServerConnector().stopSession(new TaskListener<TestResults>() {
            @Override
            public void onComplete(TestResults testResults) {
                testResults.setNew(runningSession.getIsNew());
                testResults.setUrl(runningSession.getUrl());
                logger.log("--- Test aborted.");
                runningSession = null;
                closeLogger();
                listener.onComplete(testResults);
            }

            @Override
            public void onFail() {
                logger.log("Failed to abort server session");
                runningSession = null;
                closeLogger();
                listener.onFail();
            }
        }, runningSession, true, false);
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
     * See {@link #checkWindowBase(Region, String, int, String)}.
     * {@code retryTimeout} defaults to {@code USE_DEFAULT_TIMEOUT}.
     * @param region The region to check or null for the entire window.
     * @param tag    An optional tag to be associated with the snapshot.
     * @param source A string representing the source of the checkpoint.
     * @return The result of matching the output with the expected output.
     */
    protected MatchResult checkWindowBase(Region region, String tag, String source) {
        return checkWindowBase(region, tag, USE_DEFAULT_TIMEOUT, source);
    }

    /**
     * Takes a snapshot of the application under test and matches it with the
     * expected output.
     * @param region       The region to check or null for the entire window.
     * @param tag          An optional tag to be associated with the snapshot.
     * @param retryTimeout The amount of time to retry matching in milliseconds or a negative
     *                     value to use the default retry timeout.
     * @param source       A string representing the source of the checkpoint.
     * @return The result of matching the output with the expected output.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    protected MatchResult checkWindowBase(Region region, String tag, int retryTimeout, String source) {
        return this.checkWindowBase(region, new CheckSettings(retryTimeout).withName(tag), source);
    }

    protected MatchResult checkWindowBase(Region region, String tag, ICheckSettings checkSettings) {
        return checkWindowBase(region, checkSettings.withName(tag), getAppName());
    }

    /**
     * Takes a snapshot of the application under test and matches it with the
     * expected output.
     * @param region        The region to check or null for the entire window.
     * @param checkSettings The settings to use.
     * @param source        A string representing the source of the checkpoint.
     * @return The result of matching the output with the expected output.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    protected MatchResult checkWindowBase(Region region, ICheckSettings checkSettings, String source) {
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
        return checkWindowBase(region, checkSettingsInternal, source);
    }

    protected MatchResult checkWindowBase(Region region, ICheckSettingsInternal checkSettingsInternal, String source) {

        MatchResult result;

        if (getIsDisabled()) {
            logger.verbose("Ignored");
            result = new MatchResult();
            result.setAsExpected(true);
            return result;
        }

        String tag = checkSettingsInternal.getName();
        if (tag == null) {
            tag = "";
        }

        ArgumentGuard.isValidState(getIsOpen(), "Eyes not open");

        if (runningSession == null) {
            final AtomicReference<EyesSyncObject> lock = new AtomicReference<>(new EyesSyncObject(logger, "checkWindowBase"));
            ensureRunningSession(new SyncTaskListener<Void>(lock));
            synchronized (lock.get()) {
                try {
                    lock.get().waitForNotify();
                } catch (InterruptedException e) {
                    throw new EyesException("Failed waiting for open", e);
                }
            }
        }

        result = matchWindow(region, tag, checkSettingsInternal, source);

        logger.verbose("MatchWindow Done!");

        validateResult(tag, result);

        logger.verbose("Done!");
        return result;
    }

    protected abstract String tryCaptureDom();

    protected String tryCaptureAndPostDom(ICheckSettingsInternal checkSettingsInternal) {
        String domUrl = null;
        if (shouldCaptureDom(checkSettingsInternal.isSendDom())) {
            try {
                String domJson = tryCaptureDom();
                domUrl = tryPostDomCapture(domJson);
                logger.verbose("domUrl: " + domUrl);
            } catch (Exception ex) {
                logger.log("Error: " + ex);
            }
        }

        return domUrl;
    }

    private boolean shouldCaptureDom(Boolean sendDomFromCheckSettings) {
        boolean sendDomFromConfig = getConfigurationInstance().isSendDom() == null || getConfigurationInstance().isSendDom();
        return (sendDomFromCheckSettings != null && sendDomFromCheckSettings) || (sendDomFromCheckSettings == null && sendDomFromConfig);
    }

    protected ValidationInfo fireValidationWillStartEvent(String tag) {
        String autSessionId = getAUTSessionId();

        ValidationInfo validationInfo = new ValidationInfo();
        validationInfo.setValidationId("" + (++validationId));
        validationInfo.setTag(tag);

        getSessionEventHandlers().validationWillStart(autSessionId, validationInfo);

        return validationInfo;
    }

    private MatchResult matchWindow(Region region, String tag,
                                    ICheckSettingsInternal checkSettingsInternal, String source) {
        MatchResult result;

        result = matchWindowTask.matchWindow(
                getUserInputs(), region, tag, shouldMatchWindowRunOnceOnTimeout,
                checkSettingsInternal, source);

        return result;
    }

    private String tryPostDomCapture(String domJson) {
        if (domJson != null) {
            byte[] resultStream = GeneralUtils.getGzipByteArrayOutputStream(domJson);
            return matchWindowTask.tryUploadData(resultStream, "application/octet-stream", "application/json");
        }
        return null;
    }

    private void validateResult(String tag, MatchResult result) {
        if (result.getAsExpected()) {
            return;
        }

        shouldMatchWindowRunOnceOnTimeout = true;

        if (!runningSession.getIsNew()) {
            logger.log(String.format("Mismatch! (%s)", tag));
        }

        if (getConfigurationInstance().getFailureReports() == FailureReports.IMMEDIATE) {
            throw new TestFailedException(String.format(
                    "Mismatch found in '%s' of '%s'",
                    sessionStartInfo.getScenarioIdOrName(),
                    sessionStartInfo.getAppIdOrName()));
        }
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    protected void openBase() throws EyesException {
        final AtomicReference<EyesSyncObject> lock = new AtomicReference<>(new EyesSyncObject(logger, "openBase"));
        openBaseAsync(new SyncTaskListener<Void>(lock));
        synchronized (lock.get()) {
            try {
                lock.get().waitForNotify();
            } catch (InterruptedException e) {
                throw new EyesException("Failed waiting for open", e);
            }
        }

        if (!isOpen) {
            throw new EyesException("Failed starting session with the server");
        }
    }

    protected void openBaseAsync(final TaskListener<Void> taskListener) throws EyesException {
        openLogger();
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

        RectangleSize viewportSize = getViewportSizeForOpen();
        if (viewportSize == null) {
            viewportSize = RectangleSize.EMPTY;
        }
        getConfigurationInstance().setViewportSize(viewportSize);
        if (runningSession != null) {
            logger.log("session already running.");
            return;
        }

        final AtomicInteger attemptNumber = new AtomicInteger(0);
        final TaskListener<Void> listener = new TaskListener<Void>() {
            @Override
            public void onComplete(Void unused) {
                validationId = -1;
                isOpen = true;
                taskListener.onComplete(null);
            }

            @Override
            public void onFail() {
                if (attemptNumber.incrementAndGet() < MAX_ITERATION) {
                    try {
                        ensureRunningSession(this);
                        return;
                    } catch (Throwable e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                    }

                }
                taskListener.onFail();
            }
        };

        ensureRunningSession(listener);
    }

    protected RectangleSize getViewportSizeForOpen() {
        return getConfigurationInstance().getViewportSize();
    }

    protected void ensureRunningSession(final TaskListener<Void> listener) {
        logger.log("No running session, calling start session...");
        startSession(new TaskListener<RunningSession>() {
            @Override
            public void onComplete(RunningSession result) {
                runningSession = result;
                logger.verbose("Server session ID is " + runningSession.getId());

                logger.setSessionId(runningSession.getSessionId());
                String testName = "'" + getTestName() + "'";
                if (runningSession.getIsNew()) {
                    logger.log("--- New test started - " + testName);
                    shouldMatchWindowRunOnceOnTimeout = true;
                } else {
                    logger.log("--- Test started - " + testName);
                    shouldMatchWindowRunOnceOnTimeout = false;
                }

                matchWindowTask = new MatchWindowTask(
                        logger,
                        getServerConnector(),
                        runningSession,
                        getConfigurationInstance().getMatchTimeout(),
                        EyesBase.this,
                        // A callback which will call getAppOutput
                        new AppOutputProvider() {
                            @Override
                            public AppOutputWithScreenshot getAppOutput(Region region,
                                                                        ICheckSettingsInternal checkSettingsInternal,
                                                                        ImageMatchSettings imageMatchSettings) {
                                return getAppOutputWithScreenshot(region, checkSettingsInternal, imageMatchSettings);
                            }
                        }
                );

                listener.onComplete(null);
            }

            @Override
            public void onFail() {
                listener.onFail();
            }
        });
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
        logger.log(String.format("Eyes server URL is '%s'", getServerConnector().getServerUrl()));
        logger.verbose(String.format("Timeout = '%d'", getServerConnector().getTimeout()));
        logger.log(String.format("matchTimeout = '%d' ", getConfigurationInstance().getMatchTimeout()));
        logger.log(String.format("Default match settings = '%s' ", getConfigurationInstance().getDefaultMatchSettings()));
        logger.log(String.format("FailureReports = '%s' ", getConfigurationInstance().getFailureReports()));
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
    protected abstract Configuration setViewportSize(RectangleSize size);

    protected void setEffectiveViewportSize(RectangleSize size) {
    }

    /**
     * Define the viewport size as {@code size} without doing any actual action on the
     * @param explicitViewportSize The size of the viewport. {@code null} disables the explicit size.
     */
    public void setExplicitViewportSize(RectangleSize explicitViewportSize) {
        if (explicitViewportSize == null) {
            return;
        }

        logger.verbose("Viewport size explicitly set to " + explicitViewportSize);
        getConfigurationInstance().setViewportSize(explicitViewportSize);
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
    protected abstract EyesScreenshot getScreenshot(Region targetRegion, ICheckSettingsInternal checkSettingsInternal);

    /**
     * @return The current title of of the AUT.
     */
    protected abstract String getTitle();

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
        cursorInScreenshot = cursorInScreenshot.offset(control.getLocation());
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
            cursorInScreenshot = cursorInScreenshot.offset(-l.getX(), -l.getY());
        }

        Trigger trigger = new MouseTrigger(action, controlScreenshotIntersect, cursorInScreenshot);
        addUserInput(trigger);

        logger.verbose(String.format("Added %s", trigger));
    }

    /**
     * Application environment is the environment (e.g., the host OS) which
     * runs the application under test.
     * @return The current application environment.
     */
    protected AppEnvironment getAppEnvironment() {
        AppEnvironment appEnv = new AppEnvironment();

        // If hostOS isn't set, we'll try and extract and OS ourselves.
        if (getConfigurationInstance().getHostOS() != null) {
            appEnv.setOs(getConfigurationInstance().getHostOS());
        }

        if (getConfigurationInstance().getHostApp() != null) {
            appEnv.setHostingApp(getConfigurationInstance().getHostApp());
        }

        if (getConfigurationInstance().getHostingAppInfo() != null) {
            appEnv.setHostingAppInfo(getConfigurationInstance().getHostingAppInfo());
        }

        if (getConfigurationInstance().getOsInfo() != null) {
            appEnv.setOsInfo(getConfigurationInstance().getOsInfo());
        }

        if (getConfigurationInstance().getDeviceInfo() != null) {
            appEnv.setDeviceInfo(getConfigurationInstance().getDeviceInfo());
        }

        appEnv.setInferred(getInferredEnvironment());
        appEnv.setDisplaySize(getConfigurationInstance().getViewportSize());
        return appEnv;
    }

    /**
     * Start eyes session on the eyes server.
     */
    protected void startSession(TaskListener<RunningSession> listener) {
        logger.verbose("startSession()");
        if (getServerConnector() == null) {
            throw new EyesException("server connector not set.");
        }
        ensureViewportSize();

        Configuration configGetter = getConfigurationInstance();
        BatchInfo testBatch = configGetter.getBatch();
        if (testBatch == null) {
            logger.verbose("No batch set");
            getConfigurationInstance().setBatch(new BatchInfo(null));
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
        String testInfo = "'" + getTestName() + "' of '" + appName + "' " + appEnv;
        logger.log("--- Starting test - " + testInfo);
        getServerConnector().startSession(listener, sessionStartInfo);
    }

    protected String getTestName() {
        return getConfigurationInstance().getTestName();
    }

    protected String getAppName() {
        return getConfigurationInstance().getAppName();
    }

    protected String getBaselineEnvName() {
        return getConfigurationInstance().getBaselineEnvName();
    }

    public Object getAgentSetup() {
        return null;
    }

    private void ensureViewportSize() {
        if (isViewportSizeSet) {
            return;
        }

        RectangleSize viewportSize = getConfigurationInstance().getViewportSize();
        if (viewportSize == null || viewportSize.isEmpty()) {
            try {
                viewportSize = getViewportSize();
                logger.verbose("viewport size: " + viewportSize);
                setEffectiveViewportSize(viewportSize);
                getConfigurationInstance().setViewportSize(viewportSize);
            } catch (NullPointerException e) {
                isViewportSizeSet = false;
            }
        } else {
            try {
                logger.verbose("Setting viewport size to " + viewportSize);
                setViewportSize(viewportSize);
                isViewportSizeSet = true;
            } catch (Exception ex) {
                //setEffectiveViewportSize(ex.ActualViewportSize);
                isViewportSizeSet = false;
                throw ex;
            }
        }
    }

    /**
     * @param region The region of the screenshot which will be set in the application output.
     * @return The updated app output and screenshot.
     */
    private AppOutputWithScreenshot getAppOutputWithScreenshot(Region region, ICheckSettingsInternal checkSettingsInternal, ImageMatchSettings imageMatchSettings) {
        logger.verbose("getting screenshot...");
        // Getting the screenshot (abstract function implemented by each SDK).
        EyesScreenshot screenshot = getScreenshot(region, checkSettingsInternal);
        byte[] screenshotBytes = null;
        logger.verbose("Done getting screenshot!");
        String domUrl = null;
        if (screenshot != null) {
            logger.verbose("Getting image bytes (encoded as PNG)...");
            BufferedImage screenshotImage = screenshot.getImage();
            screenshotBytes = ImageUtils.encodeAsPng(screenshotImage);
            domUrl = screenshot.domUrl;
        }

        MatchWindowTask.collectRegions(this, screenshot, checkSettingsInternal, imageMatchSettings);

        logger.verbose("Done! Getting title...");
        String title = getTitle();
        logger.verbose("Done!");

        AppOutputWithScreenshot result = new AppOutputWithScreenshot(new AppOutput(title, screenshotBytes, domUrl, null),
                screenshot, region == null || region.isEmpty() ? null : region.getLocation());
        logger.verbose("Done!");
        return result;
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
        return getConfigurationInstance().isSendDom();
    }

    public Configuration setSendDom(boolean isSendDom) {
        this.getConfigurationInstance().setSendDom(isSendDom);
        return getConfigurationInstance();
    }

    public RenderingInfo getRenderingInfo() {
        return getServerConnector().getRenderInfo();
    }

    public Map<String, DeviceSize> getDevicesSizes(String path) {
        return getServerConnector().getDevicesSizes(path);
    }

    public Map<String, String> getUserAgents() {
        return getServerConnector().getUserAgents();
    }

    public Map<String, MobileDeviceInfo> getMobileDeviceInfo() {
        return getServerConnector().getMobileDevicesInfo();
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     * @param batch The batch info to set.
     */
    public Configuration setBatch(BatchInfo batch) {
        if (isDisabled) {
            logger.verbose("Ignored");
            return getConfigurationInstance();
        }

        logger.verbose("setBatch(" + batch + ")");

        this.getConfigurationInstance().setBatch(batch);
        return getConfigurationInstance();
    }

    /**
     * @return Underlying instance of the configuration for modification
     */
    protected abstract Configuration getConfigurationInstance();

    /**
     * @return Cloned instance of the configuration
     */
    public Configuration getConfiguration() {
        return new Configuration(getConfigurationInstance());
    }

    public void abortAsync() {
        abort();
    }
}