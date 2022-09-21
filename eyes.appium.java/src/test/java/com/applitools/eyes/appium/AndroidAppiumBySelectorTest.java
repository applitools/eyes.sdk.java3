package com.applitools.eyes.appium;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.AndroidDeviceInfo;
import com.applitools.eyes.visualgrid.model.AndroidDeviceName;
import com.applitools.eyes.visualgrid.model.DeviceAndroidVersion;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

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
//        caps.setCapability("appium:deviceName","Android GoogleAPI Emulator");
        caps.setCapability("appium:deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("appium:deviceOrientation", "portrait");
        caps.setCapability("appium:platformVersion","12.0");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("Name", "Android Selectors Test");
//        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
//        caps.setCapability("deviceOrientation", "portrait");
//        caps.setCapability("platformVersion","11.0");
//        caps.setCapability("platformName", "Android");
//        caps.setCapability("automationName", "UiAutomator2");

        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/AppiumBy-Test-Applications/Android-Selectors-Test-Application.apk");
        caps.setCapability("appPackage", "com.applitools.selectors");
        caps.setCapability("appActivity", "com.applitools.selectors.MainActivity");
        caps.setCapability("newCommandTimeout", 300);
//        caps.setCapability("fullReset", true);
        caps.setCapability("autoGrantPermissions", true);

//        driver =  new AndroidDriver(new URL("http://localhost:4723/wd/hub"), capabilities);
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
        System.out.println("All selectors test");
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
        System.out.println("All selectors test");
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
    public void testIgnoreLayoutSelectors() {
        System.out.println("All selectors test");
        eyes.open(driver, "My Android App", "Android AppiumBy - Ignore Layout Selectors");
        eyes.check("ID", Target.window().layout(AppiumBy.id("id_element"))
                .layout(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]"))
                .layout(AppiumBy.accessibilityId("Accessibility ID - Content Description"))
                .layout(AppiumBy.className("android.widget.ImageView")));

        // WebView related tests
        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1

        try {
            eyes.check("Link Text", Target.window().layout(AppiumBy.partialLinkText("Wikipedia"))
                            //.layout(AppiumBy.linkText("HTML - Wikipedia"))
                    );
        } finally {
            driver.context("NATIVE_APP");
        }
        eyes.close();
    }

    @Test
    public void testFloatingSelectors() {
        System.out.println("All selectors test");
        eyes.open(driver, "My Android App", "Android AppiumBy - Floating Selectors");
        eyes.check("ID", Target.window().floating(AppiumBy.id("id_element"), 1, 1,1,1)
                .floating(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]"), 1, 1,1,1)
                .floating(AppiumBy.accessibilityId("Accessibility ID - Content Description"), 1, 1,1,1)
                .floating(AppiumBy.className("android.widget.ImageView"), 1, 1,1,1));

        // WebView related tests
        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1

        try {
            eyes.check("Link Text", Target.window().floating(AppiumBy.partialLinkText("Wikipedia"), 1, 1,1,1)
                    //.floating(AppiumBy.linkText("HTML - Wikipedia"), 1, 1,1,1)
            );
        } finally {
            driver.context("NATIVE_APP");
        }
        eyes.close();
    }

//    @Test
//    public void testById() {
//        System.out.println("Id test");
//        eyes.open(driver, "My Android App", "AppiumBy - ById");
//        eyes.check("Running", Target.region(AppiumBy.id("id_element")));
//        eyes.close();
//    }
//
//
//    @Test
//    public void testByXPath() {
//        System.out.println("XPath test");
//        eyes.open(driver, "My Android App", "AppiumBy - ByXPath");
//        eyes.check("Running", Target.region(AppiumBy.xpath("//android.widget.TextView[@text=\"ID element\"]")));
//        eyes.close();
//    }
//
//    @Test
//    public void testByAccessibilityId() {
//        System.out.println("AccessibilityId test");
//        eyes.open(driver, "My Android App", "AppiumBy - ByAccessibilityId");
//        eyes.check("Running", Target.region(AppiumBy.accessibilityId("Accessibility ID - Content Description")));
//        eyes.close();
//    }
//
//    @Test
//    public void testByClassName() {
//        System.out.println("ClassName test");
//        eyes.open(driver, "My Android App", "AppiumBy - ByClassName");
//        eyes.check("Running", Target.region(AppiumBy.className("android.widget.ImageView")));
//        eyes.close();
//    }

// "Name" is not supported for Android https://javadoc.io/static/io.appium/java-client/8.0.0/io/appium/java_client/AppiumBy.html
//    @Test
//    public void testByName() {
//        System.out.println("Name test");
//        eyes.open(driver, "My Android App", "AppiumBy - ById");
//        eyes.check("Running", Target.region(AppiumBy.name("id_element")));
//        eyes.close();
//    }

// Not supported: https://javadoc.io/static/io.appium/java-client/8.0.0/io/appium/java_client/AppiumBy.html
//    @Test
//    public void testByAndroidTagName() {
//        throw UnsupportedOperationException("TagName is not supported in Android");
//        System.out.println("TagName test");
//        eyes.open(driver, "My Android App", "AppiumBy - TagName");
//        eyes.check("Running", Target.region(AppiumBy.tagName("ImageView")));
//        eyes.close();
//            driver.findElement(AppiumBy.tagName("test"));
//    }

//    @Test
//    public void testByAndroidViewTag() {
//        System.err.println("Only supported for Espresso driver");
////        driver.findElement(AppiumBy.androidViewTag("tag_element"));
////        eyes.open(driver, "My Android App", "AppiumBy - ByAndroidViewTag");
////        eyes.check("Running", Target.window());
////        eyes.close()
//    }
//
//
//    @Test
//    public void testByPartialLinkText() {
//        throw new NotImplementedException("This needs to be in a webview");
//        // FIXME: switch to webview context.
////        eyes.open(driver, "My Android App", "AppiumBy - ByLinkText");
////        eyes.check("Running", Target.region(AppiumBy.partialLinkText("AndroidDevelopers")));
////        eyes.close();
//    }

//    @Test
//    public void testByLinkText() {
////        throw new NotImplementedException("This needs to be in a webview");
//        eyes.open(driver, "My Android App", "AppiumBy - ByLinkText");
//        Set<String> contextNames = driver.getContextHandles();
//        for (String contextName : contextNames) {
//            System.out.println(contextName); //prints out something like NATIVE_APP \n WEBVIEW_1
//        }
//        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1
//        eyes.check("Running", Target.region(AppiumBy.partialLinkText("HTML - Wikipedia")));
//        eyes.close();
//    }



//    @Test
//    public void testByImage() {
//        throw new NotImplementedException("Image locator is not supported for now");
//        // FIXME - see installation of opencv4nodejs
////        eyes.open(driver, "My Android App", "AppiumBy - ByImage");
////        eyes.check("Running", Target.region(AppiumBy.image("/Users/kananalizada/Downloads/applitools.png")));
////        eyes.close();
//    }
}
