package com.applitools.eyes.appium.android.help_lib;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class LargeRecyclerViewTests extends BaseHelperTest {


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
        super.testScrollViewMonitor("recycler_view",200,0,0);

    }
}
