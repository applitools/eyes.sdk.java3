package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.testng.annotations.Test;

import java.time.Duration;

public class RecyclerViewInNestedCollapsingTest extends AndroidTestSetup {

    @Test
    public void testScrollRootElement() throws InterruptedException {
//        TouchAction scrollAction = new TouchAction(driver);
//        scrollAction.press(new PointOption().withCoordinates(5, 1300)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(1500)));
//        scrollAction.moveTo(new PointOption().withCoordinates(5, 200));
//        scrollAction.cancel();
//        driver.performTouchAction(scrollAction);

        //driver.findElementById("btn_recycler_view_nested_collapsing").click();
        driver.findElement(AppiumBy.id("btn_recycler_view_nested_collapsing")).click();

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
