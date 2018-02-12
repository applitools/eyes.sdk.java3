package com.applitools.eyes.selenium;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;

@RunWith(Parameterized.class)
@Category(CI.class)
public abstract class TestClassicApi extends TestSetup {

    @Test
    public void TestCheckWindow() {
        eyes.checkWindow("Window");
    }

    @Test
    public void TestCheckRegion() {
        eyes.checkRegion(By.id("overflowing-div"), "Region", true);
    }

    @Test
    public void TestCheckFrame() {
        eyes.checkFrame("frame1", "frame1");
    }

    @Test
    public void TestCheckRegionInFrame() {
        eyes.checkRegionInFrame("frame1", By.id("inner-frame-div"), "Inner frame div", true);
    }

    @Test
    public void TestCheckRegion2() {
        eyes.checkRegion(By.id("overflowing-div-image"), "minions", true);
    }
}
