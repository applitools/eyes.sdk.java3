package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.exceptions.NewTestException;
import com.applitools.eyes.selenium.fluent.Target;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;


@RunWith(JUnit4.class)
public class TestServerStatus {

    private Eyes eyes;
    private WebDriver webDriver;

    @Before
    public void setup() throws MalformedURLException {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setSaveNewTests(false);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");

        DesiredCapabilities caps = DesiredCapabilities.chrome();
        caps.setCapability(ChromeOptions.CAPABILITY, options);

        webDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps);
    }

    private void teardown() {
        try {
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
            webDriver.quit();
        }
    }

    @Test(expected = DiffsFoundException.class)
    public void TestSessionSummary_Status_Failed() throws MalformedURLException {
        WebDriver driver = eyes.open(webDriver,
                TestServerStatus.class.getSimpleName(),
                TestServerStatus.class.getSimpleName(), new RectangleSize(800, 599));

        driver.get("http://applitools.github.io/demo/TestPages/FramesTestPage/");
        eyes.check("TestSessionSummary_Status_Failed", Target.window());
        teardown();
    }

    @Test(expected = NewTestException.class)
    public void TestSessionSummary_Status_New() throws MalformedURLException {
        String uuid = "_" + UUID.randomUUID().toString();

        WebDriver driver = eyes.open(webDriver,
                TestServerStatus.class.getSimpleName() + uuid,
                TestServerStatus.class.getSimpleName() + uuid, new RectangleSize(800, 599));

        driver.get("http://applitools.github.io/demo/TestPages/FramesTestPage/");
        eyes.check("TestSessionSummary_Status_New" + uuid, Target.window());
        teardown();
    }
}
