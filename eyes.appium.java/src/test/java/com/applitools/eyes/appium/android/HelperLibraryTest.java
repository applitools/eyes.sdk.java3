package com.applitools.eyes.appium.android;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HelperLibraryTest extends AndroidTestSetup {

    public static final int PIXEL_5_OFFSET = 13049;

    @Test
    public void testHelperLibOffsetCalculation() throws InterruptedException {
        testOffsetWithRetry();
    }

    private void testOffsetWithRetry() throws InterruptedException {

        for (int i = 0; i < 3; i++) {
            try {
                gestureScrollAndClick();
                Thread.sleep(1000);
                String fieldCommand = "offset_async;recycler_view;0;0;0;5000";

                WebElement edt = null;
                for (int j = 0; j < 3; j++) {
                    try {
                        Thread.sleep(50);
                        edt = driver.findElement(By.xpath("//*[@content-desc=\"EyesAppiumHelperEDT\"]"));
                        break;
                    } catch (Exception e) {
                        if(++j >= 3)
                            throw e;
                    }
                }

                edt.sendKeys(fieldCommand);
                edt.click();

                do { Thread.sleep(1000); }
                while (edt.getText().equals("WAIT"));

                String value = edt.getText().split(";", 2)[0];
                driver.navigate().back();
                Thread.sleep(1000);

                Assert.assertEquals(Integer.parseInt(value), PIXEL_5_OFFSET);
                break;
            } catch (Exception e) {
                System.out.println("test offset retry #" + i);
                if(++i >= 3)
                    throw e;
            }
        }
    }
}
