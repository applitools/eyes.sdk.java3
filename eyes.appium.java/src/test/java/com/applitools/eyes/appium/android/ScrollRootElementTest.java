package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.testng.annotations.Test;

public class ScrollRootElementTest extends AndroidTestSetup {

    @Test
    public void testScrollRootElement() throws InterruptedException {
        eyes.open(driver, getApplicationName(), "Check RecyclerView inside ScrollView");

        driver.findElementById("btn_recycler_view_in_scroll_view_activity").click();

        Thread.sleep(1000);

        eyes.check(Target.window().scrollRootElement(MobileBy.id("recyclerView")).fully().timeout(0));

        eyes.check(Target.region(MobileBy.id("recyclerView")).scrollRootElement(MobileBy.id("recyclerView")).fully().timeout(0));

        eyes.close();
    }

    @Override
    protected void setAppCapability() {
        // To run locally use https://applitools.jfrog.io/artifactory/Examples/androidx/1.0.0/app_androidx.apk
        capabilities.setCapability("app", "app_androidx");
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - AndroidX";
    }
}
