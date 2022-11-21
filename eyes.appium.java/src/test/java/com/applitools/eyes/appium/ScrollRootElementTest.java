package com.applitools.eyes.appium;

import com.applitools.eyes.appium.android.AndroidTestSetup;
import io.appium.java_client.AppiumBy;
import org.testng.annotations.Test;

public class ScrollRootElementTest extends AndroidTestSetup {

    @Override
    protected String getApplicationName() {
        return "Android Test";
    }

    @Test
    public void testScrollRootElement() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_recycler_view_in_scroll_view_activity")).click();
        Thread.sleep(3000);

        eyes.open(driver,getApplicationName(),"ScrollRootElement test");
        eyes.check(Target.window().scrollRootElement(AppiumBy.id("recyclerView")).fully().timeout(0));
        eyes.close();
    }

}