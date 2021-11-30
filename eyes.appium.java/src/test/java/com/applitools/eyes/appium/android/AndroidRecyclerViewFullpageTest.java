package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.time.Duration;

public class AndroidRecyclerViewFullpageTest extends AndroidTestSetup {

    @Test
    public void testAndroidRecyclerViewFullpage() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        eyes.setMatchTimeout(1000);

        driver.findElement(By.id("btn_recycler_view")).click();

        eyes.open(driver, getApplicationName(), "Test RecyclerView");

        eyes.check(Target.window().withName("Viewport"));

        eyes.check(Target.window().fully().withName("Fullpage"));

        eyes.close();
    }
}
