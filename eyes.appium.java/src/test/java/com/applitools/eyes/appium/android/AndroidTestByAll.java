package com.applitools.eyes.appium.android;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.GeneralUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.bys.builder.ByAll;
import io.appium.java_client.pagefactory.bys.builder.ByChained;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidTestByAll {

    final static String USERNAME = GeneralUtils.getEnvString("SAUCE_USERNAME");
    final static String ACCESS_KEY = GeneralUtils.getEnvString("SAUCE_ACCESS_KEY");
    final static String SL_URL = "https://"+USERNAME+":" + ACCESS_KEY + "@ondemand.us-west-1.saucelabs.com:443/wd/hub";

    private AndroidDriver driver;
    private Eyes eyes;

    private final String target = "com.applitools.app_androidx:id/layout";

    @BeforeTest
    public void before() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/1.3.6/app_androidx.apk");
        caps.setCapability("newCommandTimeout", 2000);

        driver = new AndroidDriver(new URL(SL_URL), caps);
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();

        eyes = new Eyes();
        eyes.setApiKey(GeneralUtils.getEnvString("APPLITOOLS_API_KEY"));
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setBatch(new BatchInfo("Android ByAll ByChained"));
    }

    @AfterTest
    public void teardown() {
        if (driver != null)
            driver.quit();

        eyes.abortIfNotClosed();
    }

    @Test
    public void testAppiumByAll() {
        eyes.open(driver,"Android Test","TestAppiumByAll");

        ByAll byAll = new ByAll(
                new By[]{
                        AppiumBy.xpath("some_xpath"),
                        AppiumBy.id(target),
                        AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"some_description\")")
                }
        );

        eyes.check(Target.region(byAll).fully(false));
        eyes.close();
    }

    @Test
    public void testAppiumByAllScrollRootElement() throws InterruptedException {
        driver.navigate().back();
        Thread.sleep(1000);
        eyes.open(driver,"Android Test","TestAppiumByAllScrollRootElement");

        ByAll byAll = new ByAll(
                new By[]{
                        AppiumBy.xpath("some_xpath"),
                        AppiumBy.id("recyclerView"),
                        AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"some_description\")")
                }
        );

        driver.findElement(AppiumBy.id("btn_recycler_view_in_scroll_view_activity")).click();
        Thread.sleep(1000);
        eyes.check(Target.window().scrollRootElement(byAll).fully());
        eyes.close();

        driver.navigate().back();
        Thread.sleep(1000);
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(1000);
    }

    @Test
    public void testAppiumByAllFloatingRegion() {
        eyes.open(driver,"Android Test","TestAppiumByAllFloatingRegion");

        ByAll byAll = new ByAll(
                new By[]{
                        AppiumBy.xpath("some_xpath"),
                        AppiumBy.id(target),
                        AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"some_description\")")
                }
        );

        eyes.check(Target.window().floating(byAll, 5, 10, 15, 20).fully(false));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        FloatingMatchSettings[] floatingRegions = info.getActualAppOutput()[0].getImageMatchSettings().getFloating();
        Assert.assertEquals(floatingRegions[0], new FloatingMatchSettings(96, 66, 200, 300, 5, 10, 15, 20));
    }

    @Test
    public void testAppiumByAllAccessibilityRegion() {
        eyes.open(driver,"Android Test","TestAppiumByAllAccessibilityRegion");

        ByAll byAll = new ByAll(
                new By[]{
                        AppiumBy.xpath("some_xpath"),
                        AppiumBy.id(target),
                        AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"some_description\")")
                }
        );

        eyes.check(Target.window().accessibility(byAll, AccessibilityRegionType.RegularText).fully(false));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        AccessibilityRegionByRectangle[] accessibilityRegion = info.getActualAppOutput()[0].getImageMatchSettings().getAccessibility();
        Assert.assertEquals(accessibilityRegion[0], new AccessibilityRegionByRectangle(96, 66, 200, 300, AccessibilityRegionType.RegularText));
    }

    @Test
    public void testAppiumByAllCodedRegions() {
        eyes.open(driver,"Android Test","TestAppiumByAllCodedRegions");

        ByAll byAll = new ByAll(
                new By[]{
                        AppiumBy.xpath("some_xpath"),
                        AppiumBy.id(target),
                        AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"some_description\")")
                }
        );

        eyes.check(Target.window()
                .ignore(byAll)
                .layout(byAll)
                .content(byAll)
                .strict(byAll)
                .fully(false)
        );

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(96, 66, 200, 300), "ignore");
        Assert.assertEquals(layoutRegion, new Region(96, 66, 200, 300), "layout");
        Assert.assertEquals(contentRegion, new Region(96, 66, 200, 300), "content");
        Assert.assertEquals(strictRegion, new Region(96, 66, 200, 300), "strict");
    }

    @Test
    public void testAppiumByChained() {
        eyes.open(driver,"Android Test","TestAppiumByChained");

        ByChained byChained = new ByChained(
                new By[]{
                        AppiumBy.id("android:id/content"),
                        AppiumBy.id(target),
                }
        );

        eyes.check(Target.region(byChained).fully(false));
        eyes.close();
    }
//
//    @Test
//    public void testAppiumByChainedScrollRootElement() throws InterruptedException {
//        driver.navigate().back();
//        Thread.sleep(1000);
//        eyes.open(driver,"Android Test","TestAppiumByChainedScrollRootElement");
//
//        ByChained byChained = new ByChained(
//                new By[]{
//                        AppiumBy.id("android:id/content"),
//                        AppiumBy.id("com.applitools.app_androidx:id/scrollView"),
//                        AppiumBy.id("recyclerView"),
//                }
//        );
//
//        driver.findElement(AppiumBy.id("btn_recycler_view_in_scroll_view_activity")).click();
//        Thread.sleep(1000);
//        eyes.check(Target.window().scrollRootElement(byChained).fully());
//        eyes.close();
//
//        driver.navigate().back();
//        Thread.sleep(1000);
//        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
//        Thread.sleep(1000);
//    }
//
//    @Test
//    public void testAppiumByChainedFloatingRegion() {
//        eyes.open(driver,"Android Test","TestAppiumByChainedFloatingRegion");
//
//        ByChained byChained = new ByChained(
//                new By[]{
//                        AppiumBy.id("android:id/content"),
//                        AppiumBy.id(target),
//                }
//        );
//
//        eyes.check(Target.window().floating(byChained, 5, 10, 15, 20).fully(false));
//
//        final TestResults result = eyes.close(false);
//        final SessionResults info = getTestInfo(result);
//
//        FloatingMatchSettings[] floatingRegions = info.getActualAppOutput()[0].getImageMatchSettings().getFloating();
//        Assert.assertEquals(floatingRegions[0], new FloatingMatchSettings(96, 66, 200, 300, 5, 10, 15, 20));
//    }
//
//    @Test
//    public void testAppiumByChainedAccessibilityRegion() {
//        eyes.open(driver,"Android Test","TestAppiumByChainedAccessibilityRegion");
//
//        ByChained byChained = new ByChained(
//                new By[]{
//                        AppiumBy.id("android:id/content"),
//                        AppiumBy.id(target),
//                }
//        );
//
//        eyes.check(Target.window().accessibility(byChained, AccessibilityRegionType.RegularText).fully(false));
//
//        final TestResults result = eyes.close(false);
//        final SessionResults info = getTestInfo(result);
//
//        AccessibilityRegionByRectangle[] accessibilityRegion = info.getActualAppOutput()[0].getImageMatchSettings().getAccessibility();
//        Assert.assertEquals(accessibilityRegion[0], new AccessibilityRegionByRectangle(96, 66, 200, 300, AccessibilityRegionType.RegularText));
//    }
//
//    @Test
//    public void testAppiumByChainedCodedRegions() {
//        eyes.open(driver,"Android Test","TestAppiumByChainedCodedRegions");
//
//        ByChained byChained = new ByChained(
//                new By[]{
//                        AppiumBy.id("android:id/content"),
//                        AppiumBy.id(target),
//                }
//        );
//
//        eyes.check(Target.window()
//                .ignore(byChained)
//                .layout(byChained)
//                .content(byChained)
//                .strict(byChained)
//                .fully(true)
//        );
//
//        final TestResults result = eyes.close(false);
//        final SessionResults info = getTestInfo(result);
//
//        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
//        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
//        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
//        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];
//
//        Assert.assertEquals(ignoreRegion, new Region(96, 66, 200, 300), "ignore");
//        Assert.assertEquals(layoutRegion, new Region(96, 66, 200, 300), "layout");
//        Assert.assertEquals(contentRegion, new Region(96, 66, 200, 300), "content");
//        Assert.assertEquals(strictRegion, new Region(96, 66, 200, 300), "strict");
//    }

    private SessionResults getTestInfo(TestResults results) {
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return sessionResults;
    }
}
