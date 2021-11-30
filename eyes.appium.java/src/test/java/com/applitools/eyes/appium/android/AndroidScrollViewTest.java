package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.PointerCancelInteraction;
import com.applitools.eyes.appium.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;

public class AndroidScrollViewTest extends AndroidTestSetup {

    @Test
    public void testAndroidScrollView() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        eyes.setMatchTimeout(1000);

        eyes.open(driver, getApplicationName(), "Check ScrollView");

        // Scroll down

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scrollAction = new Sequence(finger, 1);
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), 5, 1700));
        scrollAction.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(1500), PointerInput.Origin.viewport(), 5, 100));
        scrollAction.addAction(new PointerCancelInteraction(finger));
        driver.perform(Arrays.asList(scrollAction));

        driver.findElement(By.id("btn_scroll_view_footer_header")).click();

        eyes.check(Target.window().fully().withName("Fullpage"));

        eyes.close();
    }
}
