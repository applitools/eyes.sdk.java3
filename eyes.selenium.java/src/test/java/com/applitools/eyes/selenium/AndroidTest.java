package com.applitools.eyes.selenium;

import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class AndroidTest {
    @DataProvider(parallel = true)
    public static Object[][] data() {
        Object[][] googlePixelPermutations = TestUtils.generatePermutations(
                Arrays.asList(new Object[]{"Google Pixel GoogleAPI Emulator"}), // device
                Arrays.asList(new Object[]{"portrait", "landscape"}), // orientation
                Arrays.asList(new Object[]{"7.1"}), // OS Version
                Arrays.asList(new Object[]{false, true}) // fully
        );

        ArrayList<Object[]> returnValue = new ArrayList<>();
        returnValue.addAll(Arrays.asList(googlePixelPermutations));

        return returnValue.toArray(new Object[0][]);
    }

    @Test(dataProvider = "data")
    public void TestAndroidChromeCrop(String deviceName, String deviceOrientation, String platformVersion, boolean fully) throws MalformedURLException {
        Eyes eyes = new Eyes();

        eyes.setBatch(TestDataProvider.batchInfo);

        // This is your api key, make sure you use it in all your tests.
        DesiredCapabilities caps = DesiredCapabilities.iphone();
        caps.setCapability("appiumVersion", "1.7.2");
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("deviceOrientation", deviceOrientation);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("platformName", "Android");
        caps.setCapability("browserName", "Chrome");

        caps.setCapability("username",  SAUCE_USERNAME);
        caps.setCapability("accesskey", SAUCE_ACCESS_KEY);

        WebDriver driver = new RemoteWebDriver(new URL(SAUCE_SELENIUM_URL), caps);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        eyes.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        String testName = String.format("%s %s %s", deviceName, platformVersion, deviceOrientation);
        if (fully) {
            testName += " fully";
        }

        TestUtils.setupLogging(eyes, testName);

        eyes.setStitchMode(StitchMode.SCROLL);

        eyes.addProperty("Orientation", deviceOrientation);
        eyes.addProperty("Stitched", fully ? "True" : "False");

        try {
            driver.get("https://www.applitools.com/customers");
            eyes.open(driver, "Eyes Selenium SDK - Android Chrome Cropping", testName);
            eyes.check("Initial view", Target.region(By.cssSelector("body")).fully(fully));
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }
}