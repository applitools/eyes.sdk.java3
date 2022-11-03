package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestByAll {

    private WebDriver driver;
    private Eyes eyes;

    @BeforeTest
    public void before() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        driver = SeleniumUtils.createChromeDriver(new ChromeOptions().setHeadless(true));
        driver.get("https://applitools.github.io/demo/TestPages/CorsTestPage/index.html");

        eyes = new Eyes();
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setBatch(new BatchInfo("Selenium ByAll ByChained"));
        eyes.setApiKey(GeneralUtils.getEnvString("APPLITOOLS_API_KEY"));
    }

    @AfterTest
    public void teardown() {
        if (driver != null)
            driver.quit();

        eyes.abortIfNotClosed();
    }

    @Test
    public void testSeleniumByAll() {
        eyes.open(driver,"SeleniumByAll","TestSeleniumByAll");

        ByAll byAll = new ByAll(
                By.cssSelector("#overflowing-div-image"),
                By.xpath("//*[@id=\"overflowing-div-image\"]")
        );

        eyes.check(Target.region(byAll));
        eyes.close();
    }

    @Test
    public void testSeleniumByAllScrollRootElement() {
        eyes.open(driver,"SeleniumByAll","TestSeleniumByAllScrollRootElement");

        ByAll byAll = new ByAll(
                By.cssSelector("#overflowing-div-image"),
                By.xpath("//*[@id=\"overflowing-div-image\"]")
        );

        eyes.check(Target.window().scrollRootElement(byAll));
        eyes.close();
    }

    @Test
    public void testSeleniumByAllFloatingRegion() {
        eyes.open(driver,"SeleniumByAll","TestSeleniumByAllFloatingRegion");

        ByAll byAll = new ByAll(
                By.cssSelector("#overflowing-div-image"),
                By.xpath("//*[@id=\"overflowing-div-image\"]")
        );

        eyes.check(Target.window().floating(byAll, 5, 10, 15, 20));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        FloatingMatchSettings[] floatingRegions = info.getActualAppOutput()[0].getImageMatchSettings().getFloating();

        Assert.assertEquals(floatingRegions[0], new FloatingMatchSettings(8, 282, 304, 184, 5, 10, 15, 20));
    }

    @Test
    public void testSeleniumByAllAccessibilityRegion() {
        eyes.open(driver,"SeleniumByAll","TestSeleniumByAllAccessibilityRegion");

        ByAll byAll = new ByAll(
                By.cssSelector("#overflowing-div-image"),
                By.xpath("//*[@id=\"overflowing-div-image\"]")
        );

        eyes.check(Target.window().accessibility(byAll, AccessibilityRegionType.RegularText));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        AccessibilityRegionByRectangle[] accessibilityRegion = info.getActualAppOutput()[0].getImageMatchSettings().getAccessibility();

        Assert.assertEquals(accessibilityRegion[0], new AccessibilityRegionByRectangle(8, 284, 304, 184, AccessibilityRegionType.RegularText));
    }

    @Test
    public void testSeleniumByAllCodedRegions() {
        eyes.open(driver,"SeleniumByAll","TestSeleniumByAllCodedRegions");

        ByAll byAll = new ByAll(
                By.cssSelector("#overflowing-div-image"),
                By.xpath("//*[@id=\"overflowing-div-image\"]")
        );

        eyes.check(Target.window()
                .ignore(byAll)
                .layout(byAll)
                .content(byAll)
                .strict(byAll)
        );

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(8, 282, 304, 184), "ignore");
        Assert.assertEquals(layoutRegion, new Region(8, 282, 304, 184), "layout");
        Assert.assertEquals(contentRegion, new Region(8, 282, 304, 184), "content");
        Assert.assertEquals(strictRegion, new Region(8, 282, 304, 184), "strict");
    }

    @Test
    public void testSeleniumByChained() {
        eyes.open(driver,"SeleniumByChained","TestSeleniumByChained");

        ByChained byChained = new ByChained(
                By.cssSelector("body"),
                By.id("overflowing-div")
        );

        eyes.check(Target.region(byChained));
        eyes.close();
    }

    @Test
    public void testSeleniumByChainedScrollRootElement() {
        eyes.open(driver,"SeleniumByChained","TestSeleniumByChainedScrollRootElement");

        ByChained byChained = new ByChained(
                By.cssSelector("body"),
                By.id("overflowing-div")
        );

        eyes.check(Target.window().scrollRootElement(byChained));
        eyes.close();
    }

    @Test
    public void testSeleniumByChainedFloatingRegion() {
        eyes.open(driver,"SeleniumByChained","TestSeleniumByChainedFloatingRegion");

        ByChained byChained = new ByChained(
                By.cssSelector("body"),
                By.id("overflowing-div")
        );

        eyes.check(Target.window().floating(byChained, 5, 10, 15, 20));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        FloatingMatchSettings[] floatingRegions = info.getActualAppOutput()[0].getImageMatchSettings().getFloating();

        Assert.assertEquals(floatingRegions[0], new FloatingMatchSettings(8, 80, 304, 184, 5, 10, 15, 20));
    }

    @Test
    public void testSeleniumByChainedAccessibilityRegion() {
        eyes.open(driver,"SeleniumByChained","TestSeleniumByChainedAccessibilityRegion");

        ByChained byChained = new ByChained(
                By.cssSelector("body"),
                By.id("overflowing-div")
        );

        eyes.check(Target.window().accessibility(byChained, AccessibilityRegionType.RegularText));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        AccessibilityRegionByRectangle[] accessibilityRegion = info.getActualAppOutput()[0].getImageMatchSettings().getAccessibility();

        Assert.assertEquals(accessibilityRegion[0], new AccessibilityRegionByRectangle(8, 80, 304, 184, AccessibilityRegionType.RegularText));
    }

    @Test
    public void testSeleniumByChainedCodedRegions() {
        eyes.open(driver,"SeleniumByChained","TestSeleniumByChainedCodedRegions");

        ByChained byChained = new ByChained(
                By.cssSelector("body"),
                By.id("overflowing-div")
        );

        eyes.check(Target.window()
                .ignore(byChained)
                .layout(byChained)
                .content(byChained)
                .strict(byChained)
        );

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(8, 80, 304, 184), "ignore");
        Assert.assertEquals(layoutRegion, new Region(8, 80, 304, 184), "layout");
        Assert.assertEquals(contentRegion, new Region(8, 80, 304, 184), "content");
        Assert.assertEquals(strictRegion, new Region(8, 80, 304, 184), "strict");
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
