package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.pagefactory.ByChained;
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
    public void testSeleniumByChained() {
        eyes.open(driver,"SeleniumByChained","TestSeleniumByChained");

        ByChained byChained = new ByChained(
                By.cssSelector("body"),
                By.id("overflowing-div")
        );

        eyes.check(Target.region(byChained));
        eyes.close();
    }
}
