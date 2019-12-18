package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public final class TestScrollRootElementInCenter {

       @Test
        public void testCheckScrollRootElement(){
            String TestedPageUrl = "https://applitools.github.io/demo/TestPages/PageWithScrollableArea/index.html";
            WebDriver driver = new ChromeDriver();
            driver.get(TestedPageUrl);
            Eyes eyes = new Eyes();
            eyes.setSendDom(false);
            eyes.setLogHandler(new StdoutLogHandler(true));
            eyes.open(driver, "Eyes Selenium SDK - Scroll Root Element", "check1",new RectangleSize(700, 460));
            eyes.setWaitBeforeScreenshots(1000);
            eyes.setStitchMode(StitchMode.SCROLL);
            eyes.setHideScrollbars(false);
            eyes.check("Scrollable area",
                    Target.region(By.cssSelector("article"))
                            .scrollRootElement(By.cssSelector("div.wrapper"))
                            .fully());
            eyes.close();
            driver.quit();
        }
}