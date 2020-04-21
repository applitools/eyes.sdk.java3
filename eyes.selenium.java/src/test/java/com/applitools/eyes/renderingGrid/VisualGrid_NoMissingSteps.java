package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class VisualGrid_NoMissingSteps {

    private VisualGridRunner VisualGrid = new VisualGridRunner(10);

    private Eyes eyes = new Eyes(VisualGrid);

    private WebDriver driver;


    @BeforeClass
    protected void init(){


    }

    @BeforeMethod
    public void setUp(){
        eyes.setLogHandler(TestUtils.initLogger("VisualGrid"));
        //eyes.setProxy(new ProxySettings("https://127.0.0.1", 8888));
        VisualGrid.setLogger(eyes.getLogger());
        Configuration configuration = eyes.getConfiguration();
        configuration.setBatch(TestDataProvider.batchInfo);
        configuration.setSaveNewTests(false);
        driver = SeleniumUtils.createChromeDriver();
        for (BrowserType b : BrowserType.values())
        {
            configuration.addBrowser(800, 600, b);
//            config.addBrowser(1200, 800, b);
//            config.addBrowser(1600, 1200, b);
        }
        //configuration.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        eyes.setConfiguration(configuration);

    }

    @Test
    public void TestNoMissingSteps() {

        try {
            driver.get("https://applitools.com/helloworld");

            eyes.open(driver, "app", "TestNoMissingSteps");
            eyes.check("first check", Target.window().withName("Step 1 A"));
            Thread.sleep(10000);
            eyes.check("second check", Target.window().fully(false).withName("Step 1 B"));
            Thread.sleep(10000);
            driver.findElement(By.tagName("button")).click();
            eyes.check("third check", Target.window().withName("Step 2 A"));
            Thread.sleep(10000);
            eyes.check("forth check", Target.window().fully(false).withName("Step 2 B"));
            Thread.sleep(10000);

            eyes.check(Target.window());

            eyes.close();

            TestResultsSummary allTestResults = VisualGrid.getAllTestResults();
            System.out.println("Results: " + allTestResults);
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        }
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @AfterClass
    public void closeRunner() {
        TestResultsSummary allTestResults = this.VisualGrid.getAllTestResults();
        String message = allTestResults.toString();
        eyes.getLogger().verbose(message);
    }
}