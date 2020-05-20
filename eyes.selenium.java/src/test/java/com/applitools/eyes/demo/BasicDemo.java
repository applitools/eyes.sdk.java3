package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Runs Applitools test for the demo app https://demo.applitools.com
 */
public class BasicDemo extends ReportingTestSuite {
    private static BatchInfo batch;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler(false);

    private Eyes initEyes(EyesRunner runner) {
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logger);
        eyes.setBatch(batch);
        //eyes.setProxy(new ProxySettings("http://localhost:8888"));
        return eyes;
    }

    private Eyes sanityTest(String testName, EyesRunner runner) {
        Eyes eyes = initEyes(runner);
        eyes.open(driver, "Demo App", testName, new RectangleSize(800, 800));

        // Navigate the browser to the "ACME" demo app.
        driver.get("https://demo.applitools.com");

        // To see visual bugs after the first run, use the commented line below instead.
        //driver.get("https://demo.applitools.com/index_v2.html");

        eyes.checkWindow("Login Window");
        driver.findElement(By.id("log-in")).click();
        eyes.checkWindow("App Window");
        eyes.closeAsync();
        return eyes;
    }

    @BeforeClass
    public static void beforeAll() {
        if (TestUtils.runOnCI && System.getenv("TRAVIS") != null) {
            System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver"); // for travis build.
        }

        batch = new BatchInfo("Basic Sanity");
    }

    @BeforeMethod
    public void beforeEach() {
        driver = SeleniumUtils.createChromeDriver();
    }

    @Test
    public void classicTest() {
        EyesRunner runner = new ClassicRunner();
        Eyes eyes = null;
        try {
            eyes = sanityTest("Classic Runner", runner);
        } finally {
            if (eyes != null) {
                eyes.abortAsync();
            }

            afterEach(runner);
        }
    }

    @Test
    public void visualGridTest() {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = null;
        try {
            eyes = sanityTest("Visual Grid Runner", runner);
        } finally {
            if (eyes != null) {
                eyes.abortAsync();
            }

            afterEach(runner);
        }
    }

    public void afterEach(EyesRunner runner) {
        driver.quit();
        TestResultsSummary allTestResults = runner.getAllTestResults();
        System.out.println(allTestResults);
    }
}
