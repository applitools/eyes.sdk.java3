package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class AndroidXRecyclerViewTest extends AndroidTestSetup {

    @Test
    public void testAndroidXRecyclerView() {
        eyes.setMatchTimeout(1000);

        driver.findElement(By.id("btn_recycler_view_activity")).click();

        eyes.open(driver, getApplicationName(), "Test RecyclerView");

        eyes.check(Target.window().withName("Viewport"));

        eyes.check(Target.window().fully().scrollRootElement("recycler_view").withName("Fullpage"));

        eyes.check(Target.region(MobileBy.id("recycler_view")).scrollRootElement("recycler_view").withName("Region viewport"));

        eyes.check(Target.region(MobileBy.id("recycler_view")).fully().scrollRootElement("recycler_view").withName("Region fullpage"));

        eyes.close();
    }

    @Override
    protected void setAppCapability() {
        capabilities.setCapability("app", androidXApp());
    }

    @Override
    protected String getApplicationName() {
        return "Java Appium - AndroidX";
    }
}
