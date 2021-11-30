package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class SamsungCheckTest extends AndroidTestSetup {

    @Override
    public void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("deviceName", "Samsung Galaxy S10");
    }

    @Test
    public void testAndroidAccessibility() {
        Configuration configuration = new Configuration();
        configuration.setAppName(getApplicationName());
        configuration.setTestName("Samsung Test");

        eyes.open(driver, configuration);
        driver.findElement(By.id("btn_fragment_dialog")).click();
        eyes.check(Target.window());

        eyes.close(false);
    }
}
