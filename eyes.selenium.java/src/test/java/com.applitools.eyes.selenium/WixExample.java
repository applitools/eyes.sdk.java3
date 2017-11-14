package com.applitools.eyes.selenium;

import com.applitools.eyes.LogHandler;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URISyntaxException;

public final class WixExample {

    private WixExample() {
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("test-type", "start-maximized", "disable-popup-blocking", "disable-infobars");

        WebDriver driver = new ChromeDriver(options);
        Eyes eyes = new Eyes();
//        eyes.setServerUrl(URI.create("https://localhost.applitools.com"));
        eyes.setMatchTimeout(0);

        LogHandler logHandler = new StdoutLogHandler(true);
        eyes.setLogHandler(logHandler);

        eyes.setDebugScreenshotsPath("c:\\temp\\logs");
        eyes.setSaveDebugScreenshots(true);

        eyes.setForceFullPageScreenshot(false);

        // This is your api key, make sure you use it in all your tests.
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        try {
            WebDriver eyesDriver = eyes.open(driver, "Wix", "Wix Example");

            // Sign in to the page
            eyesDriver.get("https://eventstest.wixsite.com/events-page-e2e/events/ba837913-7dad-41b9-b530-6c2cbfc4c265");
            final String iFrameID = "TPAMultiSection_j5ocg4p8iframe";

            //Switch to frame
            eyesDriver.switchTo().frame(iFrameID);

            //click register button
            eyesDriver.findElement(By.cssSelector("[data-hook=get-tickets-button]")).click();

            //add one ticket
            eyesDriver.findElement(By.cssSelector("[data-hook=plus-button]")).click();

            //just an example, where it make us some problems with scrolling to top of the frame.
            //eyes.checkRegion(By.cssSelector("[data-hook=plus-button]"));
            eyes.check("", Target.region(By.cssSelector("[data-hook=plus-button]")));
            eyes.close();
        } finally {
            // Abort test in case of an unexpected error.
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }
}