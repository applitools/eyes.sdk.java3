package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestSpecialCases extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestSpecialCases(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Special Cases", caps, mode);
        testedPageUrl = "http://applitools.github.io/demo/TestPages/WixLikeTestPage/index.html";
    }

    @Test
    public void TestCheckRegionInAVeryBigFrame() {
        getEyes().check("map", Target.frame("frame1").region(By.tagName("img")));
    }

    @Test
    public void TestCheckRegionInAVeryBigFrameAfterManualSwitchToFrame() {
        getDriver().switchTo().frame("frame1");
        getEyes().check("", Target.region(By.cssSelector("img")));
    }
}
