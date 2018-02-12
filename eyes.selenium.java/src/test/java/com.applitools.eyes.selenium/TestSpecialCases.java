package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

@RunWith(Parameterized.class)
public abstract class TestSpecialCases extends TestSetup {

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
