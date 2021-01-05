package com.applitools.eyes.selenium.rendering;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.debug.FileDebugScreenshotsProvider;
import com.applitools.eyes.debug.NullDebugScreenshotProvider;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.fluent.GetSimpleRegion;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.fluent.*;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridRunningTest;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class VisualGridEyes implements ISeleniumEyes {
    private final Logger logger;

    private final VisualGridRunner runner;
    final Map<String, RunningTest> testList = new HashMap<>();
    private boolean isOpen = false;

    private final String PROCESS_PAGE;
    private final String PROCESS_PAGE_FOR_IE;
    private final String POLL_RESULT;
    private final String POLL_RESULT_FOR_IE;
    private EyesSeleniumDriver webDriver;
    private String url;
    private Boolean isDisabled = Boolean.FALSE;
    private final ConfigurationProvider configurationProvider;
    private UserAgent userAgent = null;
    private RectangleSize viewportSize;
    private final List<PropertyData> properties = new ArrayList<>();

    private ImageProvider imageProvider;
    private DebugScreenshotsProvider debugScreenshotsProvider = new NullDebugScreenshotProvider();

    private static final String GET_ELEMENT_XPATH_JS =
            "var el = arguments[0];" +
                    "var xpath = '';" +
                    "do {" +
                    " var parent = el.parentElement;" +
                    " var index = 1;" +
                    " if (parent !== null) {" +
                    "  var children = parent.children;" +
                    "  for (var childIdx in children) {" +
                    "    var child = children[childIdx];" +
                    "    if (child === el) break;" +
                    "    if (child.tagName === el.tagName) index++;" +
                    "  }" +
                    "}" +
                    "xpath = '/' + el.tagName + '[' + index + ']' + xpath;" +
                    " el = parent;" +
                    "} while (el !== null);" +
                    "return '/' + xpath;";

    public VisualGridEyes(VisualGridRunner renderingGridManager, ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
        ArgumentGuard.notNull(renderingGridManager, "renderingGridRunner");
        this.runner = renderingGridManager;
        this.logger = renderingGridManager.getLogger();

        try {
            PROCESS_PAGE = GeneralUtils.readToEnd(VisualGridEyes.class.getResourceAsStream("/dom-snapshot/dist/processPagePoll.js"));
            PROCESS_PAGE_FOR_IE = GeneralUtils.readToEnd(VisualGridEyes.class.getResourceAsStream("/dom-snapshot/dist/processPagePollForIE.js"));
            POLL_RESULT = GeneralUtils.readToEnd(VisualGridEyes.class.getResourceAsStream("/dom-snapshot/dist/pollResult.js"));
            POLL_RESULT_FOR_IE = GeneralUtils.readToEnd(VisualGridEyes.class.getResourceAsStream("/dom-snapshot/dist/pollResultForIE.js"));
        } catch (IOException e) {
            throw new EyesException("Failed getting resources for dom scripts", e);
        }
    }

    /**
     * Sets a handler of log messages generated by this API.
     * @param logHandler Handles log messages generated by this API.
     */
    public void setLogHandler(LogHandler logHandler) {
        if (getIsDisabled()) {
            return;
        }

        this.logger.setLogHandler(logHandler);
        if (!logHandler.isOpen()) {
            logHandler.open();
        }
    }

    public LogHandler getLogHandler() {
        if (getIsDisabled()) return NullLogHandler.instance;
        return this.logger.getLogHandler();
    }

    public void apiKey(String apiKey) {
        setApiKey(apiKey);
    }

    public void serverUrl(String serverUrl) {
        setServerUrl(serverUrl);
    }

    public void setImageProvider(ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
    }

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

    public void setDebugScreenshotsPath(String pathToSave) {
        debugScreenshotsProvider.setPath(pathToSave);
    }

    public void setDebugScreenshotsPrefix(String prefix) {
        debugScreenshotsProvider.setPrefix(prefix);
    }

    @Override
    public WebDriver open(WebDriver driver, String appName, String testName, RectangleSize viewportSize) throws EyesException {
        logger.log(TraceLevel.Info, new HashSet<String>(), Stage.OPEN, Type.CALLED,
                Pair.of("appName", appName),
                Pair.of("testName", testName),
                Pair.of("viewportSize", viewportSize == null ? "default" : viewportSize));
        getConfiguration().setAppName(appName).setTestName(testName);
        if (viewportSize != null && !viewportSize.isEmpty()) {
            getConfiguration().setViewportSize(new RectangleSize(viewportSize));
        }
        return open(driver);
    }

    public WebDriver open(WebDriver webDriver) {
        if (getIsDisabled()) {
            return webDriver;
        }

        runner.setApiKey(getApiKey());
        runner.setServerUrl(getServerUrl().toString());
        runner.setProxy(getProxy());

        ArgumentGuard.notNull(webDriver, "webDriver");
        ArgumentGuard.notNullOrEmpty(getConfiguration().getAppName(), "appIdOrName");
        ArgumentGuard.notNullOrEmpty(getConfiguration().getTestName(), "scenarioIdOrName");
        if (isOpen) {
            return this.webDriver != null ? this.webDriver : webDriver;
        }

        isOpen = true;
        initDriver(webDriver);

        String uaString = this.webDriver.getUserAgent();
        if (uaString != null) {
            userAgent = UserAgent.parseUserAgentString(uaString, true);
        }

        setViewportSize(this.webDriver);

        ensureBrowsers();

        if (getConfiguration().getBatch() == null) {
            getConfiguration().setBatch(new BatchInfo(null));
        }
        List<RenderBrowserInfo> browserInfoList = getConfiguration().getBrowsersInfo();
        getConfiguration().setViewportSize(viewportSize);
        if (getConfiguration().getBrowsersInfo() == null) {
            RectangleSize viewportSize = getConfiguration().getViewportSize();
            getConfiguration().addBrowser(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, getConfiguration().getBaselineEnvName()));
        }

        if (runner.getAgentId() == null ) {
            runner.setAgentId(getFullAgentId());
        }

        runner.setLogger(logger);

        List<VisualGridRunningTest> newTests = new ArrayList<>();
        for (RenderBrowserInfo browserInfo : browserInfoList) {
            VisualGridRunningTest test = new VisualGridRunningTest(getConfiguration(), browserInfo, this.properties, logger, runner.getServerConnector());
            this.testList.put(test.getTestId(), test);
            newTests.add(test);
        }

        try {
            runner.open(this, newTests);
        } catch (Throwable t) {
            for (RunningTest runningTest : testList.values()) {
                runningTest.openFailed(t);
            }
            throw t;
        }
        return this.webDriver != null ? this.webDriver : webDriver;
    }

    private void ensureBrowsers() {
        if (this.getConfiguration().getBrowsersInfo().isEmpty()) {
            getConfiguration().getBrowsersInfo().add(new RenderBrowserInfo(viewportSize, BrowserType.CHROME));
        }
    }

    void setViewportSize(EyesSeleniumDriver webDriver) {
        viewportSize = getConfiguration().getViewportSize();
        if (viewportSize == null) {
            List<RenderBrowserInfo> browserInfoList = getConfiguration().getBrowsersInfo();
            if (browserInfoList != null && !browserInfoList.isEmpty()) {
                for (RenderBrowserInfo deviceInfo : browserInfoList) {
                    if (deviceInfo.getEmulationInfo() != null || deviceInfo.getIosDeviceInfo() != null) {
                        continue;
                    }
                    viewportSize = new RectangleSize(deviceInfo.getWidth(), deviceInfo.getHeight());
                }
            }
        }

        if (viewportSize == null) {
            viewportSize = EyesDriverUtils.getViewportSize(webDriver);
        }

        try {
            EyesDriverUtils.setViewportSize(logger, webDriver, viewportSize);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.OPEN, e);
        }
    }

    private void initDriver(WebDriver webDriver) {
        if (webDriver instanceof RemoteWebDriver) {
            SeleniumEyes seleniumEyes = new SeleniumEyes(configurationProvider, new ClassicRunner());
            this.webDriver = new EyesSeleniumDriver(logger, seleniumEyes, (RemoteWebDriver) webDriver);
        }
        this.url = webDriver.getCurrentUrl();
    }

    public TestResults close(boolean throwException) {
        closeAsync();
        return waitForEyesToFinish(throwException);
    }

    public TestResults abortIfNotClosed() {
        abortAsync();
        return waitForEyesToFinish(false);
    }

    public TestResults abort() {
        return abortIfNotClosed();
    }

    public void closeAsync() {
        if (getIsDisabled()) {
            return;
        }

        logger.log(new HashSet<String>(), Stage.CLOSE, Type.CALLED);
        isOpen = false;
        for (RunningTest runningTest : testList.values()) {
            runningTest.issueClose();
        }
    }

    public void abortAsync() {
        logger.log(new HashSet<String>(), Stage.CLOSE, Type.CALLED);
        for (RunningTest runningTest : testList.values()) {
            runningTest.issueAbort(new EyesException("eyes.close wasn't called. Aborted the test"), false);
        }
    }

    private TestResults waitForEyesToFinish(boolean throwException) {
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

        if (errorResult != null) {
            if (throwException) {
                throw new Error(errorResult.getException());
            }
            return errorResult.getTestResults();
        }

        return firstResult;
    }

    public boolean getIsOpen() {
        return !isEyesClosed();
    }

    public String getApiKey() {
        return getConfiguration().getApiKey() == null ? runner.getApiKey() : getConfiguration().getApiKey();
    }

    public void setApiKey(String apiKey) {
        getConfiguration().setApiKey(apiKey);
    }

    public void setIsDisabled(Boolean disabled) {
        this.isDisabled = disabled;
    }

    public boolean getIsDisabled() {
        return this.isDisabled == null ? this.runner.getIsDisabled() : this.isDisabled;
    }

    public URI getServerUrl() {
        String serverUrl = getConfiguration().getServerUrl() == null ? runner.getServerUrl() : getConfiguration().getServerUrl().toString();
        try {
            return new URI(serverUrl);
        } catch (URISyntaxException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.GENERAL, e);
            return null;
        }
    }

    public void setServerUrl(String serverUrl) {
        getConfiguration().setServerUrl(serverUrl);
    }

    @Override
    public void proxy(AbstractProxySettings abstractProxySettings) {
        getConfiguration().setProxy(abstractProxySettings);
    }

    public AbstractProxySettings getProxy() {
        return getConfiguration().getProxy() == null ? runner.getProxy() : getConfiguration().getProxy();
    }

    @Override
    public boolean isEyesClosed() {
        boolean isVGEyesClosed = true;
        for (RunningTest runningTest : testList.values()) {
            isVGEyesClosed = isVGEyesClosed && runningTest.isCompleted();
        }
        return isVGEyesClosed;
    }

    public void check(ICheckSettings... checkSettings) {
        if (getIsDisabled()) {
            return;
        }
        for (ICheckSettings checkSetting : checkSettings) {
            this.check(checkSetting);
        }
    }

    public void check(String name, ICheckSettings checkSettings) {
        if (getIsDisabled()) {
            return;
        }

        ArgumentGuard.notNull(checkSettings, "checkSettings");
        if (name != null) {
            checkSettings = checkSettings.withName(name);
        }
        this.check(checkSettings);
    }

    public void check(ICheckSettings checkSettings) {
        if (getIsDisabled()) {
            return;
        }

        logger.log(TraceLevel.Info, new HashSet<String>(), Stage.CHECK, Type.CALLED,
                Pair.of("configuration", getConfiguration()),
                Pair.of("checkSettings", checkSettings));
        FrameChain originalFC = webDriver.getFrameChain().clone();
        EyesTargetLocator switchTo = ((EyesTargetLocator) webDriver.switchTo());
        try {
            ArgumentGuard.notOfType(checkSettings, ICheckSettings.class, "checkSettings");
            if (checkSettings instanceof ISeleniumCheckTarget) {
                ((ISeleniumCheckTarget) checkSettings).init(logger, webDriver);
            }

            waitBeforeDomSnapshot();

            checkSettings = switchFramesAsNeeded(checkSettings, switchTo);
            ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;
            SeleniumCheckSettings seleniumCheckSettings = (SeleniumCheckSettings) checkSettings;
            List<VisualGridSelector[]> regionsXPaths = getRegionsXPaths(checkSettingsInternal);

            trySetTargetSelector(seleniumCheckSettings);

            checkSettingsInternal = updateCheckSettings(checkSettings);

            String source = webDriver.getCurrentUrl();
            Map<Integer, List<RunningTest>> requiredWidths = mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
            if (requiredWidths.isEmpty()) {
                captureDomForResourceCollection(0, testList.values(), switchTo, checkSettingsInternal, regionsXPaths, source);
                return;
            }

            for (Map.Entry<Integer, List<RunningTest>> entry : requiredWidths.entrySet()) {
                captureDomForResourceCollection(entry.getKey(), entry.getValue(), switchTo, checkSettingsInternal, regionsXPaths, source);
            }
        } catch (Throwable e) {
            Error error = new Error(e);
            for (RunningTest runningTest : testList.values()) {
                runningTest.setTestInExceptionMode(error);
            }
        } finally {
            switchTo.frames(originalFC);
        }
    }

    private ICheckSettings switchFramesAsNeeded(ICheckSettings checkSettings, EyesTargetLocator switchTo) {
        int switchedToCount = switchToFrame((ISeleniumCheckTarget) checkSettings);
        boolean isFullPage = isFullPage((ICheckSettingsInternal) checkSettings);
        if (switchedToCount > 0 && isFullPage) {
            FrameChain frameChain = webDriver.getFrameChain().clone();
            Frame frame = frameChain.pop();
            checkSettings = ((SeleniumCheckSettings) checkSettings).region(frame.getReference());
            switchTo.parentFrame();
        }
        return checkSettings;
    }

    private boolean isFullPage(ICheckSettingsInternal checkSettingsInternal) {
        boolean isFullPage = true;
        Boolean b;
        if ((b = checkSettingsInternal.isStitchContent()) != null) {
            isFullPage = b;
        } else if ((b = getConfiguration().isForceFullPageScreenshot()) != null) {
            isFullPage = b;
        }
        return isFullPage;
    }

    Map<Integer, List<RunningTest>> mapRunningTestsToRequiredBrowserWidth(SeleniumCheckSettings seleniumCheckSettings) {
        List<Integer> layoutBreakpoint;
        boolean isDefaultLayoutBreakpointsSet;
        if (!seleniumCheckSettings.getLayoutBreakpoints().isEmpty() || seleniumCheckSettings.isDefaultLayoutBreakpointsSet()) {
            layoutBreakpoint = seleniumCheckSettings.getLayoutBreakpoints();
            isDefaultLayoutBreakpointsSet = seleniumCheckSettings.isDefaultLayoutBreakpointsSet();
        } else {
            layoutBreakpoint = getConfiguration().getLayoutBreakpoints();
            isDefaultLayoutBreakpointsSet = getConfiguration().isDefaultLayoutBreakpointsSet();
        }

        Set<String> testIds = new HashSet<>();
        for (RunningTest runningTest : testList.values()) {
            testIds.add(runningTest.getTestId());
        }

        Map<Integer, List<RunningTest>> requiredWidths = new HashMap<>();
        if (isDefaultLayoutBreakpointsSet || !layoutBreakpoint.isEmpty()) {
            for (RunningTest runningTest : testList.values()) {
                int width = runningTest.getBrowserInfo().getDeviceSize().getWidth();
                if (width <= 0) {
                    width = viewportSize.getWidth();
                }

                if (!layoutBreakpoint.isEmpty()) {
                    for (int i = layoutBreakpoint.size() - 1; i >= 0; i--) {
                        if (width >= layoutBreakpoint.get(i)) {
                            width = layoutBreakpoint.get(i);
                            break;
                        }
                    }

                    if (width < layoutBreakpoint.get(0)) {
                        width = layoutBreakpoint.get(0) - 1;
                        logger.log(TraceLevel.Warn, testIds, Stage.CHECK, Type.DOM_SCRIPT, Pair.of("message",String.format("Device width is smaller than the smallest breakpoint %d", layoutBreakpoint.get(0))));
                    }
                }

                if (requiredWidths.containsKey(width)) {
                    requiredWidths.get(width).add(runningTest);
                } else {
                    List<RunningTest> list = new ArrayList<>();
                    list.add(runningTest);
                    requiredWidths.put(width, list);
                }
            }
        }

        return requiredWidths;
    }

    private void captureDomForResourceCollection(int width, Collection<RunningTest> tests, EyesTargetLocator switchTo,
                                                 ICheckSettingsInternal checkSettingsInternal,
                                                 List<VisualGridSelector[]> regionsXPaths,String source) throws Exception {
        Set<String> testIds = new HashSet<>();
        for (RunningTest runningTest : tests) {
            testIds.add(runningTest.getTestId());
        }
        if (width != 0) {
            try {
                EyesDriverUtils.setViewportSize(logger, webDriver, new RectangleSize(width, viewportSize.getHeight()));
                Thread.sleep(300);
            } catch (Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, t, testIds.toArray(new String[0]));
            }
        }

        if (width != 0) {
            RectangleSize viewportSize = EyesDriverUtils.getViewportSize(webDriver);
            logger.log(TraceLevel.Info, testIds, Stage.CHECK, Type.DOM_SCRIPT,
                    Pair.of("requiredWidth", width),
                    Pair.of("viewportSize", viewportSize));
            BufferedImage bufferedImage = imageProvider.getImage();
            debugScreenshotsProvider.save(bufferedImage, String.format("snapshot_%s", viewportSize.toString()));
        }

        FrameData scriptResult = captureDomSnapshot(testIds, switchTo);
        String[] blobsUrls = new String[scriptResult.getBlobs().size()];
        for (int i = 0; i< scriptResult.getBlobs().size(); i++) {
            blobsUrls[i] = scriptResult.getBlobs().get(i).getUrl();
        }

        logger.log(TraceLevel.Info, testIds, Stage.CHECK, Type.DOM_SCRIPT,
                Pair.of("cdtSize", scriptResult.getCdt().size()),
                Pair.of("blobs", blobsUrls),
                Pair.of("resourceUrls", scriptResult.getResourceUrls()));

        List<CheckTask> checkTasks = new ArrayList<>();
        for (RunningTest runningTest : tests) {
            if (runningTest.isCloseTaskIssued()) {
                continue;
            }

            checkTasks.add(runningTest.issueCheck((ICheckSettings) checkSettingsInternal, regionsXPaths, source));
        }

        scriptResult.setUserAgent(userAgent);
        this.runner.setDebugResourceWriter(getConfiguration().getDebugResourceWriter());
        this.runner.check(scriptResult, checkTasks);
    }

    FrameData captureDomSnapshot(Set<String> testIds, EyesTargetLocator switchTo) throws Exception {
        String domScript = userAgent.isInternetExplorer() ? PROCESS_PAGE_FOR_IE : PROCESS_PAGE;
        String pollingScript = userAgent.isInternetExplorer() ? POLL_RESULT_FOR_IE : POLL_RESULT;

        final String skipList;
        synchronized (runner.getResourcesCacheMap()) {
            skipList = new ObjectMapper().writeValueAsString(new HashSet<>(runner.getResourcesCacheMap().keySet()));
        }

        Map<String, Object> arguments = new HashMap<String, Object>() {{
            put("serializeResources", true);
            put("dontFetchResources", getConfiguration().isDisableBrowserFetching());
            //put("skipResources", skipList);
        }};

        String result = EyesSeleniumUtils.runDomScript(logger, webDriver, userAgent, testIds, domScript, arguments, pollingScript);
        FrameData frameData = GeneralUtils.parseJsonToObject(result, FrameData.class);
        analyzeFrameData(testIds, frameData, switchTo);
        return frameData;
    }

    private void analyzeFrameData(Set<String> testIds, FrameData frameData, EyesTargetLocator switchTo) {
        FrameChain frameChain = webDriver.getFrameChain().clone();
        for (FrameData.CrossFrame crossFrame : frameData.getCrossFrames()) {
            if (crossFrame.getSelector() == null) {
                continue;
            }

            try {
                WebElement frame = webDriver.findElement(By.cssSelector(crossFrame.getSelector()));
                switchTo.frame(frame);
                FrameData result = captureDomSnapshot(testIds, switchTo);
                frameData.addFrame(result);
                frameData.getCdt().get(crossFrame.getIndex()).attributes.add(new AttributeData("data-applitools-src", result.getUrl()));
            } catch (Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, t, testIds.toArray(new String[0]));
            } finally {
                switchTo.frames(frameChain);
            }
        }

        for (FrameData frame : frameData.getFrames()) {
            if (frame.getSelector() == null) {
                continue;
            }

            try {
                WebElement frameElement = webDriver.findElement(By.cssSelector(frame.getSelector()));
                switchTo.frame(frameElement);
                analyzeFrameData(testIds, frame, switchTo);
            } catch (Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, t, testIds.toArray(new String[0]));
            } finally {
                switchTo.frames(frameChain);
            }
        }
    }

    private void waitBeforeDomSnapshot() {
        int waitBeforeScreenshots = this.getConfiguration().getWaitBeforeScreenshots();
        try {
            Thread.sleep(waitBeforeScreenshots);
        } catch (InterruptedException ignored) {}
    }

    ICheckSettingsInternal updateCheckSettings(ICheckSettings checkSettings) {
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;

        MatchLevel matchLevel = checkSettingsInternal.getMatchLevel();

        Boolean fully = checkSettingsInternal.isStitchContent();
        Boolean sendDom = checkSettingsInternal.isSendDom();
        Boolean ignoreDisplacements = checkSettingsInternal.isIgnoreDisplacements();

        if (matchLevel == null) {
            checkSettings = checkSettings.matchLevel(getConfiguration().getMatchLevel());
        }

        if (fully == null) {
            Boolean isForceFullPageScreenshot = getConfiguration().isForceFullPageScreenshot();
            boolean stitchContent = isForceFullPageScreenshot == null ? checkSettings.isCheckWindow() : isForceFullPageScreenshot;
            checkSettings = checkSettings.fully(stitchContent);
        }

        if (sendDom == null) {
            Boolean isSendDom = getConfiguration().isSendDom();
            checkSettings = checkSettings.sendDom(isSendDom == null || isSendDom);
        }

        if (ignoreDisplacements == null) {
            checkSettings = checkSettings.ignoreDisplacements(getConfiguration().getIgnoreDisplacements());
        }

        List<VisualGridOption> options = new ArrayList<>();
        options.addAll(getConfiguration().getVisualGridOptions());
        options.addAll(checkSettingsInternal.getVisualGridOptions());
        checkSettings = checkSettings.visualGridOptions(options.size() > 0 ? options.toArray(new VisualGridOption[]{}) : null);
        return (ICheckSettingsInternal) checkSettings;
    }

    private void trySetTargetSelector(SeleniumCheckSettings checkSettings) {
        WebElement element = checkSettings.getTargetElement();
        FrameChain frameChain = webDriver.getFrameChain().clone();
        EyesTargetLocator switchTo = (EyesTargetLocator) webDriver.switchTo();
        switchToFrame(checkSettings);
        if (element == null) {
            By targetSelector = checkSettings.getTargetSelector();
            if (targetSelector != null) {
                element = webDriver.findElement(targetSelector);
            }
        }
        if (element != null) {
            String xpath = (String) webDriver.executeScript(GET_ELEMENT_XPATH_JS, element);
            VisualGridSelector vgs = new VisualGridSelector(xpath, "target");
            checkSettings.setTargetSelector(vgs);
        }
        switchTo.frames(frameChain);
    }

    @SuppressWarnings("UnusedReturnValue")
    private int switchToFrame(ISeleniumCheckTarget checkTarget) {
        if (checkTarget == null) {
            return 0;
        }

        List<FrameLocator> frameChain = checkTarget.getFrameChain();
        int switchedToFrameCount = 0;
        for (FrameLocator frameLocator : frameChain) {
            if (switchToFrame(frameLocator)) {
                switchedToFrameCount++;
            }
        }
        return switchedToFrameCount;
    }

    private boolean switchToFrame(ISeleniumFrameCheckTarget frameTarget) {
        WebDriver.TargetLocator switchTo = this.webDriver.switchTo();

        if (frameTarget.getFrameIndex() != null) {
            switchTo.frame(frameTarget.getFrameIndex());
            updateFrameScrollRoot(frameTarget);
            return true;
        }

        if (frameTarget.getFrameNameOrId() != null) {
            switchTo.frame(frameTarget.getFrameNameOrId());
            updateFrameScrollRoot(frameTarget);
            return true;
        }

        if (frameTarget.getFrameReference() != null) {
            switchTo.frame(frameTarget.getFrameReference());
            updateFrameScrollRoot(frameTarget);
            return true;
        }

        if (frameTarget.getFrameSelector() != null) {
            WebElement frameElement = this.webDriver.findElement(frameTarget.getFrameSelector());
            if (frameElement != null) {
                switchTo.frame(frameElement);
                updateFrameScrollRoot(frameTarget);
                return true;
            }
        }

        return false;
    }

    private void updateFrameScrollRoot(IScrollRootElementContainer frameTarget) {
        WebElement rootElement = EyesSeleniumUtils.getScrollRootElement(logger, webDriver, frameTarget);
        Frame frame = webDriver.getFrameChain().peek();
        frame.setScrollRootElement(rootElement);
    }

    public Logger getLogger() {
        return logger;
    }

    public Map<String, RunningTest> getAllRunningTests() {
        return testList;
    }

    @Override
    public String toString() {
        return "SeleniumVGEyes - url: " + url;
    }

    public void setServerConnector(ServerConnector serverConnector) {
        if (serverConnector != null && serverConnector.getAgentId() == null) {
            serverConnector.setAgentId(getFullAgentId());
        }
        runner.setServerConnector(serverConnector);
    }

    /**
     * @return The full agent id composed of both the base agent id and the
     * user given agent id.
     */
    public String getFullAgentId() {
        String agentId = getConfiguration().getAgentId();
        if (agentId == null) {
            return getBaseAgentId();
        }
        return String.format("%s [%s]", agentId, getBaseAgentId());
    }

    private Configuration getConfiguration() {
        return configurationProvider.get();
    }

    private String getBaseAgentId() {
        return "eyes.selenium.visualgrid.java/" + ClassVersionGetter.CURRENT_VERSION;
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     * @param batch The batch info to set.
     */
    public void setBatch(BatchInfo batch) {
        if (getIsDisabled()) {
            return;
        }
        this.getConfiguration().setBatch(batch);
    }

    private List<VisualGridSelector[]> getRegionsXPaths(ICheckSettingsInternal csInternal) {
        List<VisualGridSelector[]> result = new ArrayList<>();
        List<WebElementRegion>[] elementLists = collectSeleniumRegions(csInternal);
        for (List<WebElementRegion> elementList : elementLists) {
            List<VisualGridSelector> xPaths = new ArrayList<>();
            for (WebElementRegion webElementRegion : elementList) {
                if (webElementRegion.getElement() == null) continue;
                String xpath = (String) webDriver.executeScript(GET_ELEMENT_XPATH_JS, webElementRegion.getElement());
                xPaths.add(new VisualGridSelector(xpath, webElementRegion.getRegion()));
            }
            result.add(xPaths.toArray(new VisualGridSelector[0]));
        }
        return result;
    }

    private List<WebElementRegion>[] collectSeleniumRegions(ICheckSettingsInternal csInternal) {
        CheckSettings settings = (CheckSettings) csInternal;
        GetSimpleRegion[] ignoreRegions = settings.getIgnoreRegions();
        GetSimpleRegion[] layoutRegions = settings.getLayoutRegions();
        GetSimpleRegion[] strictRegions = settings.getStrictRegions();
        GetSimpleRegion[] contentRegions = settings.getContentRegions();
        GetFloatingRegion[] floatingRegions = settings.getFloatingRegions();
        GetAccessibilityRegion[] accessibilityRegions = settings.getAccessibilityRegions();

        List<WebElementRegion> ignoreElements = getElementsFromRegions(Arrays.asList(ignoreRegions));
        List<WebElementRegion> layoutElements = getElementsFromRegions(Arrays.asList(layoutRegions));
        List<WebElementRegion> strictElements = getElementsFromRegions(Arrays.asList(strictRegions));
        List<WebElementRegion> contentElements = getElementsFromRegions(Arrays.asList(contentRegions));
        List<WebElementRegion> floatingElements = getElementsFromRegions(Arrays.asList(floatingRegions));
        List<WebElementRegion> accessibilityElements = getElementsFromRegions(Arrays.asList(accessibilityRegions));
        return (List<WebElementRegion>[]) new List[]{ignoreElements, layoutElements, strictElements, contentElements, floatingElements, accessibilityElements};
    }


    private List<WebElementRegion> getElementsFromRegions(List regionsProvider) {
        List<WebElementRegion> elements = new ArrayList<>();
        for (Object getRegion : regionsProvider) {
            if (getRegion instanceof IGetSeleniumRegion) {
                IGetSeleniumRegion getSeleniumRegion = (IGetSeleniumRegion) getRegion;
                List<WebElement> webElements = getSeleniumRegion.getElements();
                for (WebElement webElement : webElements) {
                    elements.add(new WebElementRegion(webElement, getRegion));
                }
            }
        }
        return elements;
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

    public EyesSeleniumDriver getDriver() {
        return webDriver;
    }

    @Override
    public IBatchCloser getBatchCloser() {
        return this.testList.values().iterator().next();
    }

    @Override
    public String getBatchId() {
        return this.getConfiguration().getBatch().getId();
    }

    /**
     *
     * @return If all tests completed, returns their results. Otherwise, returns null
     */
    public List<TestResultContainer> getAllTestResults() {
        List<TestResultContainer> allResults = new ArrayList<>();
        for (RunningTest runningTest : testList.values()) {
            if (!runningTest.isCompleted()) {
                return null;
            }

            allResults.add(runningTest.getTestResultContainer());
        }

        return allResults;
    }

    public boolean isCompleted() {
        return getAllTestResults() != null;
    }
}
