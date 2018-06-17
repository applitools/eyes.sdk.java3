package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

@Listeners(TestListener.class)
public class TestSpecialCases extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestsDataProvider.class)
    public TestSpecialCases(Capabilities caps, String platform) {
        super.caps = caps;
        super.platform = platform;
        super.forceFPS = false;

        testSuitName = "Eyes Selenium SDK - Special Cases";
        testedPageUrl = "http://applitools.github.io/demo/TestPages/WixLikeTestPage/index.html";
    }

    @Test
    public void TestCheckRegionInAVeryBigFrame() {
        eyes.check("map", Target.frame("frame1").region(By.tagName("img")));
    }

    @Test
    public void TestCheckRegionInAVeryBigFrameAfterManualSwitchToFrame(){
        driver.switchTo().frame("frame1");

        WebElement element = driver.findElement(By.cssSelector("img"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

        eyes.check("", Target.region(By.cssSelector("img")));
    }
}
