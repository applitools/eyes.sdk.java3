package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

class TestVGDemo
{
    public static void main(String[] args)
    {
        TestVGDemo testVGDemo = new TestVGDemo();
        testVGDemo.run();
    }

    private EyesRunner runner;
    private Eyes eyes;
    private WebDriver webDriver;

    private void run()
    {
        Init();

        TestLoginPage();
        TestDashboardPage();

        ValidateResults();
    }

    private void Init()
    {
        // Create a new webdriver
        webDriver = SeleniumUtils.createChromeDriver();

        // Navigate to the url we want to test
        webDriver.get("https://demo.applitools.com");

        // ⭐️ Note to see visual bugs, run the test using the above URL for the 1st run.
        //but then change the above URL to https://demo.applitools.com/index_v2.html (for the 2nd run)

        // Create a runner with concurrency of 10
        runner = new VisualGridRunner(10);

        // Set StdOut log handler with regular verbosity.
        runner.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        // Create Eyes object with the runner, meaning it'll be a Visual Grid eyes.
        eyes = new Eyes(runner);

        // Create configuration object
        Configuration conf = new Configuration();

        //conf.SetApiKey("APPLITOOLS_API_KEY");    // Set the Applitools API KEY here or as an environment variable "APPLITOOLS_API_KEY"
        conf.setTestName("Java VisualGrid demo")   // Set test name
                .setAppName("Demo app");             // Set app name

        conf.setBatch(TestDataProvider.batchInfo);
        // Add browsers with different viewports
        conf.addBrowser(800, 600, BrowserType.CHROME);
        conf.addBrowser(700, 500, BrowserType.FIREFOX);
        conf.addBrowser(1200, 800, BrowserType.IE_10);
        conf.addBrowser(1600, 1200, BrowserType.IE_11);
        conf.addBrowser(1024, 768, BrowserType.EDGE);

        // Add iPhone 4 device emulation in Portraig mode
        ChromeEmulationInfo iphone4 = new ChromeEmulationInfo(DeviceName.iPhone_4, ScreenOrientation.PORTRAIT);
        conf.addDeviceEmulation(DeviceName.iPhone_4, ScreenOrientation.PORTRAIT);

        // Set the configuration object to eyes
        eyes.setConfiguration(conf);

        eyes.setMatchLevel(MatchLevel.LAYOUT);
    }

    private void TestLoginPage()
    {
        // Call Open on eyes to initialize a test session
        eyes.open(webDriver);

        // check the login page
        eyes.check(Target.window().fully().withName("Login page").content());

//        eyes.closeAsync();
    }

    private void TestDashboardPage()
    {
        // Call Open on eyes to initialize a test session
//        eyes.open(webDriver);

        webDriver.findElement(By.id("log-in")).click();

        // Check the app page
        eyes.check(Target.window().fully());

        TestResults close = eyes.close();

        System.out.println(close);
    }

    private void ValidateResults()
    {
        // Close the browser
        webDriver.quit();

        //Wait and collect all test results
        TestResultsSummary allTestResults = runner.getAllTestResults();

        System.out.println(allTestResults);

    }

}