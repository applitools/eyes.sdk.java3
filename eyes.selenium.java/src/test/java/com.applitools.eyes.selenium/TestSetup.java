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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class TestSetup {

    private static LogHandler logHandler;

    protected static Eyes eyes;
    protected static WebDriver driver;
    protected static WebDriver webDriver;

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
            webDriver = new ChromeDriver(options);

            DesiredCapabilities caps = DesiredCapabilities.chrome();
            caps.setCapability(ChromeOptions.CAPABILITY, options);

            try {
                //webDriver = new RemoteWebDriver(new URL("http://192.168.1.11:4444/wd/hub"), caps);

                driver = eyes.open(webDriver,
                        testSuitName,
                        description.getMethodName(),
                        new RectangleSize(800, 599)
                );

                driver.navigate().to("http://applitools.github.io/demo/TestPages/FramesTestPage/");

                //eyes.getPositionProvider().setPosition(new Location(100,200));

            } catch (Exception ex) {

            }
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
