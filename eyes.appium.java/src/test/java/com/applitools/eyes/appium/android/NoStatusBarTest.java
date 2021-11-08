package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.PointerCancelInteraction;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Collections;

public class NoStatusBarTest extends AndroidTestSetup {

    @Test
    public void testAndroidNoStatusBar() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        eyes.setMatchTimeout(1000);

        // Scroll down

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scrollAction = new Sequence(finger, 1);
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), 5, 1700));
        scrollAction.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scrollAction.addAction(finger.createPointerMove(Duration.ofMillis(1500), PointerInput.Origin.viewport(), 5, 100));
        scrollAction.addAction(new PointerCancelInteraction(finger));
        driver.perform(Collections.singletonList(scrollAction));

        driver.findElement(By.id("btn_no_status_bar")).click();

        eyes.open(driver, getApplicationName(), "Test RecyclerView");

        eyes.checkWindow();

        eyes.close();
    }
}
