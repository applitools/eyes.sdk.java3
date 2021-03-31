package com.applitools.eyes.api;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class TestAbort extends ReportingTestSuite {
    private EyesRunner runner;
    private final boolean useVisualGrid;

    @Factory(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public TestAbort(boolean useVisualGrid) {
        super.setGroupName("selenium");
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        this.useVisualGrid = useVisualGrid;
    }

    @BeforeClass
    public void beforeClass() {
        runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
    }

    public Eyes setUp() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("data:text/html,<p>Test</p>");
        Eyes eyes = new Eyes(runner);
        eyes.setBatch(TestDataProvider.batchInfo);
        eyes.setLogHandler(new StdoutLogHandler());
        String testName = useVisualGrid ? "Test Abort_VG" : "Test Abort";

        Configuration config = eyes.getConfiguration();
        config.addBrowser(800, 600, BrowserType.CHROME);
        eyes.setConfiguration(config);

        eyes.open(driver, testName, testName, new RectangleSize(1200, 800));
        return eyes;
    }

    public void tearDown(Eyes eyes) {
        eyes.getDriver().quit();
    }

    @AfterClass
    public void afterClass() {
        runner.getAllTestResults(false);
    }

    @Test
    public void TestAbortIfNotClosed() throws InterruptedException {
        Eyes eyes = setUp();
        eyes.check(useVisualGrid ? "VG" : "SEL", Target.window());
        Thread.sleep(5000);
        eyes.abortIfNotClosed();
        tearDown(eyes);
    }

    @Test
    public void TestAbortAsyncIfNotClosed() throws InterruptedException {
        Eyes eyes = setUp();
        eyes.check(useVisualGrid ? "VG" : "SEL", Target.window());
        Thread.sleep(5000);
        eyes.abortAsync();
        tearDown(eyes);
    }

    @Test
    public void TestAbortIfNotClosedAndNotAborted() throws InterruptedException {
        Eyes eyes = setUp();
        eyes.check(useVisualGrid ? "VG" : "SEL", Target.window());
        Thread.sleep(5000);
        tearDown(eyes);
    }
}
