package com.applitools.eyes.demo;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MinimalVisualGridDemo {

    public static void main(String[] args){
        MinimalVisualGridDemo program = new MinimalVisualGridDemo();
        program.run();
    }

    private void run()
    {
        // Create a new webdriver
        WebDriver webDriver = SeleniumUtils.createChromeDriver();

        // Navigate to the url we want to test
        webDriver.get("https://applitools.com/helloworld");

        // Create a runner with concurrency of 10
        VisualGridRunner runner = new VisualGridRunner(10);

        // Create Eyes object with the runner, meaning it'll be a Visual Grid eyes.
        Eyes eyes = new Eyes(runner);

        // Create Configuration.
        Configuration sconf = new Configuration();

        // Set test name
        sconf.setTestName("Visual Grid Demo");

        // Set app name
        sconf.setAppName("Visual Grid Demo");

        // Add browsers
        sconf.addBrowser(800, 600, BrowserType.CHROME);
        sconf.addBrowser(700, 500, BrowserType.CHROME);
        sconf.addBrowser(1200, 800, BrowserType.CHROME);
        sconf.addBrowser(1600, 1200, BrowserType.CHROME);

        // Add iPhone 4 device emulation
        ChromeEmulationInfo iphone4 = new ChromeEmulationInfo(DeviceName.iPhone_4, ScreenOrientation.PORTRAIT);
        sconf.addDeviceEmulation(DeviceName.iPhone_X);

        // Add custom mobile device emulation
//        EmulationDevice customMobile = new EmulationDevice(1024, 768, 2, ScreenOrientation.LANDSCAPE);
//        sconf.addDeviceEmulation(1024, 768, 2);

        // Set the configuration object to eyes
        eyes.setConfiguration(sconf);

        // Call Open on eyes to initialize a test session
        eyes.open(webDriver);

        // Add 2 checks
        eyes.check(Target.window().withName("Step 1 - Viewport"));
        eyes.check(Target.window().fully().withName("Step 1 - Full Page"));

        webDriver.findElement(By.tagName("button")).click();

        // Add 2 checks
        eyes.check(Target.window().withName("Step 2 - Viewport"));
        eyes.check(Target.window().fully().withName("Step 2 - Full Page"));

        // Close the browser
        webDriver.quit();

        // Close eyes and collect results
        TestResults results = eyes.close();
        TestResultsSummary allTestResults = runner.getAllTestResults();
    }
}
