package com.applitools.eyes.appium.android;

import com.applitools.eyes.appium.Target;
import com.applitools.eyes.appium.android.AndroidTestSetup;
import org.testng.annotations.Test;

public class AndroidStitchOverlapTest extends AndroidTestSetup {

    @Test
    public void testStitchOverlap() {
        eyes.setStitchOverlap(300);

        eyes.open(driver, getApplicationName(),"SetStitchOverlap test");
        eyes.check(Target.window().fully(false));
        eyes.close();
    }

}