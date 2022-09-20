package com.applitools.eyes.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class IgnoreRegionWithByTest {
    public static final String USERNAME = "applitools-dev";
    public static final String ACCESS_KEY = "7f853c17-24c9-4d8f-a679-9cfde5b43951";
    public static final String URL0 = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";

    @Test
    public void test() throws InterruptedException, MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","iPhone XS Simulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion", "15.4");
        caps.setCapability("platformName", "iOS");
        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/eyes-ios-hello-world/1.2/eyes-ios-hello-world.zip");
        caps.setCapability("newCommandTimeout", 600);
        IOSDriver driver = new IOSDriver(new URL(URL0), caps);
        Eyes eyes = new Eyes();
        try {
            driver.findElement(AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton'")).click();
            eyes.open(driver, "Ignore Region", "Appium_Ignore_Region_With_By");
            eyes.check(Target.region(AppiumBy.accessibilityId("BottomContainer")).ignore(AppiumBy.accessibilityId("BottomLabel")).ignore(AppiumBy.accessibilityId("BottomImage")));
            eyes.close(true);
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }
}
