package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.wrappers.EyesRemoteWebElement;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class TestScrolling {

    private WebDriver getSeleniumWebDriver(DesiredCapabilities capabilities) {
        WebDriver driver = null;
        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accessKey", SAUCE_ACCESS_KEY);

        try {
            driver = new RemoteWebDriver(new URL(SAUCE_SELENIUM_URL), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void TestWebAppScrolling(boolean useEmulation) {
        WebDriver driver;
        if (useEmulation) {
            ChromeOptions options = new ChromeOptions();
            Map<String, Object> mobileSettings = new HashMap<>();
            mobileSettings.put("pixelRatio", 4);
            mobileSettings.put("width", 360);
            mobileSettings.put("height", 740);
            mobileSettings.put("userAgent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.137 Mobile Safari/537.36");
            options.setExperimentalOption("mobileEmulation", mobileSettings);
            driver = SeleniumUtils.createChromeDriver(options);
        } else {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("name", "TestWebAppScrolling");
            capabilities.setCapability("deviceName", "Samsung Galaxy S9 WQHD GoogleAPI Emulator");
            capabilities.setCapability("deviceOrientation", "portrait");
            capabilities.setCapability("platformVersion", "8.1");
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("browserName", "Chrome");
            driver = getSeleniumWebDriver(capabilities);
        }

        if (driver == null){
            return;
        }

        driver.get("https://applitools.github.io/demo/TestPages/MobileDemo/adaptive.html");

        Eyes eyes = new Eyes();
        try {
            TestUtils.setupLogging(eyes);
            eyes.setBatch(TestDataProvider.batchInfo);
            EyesWebDriver eyesDriver = (EyesWebDriver) eyes.open(driver, "TestScrolling", "TestWebAppScrolling");

            WebElement contentElement = driver.findElement(By.className("content"));
            EyesRemoteWebElement eyesContentElement = new EyesRemoteWebElement(eyes.getLogger(), eyesDriver, contentElement);
            RectangleSize s = eyesContentElement.getScrollSize();
            List<ICheckSettings> regions = new ArrayList<>();
            for (int i = 0; i < s.getHeight(); i += 6000) {
                int height = Math.min(6000, s.getHeight() - i);
                ICheckSettings region = Target.region(new Region(0, i, s.getWidth(), height))
                        .withName("TestWebAppScrolling").fully().scrollRootElement(contentElement);
                regions.add(region);
            }

            eyes.check(regions.toArray(new ICheckSettings[0]));

            eyes.close();
        } finally {
            eyes.abort();
            driver.quit();
        }
    }

    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void TestWebAppScrolling2(boolean useEmulation) {
        WebDriver driver;
        if (useEmulation) {
            ChromeOptions options = new ChromeOptions();
            Map<String, Object> mobileSettings = new HashMap<>();
            mobileSettings.put("pixelRatio", 4);
            mobileSettings.put("width", 386);
            mobileSettings.put("height", 512);
            mobileSettings.put("userAgent", "Mozilla/5.0 (Linux; Android 7.1.1; Nexus 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            options.setExperimentalOption("mobileEmulation", mobileSettings);
            driver = SeleniumUtils.createChromeDriver(options);
        } else {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("name", "TestWebAppScrolling2");
            capabilities.setCapability("deviceName", "Samsung Galaxy S9 WQHD GoogleAPI Emulator");
            capabilities.setCapability("deviceOrientation", "portrait");
            capabilities.setCapability("platformVersion", "8.1");
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("browserName", "Chrome");
            driver = getSeleniumWebDriver(capabilities);
        }

        if (driver == null) {
            return;
        }

        driver.get("https://applitools.github.io/demo/TestPages/MobileDemo/AccessPayments/");

        Eyes eyes = new Eyes();
        try {
            TestUtils.setupLogging(eyes);
            eyes.setBatch(TestDataProvider.batchInfo);
            eyes.open(driver, "TestScrolling", "TestWebAppScrolling2");
            eyes.setStitchMode(StitchMode.CSS);
            eyes.check("big page on mobile", Target.window().fully());
            eyes.close();
        } finally {
            eyes.abort();
            driver.quit();
        }
    }

    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void TestWebAppScrolling3(boolean useEmulation) {
        WebDriver driver;
        if (useEmulation) {
            ChromeOptions options = new ChromeOptions();
            Map<String, Object> mobileSettings = new HashMap<>();
            mobileSettings.put("pixelRatio", 1);
            mobileSettings.put("width", 386);
            mobileSettings.put("height", 512);
            mobileSettings.put("userAgent", "Mozilla/5.0 (Linux; Android 7.1.1; Nexus 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            options.setExperimentalOption("mobileEmulation", mobileSettings);
            driver = SeleniumUtils.createChromeDriver(options);
        } else {

            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("name", "TestWebAppScrolling3");
            capabilities.setCapability("deviceName", "Samsung Galaxy S9 WQHD GoogleAPI Emulator");
            capabilities.setCapability("deviceOrientation", "portrait");
            capabilities.setCapability("platformVersion", "8.1");
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("browserName", "Chrome");
            driver = getSeleniumWebDriver(capabilities);
        }

        if (driver == null) {
            return;
        }

        driver.get("https://www.applitools.com/customers");

        Eyes eyes = new Eyes();
        try {
            TestUtils.setupLogging(eyes);
            eyes.setBatch(TestDataProvider.batchInfo);
            eyes.open(driver, "TestScrolling", "TestWebAppScrolling3");
            eyes.check("long page on mobile", Target.region(By.cssSelector("div.page")).fully(false).sendDom(false));
            eyes.close();
        } finally {
            eyes.abort();
            driver.quit();
        }
    }
}
