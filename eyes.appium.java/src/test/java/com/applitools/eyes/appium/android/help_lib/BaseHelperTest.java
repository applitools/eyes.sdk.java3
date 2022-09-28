package com.applitools.eyes.appium.android.help_lib;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.junit.Assert;

import java.time.Duration;

public abstract class BaseHelperTest extends HelpLibTestSetup{
    public void testScrollViewMonitor(String scrollableId, int pixelsPerSecond, int initialOffset, int offsetToScroll) throws Exception {
//        int initialOffset = 0;//2620;
//        int offsetToScroll = 1000;

        sendEDTCommand("register_scrollable;" + scrollableId + ";" + initialOffset + ";0;0;0");
        validateNoError();


        Thread.sleep(200);

        for(int i = 0; i < 2;i++) {
            new TouchAction((MobileDriver) driver).press(new PointOption().withCoordinates(500, 1000))
                    .waitAction(new WaitOptions().withDuration(Duration.ofSeconds(calculateMotionSpeed(pixelsPerSecond,500))))
                    .moveTo(new PointOption().withCoordinates(500, 500))
                    .release()
                    .perform();
        }

        Thread.sleep(500);

        for(int i = 0; i < 2;i++) {
            new TouchAction((MobileDriver) driver).press(new PointOption().withCoordinates(500, 500))
                    .waitAction(new WaitOptions().withDuration(Duration.ofSeconds(calculateMotionSpeed(pixelsPerSecond,500))))
                    .moveTo(new PointOption().withCoordinates(500, 1000))
                    .release()
                    .perform();
        }



//        for(int i = 0 ; i < 1; i++)
//            driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().resourceIdMatches(\"com.applitools.app_androidx:id/recycler_view\").scrollable(true)).scrollForward(1000)"));
//        for(int i = 0 ; i < 1; i++)
//            driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().resourceIdMatches(\"com.applitools.app_androidx:id/recycler_view\").scrollable(true)).scrollBackward(1000)"));



//        Thread.sleep(500);
        sendEDTCommand("get_scrolled_offset;" + scrollableId  + ";0;0;0;0");

        String value = getEDTValue();

        sendEDTCommand("unregister_scrollable;" + scrollableId + ";0;0;0;0");
        validateNoError();



        Assert.assertEquals(0,Integer.parseInt(value));

    }
}
