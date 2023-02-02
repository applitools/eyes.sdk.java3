package com.applitools.eyes;

import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestExecutionCloudURL {

    Eyes eyes;
    EyesRunner runner;
    WebDriver driver;
    String EG_URL;

    @BeforeClass
    public void setup() {
        EG_URL = Eyes.getExecutionCloudURL();
        System.out.println("EG Client URL: " + EG_URL);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeEach() throws MalformedURLException {
        runner = new VisualGridRunner(5);
        eyes = new Eyes(runner);

        MutableCapabilities caps = new DesiredCapabilities("chrome", "", Platform.ANY);
        driver = new RemoteWebDriver(new URL(EG_URL), caps);

        System.out.println(getClass().getName());
    }

    @AfterMethod(alwaysRun = true)
    public void afterEach(){
        runner.getAllTestResults(false);
        if (driver != null) driver.quit();
        eyes.abort();
    }

    @Test
    public void CheckWindowFullyWithCustomScrollRootWithScrollStitching() {
        driver.get("https://demo.applitools.com");
        eyes.open(driver, "Eyes Selenium SDK", "Test EC Url", new RectangleSize(700, 460));
        eyes.check(Target.window().fully(false));
        eyes.close(true);
    }
}
