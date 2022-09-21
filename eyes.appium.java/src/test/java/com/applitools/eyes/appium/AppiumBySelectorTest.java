package com.applitools.eyes.appium;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.AndroidDeviceInfo;
import com.applitools.eyes.visualgrid.model.AndroidDeviceName;
import com.applitools.eyes.visualgrid.model.DeviceAndroidVersion;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class AppiumBySelectorTest {
    private Eyes eyes;
    private AndroidDriver driver;
    private VisualGridRunner visualGridRunner;

    @BeforeMethod(alwaysRun = true)
    public void before() throws MalformedURLException {
        visualGridRunner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        eyes = new Eyes(visualGridRunner);
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setServerUrl("https://eyesapi.applitools.com");

        // Configure grid devices to render on
        Configuration configuration = eyes.getConfiguration();
        configuration.addMobileDevice(new AndroidDeviceInfo(AndroidDeviceName.Pixel_4_XL, DeviceAndroidVersion.LATEST));

        eyes.setConfiguration(configuration);

        // Create an AndroidDriver instance to automate your app
        driver = startApp();
    }

    @Test
    public void testById() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ById");
//            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.id("id_element"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByXPath() {
        try {
            // Visually validate the stickers dialog
//            eyes.open(driver, "My Android App", "AppiumBy - ByXPath");
//            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByLinkText() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ByLinkText");
//            eyes.check("Running", Target.window());
              WebElement webElement =  driver.findElement(AppiumBy.linkText("https://developer.android.com/"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByAccessibilityId() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ByAccessibilityId");
//            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.accessibilityId("Accessibility ID - Content Description"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByAndroidViewTag() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ByAndroidViewTag");
//            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.androidViewTag("tag_element"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByClassName() {
        try {
            eyes.open(driver, "My Android App", "AppiumBy - ByClassName");
            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.className("android.widget.ImageView"));
            waitFor();
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByPartialLinkText() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ByLinkText");
//            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.partialLinkText("AndroidDevelopers"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByName() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ById");
//            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.name("id_element"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByAndroidTagName() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ByAndroidViewTag");
//            eyes.check("Running", Target.window());
            driver.findElement(AppiumBy.tagName("test"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    @Test
    public void testByImage() {
        try {
//            eyes.open(driver, "My Android App", "AppiumBy - ByLinkText");
//            eyes.check("Running", Target.window());
            //WebElement webElement =  driver.findElement(AppiumBy.linkText("https://developer.android.com/"));
            driver.findElement(AppiumBy.image("/Users/kananalizada/Downloads/applitools.png"));
//            waitFor();
//            eyes.closeAsync();
        } finally {
            driver.quit();
//            eyes.abortAsync();
//            visualGridRunner.getAllTestResults();
        }
    }

    private static AndroidDriver startApp() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "Pixel 4");
        capabilities.setCapability("app", "/Users/kananalizada/Downloads/AndroidSelectors.apk");
        capabilities.setCapability("appPackage", "com.applitools.selectors");
        capabilities.setCapability("appActivity", "com.applitools.selectors.MainActivity");
        capabilities.setCapability("platformVersion", "11");
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("newCommandTimeout", 300);
        capabilities.setCapability("fullReset", true);
        capabilities.setCapability("autoGrantPermissions", true);
        return new AndroidDriver(new URL("http://localhost:4723/wd/hub"), capabilities);
    }

    private static void waitFor() {
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
