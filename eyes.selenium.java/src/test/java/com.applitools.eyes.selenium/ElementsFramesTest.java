package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Unit test for simple App.
 */
public class ElementsFramesTest {
    private static Eyes eyes;
    private static WebDriver driver;

    @BeforeClass
    public static void setUp () throws URISyntaxException {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setServerUrl(URI.create("https://localhost.applitools.com"));
        eyes.setStitchMode(StitchMode.CSS);
        eyes.setForceFullPageScreenshot(true);
        //eyes.setScaleMethod(ScaleMethod.SPEED);

        ChromeOptions co = new ChromeOptions();
        co.addArguments("--force-device-scale-factor=1.25");
        driver = new ChromeDriver(co);
    }

    @AfterClass
    public static void tearDown () {
        driver.quit();
        eyes.abortIfNotClosed();
    }

    @org.junit.Test
    public void home1 () throws IOException {
        driver.get("https://astappev.github.io/test-html-pages/");
        eyes.checkWindow("Initial");
        eyes.checkRegion(By.id("overflowing-div"), "Initial", true);
        eyes.checkRegionInFrame("frame1", By.id("inner-frame-div"), "Inner frame div", true);
        eyes.checkRegion(By.id("overflowing-div-image"), "minions", true);
    }

    @org.junit.Test
    public static void playbuzz() {
        driver.get("http://alpha.playbuzz.com/plabuzz1010/automation-test-story-default");
        driver.findElement(By.cssSelector("#pb-story > pb-story > div > div.bottom-share-bar-container.ng-scope.ng-isolate-scope > button")).click();
        eyes.checkRegion(By.cssSelector("body > div.modal.fade.ng-isolate-scope.embed-modal.in > div > div"));
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            eyes.open(driver, "Eyes Selenium SDK", description.getMethodName());
        }

        protected void finished(Description description) {
            eyes.close();
        }
    };

}
