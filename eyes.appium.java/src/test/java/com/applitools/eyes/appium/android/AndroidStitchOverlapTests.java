package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class AndroidStitchOverlapTests extends AndroidTestSetup {

    @Test
    public void testAndroidXRecyclerView() {
        eyes.setMatchTimeout(1000);

        driver.findElement(By.id("btn_recycler_view_activity")).click();

        eyes.open(driver, getApplicationName(), "Test StitchOverlap");

        eyes.setStitchOverlap(100);

        eyes.check(Target.window().fully().withName("Fullpage"));

        eyes.check(Target.region(By.id("recycler_view")).fully().withName("Region fullpage"));

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
