package com.applitools.eyes.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class TestSuncorp {

    AndroidDriver nativeDriver;

    @Test
    public void testSuncorp() throws MalformedURLException, InterruptedException {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("app", "/Users/idos/Downloads/MarketPlace_with_helper_1.8.8 (1).apk");
        caps.setCapability("noReset", true);
        caps.setCapability("fullReset", false);
        caps.setCapability("appium:settings[allowInvisibleElements]", true);
        caps.setCapability("appium:automationName", "UiAutomator2");

        nativeDriver = new AndroidDriver(new URL("http://0.0.0.0:4723/wd/hub"), caps);
        Eyes eyes = new Eyes();
        eyes.open(nativeDriver, "Suncorp tests", "test suncorp");

        Thread.sleep(60000);

        List<WebElement> sre = nativeDriver.findElements(AppiumBy.xpath("//*[@scrollable=\"true\"]"));
        eyes.check(Target.window().scrollRootElement(sre.get(0)).fully(true));
        eyes.check(Target.window().scrollRootElement(sre.get(1)).fully(true));
        System.out.println(eyes.close(false));
    }

    @AfterTest
    public void teardown() {
        if (nativeDriver != null) {
            nativeDriver.quit();
        }
    }
}
