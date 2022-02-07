package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class NoClickWhileScrollingTest extends IOSTestSetup {
    @Test
    public void testNoClickWhileScrolling() {
        // Start the test.
        eyes.open(driver, "IOS test application", "NoClickWhileScrolling");

        WebElement showTableWithButtons = driver.findElement(MobileBy.AccessibilityId("Table view with big buttons"));
        showTableWithButtons.click();

        // Full page screenshot.
        eyes.check(Target.window().fully(true).withName("Window Fullpage"));

        // End the test.
        eyes.close();
    }
}
