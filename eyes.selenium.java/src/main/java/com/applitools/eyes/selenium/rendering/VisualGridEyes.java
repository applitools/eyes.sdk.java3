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
import com.applitools.eyes.dom.DomScriptUtils;
import com.applitools.eyes.dom.ScriptExecutor;
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
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.CheckTask;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.services.VisualGridRunningTest;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.ClassVersionGetter;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class VisualGridEyes implements ISeleniumEyes {
    private final Logger logger;
    private final String eyesId = UUID.randomUUID().toString();

    private final VisualGridRunner runner;
    final Map<String, RunningTest> testList = Collections.synchronizedMap(new HashMap<String, RunningTest>());
    private boolean isOpen = false;

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
        logger.log(TraceLevel.Info, Collections.singleton(eyesId), Stage.OPEN, Type.CALLED,
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
        ServerConnector serverConnector = runner.getServerConnector();
        String agentRunId = String.format("%s_%s", getConfiguration().getTestName(), UUID.randomUUID().toString());
        for (RenderBrowserInfo browserInfo : browserInfoList) {
            if (browserInfo.getEmulationInfo() != null) {
                Map<String, DeviceSize> deviceSizes = serverConnector.getDevicesSizes(ServerConnector.EMULATED_DEVICES_PATH);
                browserInfo.setEmulationDeviceSize(deviceSizes.get(browserInfo.getEmulationInfo().getDeviceName()));
            }
            if (browserInfo.getIosDeviceInfo() != null) {
                Map<String, DeviceSize> deviceSizes = serverConnector.getDevicesSizes(ServerConnector.IOS_DEVICES_PATH);
                browserInfo.setIosDeviceSize(deviceSizes.get(browserInfo.getIosDeviceInfo().getDeviceName()));
            }
            VisualGridRunningTest test = new VisualGridRunningTest(logger, eyesId, getConfiguration(), browserInfo, this.properties, serverConnector, agentRunId);
            this.testList.put(test.getTestId(), test);
            newTests.add(test);
        }

        try {
            runner.open(this, newTests);
        } catch (Throwable t) {
            synchronized (testList) {
                for (RunningTest runningTest : testList.values()) {
                    runningTest.openFailed(t);
                }
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

        logger.log(eyesId, Stage.CLOSE, Type.CALLED);
        isOpen = false;
        synchronized (testList) {
            for (RunningTest runningTest : testList.values()) {
                runningTest.issueClose();
            }
        }
    }

    public void abortAsync() {
        logger.log(eyesId, Stage.CLOSE, Type.CALLED);
        synchronized (testList) {
            for (RunningTest runningTest : testList.values()) {
                runningTest.issueAbort(new EyesException(String.format("Didn't close test %s. Aborted the test", getConfiguration().getTestName())), false);
            }
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
        synchronized (testList) {
            for (RunningTest runningTest : testList.values()) {
                isVGEyesClosed = isVGEyesClosed && runningTest.isCompleted();
            }
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

        String source = webDriver.getCurrentUrl();
        logger.log(TraceLevel.Info, Collections.singleton(eyesId), Stage.CHECK, Type.CALLED,
                Pair.of("configuration", getConfiguration()),
                Pair.of("checkSettings", checkSettings),
                Pair.of("source", source));
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
            Map<Integer, List<RunningTest>> requiredWidths = mapRunningTestsToRequiredBrowserWidth(seleniumCheckSettings);
            if (requiredWidths.isEmpty()) {
                captureDomForResourceCollection(0, testList.values(), switchTo, checkSettingsInternal, regionsXPaths, source);
                return;
            }

            for (Map.Entry<Integer, List<RunningTest>> entry : requiredWidths.entrySet()) {
                captureDomForResourceCollection(entry.getKey(), entry.getValue(), switchTo, checkSettingsInternal, regionsXPaths, source);
            }


            Set<String> testIds = new HashSet<>();
            synchronized (testList) {
                for (RunningTest runningTest : testList.values()) {
                    testIds.add(runningTest.getTestId());
                }
            }

            try {
                EyesDriverUtils.setViewportSize(logger, webDriver, viewportSize);
                Thread.sleep(300);
            } catch (Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, t, testIds.toArray(new String[0]));
            }
        } catch (Throwable e) {
            Error error = new Error(e);
            synchronized (testList) {
                for (RunningTest runningTest : testList.values()) {
                    runningTest.setTestInExceptionMode(error);
                }
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
        synchronized (testList) {
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
                            logger.log(TraceLevel.Warn, testIds, Stage.CHECK, Type.DOM_SCRIPT,
                                    Pair.of("message",String.format("Device width is smaller than the smallest breakpoint %d", layoutBreakpoint.get(0))));
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
    }

    private void captureDomForResourceCollection(int width, Collection<RunningTest> tests, EyesTargetLocator switchTo,
                                                 ICheckSettingsInternal checkSettingsInternal,
                                                 List<VisualGridSelector[]> regionsXPaths,String source) throws Exception {
        Set<String> testIds = new HashSet<>();
        synchronized (testList) {
            for (RunningTest runningTest : tests) {
                testIds.add(runningTest.getTestId());
            }
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
            debugScreenshotsProvider.save(bufferedImage, String.format("snapshot_%s", viewportSize));
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
        synchronized (testList) {
            for (RunningTest runningTest : tests) {
                if (runningTest.isCloseTaskIssued()) {
                    continue;
                }

                checkTasks.add(runningTest.issueCheck((ICheckSettings) checkSettingsInternal, regionsXPaths, source));
            }
        }

        if (checkTasks.isEmpty()) {
            logger.log(TraceLevel.Warn, testIds, Stage.CHECK, null, Pair.of("message", "No check tasks created. Tests were probably aborted"));
            return;
        }
        scriptResult.setUserAgent(userAgent);
        this.runner.setDebugResourceWriter(getConfiguration().getDebugResourceWriter());
        this.runner.check(scriptResult, checkTasks);
    }

    FrameData captureDomSnapshot(Set<String> testIds, EyesTargetLocator switchTo) throws Exception {
        ScriptExecutor executor = new ScriptExecutor() {
            @Override
            public Object execute(String script) {
                return webDriver.executeScript(script);
            }
        };

        FrameData frameData = DomScriptUtils.runDomSnapshot(logger, executor, testIds, userAgent, true, getConfiguration().isDisableBrowserFetching());
        analyzeFrameData(testIds, frameData, switchTo);
        return frameData;
    }

    private void analyzeFrameData(Set<String> testIds, FrameData frameData, EyesTargetLocator switchTo) {
        FrameChain frameChain = webDriver.getFrameChain().clone();

        Set<com.applitools.connectivity.Cookie> sdkCookies = new HashSet<>();
        if (getConfiguration().isUseCookies()) {
            Set<Cookie> cookies = webDriver.manage().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    com.applitools.connectivity.Cookie sdkCookie = new com.applitools.connectivity.Cookie(
                            cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.isSecure(), cookie.isHttpOnly(), cookie.getExpiry());
                    sdkCookies.add(sdkCookie);
                }
            }

        }

        frameData.setCookies(sdkCookies);
        for (FrameData.CrossFrame crossFrame : frameData.getCrossFrames()) {
            if (crossFrame.getSelector() == null) {
                continue;
            }

            try {
                WebElement frame = webDriver.findElement(By.cssSelector(crossFrame.getSelector()));
                if (!switchToFrame(testIds, frame, switchTo)) {
                    continue;
                }

                FrameData result = captureDomSnapshot(testIds, switchTo);
                try {
                    String url = GeneralUtils.sanitizeURL(result.getUrl());
                    URIBuilder builder = new URIBuilder(url);
                    builder.addParameter("applitools-iframe", UUID.randomUUID().toString());
                    result.setUrl(builder.toString());
                } catch (Throwable t) {
                    GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, t, testIds.toArray(new String[0]));
                }

                frameData.addFrame(result);
                List<AttributeData> attributeData = (List<AttributeData>) frameData.getCdt().get(crossFrame.getIndex()).get("attributes");
                attributeData.add(new AttributeData("data-applitools-src", result.getUrl()));
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
                if (switchToFrame(testIds, frameElement, switchTo)) {
                    analyzeFrameData(testIds, frame, switchTo);
                }
            } catch (Throwable t) {
                GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, t, testIds.toArray(new String[0]));
            } finally {
                switchTo.frames(frameChain);
            }
        }
    }

    private boolean switchToFrame(Set<String> testIds, WebElement frame, EyesTargetLocator switchTo) {
        EyesRemoteWebElement htmlBeforeSwitch = (EyesRemoteWebElement) webDriver.findElement(By.tagName("html"));
        String idBeforeSwitch = htmlBeforeSwitch.getId();
        switchTo.frame(frame);
        EyesRemoteWebElement htmlAfterSwitch = (EyesRemoteWebElement) webDriver.findElement(By.tagName("html"));
        String idAfterSwitch = htmlAfterSwitch.getId();
        if (idBeforeSwitch.equals(idAfterSwitch)) {
            logger.log(TraceLevel.Warn, testIds, Stage.CHECK, Type.DOM_SCRIPT, "Failed switching to cross origin iframe. Ignoring");
            webDriver.getFrameChain().pop();
            return false;
        }
        return true;
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
        synchronized (testList) {
            for (RunningTest runningTest : testList.values()) {
                if (!runningTest.isCompleted()) {
                    if (runner.getError() != null) {
                        throw new EyesException("Execution crashed", runner.getError());
                    }
                    return null;
                }

                allResults.add(runningTest.getTestResultContainer());
            }
        }

        return allResults;
    }

    @Override
    public boolean isCompleted() {
        return getAllTestResults() != null;
    }
}
