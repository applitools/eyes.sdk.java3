package com.applitools.selenium4;

import com.applitools.eyes.selenium.Eyes;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class WrapperWebdriverTests {

    private WebDriver driver;
    private Eyes eyes;

    @BeforeMethod
    public void setup() throws MalformedURLException {
        eyes = new Eyes();
        String url = "https://applitools:zBo67o7BsoKhdkf8Va4u@hub-cloud.browserstack.com/wd/hub";

        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "latest");
        HashMap<String, Object> browserstackOptions = new HashMap<>();
        browserstackOptions.put("idleTimeout", 300);
        capabilities.setCapability("bstack:options", browserstackOptions);

        driver = new RemoteWebDriver(new URL(url), capabilities);
        driver = new Augmenter().augment(driver);
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) driver.quit();
        eyes.abort();
    }

    @Test
    public void should_OpenEyes_WhenWrappedDriver() {
        eyes.open(driver, "appName", "testName");
        eyes.close(false);
    }
}