package com.applitools.eyes;

import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestMatchLevel {

    private Eyes eyes;
    private WebDriver driver;

    @BeforeMethod
    public void setup() {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions().setHeadless(true);
        options.addArguments("--disable-gpu"); // applicable to windows os only
        options.addArguments("--no-sandbox"); // Bypass OS security model
        driver = new ChromeDriver(options);

        driver.get("https://applitools.github.io/demo/TestPages/PaddedBody/region-padding.html");
    }

    @AfterMethod
    public void teardown() {
        driver.quit();
        eyes.abortIfNotClosed();
    }

    @Test
    public void testIgnoreColors() {
        eyes.open(driver, "Test IgnoreColors MatchLevel", "Test IgnoreColors MatchLevel", new RectangleSize(1100, 700));
        eyes.check(Target.window().matchLevel(MatchLevel.IGNORE_COLORS));
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();
        Assert.assertEquals(matchLevel, MatchLevel.CONTENT);
    }

    @Test
    public void testStrict() {
        eyes.open(driver, "Test Strict MatchLevel", "Test Strict MatchLevel", new RectangleSize(1100, 700));
        eyes.check(Target.window());
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();
        Assert.assertEquals(matchLevel, MatchLevel.STRICT);
    }

    @Test
    public void testLayout() {
        eyes.open(driver, "Test Layout MatchLevel", "Test Layout MatchLevel", new RectangleSize(1100, 700));
        eyes.check(Target.window().matchLevel(MatchLevel.LAYOUT));
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();
        Assert.assertEquals(matchLevel, MatchLevel.LAYOUT2);
    }

    @Test
    public void testLayout2() {
        eyes.open(driver, "Test Layout MatchLevel", "Test Layout MatchLevel", new RectangleSize(1100, 700));
        eyes.check(Target.window().matchLevel(MatchLevel.LAYOUT2));
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();
        Assert.assertEquals(matchLevel, MatchLevel.LAYOUT2);
    }

    @Test
    public void testContent() {
        eyes.open(driver, "Test Content MatchLevel", "Test Content MatchLevel", new RectangleSize(1100, 700));
        eyes.check(Target.window().matchLevel(MatchLevel.CONTENT));
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();
        Assert.assertEquals(matchLevel, MatchLevel.CONTENT);
    }

    @Test
    public void testExact() {
        eyes.open(driver, "Test Exact MatchLevel", "Test Exact MatchLevel", new RectangleSize(1100, 700));
        eyes.check(Target.window().matchLevel(MatchLevel.EXACT));
        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();
        Assert.assertEquals(matchLevel, MatchLevel.EXACT);
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