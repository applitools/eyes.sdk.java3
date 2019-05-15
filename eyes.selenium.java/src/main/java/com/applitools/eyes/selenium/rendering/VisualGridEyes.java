package com.applitools.eyes.selenium.rendering;

import com.applitools.ICheckSettings;
import com.applitools.ICheckSettingsInternal;
import com.applitools.eyes.*;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.fluent.GetRegion;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.fluent.*;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class VisualGridEyes implements IRenderingEyes {

    private static long DOM_EXTRACTION_TIMEOUT = 5 * 60 * 1000;
    private Logger logger;

    private String apiKey;
    private String serverUrl;

    private final VisualGridRunner renderingGridRunner;
    private final List<RunningTest> testList = Collections.synchronizedList(new ArrayList<RunningTest>());
    private final List<RunningTest> testsInCloseProcess = Collections.synchronizedList(new ArrayList<RunningTest>());
    private AtomicBoolean isVGEyesIssuedOpenTasks = new AtomicBoolean(false);
    private IRenderingEyes.EyesListener listener;

    private String PROCESS_RESOURCES;
    private EyesWebDriver webDriver;
    private RenderingInfo renderingInfo;
    private IEyesConnector VGEyesConnector;
    private IDebugResourceWriter debugResourceWriter;
    private String url;
    private Set<Future<TestResultContainer>> closeFuturesSet = new HashSet<>();
    private Boolean isDisabled;
    private IServerConnector serverConnector = null;
    private ISeleniumConfigurationProvider configProvider;
    private RectangleSize viewportSize;
    private AtomicBoolean isCheckTimerTimedout = new AtomicBoolean(false);
    private Timer timer = new Timer("VG_StopWatch", true);
    private final List<PropertyData> properties = new ArrayList<>();

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


    {
        try {
            PROCESS_RESOURCES = GeneralUtils.readToEnd(VisualGridEyes.class.getResourceAsStream("/processPageAndPoll.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VisualGridEyes(VisualGridRunner renderingGridManager, ISeleniumConfigurationProvider configProvider) {
        this.configProvider = configProvider;
        ArgumentGuard.notNull(renderingGridManager, "renderingGridRunner");
        this.renderingGridRunner = renderingGridManager;
        this.logger = renderingGridManager.getLogger();
    }

    private RunningTest.RunningTestListener testListener = new RunningTest.RunningTestListener() {
        @Override
        public void onTaskComplete(VisualGridTask task, RunningTest test) {
            switch (task.getType()) {
                case CLOSE:
                case ABORT:
                    VisualGridEyes.this.isVGEyesIssuedOpenTasks.set(false);
            }
            if (VisualGridEyes.this.listener != null) {
                VisualGridEyes.this.listener.onTaskComplete(task, VisualGridEyes.this);
            }
        }

        @Override
        public void onRenderComplete() {
            VisualGridEyes.this.listener.onRenderComplete();
        }

    };


    /**
     * Sets a handler of log messages generated by this API.
     *
     * @param logHandler Handles log messages generated by this API.
     */
    public void setLogHandler(LogHandler logHandler) {
        if (getIsDisabled()) return;
        LogHandler currentLogHandler = logger.getLogHandler();
        this.logger = new Logger();
        this.logger.setLogHandler(new MultiLogHandler(currentLogHandler, logHandler));

        if (currentLogHandler.isOpen() && !logHandler.isOpen()) {
            logHandler.open();
        }
    }

    public WebDriver open(WebDriver webDriver) {
        logger.verbose("enter");

        if (getIsDisabled()) return webDriver;

        ArgumentGuard.notNull(webDriver, "webDriver");
        ArgumentGuard.notNull(getConfigGetter().getTestName(), "testName");
        ArgumentGuard.notNull(getConfigGetter().getAppName(), "appName");

        initDriver(webDriver);

        setViewportSize(this.webDriver);

        ensureBrowsers();

        logger.verbose("getting all browsers info...");
        List<RenderBrowserInfo> browserInfoList = getConfigGetter().getBrowsersInfo();
        logger.verbose("creating test descriptors for each browser info...");
        IConfigurationSetter configurationSetter = configProvider.set();
        configurationSetter.setViewportSize(viewportSize);
        for (RenderBrowserInfo browserInfo : browserInfoList) {
            logger.verbose("creating test descriptor");
            RunningTest test = new RunningTest(createVGEyesConnector(browserInfo), configProvider, browserInfo, logger, testListener);
            this.testList.add(test);
        }

        logger.verbose(String.format("opening %d tests...", testList.size()));
        this.renderingGridRunner.open(this, renderingInfo);
        logger.verbose("done");
        return webDriver;
    }

    private void ensureBrowsers() {
        if (this.configProvider.get().getBrowsersInfo().isEmpty()) {
            this.configProvider.get().getBrowsersInfo().add(new RenderBrowserInfo(viewportSize, BrowserType.CHROME));
        }
    }

    private void setViewportSize(EyesWebDriver webDriver) {
        viewportSize = configProvider.get().getViewportSize();

        if (viewportSize == null) {
            List<RenderBrowserInfo> browserInfoList = getConfigGetter().getBrowsersInfo();
            if (browserInfoList != null && !browserInfoList.isEmpty()) {
                for (RenderBrowserInfo renderBrowserInfo : browserInfoList) {
                    if (renderBrowserInfo.getEmulationInfo() != null) continue;
                    viewportSize = new RectangleSize(renderBrowserInfo.getWidth(), renderBrowserInfo.getHeight());
                }
            }
        }

        if (viewportSize == null) {
            viewportSize = EyesSeleniumUtils.getViewportSize(webDriver);

        }

        try {

            EyesSeleniumUtils.setViewportSize(logger, webDriver, viewportSize);

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    private IEyesConnector createVGEyesConnector(RenderBrowserInfo browserInfo) {
        logger.verbose("creating VisualGridEyes server connector");
        EyesConnector VGEyesConnector = new EyesConnector(this.configProvider, this.properties, browserInfo);
        if (browserInfo.getEmulationInfo() != null) {
            VGEyesConnector.setDevice(browserInfo.getEmulationInfo().getDeviceName());
        }
        VGEyesConnector.setLogHandler(this.logger.getLogHandler());
        VGEyesConnector.setProxy(this.getConfigGetter().getProxy());
        if (serverConnector != null) {
            VGEyesConnector.setServerConnector(serverConnector);
        }

        URI serverUri = this.getServerUrl();
        if (serverUri != null) {
            VGEyesConnector.setServerUrl(serverUri.toString());
        }

        String apiKey = this.getApiKey();
        if (apiKey != null) {
            VGEyesConnector.setApiKey(apiKey);
        } else {
            throw new EyesException("Missing API key");
        }

        if (this.renderingInfo == null) {
            logger.verbose("initializing rendering info...");
            this.renderingInfo = VGEyesConnector.getRenderingInfo();
        }
        VGEyesConnector.setRenderInfo(this.renderingInfo);

        this.VGEyesConnector = VGEyesConnector;
        return VGEyesConnector;
    }

    private void initDriver(WebDriver webDriver) {
        if (webDriver instanceof RemoteWebDriver) {
            this.webDriver = new EyesWebDriver(logger, null, (RemoteWebDriver) webDriver);
        }
        @SuppressWarnings("UnnecessaryLocalVariable") String currentUrl = webDriver.getCurrentUrl();
        this.url = currentUrl;
    }

    public RunningTest getNextTestToClose() {
        synchronized (testsInCloseProcess) {
            synchronized (testList) {
                for (RunningTest runningTest : testList) {
                    if (!runningTest.isTestClose() && runningTest.isTestReadyToClose() &&
                            !this.testsInCloseProcess.contains(runningTest)) {
                        return runningTest;
                    }
                }
            }
        }
        return null;
    }

    public Collection<Future<TestResultContainer>> close(boolean throwException) {
        if (getIsDisabled()) return null;
        return closeAndReturnResults(throwException);
    }

    public Collection<Future<TestResultContainer>> close() {
        if (getIsDisabled()) return null;
        return closeAndReturnResults(true);
    }

    public void abortIfNotClosed() {
    }

    public boolean getIsOpen() {
        return !isEyesClosed();
    }

    public String getApiKey() {
        return this.apiKey == null ? this.renderingGridRunner.getApiKey() : this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setIsDisabled(Boolean disabled) {
        this.isDisabled = disabled;
    }

    public boolean getIsDisabled() {
        return this.isDisabled == null ? this.renderingGridRunner.getIsDisabled() : this.isDisabled;
    }

    public URI getServerUrl() {
        if (this.VGEyesConnector != null) {
            URI uri = this.VGEyesConnector.getServerUrl();
            if (uri != null) return uri;
        }
        String str = this.serverUrl == null ? this.renderingGridRunner.getServerUrl() : this.serverUrl;
        return str == null ? null : URI.create(str);
    }

    private Collection<Future<TestResultContainer>> closeAndReturnResults(boolean throwException) {
        if (getIsDisabled()) return new HashSet<>();
        if (this.closeFuturesSet == null) {
            closeFuturesSet = new HashSet<>();
        }
        Throwable exception = null;
        logger.verbose("enter " + getConfigGetter().getBatch());
        try {
            Collection<Future<TestResultContainer>> futureList = closeAsync();
            this.renderingGridRunner.close(this);
            for (Future<TestResultContainer> future : futureList) {
                TestResultContainer testResultContainer = null;
                try {
                    testResultContainer = future.get();
                    if (exception == null && testResultContainer.getException() != null) {
                        exception = testResultContainer.getException();
                    }
                } catch (Throwable e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    if (exception == null) {
                        exception = e;
                    }
                }


            }
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        if (throwException) {
            throw new Error(exception);
        }
        return closeFuturesSet;
    }

    public Collection<Future<TestResultContainer>> closeAsync() {
        List<Future<TestResultContainer>> futureList = null;
        try {
            futureList = new ArrayList<>();
            for (RunningTest runningTest : testList) {
                logger.verbose("running test name: " + getConfigGetter().getTestName());
                logger.verbose("is current running test open: " + runningTest.isTestOpen());
                logger.verbose("is current running test ready to close: " + runningTest.isTestReadyToClose());
                logger.verbose("is current running test closed: " + runningTest.isTestClose());
                logger.verbose("closing current running test");
                FutureTask<TestResultContainer> closeFuture = runningTest.close();
                futureList.addAll(Collections.singleton(closeFuture));
                logger.verbose("adding closeFuture to futureList");
            }
            closeFuturesSet.addAll(futureList);
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return futureList;
    }

    @Override
    public synchronized ScoreTask getBestScoreTaskForCheck() {

        int bestScore = -1;

        ScoreTask currentBest = null;
        for (RunningTest runningTest : testList) {

            List<VisualGridTask> visualGridTaskList = runningTest.getVisualGridTaskList();

            VisualGridTask visualGridTask;
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (visualGridTaskList) {
                if (visualGridTaskList.isEmpty()) continue;

                visualGridTask = visualGridTaskList.get(0);
                if (!runningTest.isTestOpen() || visualGridTask.getType() != VisualGridTask.TaskType.CHECK || !visualGridTask.isTaskReadyToCheck())
                    continue;
            }


            ScoreTask scoreTask = runningTest.getScoreTaskObjectByType(VisualGridTask.TaskType.CHECK);

            if (scoreTask == null) continue;

            if (bestScore < scoreTask.getScore()) {
                currentBest = scoreTask;
                bestScore = scoreTask.getScore();
            }
        }
        return currentBest;
    }

    @Override
    public ScoreTask getBestScoreTaskForOpen() {
        int bestMark = -1;
        ScoreTask currentBest = null;
        synchronized (testList) {
            for (RunningTest runningTest : testList) {

                ScoreTask currentScoreTask = runningTest.getScoreTaskObjectByType(VisualGridTask.TaskType.OPEN);
                if (currentScoreTask == null) continue;

                if (bestMark < currentScoreTask.getScore()) {
                    bestMark = currentScoreTask.getScore();
                    currentBest = currentScoreTask;

                }
            }
        }
        return currentBest;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public boolean isEyesClosed() {
        boolean isVGEyesClosed = true;
        for (RunningTest runningTest : testList) {
            isVGEyesClosed = isVGEyesClosed && runningTest.isTestClose();
        }
        return isVGEyesClosed;
    }

    public void setListener(EyesListener listener) {
        this.listener = listener;
    }


    public void check(ICheckSettings... checkSettings) {
        if (getIsDisabled()) {
            logger.log(String.format("check(ICheckSettings[%d]): Ignored", checkSettings.length));
            return;
        }
        for (ICheckSettings checkSetting : checkSettings) {
            this.check(checkSetting);
        }
    }

    public void check(String name, ICheckSettings checkSettings) {
        if (getIsDisabled()) return;
        ArgumentGuard.notNull(checkSettings, "checkSettings");
        trySetTargetSelector((SeleniumCheckSettings) checkSettings);
        if (name != null) {
            checkSettings = checkSettings.withName(name);
        }
        this.check(checkSettings);
    }

    public void check(ICheckSettings checkSettings) {
        logger.verbose("enter");

        if (getIsDisabled()) return;

        ArgumentGuard.notOfType(checkSettings, ICheckSettings.class, "checkSettings");

        try {
            isCheckTimerTimedout.set(false);

            List<VisualGridTask> openVisualGridTasks = addOpenTaskToAllRunningTest();

            List<VisualGridTask> visualGridTaskList = new ArrayList<>();

            ICheckSettingsInternal checkSettingsInternal = updateCheckSettings(checkSettings);

            logger.verbose("Dom extraction starting   (" + checkSettingsInternal.toString() + ")");

            timer.schedule(new TimeoutTask(), DOM_EXTRACTION_TIMEOUT);
            String resultAsString;
            ScriptResponse.Status status = null;
            ScriptResponse scriptResponse = null;
            do {
                resultAsString = (String) this.webDriver.executeScript(PROCESS_RESOURCES + "return __processPageAndPoll();");
                try {
                    scriptResponse = GeneralUtils.parseJsonToObject(resultAsString, ScriptResponse.class);
                    status = scriptResponse.getStatus();
                } catch (IOException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
                Thread.sleep(200);
            } while (status == ScriptResponse.Status.WIP && !isCheckTimerTimedout.get());

            if (status == ScriptResponse.Status.ERROR) {
                throw new EyesException("DomSnapshot Error: " + scriptResponse.getError());
            }

            if (isCheckTimerTimedout.get()) {
                throw new EyesException("Domsnapshot Timed out");
            }
            FrameData scriptResult = scriptResponse.getValue();

            logger.verbose("Dom extracted  (" + checkSettingsInternal.toString() + ")");

            List<VisualGridSelector[]> regionsXPaths = getRegionsXPaths(checkSettingsInternal);

            logger.verbose("regionXPaths : " + regionsXPaths);

            List<RunningTest> filtteredTests = new ArrayList<>();

            for (final RunningTest test : testList) {
                VisualGridTask.TaskType taskType = null;
                List<VisualGridTask> taskList = test.getVisualGridTaskList();
                if (!taskList.isEmpty()) {
                    VisualGridTask visualGridTask = taskList.get(taskList.size() - 1);
                    taskType = visualGridTask.getType();
                }
                if (taskType != VisualGridTask.TaskType.CLOSE && taskType != VisualGridTask.TaskType.ABORT) {
                    filtteredTests.add(test);
                }

            }

            for (RunningTest runningTest : filtteredTests) {
                VisualGridTask checkVisualGridTask = runningTest.check((ICheckSettings) checkSettingsInternal, regionsXPaths);
                visualGridTaskList.add(checkVisualGridTask);
            }

            logger.verbose("added check tasks  (" + checkSettingsInternal.toString() + ")");

            this.renderingGridRunner.check((ICheckSettings) checkSettingsInternal, debugResourceWriter, scriptResult,
                    this.VGEyesConnector, visualGridTaskList, openVisualGridTasks,
                    new VisualGridRunner.RenderListener() {
                        @Override
                        public void onRenderSuccess() {

                        }

                        @Override
                        public void onRenderFailed(Exception e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }, regionsXPaths, this.configProvider.get().getForceFullPageScreenshot());

            logger.verbose("created renderTask  (" + checkSettings.toString() + ")");
        } catch (IllegalArgumentException | EyesException | InterruptedException e) {
            Error error = new Error(e);
            abort();
            for (RunningTest runningTest : testList) {
                runningTest.setTestInExceptionMode(error);
            }
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    private ICheckSettingsInternal updateCheckSettings(ICheckSettings checkSettings) {
        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;

        MatchLevel matchLevel = checkSettingsInternal.getMatchLevel();

        Boolean fully = checkSettingsInternal.isStitchContent();
        Boolean sendDom = checkSettingsInternal.isSendDom();

        if (matchLevel == null) {
            checkSettings = checkSettings.matchLevel(getConfigGetter().getMatchLevel());
        }

        if (fully == null) {
            checkSettings = checkSettings.fully(getConfigGetter().isForceFullPageScreenshot());
        }

        if (sendDom == null) {
            checkSettings = checkSettings.sendDom(getConfigGetter().isSendDom());
        }

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
        WebElement rootElement = getScrollRootElement(frameTarget);
        Frame frame = webDriver.getFrameChain().peek();
        frame.setScrollRootElement(rootElement);
    }

    private WebElement getScrollRootElement(IScrollRootElementContainer scrollRootElementContainer) {
        WebElement scrollRootElement = null;
        if (!EyesSeleniumUtils.isMobileDevice(webDriver)) {
            if (scrollRootElementContainer == null) {
                scrollRootElement = webDriver.findElement(By.tagName("html"));
            } else {
                scrollRootElement = scrollRootElementContainer.getScrollRootElement();
                if (scrollRootElement == null) {
                    By scrollRootSelector = scrollRootElementContainer.getScrollRootSelector();
                    scrollRootElement = webDriver.findElement(scrollRootSelector != null ? scrollRootSelector : By.tagName("html"));
                }
            }
        }

        return scrollRootElement;
    }

    private synchronized List<VisualGridTask> addOpenTaskToAllRunningTest() {
        logger.verbose("enter");
        List<VisualGridTask> visualGridTasks = new ArrayList<>();
        for (RunningTest runningTest : testList) {
            if (!runningTest.isOpenTaskIssued()) {
                VisualGridTask visualGridTask = runningTest.open();
                visualGridTasks.add(visualGridTask);
            }
        }
        logger.verbose("calling addOpenTaskToAllRunningTest.open");
        this.isVGEyesIssuedOpenTasks.set(true);
        logger.verbose("exit");
        return visualGridTasks;
    }

    public Logger getLogger() {
        return logger;
    }

    public List<RunningTest> getAllRunningTests() {
        return testList;
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter;
    }

    @Override
    public String toString() {
        return "SeleniumVGEyes - url: " + url;
    }

    public void setServerConnector(IServerConnector serverConnector) {
        this.serverConnector = serverConnector;
    }


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

    private IConfigurationGetter getConfigGetter() {
        return this.configProvider.get();
    }

    private IConfigurationSetter getConfigSetter() {
        return this.configProvider.set();
    }

    @SuppressWarnings("WeakerAccess")
    public String getBaseAgentId() {
        //noinspection SpellCheckingInspection
        return "eyes.selenium.visualgrid.java/3.152.1";
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     *
     * @param batch The batch info to set.
     */
    public void setBatch(BatchInfo batch) {
        if (isDisabled) {
            logger.verbose("Ignored");
            return;
        }

        logger.verbose("setBatch(" + batch + ")");

        this.getConfigSetter().setBatch(batch);
    }

    private List<VisualGridSelector[]> getRegionsXPaths(ICheckSettingsInternal csInternal) {
        List<VisualGridSelector[]> result = new ArrayList<>();
        FrameChain frameChain = webDriver.getFrameChain().clone();
        EyesTargetLocator switchTo = (EyesTargetLocator) webDriver.switchTo();
        switchToFrame((ISeleniumCheckTarget) csInternal);
        List<WebElementRegion>[] elementLists = collectSeleniumRegions(csInternal);
        for (List<WebElementRegion> elementList : elementLists) {
            //noinspection SpellCheckingInspection
            List<VisualGridSelector> xpaths = new ArrayList<>();
            for (WebElementRegion webElementRegion : elementList) {
                if (webElementRegion.getElement() == null) continue;
                String xpath = (String) webDriver.executeScript(GET_ELEMENT_XPATH_JS, webElementRegion.getElement());
                xpaths.add(new VisualGridSelector(xpath, webElementRegion.getRegion()));
            }
            result.add(xpaths.toArray(new VisualGridSelector[0]));
        }
        switchTo.frames(frameChain);
        return result;
    }

    private List<WebElementRegion>[] collectSeleniumRegions(ICheckSettingsInternal csInternal) {
        CheckSettings settings = (CheckSettings) csInternal;
        GetRegion[] ignoreRegions = settings.getIgnoreRegions();
        GetRegion[] layoutRegions = settings.getLayoutRegions();
        GetRegion[] strictRegions = settings.getStrictRegions();
        GetRegion[] contentRegions = settings.getContentRegions();
        GetFloatingRegion[] floatingRegions = settings.getFloatingRegions();

        List<WebElementRegion> ignoreElements = getElementsFromRegions(Arrays.asList(ignoreRegions));
        List<WebElementRegion> layoutElements = getElementsFromRegions(Arrays.asList(layoutRegions));
        List<WebElementRegion> strictElements = getElementsFromRegions(Arrays.asList(strictRegions));
        List<WebElementRegion> contentElements = getElementsFromRegions(Arrays.asList(contentRegions));
        List<WebElementRegion> floatingElements = getElementsFromRegions(Arrays.asList(floatingRegions));


        ISeleniumCheckTarget iSeleniumCheckTarget = (ISeleniumCheckTarget) csInternal;
        WebElement targetElement = iSeleniumCheckTarget.getTargetElement();

        if (targetElement == null) {
            By targetSelector = iSeleniumCheckTarget.getTargetSelector();
            if (targetSelector != null) {
                targetElement = webDriver.findElement(targetSelector);
            }
        }

        WebElementRegion target = new WebElementRegion(targetElement, "target");
        List<WebElementRegion> targetElementList = new ArrayList<>();
        targetElementList.add(target);
        //noinspection UnnecessaryLocalVariable,unchecked
        List<WebElementRegion>[] lists = new List[]{ignoreElements, layoutElements, strictElements, contentElements, floatingElements, targetElementList};
        return lists;
    }


    private List<WebElementRegion> getElementsFromRegions(List regionsProvider) {
        List<WebElementRegion> elements = new ArrayList<>();
        for (Object getRegion : regionsProvider) {
            if (getRegion instanceof IGetSeleniumRegion) {
                IGetSeleniumRegion getSeleniumRegion = (IGetSeleniumRegion) getRegion;
                List<WebElement> webElements = getSeleniumRegion.getElements(webDriver);
                for (WebElement webElement : webElements) {
                    elements.add(new WebElementRegion(webElement, getRegion));
                }
            }
        }
        return elements;
    }

    /**
     * Adds a property to be sent to the server.
     *
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

    private class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            logger.verbose("Check Timer timeout.");
            isCheckTimerTimedout.set(true);
        }
    }

    private void abort() {
        for (RunningTest runningTest : testList) {
            runningTest.abort();
        }
    }

}
