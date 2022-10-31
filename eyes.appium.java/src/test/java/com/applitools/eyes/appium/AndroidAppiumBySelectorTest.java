package com.applitools.eyes.appium;

import com.applitools.eyes.AccessibilityRegionType;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidAppiumBySelectorTest {
    private static Eyes eyes;
    private static AndroidDriver driver;

    public static final String USERNAME = "applitools-dev";
    public static final String ACCESS_KEY = "7f853c17-24c9-4d8f-a679-9cfde5b43951";
    public static final String URL0 = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";

    @BeforeClass(alwaysRun = true)
    public static void setUpClass() throws MalformedURLException {
        eyes = new Eyes();


        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName","Android");
        caps.setCapability("appium:deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("appium:deviceOrientation", "portrait");
        caps.setCapability("appium:platformVersion","12.0");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("Name", "Android Selectors Test");

        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/AppiumBy-Test-Applications/Android-Selectors-Test-Application.apk");
        caps.setCapability("appPackage", "com.applitools.selectors");
        caps.setCapability("appActivity", "com.applitools.selectors.MainActivity");
        caps.setCapability("newCommandTimeout", 300);
        caps.setCapability("autoGrantPermissions", true);

        driver =  new AndroidDriver(new URL(URL0), caps);

        // Waiting for the application to load.
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownClass() {
        driver.quit();
    }

    @AfterMethod
    public void tearDownTest() {
        eyes.abortIfNotClosed();
    }

    @Test
    public void testCheckRegion() {
        eyes.open(driver, "My Android App", "Android AppiumBy - Check Region Selectors");
        eyes.check("ID", Target.region(AppiumBy.id("id_element")));
        eyes.check("XPATH", Target.region(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]")));
        eyes.check("Accessibility ID", Target.region(AppiumBy.accessibilityId("Accessibility ID - Content Description")));
        eyes.check("Class Name", Target.region(AppiumBy.className("android.widget.ImageView")));

        // WebView related tests
        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1

        try {
//            eyes.check("Link Text", Target.region(AppiumBy.linkText("HTML - Wikipedia")));
            eyes.check("partial link text", Target.region(AppiumBy.partialLinkText("Wikipedia")));
        } finally {
            driver.context("NATIVE_APP");
        }

        eyes.close();
    }

    @Test
    public void testIgnoreRegionSelectors() {
        eyes.open(driver, "My Android App", "Android AppiumBy - Ignore Region Selectors");
        eyes.check("ID", Target.window().ignore(AppiumBy.id("id_element"))
                .ignore(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]"))
                .ignore(AppiumBy.accessibilityId("Accessibility ID - Content Description"))
                .ignore(AppiumBy.className("android.widget.ImageView")));

        // WebView related tests
        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1

        try {
            eyes.check("Link Text", Target.window().ignore(AppiumBy.partialLinkText("Wikipedia"))
                            //.ignore(AppiumBy.linkText("HTML - Wikipedia"))
                    );
        } finally {
            driver.context("NATIVE_APP");
        }
        eyes.close();
    }

    @Test
    public void testLayoutSelectors() {
        eyes.open(driver, "My Android App", "Android AppiumBy - Layout Selectors");
        eyes.check("ID", Target.window().fully(false).layout(AppiumBy.id("id_element"))
                .layout(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]"))
                .layout(AppiumBy.accessibilityId("Accessibility ID - Content Description"))
                .layout(AppiumBy.className("android.widget.ImageView")));

        // WebView related tests
//        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1
//
//        try {
//            eyes.check("Link Text", Target.window().layout(AppiumBy.partialLinkText("Wikipedia"))
//                            //.layout(AppiumBy.linkText("HTML - Wikipedia"))
//                    );
//        } finally {
//            driver.context("NATIVE_APP");
//        }
        eyes.close();
    }

    @Test
    public void testFloatingSelectors() {
        eyes.open(driver, "My Android App", "Android AppiumBy - Floating Selectors");
        eyes.check("ID", Target.window().floating(AppiumBy.id("id_element"), 1, 1,1,1)
                .floating(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]"), 1, 1,1,1)
                .floating(AppiumBy.accessibilityId("Accessibility ID - Content Description"), 1, 1,1,1)
                .floating(AppiumBy.className("android.widget.ImageView"), 1, 1,1,1));

        // WebView related tests
//        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1
//
//        try {
//            eyes.check("Link Text", Target.window().floating(AppiumBy.partialLinkText("Wikipedia"), 1, 1,1,1)
//                    //.floating(AppiumBy.linkText("HTML - Wikipedia"), 1, 1,1,1)
//            );
//        } finally {
//            driver.context("NATIVE_APP");
//        }
        eyes.close();
    }


    @Test
    public void testAccessibilitySelectors() {
        eyes.open(driver, "My Android App", "Android AppiumBy - Accessibility Selectors");
        eyes.check("ID", Target.window().accessibility(AppiumBy.id("id_element"), AccessibilityRegionType.IgnoreContrast)
                .accessibility(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]"), AccessibilityRegionType.IgnoreContrast)
                .accessibility(AppiumBy.accessibilityId("Accessibility ID - Content Description"), AccessibilityRegionType.IgnoreContrast)
                .accessibility(AppiumBy.className("android.widget.ImageView"), AccessibilityRegionType.IgnoreContrast));

        // WebView related tests
//        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1
//
//        try {
//            eyes.check("Link Text", Target.window().floating(AppiumBy.partialLinkText("Wikipedia"), 1, 1,1,1)
//                    //.floating(AppiumBy.linkText("HTML - Wikipedia"), 1, 1,1,1)
//            );
//        } finally {
//            driver.context("NATIVE_APP");
//        }
        eyes.close();
    }

}
