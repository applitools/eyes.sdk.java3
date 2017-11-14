package com.applitools.eyes.selenium;

import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.fluent.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RunWith(JUnit4.class)
public abstract class TestFluentApi extends TestSetup {

    @Test
    public void TestCheckWindowWithIgnoreRegion_Fluent() {
        eyes.check("Fluent - Window with Ignore region", Target.window()
                .fully()
                .timeout(5000)
                .ignore(new Region(50, 50, 100, 100)));
    }

    @Test
    public void TestCheckRegionWithIgnoreRegion_Fluent() {
        eyes.check("Fluent - Region with Ignore region", Target.region(By.id("overflowing-div"))
                .ignore(new Region(50, 50, 100, 100)));
    }

    @Test
    public void TestCheckFrame_Fully_Fluent() {
        eyes.check("Fluent - Full Frame", Target.frame("frame1").fully());
    }

    @Test
    public void TestCheckFrame_Fluent() {
        eyes.check("Fluent - Frame", Target.frame("frame1"));
    }

    @Test
    public void TestCheckFrameInFrame_Fully_Fluent() {
        eyes.check("Fluent - Full Frame in Frame", Target.frame("frame1")
                .frame("frame1-1")
                .fully());
    }

    @Test
    public void TestCheckRegionInFrame_Fluent() {
        eyes.check("Fluent - Region in Frame", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully());
    }

    @Test
    public void TestCheckRegionInFrameInFrame_Fluent() {
        eyes.check("Fluent - Region in Frame in Frame", Target.frame("frame1")
                .frame("frame1-1")
                .region(By.tagName("img"))
                .fully());
    }

    /*
    @Test
    public void TestCheckRegionInFrame2_Fluent() {
        eyes.check("Fluent - Inner frame div 1", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully()
                .timeout(5000)
                .ignore(new Region(50, 50, 100, 100)));

        eyes.check("Fluent - Inner frame div 2", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully()
                .ignore(new Region(50, 50, 100, 100))
                .ignore(new Region(70, 170, 90, 90)));

        eyes.check("Fluent - Inner frame div 3", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully()
                .timeout(5000));

        eyes.check("Fluent - Inner frame div 4", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully());

        eyes.check("Fluent - Full frame with floating region", Target.frame("frame1")
                .fully()
                .layout()
                .floating(25, new Region(200, 200, 150, 150)));
    }
    */

    @Test
    public void TestCheckFrameInFrame_Fully_Fluent2() {
        eyes.check("Fluent - Window with Ignore region 2", Target.window()
                .fully()
        );

        eyes.check("Fluent - Full Frame in Frame 2", Target.frame("frame1")
                .frame("frame1-1")
                .fully());
    }

    @Test
    public void TestCheckWindowWithIgnoreBySelector_Fluent() {
        eyes.check("Fluent - Window with ignore region by selector", Target.window()
                .ignore(By.id("overflowing-div")));
    }

    @Test
    public void TestCheckWindowWithFloatingBySelector_Fluent()
    {
        eyes.check("Fluent - Window with floating region by selector", Target.window()
                .floating(By.id("overflowing-div"), 3, 3, 20, 30));
    }

    @Test
    public void TestCheckElementFully_Fluent()
    {
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        eyes.check("Fluent - Region by element - fully", Target.region(element).fully());
    }

    @Test
    public void TestCheckElement_Fluent()
    {
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        eyes.check("Fluent - Region by element", Target.region(element));
    }
}
