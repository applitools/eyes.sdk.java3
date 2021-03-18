package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestFluentApi extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestFluentApi(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Fluent API", caps, mode);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/FramesTestPage/";
    }

    @Test
    public void TestCheckWindowWithIgnoreRegion_Fluent() {
        super.getWebDriver().findElement(By.tagName("input")).sendKeys("My Input");
        getEyes().check("Fluent - Window with Ignore region", Target.window()
                .fully()
                .ignoreCaret()
                .ignore(new Region(50, 50, 100, 100)));

        setExpectedIgnoreRegions(new Region(50, 50, 100, 100));
    }

    @Test
    public void TestCheckRegionWithIgnoreRegion_Fluent() {
        getEyes().check("Fluent - Region with Ignore region", Target.region(By.id("overflowing-div"))
                .ignore(new Region(50, 50, 100, 100)));

        setExpectedIgnoreRegions(new Region(50, 50, 100, 100));
    }

    @Test
    public void TestCheckWindow_Fluent() {
        getEyes().check("Fluent - Window", Target.window());
    }

    @Test
    public void TestCheckWindowWithIgnoreBySelector_Fluent() {
        getEyes().check("Fluent - Window with ignore region by selector", Target.window()
                .ignore(By.id("overflowing-div"))
                .ignore(By.id("overflowing-div"), 10, -10, 20, 50));

        setExpectedIgnoreRegions(new Region(8, 81, 304, 184),
                new Region(-2, 91, 334, 224));
    }

    @Test
    public void TestCheckWindowWithIgnoreBySelector_Centered_Fluent() {
        getEyes().check("Fluent - Window with ignore region by selector centered", Target.window()
                .ignore(By.id("centered")));

        setExpectedIgnoreRegions(new Region(122, 933, 456, 306));
    }

    @Test
    public void TestCheckWindowWithIgnoreBySelector_Stretched_Fluent() {
        getEyes().check("Fluent - Window with ignore region by selector stretched", Target.window()
                .ignore(By.id("stretched")));

        setExpectedIgnoreRegions(new Region(8, 1277, 690, 206));
    }

    @Test
    public void TestCheckWindowWithFloatingBySelector_Fluent() {
        getEyes().check("Fluent - Window with floating region by selector", Target.window()
                .floating(By.id("overflowing-div"), 3, 3, 20, 30));

        setExpectedFloatingRegions(new FloatingMatchSettings(8, 81, 304, 184, 3, 3, 20, 30));
    }

    @Test
    public void TestCheckRegionByCoordinates_Fluent() {
        getEyes().check("Fluent - Region by coordinates", Target.region(new Region(50, 70, 90, 110)));
    }

    @Test
    public void TestCheckOverflowingRegionByCoordinates_Fluent() {
        getEyes().check("Fluent - Region by overflowing coordinates", Target.region(new Region(50, 110, 90, 550)));
    }

    @Test
    public void TestCheckElementWithIgnoreRegionByElementOutsideTheViewport_Fluent() {
        WebElement element = getWebDriver().findElement(By.id("overflowing-div-image"));
        WebElement ignoreElement = getWebDriver().findElement(By.id("overflowing-div"));
        setExpectedIgnoreRegions();
        getEyes().check("Fluent - Region by element", Target.region(element).ignore(ignoreElement));
    }

    @Test
    public void TestCheckElementWithIgnoreRegionBySameElement_Fluent() {
        WebElement element = getWebDriver().findElement(By.id("overflowing-div-image"));
        getEyes().check("Fluent - Region by element", Target.region(element).ignore(element));
        setExpectedIgnoreRegions(new Region(0, 0, 304, 184));
    }

    @Test
    public void TestScrollbarsHiddenAndReturned_Fluent() {
        getEyes().check("Fluent - Window (Before)", Target.window().fully());
        getEyes().check("Fluent - Inner frame div",
                Target.frame("frame1")
                        .region(By.id("inner-frame-div"))
                        .fully());
        getEyes().check("Fluent - Window (After)", Target.window().fully());
    }

    @Test
    public void TestCheckFullWindowWithMultipleIgnoreRegionsBySelector_Fluent() {
        getEyes().check("Fluent - Region by element", Target.window().fully().ignore(By.cssSelector(".ignore")));
        setExpectedIgnoreRegions(
                new Region(8, 1277, 690, 206),
                new Region(122, 933, 456, 306),
                new Region(10, 286, 800, 500)
        );
    }

    @Test
    public void TestCheckMany() {
        getEyes().check(
                Target.region(By.id("overflowing-div-image")).withName("overflowing div image"),
                Target.region(By.id("overflowing-div")).withName("overflowing div"),
                Target.region(By.id("overflowing-div-image")).fully().withName("overflowing div image (fully)"),
                Target.frame("frame1").frame("frame1-1").fully().withName("Full Frame in Frame"),
                Target.frame("frame1").withName("frame1"),
                Target.region(new Region(30, 50, 300, 620)).withName("rectangle")
        );
    }

    @Test
    public void TestCheckScrollableModal() {
        getDriver().findElement(By.id("centered")).click();
        getEyes().check("Scrollable Modal", Target.region(By.id("modal-content")).fully().scrollRootElement(By.id("modal1")));
    }

    @Test
    public void TestCheckWindowWithFloatingByRegion_Fluent() {
        ICheckSettings settings = Target.window()
                .floating(new Region(10, 10, 20, 20), 3, 3, 20, 30);
        getEyes().check("Fluent - Window with floating region by region", settings);

        setExpectedFloatingRegions(new FloatingMatchSettings(10, 10, 20, 20, 3, 3, 20, 30));
    }

    @Test
    public void TestCheckElementFully_Fluent() {
        WebElement element = getWebDriver().findElement(By.id("overflowing-div-image"));
        getEyes().check("Fluent - Region by element - fully", Target.region(element).fully());
    }

    @Test
    public void TestCheckElementFullyAfterScroll() {
        ((JavascriptExecutor)getDriver()).executeScript("window.scrollTo(0, 500)");
        WebElement element = getWebDriver().findElement(By.id("overflowing-div-image"));
        getEyes().check("Fluent - Region by element - fully after scroll", Target.region(element).fully());
    }

    @Test
    public void TestCheckRegionBySelectorAfterManualScroll_Fluent() {
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollBy(0,900)");
        getEyes().check("Fluent - Region by selector after manual scroll", Target.region(By.id("centered")));
    }

    @Test
    public void TestSimpleRegion() {
        getEyes().check(Target.window().region(new Region(50, 50, 100, 100)));
    }


    @Test(dataProvider = "booleanDP", dataProviderClass = TestDataProvider.class)
    public void TestIgnoreDisplacements(boolean ignoreDisplacements) {
        getEyes().check("Fluent - Ignore Displacements = " + ignoreDisplacements, Target.window().ignoreDisplacements(ignoreDisplacements).fully());
        addExpectedProperty("IgnoreDisplacements", ignoreDisplacements);
    }

    @Override
    protected void beforeOpen(Eyes eyes) {
        AccessibilitySettings accessibilitySettings = new AccessibilitySettings(AccessibilityLevel.AAA, AccessibilityGuidelinesVersion.WCAG_2_0);
        eyes.getDefaultMatchSettings().setAccessibilitySettings(accessibilitySettings);
    }
}
