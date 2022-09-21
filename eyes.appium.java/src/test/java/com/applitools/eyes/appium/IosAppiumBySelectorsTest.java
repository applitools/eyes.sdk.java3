package com.applitools.eyes.appium;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class IosAppiumBySelectorsTest {
    private static Eyes eyes;
    private static IOSDriver driver;

    public static final String USERNAME = "applitools-dev";
    public static final String ACCESS_KEY = "7f853c17-24c9-4d8f-a679-9cfde5b43951";
    public static final String URL0 = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";


    @BeforeClass(alwaysRun = true)
    public static void setUpClass() throws MalformedURLException {
        eyes = new Eyes();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("platformVersion", "15.4");
        capabilities.setCapability("newCommandTimeout", 600);
        capabilities.setCapability("deviceName", "iPhone 8 Simulator");
        capabilities.setCapability("automationName", "XCUITest");
        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/AppiumBy-Test-Applications/Ios-Selectors-Test-Application.zip");
        capabilities.setCapability("noReset", false);

        driver =  new IOSDriver(new URL(URL0), capabilities);

        // Waiting for the application to load.
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDownTest() {
        eyes.abortIfNotClosed();
    }

    @AfterClass
    public static void tearDownClass() {
        driver.quit();
    }

    @Test
    public void testCheckRegion() {
        System.out.println("All selectors test");
        eyes.open(driver, "My iOS App", "iOS AppiumBy - Check Region Selectors");
        eyes.check("ID", Target.region(AppiumBy.id("Collection view")));
        eyes.check("XPATH", Target.region(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"Collection view\"]")));
        eyes.check("Accessibility ID", Target.region(AppiumBy.accessibilityId("Collection view")));
        eyes.check("Class Name", Target.region(AppiumBy.className("XCUIElementTypeButton")));
        eyes.check("Name", Target.region(AppiumBy.name("Collection view")));
        eyes.check("NS Predicate", Target.region(AppiumBy.iOSNsPredicateString("wdVisible==1")));
        eyes.check("ClassChain", Target.region(AppiumBy.iOSClassChain("**/XCUIElementTypeStaticText[`label == \"Collection view\"`]")));


        // WebView related tests
//        driver.context("WEBVIEW_com.applitools.selectors"); // set context to WEBVIEW_1
//
//        try {
////            eyes.check("Link Text", Target.region(AppiumBy.linkText("HTML - Wikipedia")));
//            eyes.check("partial link text", Target.region(AppiumBy.partialLinkText("Wikipedia")));
//        } finally {
//            driver.context("NATIVE_APP");
//        }

        eyes.close();
    }


    @Test
    public void testFloatingSelectors() {
        System.out.println("All selectors test");
        eyes.open(driver, "My Android App", "Android AppiumBy - Floating Selectors");
        eyes.check("ID", Target.window().floating(AppiumBy.id("Collection view"), 1, 1,1,1)
                .floating(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"Collection view\"]"), 1, 1,1,1)
                .floating(AppiumBy.accessibilityId("Collection view"), 1, 1,1,1)
                .floating(AppiumBy.className("XCUIElementTypeButton"), 1, 1,1,1)
                .floating(AppiumBy.iOSNsPredicateString("wdVisible==1"), 1, 1,1,1)
                .floating(AppiumBy.iOSClassChain("**/XCUIElementTypeStaticText[`label == \"Collection view\"`]"), 1, 1,1,1)
                .floating(AppiumBy.name("Collection view"), 1, 1,1,1));

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

//    @Test
//    public void byId() {
//        driver.findElement(AppiumBy.id("Collection view")).click();
//    }
//
//    @Test
//    public void byXPath() {
//        driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"Collection view\"]")).click();
//    }
//
//    @Test
//    public void byLinkText() {
//        //driver.findElement(AppiumBy.linkText("")).click();
//        // TODO Locator Strategy 'link text' is not supported for this session
//    }
//
//    @Test
//    public void byPartialLinkText() {
//        //driver.findElement(AppiumBy.partialLinkText("name=Collect")).click();
//        // TODO Locator Strategy 'partial link text' is not supported for this session
//    }
//
//    @Test
//    public void byName() {
//        driver.findElement(AppiumBy.name("Collection view")).click();
//    }
//
//    @Test
//    public void byTagName() {
//        //driver.findElement(AppiumBy.tagName("Collection view"));
//        //  TODO Locator Strategy 'tag name' is not supported for this session
//    }
//
//
//    @Test
//    public void byClassName() {
//        driver.findElement(AppiumBy.className("XCUIElementTypeButton")).click();
//    }
//
//    @Test
//    public void byCssSelector() {
//        //driver.findElement(AppiumBy.cssSelector("")).click(); // TODO
//    }
//
//    @Test
//    public void byAccessibilityId() {
//        driver.findElement(AppiumBy.accessibilityId("Collection view")).click();
//    }
//
//    @Test
//    public void byPredicateString() {
//        driver.findElement(AppiumBy.iOSNsPredicateString("wdVisible==1"));
//    }
//
//    @Test
//    public void byClassChain() {
//        driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeStaticText[`label == \"Collection view\"`]")).click();
//    }


}
