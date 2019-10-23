package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class SeleniumEyesCompileTest {
    private Eyes eyes;
    private Region region = new Region(10, 10, 10, 10);
    private WebElement webElement = null;
    private String tag = "tag";
    private By selector = null;
    private String[] strs = new String[1];
    private MouseAction action = null;
    private Location location = null;
    private RectangleSize size = null;
    private WebDriver driver;


    public SeleniumEyesCompileTest() {


    }

    @BeforeClass()
    public void OneTimeSetUp2() {
        eyes = new Eyes();
        //eyes.setServerConnector(new ServerConnector());

//        RemoteSessionEventHandler remoteSessionEventHandler = new RemoteSessionEventHandler(
//                eyes.getLogger(), URI.create("http://localhost:3000/"), "MyAccessKey");
//        remoteSessionEventHandler.setThrowExceptions(false);
//        eyes.addSessionEventHandler(remoteSessionEventHandler);

        LogHandler logHandler = new StdoutLogHandler(TestUtils.verboseLogs);

        eyes.setLogHandler(logHandler);
        eyes.setStitchMode(StitchMode.CSS);

        eyes.setHideScrollbars(true);

        String batchId = System.getenv("APPLITOOLS_BATCH_ID");

        eyes.setIsDisabled(true);

        WebDriver webDriver = SeleniumUtils.createChromeDriver();

        eyes.setConfiguration( new Configuration(new RectangleSize(100, 100)));
        driver = eyes.open(webDriver);

    }

    @Test
    public void testSetAgentId() {
        eyes.setAgentId("test");
    }

    @Test
    public void testGetAgentId() {
        eyes.getAgentId();
    }

    @Test
    public void testSetServerConnector() {
        eyes.setServerConnector(null);
    }

    @Test
    public void testSetApiKey() {
        eyes.setApiKey("abcdefg");
    }

    @Test
    public void testGetApiKey() {
        eyes.getApiKey();
    }

    @Test
    public void testSetServerUrl() {
        eyes.setServerUrl("");
    }

    @Test
    public void testGetServerUrl() {
        eyes.getServerUrl();
    }

    @Test
    public void testSetProxy() {
        eyes.setProxy(new ProxySettings(""));
    }

    @Test
    public void testGetProxy() {
        eyes.getProxy();
    }

    @Test
    public void testSetIsDisabled() {
        eyes.setIsDisabled(true);
    }

    @Test
    public void testGetIsDisabled() {
        eyes.getIsDisabled();
    }

    @Test
    public void testSetAppName() {
        eyes.setAppName("");
    }

    @Test
    public void testGetAppName() {
        eyes.getAppName();
    }

    @Test
    public void testSetBranchName() {
        eyes.setBranchName("branch name");
    }

    @Test
    public void testGetBranchName() {
        eyes.getBranchName();
    }

    @Test
    public void testSetParentBranchName() {
        eyes.setParentBranchName("parent");
    }

    @Test
    public void testGetParentBranchName() {
        eyes.getParentBranchName();
    }

    @Test
    public void testSetBaselineBranchName() {
        eyes.setBaselineBranchName("bbn");
    }

    @Test
    public void testGetBaselineBranchName() {
        eyes.getBaselineBranchName();
    }

    @Test
    public void testSetSaveDiffs() {
        eyes.getSaveDiffs();
    }

    @Test
    public void testGetSaveDiffs() {
        eyes.getSaveDiffs();
    }

    @Test
    public void testSetMatchTimeout() {
        eyes.setMatchTimeout(0);
    }

    @Test
    public void testGetMatchTimeout() {
        eyes.getMatchTimeout();
    }

    @Test
    public void testSetSaveNewTests() {
        eyes.setSaveNewTests(true);
    }

    @Test
    public void testGetSaveNewTests() {
        eyes.getSaveNewTests();
    }

    @Test
    public void testSetSaveFailedTests() {
        eyes.setSaveFailedTests(true);
    }

    @Test
    public void testGetSaveFailedTests() {
        eyes.getSaveFailedTests();
    }

    @Test
    public void testSetBatch() {
        eyes.setBatch(new BatchInfo("new batch"));
    }

    @Test
    public void testGetBatch() {
        eyes.getBatch();
    }

    @Test
    public void testSetFailureReports() {
        eyes.setFailureReports(FailureReports.IMMEDIATE);
    }

    @Test
    public void testGetFailureReports() {
        eyes.getFailureReports();
    }

    @Test
    public void testSetDefaultMatchSettings() {
        eyes.setDefaultMatchSettings(new ImageMatchSettings(MatchLevel.EXACT, new ExactMatchSettings(), true));
    }

    @Test
    public void testGetDefaultMatchSettings() {
        eyes.getDefaultMatchSettings();
    }

    @Test
    public void testSetMatchLevel() {
        eyes.setMatchLevel(MatchLevel.EXACT);
    }

    @Test
    public void testGetMatchLevel() {
        eyes.getMatchLevel();
    }

    @Test
    public void testGetFullAgentId() {
        eyes.getFullAgentId();
    }

    @Test
    public void testGetIsOpen() {
        eyes.getIsOpen();
    }

    @Test
    public void testGetDefaultServerUrl() {
        eyes.getDefaultServerUrl();
    }

    @Test
    public void testSetLogHandler() {
        eyes.setLogHandler(new NullLogHandler());
    }

    @Test
    public void testGetLogHandler() {
        eyes.getLogHandler();
    }

    @Test
    public void testGetLogger() {
        eyes.getLogger();
    }

    @Test
    public void testSetImageCut() {
        eyes.setImageCut(null);
    }

    @Test
    public void testGetIsCutProviderExplicitlySet() {
        eyes.getIsCutProviderExplicitlySet();
    }

    @Test
    public void testSetScaleRatio() {
        eyes.setScaleRatio(0.5);
    }

    @Test
    public void testGetScaleRatio() {
        eyes.getScaleRatio();
    }

    @Test
    public void testAddProperty() {
        eyes.addProperty("name", "value");
    }

    @Test
    public void testClearProperties() {
        eyes.clearProperties();
    }

    @Test
    public void testSetSaveDebugScreenshots() {
        eyes.setSaveDebugScreenshots(true);
    }

    @Test
    public void testGetSaveDebugScreenshots() {
        eyes.getSaveDebugScreenshots();
    }

    @Test
    public void testSetDebugScreenshotsPath() {
        eyes.setDebugScreenshotsPath("");
    }

    @Test
    public void testGetDebugScreenshotsPath() {
        eyes.getDebugScreenshotsPath();
    }

    @Test
    public void testSetDebugScreenshotsPrefix() {
        eyes.setDebugScreenshotsPrefix("prefix");
    }

    @Test
    public void testGetDebugScreenshotsPrefix() {
        eyes.getDebugScreenshotsPrefix();
    }

    @Test
    public void testGetDebugScreenshotsProvider() {
        eyes.getDebugScreenshotsProvider();
    }

    @Test
    public void testGetIgnoreCaret() {
        eyes.getIgnoreCaret();
    }

    @Test
    public void testSetIgnoreCaret() {
        eyes.setIgnoreCaret(false);
    }

    @Test
    public void testGetStitchOverlap() {
        eyes.getStitchOverlap();
    }

    @Test
    public void testSetStitchOverlap() {
        eyes.setStitchOverlap(0);
    }

    @Test
    public void testClose() {
        eyes.close();
    }

    @Test
    public void testClose1() {
        eyes.close(true);
    }

    @Test
    public void testAbortIfNotClosed() {
        eyes.abortIfNotClosed();
    }

    @Test
    public void testSetHostOS() {
        eyes.setHostOS("Host Os");
    }

    @Test
    public void testGetHostOS() {
        eyes.getHostOS();
    }

    @Test
    public void testSetHostApp() {
        eyes.setHostApp("Host App");
    }

    @Test
    public void testGetHostApp() {
        eyes.getHostApp();
    }

    @Test
    public void testSetBaselineName() {
        eyes.setBaselineName("");
    }

    @Test
    public void testGetBaselineName() {
        eyes.getBaselineName();
    }

    @Test
    public void testSetBaselineEnvName() {
        eyes.setBaselineEnvName("");
    }

    @Test
    public void testGetBaselineEnvName() {
        eyes.getBaselineEnvName();
    }

    @Test
    public void testSetEnvName() {
        eyes.setEnvName("");
    }

    @Test
    public void testGetEnvName() {
        eyes.getEnvName();
    }

    @Test
    public void testSetAppEnvironment() {
        eyes.setAppEnvironment("", "");
    }

    @Test
    public void testGetPositionProvider() {
        eyes.getPositionProvider();
    }

    @Test
    public void testSetPositionProvider() {
        eyes.setPositionProvider(null);
    }

    @Test
    public void testSetExplicitViewportSize() {
        eyes.setExplicitViewportSize(new RectangleSize(100, 100));
    }

    @Test
    public void testGetAgentSetup() {
        eyes.getAgentSetup();
    }

    @Test
    public void testLog() {
        eyes.log("log test");
    }

    @Test
    public void testAddSessionEventHandler() {
        eyes.addSessionEventHandler(null);
    }

    @Test
    public void testRemoveSessionEventHandler() {
        eyes.removeSessionEventHandler(null);
    }

    @Test
    public void testClearSessionEventHandlers() {
        eyes.clearSessionEventHandlers();
    }

    @Test
    public void testIsSendDom() {
        eyes.isSendDom();
    }

    @Test
    public void testSetSendDom() {
        eyes.setSendDom(true);
    }

    @Test
    public void testGetRenderingInfo() {
        eyes.getRenderingInfo();
    }

    @Test
    public void testGetDriver() {
        eyes.getDriver();
    }

    @Test
    public void testGetCurrentFramePositionProvider() {
        eyes.getCurrentFramePositionProvider();
    }

    @Test
    public void testGetRegionToCheck() {
        eyes.getRegionToCheck();
    }

    @Test
    public void testSetRegionToCheck() {
        eyes.setRegionToCheck(new Region(10, 10, 10, 10));
    }

    @Test
    public void testGetHideCaret() {
        eyes.getHideCaret();
    }

    @Test
    public void testSetHideCaret() {
        eyes.setHideCaret(true);
    }

    @Test
    public void testShouldStitchContent() {
        eyes.shouldStitchContent();
    }

    @Test
    public void testSetForceFullPageScreenshot() {
        eyes.setForceFullPageScreenshot(true);
    }

    @Test
    public void testGetForceFullPageScreenshot() {
        eyes.getForceFullPageScreenshot();
    }

    @Test
    public void testSetWaitBeforeScreenshots() {
        eyes.setWaitBeforeScreenshots(5);
    }

    @Test
    public void testGetWaitBeforeScreenshots() {
        eyes.getWaitBeforeScreenshots();
    }

    @Test
    public void testSetScrollToRegion() {
        eyes.setScrollToRegion(true);
    }

    @Test
    public void testGetScrollToRegion() {
        eyes.getScrollToRegion();
    }

    @Test
    public void testSetStitchMode() {
        eyes.setStitchMode(StitchMode.CSS);
    }

    @Test
    public void testGetStitchMode() {
        eyes.getStitchMode();
    }

    @Test
    public void testSetHideScrollbars() {
        eyes.setHideScrollbars(true);
    }

    @Test
    public void testGetHideScrollbars() {
        eyes.getHideScrollbars();
    }

    @Test
    public void testGetRotation() {
        eyes.getRotation();
    }

    @Test
    public void testSetRotation() {
        eyes.setRotation(new ImageRotation(50));
    }

    @Test
    public void testGetDevicePixelRatio() {
        eyes.getDevicePixelRatio();
    }

    @Test
    public void testOpen() {
        eyes.open(this.driver);
    }

    @Test
    public void testOpen1() {
        eyes.open(this.driver, "App Name", "Test Name");
    }

    @Test
    public void testOpen2() {
        eyes.open(driver, "app Name", "Test Name", new RectangleSize(10, 10));
    }

    @Test
    public void testCheckWindow() {
        eyes.checkWindow("tag");
    }

    @Test
    public void testCheckWindow2() {
        eyes.checkWindow(5, "tag");
    }

    @Test
    public void testCheck() {
        eyes.check(new SeleniumCheckSettings(""));
    }

    @Test
    public void testCheck1() {
        eyes.check("", new SeleniumCheckSettings(""));
    }

    @Test
    public void testCheck2() {
        eyes.check(new SeleniumCheckSettings(""), new SeleniumCheckSettings(""));
    }

    @Test
    public void testSetIsDisabled1() {
        eyes.setIsDisabled(true);
    }

    @Test
    public void testCheckRegion() {
        eyes.checkRegion(region);
    }

    @Test
    public void testCheckRegion1() {
        eyes.checkRegion(region, 60, tag);
    }

    @Test
    public void testCheckRegion2() {
        eyes.checkRegion(webElement);
    }

    @Test
    public void testCheckRegion3() {
        eyes.checkRegion(webElement, tag);
    }

    @Test
    public void testCheckRegion4() {
        eyes.checkRegion(webElement, true);
    }

    @Test
    public void testCheckRegion5() {
        eyes.checkRegion(webElement, tag, true);
    }

    @Test
    public void testCheckRegion6() {
        eyes.checkRegion(webElement, 60, tag);
    }

    @Test
    public void testCheckRegion7() {
        eyes.checkRegion(webElement, 60, tag, true);
    }

    @Test
    public void testCheckRegion8() {
        eyes.checkRegion(selector);
    }

    @Test
    public void testCheckRegion9() {
        eyes.checkRegion(selector, true);
    }

    @Test
    public void testCheckRegion10() {
        eyes.checkRegion(selector, tag);
    }

    @Test
    public void testCheckRegion11() {
        eyes.checkRegion(selector, 50, tag);
    }

    @Test
    public void testCheckRegion12() {
        eyes.checkRegion(selector, tag, true);
    }

    @Test
    public void testCheckRegionInFrame() {
        eyes.checkRegionInFrame(50, selector);
    }

    @Test
    public void testCheckRegionInFrame1() {
        eyes.checkRegionInFrame(50, selector, true);
    }

    @Test
    public void testCheckRegionInFrame2() {
        eyes.checkRegionInFrame(50, selector, tag);
    }

    @Test
    public void testCheckRegionInFrame3() {
        eyes.checkRegionInFrame(50, selector, tag, true);
    }

    @Test
    public void testCheckRegionInFrame4() {
        eyes.checkRegionInFrame(50, selector, 50, tag, true);
    }

    @Test
    public void testCheckRegionInFrame5() {
        eyes.checkRegionInFrame(50, selector);
    }

    @Test
    public void testCheckRegionInFrame6() {
        eyes.checkRegionInFrame("frameOrId", selector);
    }

    @Test
    public void testCheckRegionInFrame7() {
        eyes.checkRegionInFrame("frameOrId", selector, true);
    }

    @Test
    public void testCheckRegionInFrame8() {
        eyes.checkRegionInFrame("frameOrId", selector, tag);
    }

    @Test
    public void testCheckRegionInFrame9() {
        eyes.checkRegionInFrame("frameOrId", selector, tag, true);
    }

    @Test
    public void testCheckRegionInFrame10() {
        eyes.checkRegionInFrame("frameOrId", selector, 50, tag);
    }

    @Test
    public void testCheckRegionInFrame11() {
        eyes.checkRegionInFrame("frameOrId", selector, 50, tag, true);
    }

    @Test
    public void testCheckRegionInFrame12() {
        eyes.checkRegionInFrame(webElement, selector);
    }

    @Test
    public void testCheckRegionInFrame13() {
        eyes.checkRegionInFrame(webElement, selector, tag);
    }

    @Test
    public void testCheckRegionInFrame14() {
        eyes.checkRegionInFrame(webElement, selector, true);
    }

    @Test
    public void testCheckRegionInFrame15() {
        eyes.checkRegionInFrame(webElement, selector, tag, true);
    }

    @Test
    public void testCheckRegionInFrame16() {
        eyes.checkRegionInFrame(webElement, selector, 50, tag);
    }

    @Test
    public void testCheckRegionInFrame17() {
        eyes.checkRegionInFrame(webElement, selector, 50, tag, true);
    }

    @Test
    public void testGetCurrentFrameScrollRootElement() {
        eyes.getCurrentFrameScrollRootElement();
    }

    @Test
    public void testCheckFrame() {
        eyes.checkFrame("frameOrId");
    }

    @Test
    public void testCheckFrame1() {
        eyes.checkFrame("frameOrId", tag);
    }

    @Test
    public void testCheckFrame2() {
        eyes.checkFrame("frameOrId", 50, tag);
    }

    @Test
    public void testCheckFrame3() {
        eyes.checkFrame(50);
    }

    @Test
    public void testCheckFrame4() {
        eyes.checkFrame(50, tag);
    }

    @Test
    public void testCheckFrame5() {
        eyes.checkFrame(50, 50, tag);
    }

    @Test
    public void testCheckFrame6() {
        eyes.checkFrame(webElement);
    }

    @Test
    public void testCheckFrame7() {
        eyes.checkFrame(webElement, tag);
    }

    @Test
    public void testCheckFrame8() {
        eyes.checkFrame(webElement, 50, tag);
    }

    @Test
    public void testCheckFrame9() {
        eyes.checkFrame(strs);
    }

    @Test
    public void testCheckFrame10() {
        eyes.checkFrame(strs, tag);
    }

    @Test
    public void testCheckFrame11() {
        eyes.checkFrame(strs, 50, tag);
    }

    @Test
    public void testCheckElement() {
        eyes.checkElement(webElement);
    }

    @Test
    public void testCheckElement1() {
        eyes.checkElement(webElement, tag);
    }

    @Test
    public void testCheckElement2() {
        eyes.checkElement(webElement, 50, tag);
    }

    @Test
    public void testCheckElement3() {
        eyes.checkElement(selector);
    }

    @Test
    public void testCheckElement4() {
        eyes.checkElement(selector, tag);
    }

    @Test
    public void testCheckElement5() {
        eyes.checkElement(selector, 50, tag);
    }

    @Test
    public void testAddMouseTrigger() {
        eyes.addMouseTrigger(action, region, location);
    }

    @Test
    public void testAddMouseTrigger1() {
        eyes.addMouseTrigger(action, webElement);
    }

    @Test
    public void testAddTextTrigger() {
        eyes.addTextTrigger(region, "text");
    }

    @Test
    public void testAddTextTrigger1() {
        eyes.addTextTrigger(webElement, "text");
    }

    @Test
    public void testGetViewportSize() {
        eyes.getViewportSize();
    }

    @Test
    public void testGetViewportSize1() {
        eyes.getViewportSize();
    }

    @Test
    public void testSetViewportSize() {
        eyes.setViewportSize(driver, size);
    }

    @Test
    public void testGetAgentSetup1() {
        eyes.getAgentSetup();
    }

    @Test
    public void testGetServerConnector() {
        eyes.getServerConnector();
    }

    @Test
    public void testIsSendDom1() {
        eyes.isSendDom();
    }
}