package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.testng.annotations.Test;

public class RecyclerViewInNestedCollapsingTest extends AndroidTestSetup {

    @Test
    public void testScrollRootElement() throws InterruptedException {
        eyes.open(driver, getApplicationName(), "Check RecyclerView inside NestedScrollView and Collapsing layout");

        driver.findElementById("btn_recycler_view_nested_collapsing").click();

        Thread.sleep(1000);

        eyes.check(Target.window().scrollRootElement(MobileBy.id("recyclerView")).fully().timeout(0));

        eyes.close();
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability("app", "app_androidx");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - AndroidX";
    }
}
