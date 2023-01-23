package com.applitools.eyes;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.TestSetup;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
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
        runner = new ClassicRunner();
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
        driver.get("https://applitools.github.io/demo/TestPages/SimpleTestPage/scrollablebody.html");
        eyes.open(driver, "Eyes Selenium SDK - Scroll Root Element", "TestCheckWindow_Body_Scroll", new RectangleSize(700, 460));
        eyes.check(Target.window().scrollRootElement(By.cssSelector("body")).fully());
        eyes.close(true);
    }
}
