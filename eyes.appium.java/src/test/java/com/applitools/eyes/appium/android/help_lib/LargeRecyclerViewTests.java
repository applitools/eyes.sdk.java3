package com.applitools.eyes.appium.android.help_lib;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class LargeRecyclerViewTests extends HelpLibTestSetup {


    @Override
    protected MainMenuButton getMainMenuButton() {
        return MainMenuButton.btn_large_recyclerView_activity;
    }

    @Test
    public void testMoveBy_async() throws Exception {
        int offset = 1 + (int) (Math.random() * 10980);
        sendEDTCommand("moveBy_async;recycler_view;" + offset + ";0;0;1000");
        String value = getEDTValue().split(";", 2)[0];
        Assert.assertEquals(offset,Integer.parseInt(value));

    }

    @Test
    public void testScrollViewMonitor() throws Exception {
        int initialOffset = 0;//2620;
        int offsetToScroll = 1000;

        sendEDTCommand("register_scrollable;recycler_view;" + initialOffset + ";0;0;0");

        Thread.sleep(200);

        for(int i = 0; i < 2;i++) {
            new TouchAction((MobileDriver) driver).press(new PointOption().withCoordinates(500, 1000))
                    .waitAction(new WaitOptions().withDuration(Duration.ofMillis(5000)))
                    .moveTo(new PointOption().withCoordinates(500, 500))
                    .release()
                    .perform();
        }

        Thread.sleep(500);

        for(int i = 0; i < 2;i++) {
            new TouchAction((MobileDriver) driver).press(new PointOption().withCoordinates(500, 500))
                    .waitAction(new WaitOptions().withDuration(Duration.ofMillis(5000)))
                    .moveTo(new PointOption().withCoordinates(500, 1000))
                    .release()
                    .perform();
        }



//        for(int i = 0 ; i < 1; i++)
//            driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().resourceIdMatches(\"com.applitools.app_androidx:id/recycler_view\").scrollable(true)).scrollForward(1000)"));
//        for(int i = 0 ; i < 1; i++)
//            driver.findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().resourceIdMatches(\"com.applitools.app_androidx:id/recycler_view\").scrollable(true)).scrollBackward(1000)"));



//        Thread.sleep(500);
        sendEDTCommand("get_scrolled_offset;recycler_view;0;0;0;0");

        String value = getEDTValue();
        sendEDTCommand("unregister_scrollable;recycler_view;0;0;0;0");



        Assert.assertEquals(0,Integer.parseInt(value));

    }
}
