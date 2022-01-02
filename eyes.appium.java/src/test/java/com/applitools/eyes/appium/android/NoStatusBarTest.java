package com.applitools.eyes.appium.android;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class NoStatusBarTest extends AndroidTestSetup {

    @Test
    public void testAndroidNoStatusBar() {
        driver.manage().timeouts().implicitlyWait(10_000, TimeUnit.MILLISECONDS);

        eyes.setMatchTimeout(1000);

        // Scroll down
        scrollTo(5, 1700, 5, 100);

        driver.findElement(By.id("btn_no_status_bar")).click();

        eyes.open(driver, getApplicationName(), "Test No Status Bar");

        eyes.checkWindow();

        eyes.close();
    }
}
