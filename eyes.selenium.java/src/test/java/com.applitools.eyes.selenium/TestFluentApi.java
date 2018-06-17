package com.applitools.eyes.selenium;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.ICheckSettings;
import com.applitools.eyes.selenium.fluent.Target;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

@Listeners(TestListener.class)
public class TestFluentApi extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestsDataProvider.class)
    public TestFluentApi(Capabilities caps, String platform) {
        super.caps = caps;
        super.platform = platform;
        super.forceFPS = false;

        super.compareExpectedRegions = caps.getBrowserName().equalsIgnoreCase("chrome");
        testSuitName = "Eyes Selenium SDK - Fluent API";
        testedPageUrl = "http://applitools.github.io/demo/TestPages/FramesTestPage/";
    }

    @Test
    public void TestCheckWindowWithIgnoreRegion_Fluent() {
        webDriver.findElement(By.tagName("input")).sendKeys("My Input");
        eyes.check("Fluent - Window with Ignore region", Target.window()
                .fully()
                .timeout(5000)
                .ignore(new Region(50, 50, 100, 100)));

        setExpectedIgnoreRegions(new Region(50, 50, 100, 100));
    }

    @Test
    public void TestCheckRegionWithIgnoreRegion_Fluent() {
        eyes.check("Fluent - Region with Ignore region", Target.region(By.id("overflowing-div"))
                .ignore(new Region(50, 50, 100, 100)));

        setExpectedIgnoreRegions(new Region(50, 50, 100, 100));
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

    @Test
    public void TestScrollbarsHiddenAndReturned_Fluent() {
        eyes.check("Fluent - Window (Before)", Target.window().fully());
        eyes.check("Fluent - Inner frame div",
                Target.frame("frame1")
                        .region(By.id("inner-frame-div"))
                        .fully());
        eyes.check("Fluent - Window (After)", Target.window().fully());
    }

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

    @Test
    public void TestCheckFrameInFrame_Fully_Fluent2() {
        eyes.check("Fluent - Window", Target.window()
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
    public void TestCheckWindowWithFloatingBySelector_Fluent() {
        eyes.check("Fluent - Window with floating region by selector", Target.window()
                .floating(By.id("overflowing-div"), 3, 3, 20, 30));
    }

    @Test
    public void TestCheckWindowWithFloatingByRegion_Fluent() {
        ICheckSettings settings = Target.window()
                .floating(new Region(10, 10, 20, 20), 3, 3, 20, 30);
        eyes.check("Fluent - Window with floating region by region", settings);

        setExpectedFloatingsRegions(new FloatingMatchSettings(10, 10, 20, 20, 3, 3, 20, 30));
    }

    @Test
    public void TestCheckElementFully_Fluent() {
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        eyes.check("Fluent - Region by element - fully", Target.region(element).fully());
    }

    @Test
    public void TestCheckElementWithIgnoreRegionByElementOutsideTheViewport_Fluent() {
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        WebElement ignoreElement = webDriver.findElement(By.id("overflowing-div"));
        setExpectedIgnoreRegions();
        eyes.check("Fluent - Region by element", Target.region(element).ignore(ignoreElement));
    }

    @Test
    public void TestCheckElementWithIgnoreRegionBySameElement_Fluent() {
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        eyes.check("Fluent - Region by element", Target.region(element).ignore(element));
        setExpectedIgnoreRegions(new Region(0, 0, 304, 184));
    }

    @Test
    public void TestCheckFullWindowWithMultipleIgnoreRegionsBySelector_Fluent() {
        eyes.check("Fluent - Region by element", Target.window().fully().ignore(By.cssSelector(".ignore")));
        setExpectedIgnoreRegions(
                new Region(172, 928, 456, 306),
                new Region(8, 1270, 784, 206),
                new Region(10, 284, 302, 182)
        );
    }

    @Test
    public void TestCheckMany() {
        eyes.check(
                Target.region(By.id("overflowing-div-image")).withName("overflowing div image"),
                Target.region(By.id("overflowing-div")).withName("overflowing div"),
                Target.region(By.id("overflowing-div-image")).fully().withName("overflowing div image (fully)"),
                Target.frame("frame1").frame("frame1-1").fully().withName("Full Frame in Frame"),
                Target.frame("frame1").withName("frame1"),
                Target.region(new Region(30, 50, 300, 620)).withName("rectangle")
        );
    }
}
