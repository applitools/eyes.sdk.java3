package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ContentInset;
import org.testng.annotations.Test;

public class AndroidContentInsetTest extends AndroidTestSetup {

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
