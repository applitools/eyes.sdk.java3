package com.applitools.selenium4;

import com.applitools.eyes.selenium.Eyes;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.URL;

public class WrapperWebdriverTests {

    @Test
    public void should_OpenEyes_WhenWrappedDriver() throws Exception {
        Eyes eyes = new Eyes();
        String url = "https://applitools:zBo67o7BsoKhdkf8Va4u@hub-cloud.browserstack.com/wd/hub";
        ImmutableCapabilities capabilities = new ImmutableCapabilities("browserName", "chrome");

        WebDriver driver = new RemoteWebDriver(new URL(url), capabilities);
        driver = new Augmenter().augment(driver);
        eyes.open(driver, "appName", "testName");
        eyes.close(false);
        if (driver != null) driver.quit();
        eyes.abort();
    }
}
