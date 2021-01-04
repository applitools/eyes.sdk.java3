package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AndroidRecyclerViewFullpageTest extends AndroidTestSetup {

    @Test
    public void testAndroidRecyclerViewFullpage() {
        driver.manage().timeouts().implicitlyWait(10_000, TimeUnit.MILLISECONDS);

        eyes.setMatchTimeout(1000);

        driver.findElementById("btn_recycler_view").click();

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        eyes.open(driver, getApplicationName(), "Test RecyclerView");

        eyes.check(Target.window().withName("Viewport"));

        eyes.check(Target.window().fully().withName("Fullpage"));

        eyes.close();
    }
}
