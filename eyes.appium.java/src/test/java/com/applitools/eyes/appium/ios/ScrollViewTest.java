package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class ScrollViewTest extends IOSTestSetup {

    @Test
    public void testScrollView() {
        eyes.setMatchTimeout(1000);

        // Start the test.
        eyes.open(driver, getApplicationName(), "XCUIElementTypeScrollView");

        WebElement showScrollView = driver.findElement(MobileBy.AccessibilityId("Scroll view"));
        showScrollView.click();

        // Check viewport.
        eyes.check(Target.window().fully(false).withName("Window Viewport"));
        // Full page screenshot.
        eyes.check(Target.window().fully(true).withName("Window Fullpage"));
        // Check scroll view's viewport.
        eyes.check(Target.region(MobileBy.xpath("//XCUIElementTypeScrollView[1]")).withName("ScrollView Viewport"));
        // Check full content of scroll view.
        eyes.check(Target.region(MobileBy.xpath("//XCUIElementTypeScrollView[1]")).fully().withName("ScrollView Fullpage"));

        // End the test.
        eyes.close();
    }
}
