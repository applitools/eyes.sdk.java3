package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ScrollViewWithClickableElementTest extends AndroidTestSetup {

    @Test
    public void testScrollViewWithClickableElement() {
        driver.manage().timeouts().implicitlyWait(10_000, TimeUnit.MILLISECONDS);

        eyes.setMatchTimeout(1000);

        eyes.open(driver, getApplicationName(), "Check ScrollView with clickable element");

        // Scroll down
        TouchAction scrollAction = new TouchAction(driver);
        scrollAction.press(new PointOption().withCoordinates(5, 1700)).waitAction(new WaitOptions().withDuration(Duration.ofMillis(1500)));
        scrollAction.moveTo(new PointOption().withCoordinates(5, 100));
        scrollAction.cancel();
        driver.performTouchAction(scrollAction);

        driver.findElementById("btn_clickable_scroll_view").click();

        eyes.check(Target.window().fully().withName("Fully screenshot"));

        eyes.close();
    }
}
