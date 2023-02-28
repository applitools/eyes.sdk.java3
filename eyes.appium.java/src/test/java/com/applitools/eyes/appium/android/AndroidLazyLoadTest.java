package com.applitools.eyes.appium.android;

import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.appium.Target;
import org.testng.annotations.Test;


public class AndroidLazyLoadTest extends AndroidTestSetup {

    public static final int PIXEL_5_OFFSET = 13049;
    public static final int PIXEL_3XL_OFFSET = 13601;

    @Test
    public void testLazyLoad() {
        testLazyLoadWithRetry();
    }

    private void testLazyLoadWithRetry() {
        for (int j = 0; j < 2; j++) {
            try {
                gestureScrollAndClick();
                eyes.open(driver, getApplicationName(), "Check LazyLoad");
                LazyLoadOptions lazyLoadOptions = new LazyLoadOptions().waitingTime(-PIXEL_5_OFFSET);
                eyes.check(Target.window().fully().lazyLoad(lazyLoadOptions).withName("lazyLoad"));
                eyes.close();
                break;
            } catch (Exception e) {
                driver.navigate().back();
                System.out.println("test lazyLoad retry #" + j);
                if(++j >= 2)
                    throw e;
            }
        }
    }

}
