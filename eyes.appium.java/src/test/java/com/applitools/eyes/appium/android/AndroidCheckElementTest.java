package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.time.Duration;

public class AndroidCheckElementTest extends AndroidTestSetup {

    @Test
    public void testAndroidCheckElement() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        eyes.setMatchTimeout(1000);

        eyes.open(driver, getApplicationName(), "Check element test");

        eyes.check(Target.region(By.id("btn_recycler_view")));

        driver.findElement(By.id("btn_recycler_view")).click();

        eyes.check(Target.region(By.id("recycler_view")));

        eyes.check(Target.region(By.id("recycler_view")).fully());

        eyes.close();
    }
}
