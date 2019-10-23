package com.applitools.eyes.demo;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestSuite {

    private Eyes eyes;
    private WebDriver webDriver;
    private ICheckSettings checkSettings;
    private VisualGridRunner visualGridManager;

    public TestSuite() {
        this.webDriver = SeleniumUtils.createChromeDriver();
    }

    @BeforeClass
    public void before_VisualGrid() {
        visualGridManager = new VisualGridRunner(4);
        visualGridManager.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        visualGridManager.setServerUrl("https://fabricdemoeyes.applitools.com/");
        visualGridManager.setApiKey(System.getenv("FABRICAM_DEMO_EYES_API_KEY"));

        Eyes eyes = new Eyes(visualGridManager);
        eyes.setBatch(new BatchInfo("Applitools Visual Grid Demo"));

        //VisualGridEyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));

        Configuration configuration = new Configuration();
        initConfig(configuration);
        configuration.setTestName("Applitools Visual Grid Demo");

        configuration.addBrowser(800, 600, BrowserType.CHROME);
        configuration.addBrowser(1200, 800, BrowserType.CHROME);
        configuration.addBrowser(1920, 1000, BrowserType.CHROME);

        eyes.setConfiguration(configuration);
        eyes.open(webDriver);
        this.eyes = eyes;

        this.checkSettings = Target.window();
    }

    private void initConfig(Configuration configuration) {
        configuration.setAppName("Applitools Java3 Demo");
    }

    @Test
    public void test() {
        webDriver.get("https://applitools.github.io/demo/TestPages/FramesTestPage");
        eyes.check("check window", checkSettings);
    }

    @AfterMethod
    public void close() {
        try {
            eyes.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            eyes.abortIfNotClosed();
            webDriver.quit();
        }
    }

    @AfterClass
    public void collectResults() {
        if (visualGridManager != null) {
            eyes.getLogger().log(visualGridManager.getAllTestResults().toString());
        }
    }
}

