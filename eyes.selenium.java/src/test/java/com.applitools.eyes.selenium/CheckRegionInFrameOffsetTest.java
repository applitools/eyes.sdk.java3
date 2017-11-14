package com.applitools.eyes.selenium;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This encapsulates a test for a specific bug which caused a region to be taken incorrectly.
 * The region is inside a {@code iframe} which is inside an absolute positioned {@code div} element.
 */
public class CheckRegionInFrameOffsetTest {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        // Test 1
        WebDriver driver = new ChromeDriver();

        Eyes eyes = new Eyes();

        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        eyes.setForceFullPageScreenshot(true);
        eyes.setStitchMode(StitchMode.CSS);

        LogHandler logHandler;
        //logHandler = new FileLogger("c:\\temp\\logs\\Java\\TestElement.log", true, true);
        logHandler = new StdoutLogHandler(true);
        eyes.setLogHandler(logHandler);

//        eyes.setDebugScreenshotsPath("c:\\temp\\logs");
//        eyes.setSaveDebugScreenshots(true);

        try {
            driver = eyes.open(driver, "Eyes Selenium SDK", "WIX like test",
                    new RectangleSize(1024, 600));

            driver.get("http://applitools.github.io/demo/TestPages/WixLikeTestPage/index.html");

            eyes.check("map", Target.frame("frame1").region(By.tagName("img")));

            eyes.close();

        } finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }
}
