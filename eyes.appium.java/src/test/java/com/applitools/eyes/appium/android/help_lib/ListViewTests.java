package com.applitools.eyes.appium.android.help_lib;

import org.testng.annotations.Test;

public class ListViewTests extends BaseHelperTest{
    @Override
    protected MainMenuButton getMainMenuButton() {
        return MainMenuButton.btn_list_view_activity;
    }

    @Test
    public void testScrollViewMonitor() throws Exception {
        super.testScrollViewMonitor("list_view",250,0,0);
    }
}
