package com.applitools.eyes.appium.android;

import com.applitools.eyes.Region;
import com.applitools.eyes.appium.Target;
import org.testng.annotations.Test;

public class CaptureStatusBarTest extends AndroidTestSetup {

    @Test
    public void testCaptureStatusBar() {
        eyes.open(driver, getApplicationName(), "captureStatusBar()");

        driver.findElementById("btn_scroll_view_footer_header").click();
        Region ignoreRegion = new Region(0, 0, 412, 26);

        eyes.check(Target.window().fully(false).captureStatusBar(true).withName("Viewport").ignore(ignoreRegion));

        eyes.check(Target.window().fully().captureStatusBar(true).withName("Full page").ignore(ignoreRegion));

        eyes.close();
    }
}
