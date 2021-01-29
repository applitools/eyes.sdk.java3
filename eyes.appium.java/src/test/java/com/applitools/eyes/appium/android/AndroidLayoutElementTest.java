package com.applitools.eyes.appium.android;

import com.applitools.eyes.AccessibilityRegionType;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileElement;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AndroidLayoutElementTest extends AndroidTestSetup {

    @Test
    public void testAndroidLayoutElement() {
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        // Start visual UI testing
        eyes.open(driver, getApplicationName(), "Test regions with element");

        driver.findElementById("btn_activity_as_dialog").click();

        MobileElement elem = driver.findElementById("btn_open_dialog");
        eyes.check(Target.window().layout(elem).withName("layout()"));

        eyes.check(Target.window().content(elem).withName("content()"));

        eyes.check(Target.window().strict(elem).withName("strict()"));

        eyes.check(Target.window().ignore(elem).withName("ignore()"));

        eyes.check(Target.window().floating(elem, 100, 100, 100, 100).withName("floating()"));

        eyes.check(Target.window().accessibility(elem, AccessibilityRegionType.RegularText).withName("accessibility()"));

        // End visual UI testing. Validate visual correctness.
        eyes.close();
    }
}
