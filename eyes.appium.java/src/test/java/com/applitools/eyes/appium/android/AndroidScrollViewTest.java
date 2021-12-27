package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AndroidScrollViewTest extends AndroidTestSetup {

    @Test
    public void testAndroidScrollView() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(10_000, TimeUnit.MILLISECONDS);

        eyes.setMatchTimeout(1000);

        eyes.open(driver, getApplicationName(), "Check ScrollView");

        // Scroll down
        scrollTo(5, 1700, 5, 100);

        driver.findElement(By.id("btn_scroll_view_footer_header")).click();

        eyes.check(Target.window().fully().withName("Fullpage"));

        eyes.close();
    }
}
