package com.applitools.eyes.appium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DemoSuncorp {

    private Eyes eyes;
    private EyesRunner runner;
    private IOSDriver driver;

    private final String APPLITOOLS_API_KEY = System.getenv("APPLITOOLS_API_KEY");

    @BeforeTest
    public void setup() throws MalformedURLException {
        DesiredCapabilities caps = setCapabilities();

        // Set NMG capabilities with Applitools API Key. Use default Server URL.
        Eyes.setNMGCapabilities(caps, APPLITOOLS_API_KEY);
        driver = new IOSDriver(new URL("http://0.0.0.0:4723/wd/hub"), caps);

        runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        eyes = new Eyes(runner);

        eyes.setApiKey(APPLITOOLS_API_KEY);
        eyes.setConfiguration(eyes.getConfiguration()
                .addMobileDevice(new IosDeviceInfo(IosDeviceName.iPhone_14))
                .addMobileDevice(new IosDeviceInfo(IosDeviceName.iPhone_12_Pro_Max, ScreenOrientation.LANDSCAPE))
        );

        eyes.open(driver, "Demo", "NMG Options nonNMGCheck");
    }

    @Test
    public void testCameraPrivacy() throws InterruptedException {
        clickOnElementByText("Camera");
        requestSystemAccess();

        eyes.check(Target.window().fully().NMGOptions(new NMGOptions("nonNMGCheck", "addToAllDevices"))
                .withName("test with NMG Options"));

        acceptSystemAlert();
        driver.navigate().back();
    }

    @Test
    public void testCalendarsPrivacy() throws InterruptedException {
        clickOnElementByText("Calendars");
        requestSystemAccess();

        eyes.check(Target.window().fully().NMGOptions(new NMGOptions("nonNMGCheck", "addToAllDevices"))
                .withName("test with NMG Options"));

        acceptSystemAlert();
        driver.navigate().back();
    }

    @AfterTest
    public void teardown() {
        if (driver != null) {

            try {
                acceptSystemAlert();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            driver.quit();
        }

        eyes.closeAsync();
        eyes.abortIfNotClosed();

        TestResultsSummary results = runner.getAllTestResults();
        System.out.println(results);
    }

    private DesiredCapabilities setCapabilities() {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("platformName", "iOS");
        caps.setCapability("platformVersion", "15.4");
        caps.setCapability("deviceName", "iPhone 11 Pro");
        caps.setCapability("automationName", "XCUITest");
        caps.setCapability("app", "/Users/idos/Desktop/Example.app");
        caps.setCapability("newCommandTimeout", 300);

        return caps;
    }

    private void clickOnElementByText(String text) throws InterruptedException {
        driver.findElement(AppiumBy.xpath(String.format("//XCUIElementTypeStaticText[@name=\"%s\"]", text))).click();
        Thread.sleep(1000);
    }

    private void requestSystemAccess() throws InterruptedException {
        driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"Request Access\"]")).click();
        Thread.sleep(1000);
    }

    private void acceptSystemAlert() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            System.err.println("   no alert visible after 10 sec.");
        }
        Thread.sleep(1000);
    }
}
