package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestTimeouts {
    private int originalTimeout;

    @BeforeClass
    public void Before() {
        try {
            originalTimeout = (int) (long) TestUtils.getFinalStatic(VisualGridEyes.class, "DOM_EXTRACTION_TIMEOUT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void After() {
        try {
            TestUtils.setFinalStatic(VisualGridEyes.class, "DOM_EXTRACTION_TIMEOUT", originalTimeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestTimeout() {
        //RenderingTask.pollTimeout_ = TimeSpan.FromSeconds(100);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            EyesRunner runner = new VisualGridRunner(10);
            Eyes eyes = new Eyes(runner);
            eyes.setLogHandler(TestUtils.initLogger());
            driver.get("https://applitools.com/helloworld");
            eyes.setBatch(TestDataProvider.batchInfo);
            eyes.open(driver, "Timeout Test", "Visual Grid Timeout Test", new RectangleSize(1200, 800));
            eyes.check(Target.window().withName("Test"));
            eyes.close();
            runner.getAllTestResults();
        } finally {
            driver.quit();
        }
    }

    @Test
    public void TestTimeout2() {
        WebDriver driver = SeleniumUtils.createChromeDriver();

        try {
            TestUtils.setFinalStatic(VisualGridEyes.class, "DOM_EXTRACTION_TIMEOUT", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final EyesRunner runner = new VisualGridRunner(10);
            final Eyes eyes = new Eyes(runner);
            eyes.setLogHandler(TestUtils.initLogger());
            driver.get("https://applitools.com/helloworld");
            eyes.setBatch(TestDataProvider.batchInfo);

            Configuration configuration = eyes.getConfiguration();
            configuration.setAppName("Test Timeouts").setTestName("Test Timeouts").setBatch(TestDataProvider.batchInfo);
            configuration.addBrowser(800, 600, BrowserType.CHROME);
            configuration.addBrowser(700, 500, BrowserType.FIREFOX);
            configuration.addBrowser(600, 400, BrowserType.EDGE);
            configuration.addBrowser(900, 700, BrowserType.IE_10);
            configuration.addBrowser(1000, 800, BrowserType.IE_11);
            configuration.addDeviceEmulation(DeviceName.Galaxy_S5);
            configuration.addDeviceEmulation(DeviceName.iPhone6_7_8_Plus);
            configuration.addDeviceEmulation(DeviceName.Laptop_with_HiDPI_screen);
            eyes.setConfiguration(configuration);
            eyes.open(driver);
            Error ex = Assert.expectThrows(Error.class, new Assert.ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    eyes.check(Target.window().withName("Test"));
                    eyes.close();
                    runner.getAllTestResults();
                }
            });
            Assert.assertEquals(ex.getMessage(), "java.lang.Error: com.applitools.eyes.EyesException: Domsnapshot Timed out");
        } finally {
            driver.quit();
        }
    }
}
