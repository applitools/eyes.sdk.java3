package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class ScrollViewWithClickableElementTest extends AndroidTestSetup {

    @Test
    public void testScrollViewWithClickableElement() {
        driver.manage().timeouts().implicitlyWait(10_000, TimeUnit.MILLISECONDS);

        eyes.setMatchTimeout(1000);

        eyes.open(driver, getApplicationName(), "Check ScrollView with clickable element");

        // Scroll down
        scrollTo(5, 1700, 5, 100);

        driver.findElement(By.id("btn_clickable_scroll_view")).click();

        eyes.check(Target.window().fully().withName("Fully screenshot"));

        eyes.close();
    }
}
