package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class RecyclerViewInNestedCollapsingTest extends AndroidTestSetup {

    @Test
    public void testScrollRootElement() throws InterruptedException {
        scrollTo(5, 1700, 5, 100);

        driver.findElement(By.id("btn_recycler_view_nested_collapsing")).click();

        eyes.open(driver, getApplicationName(), "Check RecyclerView inside NestedScrollView and Collapsing layout");

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
