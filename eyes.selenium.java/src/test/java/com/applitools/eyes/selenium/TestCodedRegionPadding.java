package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class TestCodedRegionPadding extends ReportingTestSuite {

    private Eyes eyes;
    private WebDriver driver;

    @BeforeMethod
    public void setup() {
        super.setGroupName("selenium");
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        driver = SeleniumUtils.createChromeDriver(new ChromeOptions().setHeadless(true));

        driver.get("https://applitools.github.io/demo/TestPages/PaddedBody/region-padding.html");
    }

    @AfterMethod
    public void teardown() {
        if (driver != null)
            driver.quit();
        eyes.abortIfNotClosed();
    }

    @Test
    public void testRegionPaddingNew() {
        eyes.open(driver, "Test Regions Padding", "Test Regions Padding", new RectangleSize(1100, 700));
        eyes.check(Target.window()
                .ignore(By.cssSelector("#ignoreRegions"), new Padding(20))
                .layout(By.cssSelector("#layoutRegions"), new Padding().setTop(20).setRight(20))
                .content(By.cssSelector("#contentRegions"), new Padding().setLeft(20).setRight(20))
                .strict(By.cssSelector("#strictRegions"), new Padding().setBottom(20))
                .fully());
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(131, 88, 838, 110), "ignore");
        Assert.assertEquals(layoutRegion, new Region(151, 238, 818, 90), "layout");
        Assert.assertEquals(contentRegion, new Region(131, 408, 838, 70), "content");
        Assert.assertEquals(strictRegion, new Region(151, 558, 798, 548), "strict");
    }

    @Test
    public void testRegionPaddingLegacy() {
        eyes.open(driver, "Test Regions Padding", "Test Regions Padding Legacy", new RectangleSize(1100, 700));
        eyes.check(Target.window()
                .ignore(By.cssSelector("#ignoreRegions"), 20, 20, 20, 20)
                .layout(By.cssSelector("#layoutRegions"), 0, 20, 20, 0)
                .content(By.cssSelector("#contentRegions"), 20, 0, 20, 0)
                .strict(By.cssSelector("#strictRegions"), 0, 0, 0, 20)
                .fully());
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(131, 88, 838, 110), "ignore");
        Assert.assertEquals(layoutRegion, new Region(151, 238, 818, 90), "layout");
        Assert.assertEquals(contentRegion, new Region(131, 408, 838, 70), "content");
        Assert.assertEquals(strictRegion, new Region(151, 558, 798, 548), "strict");
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

