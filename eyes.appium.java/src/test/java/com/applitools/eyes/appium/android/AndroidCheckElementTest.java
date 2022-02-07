package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AndroidCheckElementTest extends AndroidTestSetup {

    @Test
    public void testAndroidCheckElement() {
        driver.manage().timeouts().implicitlyWait(10_000, TimeUnit.MILLISECONDS);
        eyes.setMatchTimeout(1000);

        eyes.open(driver, getApplicationName(), "Check element test");

        eyes.check(Target.region(MobileBy.id("btn_recycler_view")));

        driver.findElementById("btn_recycler_view").click();

        eyes.check(Target.region(MobileBy.id("recycler_view")));

        eyes.check(Target.region(MobileBy.id("recycler_view")).fully());

        eyes.close();
    }
}
