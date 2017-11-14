package com.applitools;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import javafx.print.PageLayout;
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
import org.openqa.selenium.Platform;
import java.io.IOException;
import java.net.URISyntaxException;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for simple App.
 */
@RunWith(Parameterized.class)
public class ElementsFramesTest {
    private static Eyes eyes;
    private static WebDriver driver;

    @Parameters
    public static Collection<Object[]> data() {

            return Arrays.asList(new Object[][] {
                    { Platform.WIN10, "chrome" },
                    { Platform.WIN10, "firefox" },
                    { Platform.WIN10, "internet explorer" },
                    { Platform.LINUX, "chrome" },
                    { Platform.LINUX, "firefox" },
                    { Platform.MAC, "chrome" },
                    { Platform.MAC, "firefox" },});

    }

    @BeforeClass
    public static void setUp () throws URISyntaxException, java.net.MalformedURLException {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setStitchMode(StitchMode.CSS);
        eyes.setForceFullPageScreenshot(true);
    }


    private static WebDriver createDriver(Platform platform, String browserName) throws java.net.MalformedURLException
    {
        final String SAUCE_ACCESS_KEY = System.getenv("SAUCE_ACCESS_KEY");
        final String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
        final String URL = "https://" + SAUCE_USERNAME + ":" + SAUCE_ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";
        WebDriver driver = null;

        if (URL != null) {
            DesiredCapabilities desired_cap = new DesiredCapabilities();
            desired_cap.setBrowserName(browserName);
            desired_cap.setPlatform(platform);
            if (browserName == "chrome")
            {
                desired_cap.setVersion("48.0");
            }

            DesiredCapabilities caps = DesiredCapabilities.chrome();
            driver = new RemoteWebDriver(new URL(URL), caps);
        }
        else
        {
            ChromeOptions co = new ChromeOptions();
            driver = new ChromeDriver(co);
        }
        return driver;
    }

    @AfterClass
    public static void tearDown () {
        driver.quit();
        eyes.abortIfNotClosed();
    }

    public ElementsFramesTest(Platform platform, String browser)
    {
        try {
            System.out.println("Testing "+ platform + " " + browser);
            driver = createDriver(platform, browser);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

        }
    }

    @org.junit.Test
    public void home1 () throws IOException {
        driver.get("https://astappev.github.io/test-html-pages/");
        eyes.checkWindow("Initial");
        eyes.checkRegion(By.id("overflowing-div"), "Initial", true);
        eyes.checkRegionInFrame("frame1", By.id("inner-frame-div"), "Inner frame div", true);
        eyes.checkRegion(By.id("overflowing-div-image"), "minions", true);
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting( Description description) {
            eyes.open(driver, "Eyes Selenium SDK", description.getMethodName());
        }

        protected void finished(Description description) {
            eyes.close();
        }
    };

}
