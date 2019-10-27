package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;

public class TestTopSites {
    public static BatchInfo batch = new BatchInfo("TTS");
    private EyesRunner visualGridRunner;


    public static Eyes initializeEyes(VisualGridRunner runner) {
        // Create Eyes object with the runner, meaning it'll be a Visual Grid eyes.
        Eyes eyes = new Eyes(runner);

        eyes.setLogHandler(new FileLogger("eyes.log", true, true));

        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        Configuration sconf = new Configuration();

        sconf.setAppName("Test");

        sconf.setTestName("Test Name");

        sconf.setBatch(new BatchInfo("VIP Browser combo batch"));

        sconf.setStitchMode(StitchMode.CSS);

        sconf.addBrowser(800, 600, BrowserType.CHROME);

        sconf.addBrowser(1200, 800, BrowserType.FIREFOX);

        // Add iPhone 4 device emulation
        // sconf.addDeviceEmulation(DeviceName.iPhone_4);

        eyes.setConfiguration(sconf);

        return eyes;
    }
    @Test
    public void runTest() {

        // Create a runner with concurrency of 10
        VisualGridRunner runner = new VisualGridRunner(10);

        Eyes eyes = initializeEyes(runner);

        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://www.delhaize.be");
        webDriver.findElement(By.cssSelector("body > div.Modal.LanguageSelector > div > div > div > div > div.options > div:nth-child(1) > button")).click();

        eyes.open(webDriver);
        eyes.check(Target.window().fully().withName("Step 1 - Login page"));

        webDriver.quit();

        System.out.println(
                "Please wait... we are now: \n1. Uploading resources, \n2. Rendering in Visual Grid, and \n3. Using Applitools A.I. to validate the checkpoints. \nIt'll take about 30 secs to a minute...");

        eyes.close();
        TestResultsSummary allTestResults = runner.getAllTestResults();
        System.out.println(allTestResults);

    }
}
