package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ContentInset;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class AndroidContentInsetTest extends AndroidTestSetup {

    @AfterMethod(alwaysRun = true)
    public void after(){
        if (driver != null) driver.quit();
    }

    @Test
    public void testContentInset() {

        Configuration config = eyes.getConfiguration();
        config.setContentInset(new ContentInset(30, 0, 20, 0));
        eyes.setConfiguration(config);

        eyes.open(driver, getApplicationName(), "Check ContentInset");
        eyes.check(Target.window().fully(false));
        eyes.close();
    }
}
