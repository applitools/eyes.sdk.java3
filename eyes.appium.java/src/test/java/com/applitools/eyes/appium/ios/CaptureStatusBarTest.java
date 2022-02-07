package com.applitools.eyes.appium.ios;

import com.applitools.eyes.Region;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class CaptureStatusBarTest extends IOSTestSetup {

    @Test
    public void testIOSCheckElement() {
        WebElement showScrollView = driver.findElement(MobileBy.AccessibilityId("Scroll view"));
        showScrollView.click();

        eyes.open(driver, getApplicationName(), "captureStatusBar()");

        Region ignoreRegion = new Region(0, 0, 390, 45);

        eyes.check(Target.window().fully(false).captureStatusBar(true).withName("Viewport").ignore(ignoreRegion));

        eyes.check(Target.window().fully(true).captureStatusBar(true).withName("Full page").ignore(ignoreRegion));

        eyes.close();
    }
}
