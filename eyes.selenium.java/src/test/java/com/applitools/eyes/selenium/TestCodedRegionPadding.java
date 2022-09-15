package com.applitools.eyes.selenium;

import com.applitools.eyes.Padding;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestCodedRegionPadding {

    private Eyes eyes;
    private WebDriver driver;

    @BeforeMethod
    public void setup() {
        eyes = new Eyes();
        driver = new ChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/PaddedBody/region-padding.html");
    }

    @Test
    public void testIgnorePadding() {
        eyes.open(driver, "Test Regions Padding", "TestRegionsPadding", new RectangleSize(1100, 700));
        eyes.check(Target.window().ignore(By.cssSelector("#ignoreRegions"), new Padding(20)).fully());
        eyes.check(Target.window().ignore(By.cssSelector("#ignoreRegions"), 10, 10, 10, 10).fully());
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);
    }

    @Test
    public void testLayoutPadding() {
        eyes.open(driver, "Test Regions Padding", "TestRegionsPadding", new RectangleSize(1100, 700));
//        eyes.check(Target.window().layout(By.cssSelector("#ignoreRegions"), new Padding(20)).fully());
        eyes.check(Target.window().layout(By.cssSelector("#ignoreRegions"), 10, 10, 10, 10).fully());
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);
    }

    @Test
    public void testContentPadding() {
        eyes.open(driver, "Test Regions Padding", "TestRegionsPadding", new RectangleSize(1100, 700));
//        eyes.check(Target.window().content(By.cssSelector("#ignoreRegions"), new Padding(20)).fully());
        eyes.check(Target.window().content(By.cssSelector("#ignoreRegions"), 10, 10, 10, 10).fully());
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);
    }

    @Test
    public void testStrictPadding() {
        eyes.open(driver, "Test Regions Padding", "TestRegionsPadding", new RectangleSize(1100, 700));
//        eyes.check(Target.window().strict(By.cssSelector("#ignoreRegions"), new Padding(20)).fully());
        eyes.check(Target.window().strict(By.cssSelector("#ignoreRegions"), 10, 10, 10, 10).fully());
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);
    }

    private SessionResults getTestInfo(TestResults results) {
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return sessionResults;
    }
}
