package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Runs Applitools test for the demo app https://demo.applitools.com
 */
@RunWith(JUnit4.class)
public class BasicDemo extends ReportingTestSuite {
    private EyesRunner currentRunner;
    private Eyes eyes;
    private static BatchInfo batch;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler(true);

    private void initEyes() {
        currentRunner.setLogHandler(logger);
        eyes = new Eyes(currentRunner);
        eyes.setLogHandler(logger);
        if(isNullOrEmpty(System.getenv("APPLITOOLS_API_KEY"))) {
            throw new RuntimeException("No API Key found; Please set environment variable 'APPLITOOLS_API_KEY'.");
        }

        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setBatch(batch);
        //eyes.setProxy(new ProxySettings("http://localhost:8888"));
    }

    private void sanityTest() {
        initEyes();
        eyes.open(driver, "Demo App", "Smoke Test", new RectangleSize(800, 800));

        // Navigate the browser to the "ACME" demo app.
        driver.get("https://demo.applitools.com");

        // To see visual bugs after the first run, use the commented line below instead.
        //driver.get("https://demo.applitools.com/index_v2.html");

        eyes.checkWindow("Login Window");
        driver.findElement(By.id("log-in")).click();
        eyes.checkWindow("App Window");
        eyes.closeAsync();
    }

    @BeforeClass
    public static void setBatch() {
        // Must be before ALL tests (at Class-level)
        batch = new BatchInfo("Demo batch");
    }

    @Before
    public void beforeEach() {
        driver = new ChromeDriver();
    }

    @Test
    public void classicTest() {
        currentRunner = new ClassicRunner();
        sanityTest();
    }

    @Test
    public void visualGridTest() {
        currentRunner = new VisualGridRunner(10);
        sanityTest();
    }

    @After
    public void afterEach() {
        driver.quit();
        try {
            TestResultsSummary allTestResults = currentRunner.getAllTestResults();
            System.out.println(allTestResults);
        } catch (DiffsFoundException e) {
            System.out.println("Diffs found");
        }
    }
}
