package com.applitools.eyes.api;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class TestAbort2 extends ReportingTestSuite {
    private final int concurrentSessions = 5;
    private final int viewPortWidth = 800;
    private final int viewPortHeight = 600;
    private final String appName = "My application";
    private final String batchName = "My batch";
    private final String testedUrl = "https://applitools.com/docs/topics/overview.html";
    private final boolean useVisualGrid_;

    private EyesRunner runner = null;
    private Configuration suiteConfig;
    private Eyes eyes;
    private WebDriver webDriver;

    @Factory(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public TestAbort2(boolean useVisualGrid) {
        super.setGroupName("selenium");
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        useVisualGrid_ = useVisualGrid;
    }

    @BeforeClass
    public void beforeTestSuite() {
        // 1. Create the runner that manages multiple tests
        if (useVisualGrid_) {
            runner = new VisualGridRunner(concurrentSessions);
        } else {
            runner = new ClassicRunner();
        }
        // continued below....
        // 2. Create a configuration object, we will use this when setting up each test
        suiteConfig = new Configuration();

        // 3. Set the various configuration values
        suiteConfig
                // 4. Add Visual Grid browser configurations
                .addBrowser(900, 600, BrowserType.CHROME)
                .addBrowser(1024, 786, BrowserType.CHROME)
                .addBrowser(900, 600, BrowserType.FIREFOX)
                .addBrowser(900, 600, BrowserType.IE_10)
                .addBrowser(900, 600, BrowserType.IE_11)
                .addBrowser(900, 600, BrowserType.EDGE)
                .addDeviceEmulation(DeviceName.iPhone_4, ScreenOrientation.PORTRAIT)
                .addDeviceEmulation(DeviceName.Galaxy_S5, ScreenOrientation.LANDSCAPE)

                // 5. set up default Eyes configuration values
                .setBatch(new BatchInfo(batchName))
                .setAppName(appName)
                .setViewportSize(new RectangleSize(viewPortWidth, viewPortHeight));

    }

    private void beforeEachTest() {
        // 1. Create the Eyes instance for the test and associate it with the runner
        eyes = new Eyes(runner);

        // 2. Set the configuration values we set up in beforeTestSuite
        eyes.setConfiguration(suiteConfig);

        // 3. Create a WebDriver for the test
        webDriver = SeleniumUtils.createChromeDriver();
    }

    @Test
    public void Test_GetAllResults() throws Exception {
        beforeEachTest();
        Assert.assertThrows(Exception.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Test_ThrowBeforeOpen();
            }
        });
        AfterEachTest();

        beforeEachTest();
        Assert.assertThrows(Exception.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Test_ThrowAfterOpen();
            }
        });
        AfterEachTest();

        beforeEachTest();
        if (!useVisualGrid_) {
            Assert.assertThrows(Exception.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    Test_ThrowDuringCheck();
                }
            });
        }
        else {
            Test_ThrowDuringCheck();
        }
        AfterEachTest();

        beforeEachTest();
        Assert.assertThrows(Exception.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Test_ThrowAfterCheck();
            }
        });
        AfterEachTest();

        Assert.assertThrows(Error.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                runner.getAllTestResults();
            }
        });
    }

    private void Test_ThrowBeforeOpen() throws Exception {
        // 1. Update the Eyes configuration with test specific values
        Configuration testConfig = eyes.getConfiguration();
        testConfig.setTestName("test URL : " + testedUrl);
        eyes.setConfiguration(testConfig);
        throw new Exception("Before Open");
    }

    private void Test_ThrowAfterOpen() throws Exception {
        // 1. Update the Eyes configuration with test specific values
        Configuration testConfig = eyes.getConfiguration();
        testConfig.setTestName("test URL : " + testedUrl);
        eyes.setConfiguration(testConfig);

        //2. Open Eyes, the application,test name and viewport size are already configured
        WebDriver driver = eyes.open(webDriver);
        throw new Exception("After Open");
    }

    private void Test_ThrowDuringCheck() throws Exception {
        // 1. Update the Eyes configuration with test specific values
        Configuration testConfig = eyes.getConfiguration();
        testConfig.setTestName("test URL : " + testedUrl);
        eyes.setConfiguration(testConfig);

        //2. Open Eyes, the application,test name and viewport size are already configured
        WebDriver driver = eyes.open(webDriver);

        //3. Now run the test
        driver.get(testedUrl);
        eyes.check("Step 1 Content - " + testedUrl, Target.frame("non-existing-frame"));
    }

    private void Test_ThrowAfterCheck() throws Exception {
        // 1. Update the Eyes configuration with test specific values
        Configuration testConfig = eyes.getConfiguration();
        testConfig.setTestName("test URL : " + testedUrl);
        eyes.setConfiguration(testConfig);

        //2. Open Eyes, the application,test name and viewport size are already configured
        WebDriver driver = eyes.open(webDriver);

        //3. Now run the test
        driver.get(testedUrl);
        eyes.check("Step 1 Content - " + testedUrl, Target.window());
        throw new Exception("After Check");
    }

    private void AfterEachTest() {
        if (eyes.isOpen()) {
            eyes.close(false);
        } else {
            eyes.abort();
        }
        webDriver.quit();
    }

    @AfterClass
    public void AfterTestSuite() {
        // Wait until the test results are available and retrieve them
        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        for(TestResultContainer result : allTestResults)
        {
            handleTestResults(result);
        }
    }

    private void handleTestResults(TestResultContainer summary) {
        Throwable ex = summary.getException();
        if (ex != null) {
            runner.getLogger().log("System error occurred while checking target.");
        }
        TestResults result = summary.getTestResults();
        if (result == null) {
            runner.getLogger().log("No test results information available");
        } else {
            runner.getLogger().log(String.format(
                    "AppName = %s, testname = %s, Browser = %s, OS = %s viewport = %dx%d, matched = %d, mismatched = %d, missing = %d, aborted = %s\n",
                    result.getAppName(),
                    result.getName(),
                    result.getHostApp(),
                    result.getHostOS(),
                    result.getHostDisplaySize().getWidth(),
                    result.getHostDisplaySize().getHeight(),
                    result.getMatches(),
                    result.getMismatches(),
                    result.getMissing(),
                    (result.isAborted() ? "aborted" : "no")));
        }
    }
}
