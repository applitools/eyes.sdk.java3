package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import org.junit.*;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;

public class TestSetup {

    protected static Eyes eyes;
    private static WebDriver driver;
    private static LogHandler logHandler;

    protected static String testSuitName;

    @BeforeClass
    public static void OneTimeSetUp() {

        // Initialize the eyes SDK and set your private API key.
        eyes = new Eyes();
//        eyes.setServerUrl(URI.create("https://localhost.applitools.com"));
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

//        logHandler = new FileLogger("c:\\temp\\logs\\Java\\TestElement.log", true, true);
        logHandler = new StdoutLogHandler(true);
        eyes.setLogHandler(logHandler);
        eyes.setForceFullPageScreenshot(true);
        eyes.setStitchMode(StitchMode.CSS);

        eyes.setHideScrollbars(true);

//        eyes.setDebugScreenshotsPath("c:\\temp\\logs");
//        eyes.setSaveDebugScreenshots(true);
        eyes.setBatch(new BatchInfo(testSuitName));
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("disable-infobars");
            ChromeDriver chromeDriver = new ChromeDriver(options);

            driver = eyes.open(chromeDriver,
                    "Eyes Selenium SDK",
                    description.getMethodName(),
                    new RectangleSize(800, 599)
            );

            driver.navigate().to("http://applitools.github.io/demo/TestPages/FramesTestPage/");
        }

        protected void finished(Description description) {
            try {
                eyes.close();
            } finally {
                eyes.abortIfNotClosed();
                driver.quit();
            }
        }
    };
}
