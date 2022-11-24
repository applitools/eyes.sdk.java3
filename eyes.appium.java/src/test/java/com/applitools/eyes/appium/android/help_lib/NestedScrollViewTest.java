package com.applitools.eyes.appium.android.help_lib;

import org.testng.annotations.Test;

public class NestedScrollViewTest extends BaseHelperTest{
    @Override
    protected MainMenuButton getMainMenuButton() {
        return MainMenuButton.btn_nested_scroll_view_activity;
    }

    @Test
    public void testScrollViewMonitor() throws Exception {
        super.testScrollViewMonitor("nested_scroll_view",200,100,500);
    }
}
