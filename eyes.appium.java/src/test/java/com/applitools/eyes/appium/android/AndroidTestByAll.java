package com.applitools.eyes.appium.android;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.bys.builder.ByAll;
import io.appium.java_client.pagefactory.bys.builder.ByChained;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class AndroidTestByAll extends AndroidTestSetup {

    private final String target = "com.applitools.app_androidx:id/layout";
    private final Integer TIMEOUT = 5000;

    @BeforeClass
    public void before() throws MalformedURLException {
        eyes.setBatch(new BatchInfo("Android ByAll ByChained"));
    }

    @Test
    public void testAppiumByAll() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

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

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByAllScrollRootElement() throws InterruptedException {
        eyes.open(driver,"Android Test","TestAppiumByAllScrollRootElement");

        ByAll byAll = new ByAll(
                new By[]{
                        AppiumBy.xpath("some_xpath"),
                        AppiumBy.id("recyclerView"),
                        AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"some_description\")")
                }
        );

        driver.findElement(AppiumBy.id("btn_recycler_view_in_scroll_view_activity")).click();
        Thread.sleep(TIMEOUT);
        eyes.check(Target.window().scrollRootElement(byAll).fully());
        eyes.close();

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByAllFloatingRegion() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

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

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByAllAccessibilityRegion() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

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

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByAllCodedRegions() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

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

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByChained() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

        eyes.open(driver,"Android Test","TestAppiumByChained");

        ByChained byChained = new ByChained(
                new By[]{
                        AppiumBy.id("android:id/content"),
                        AppiumBy.id(target),
                }
        );

        eyes.check(Target.region(byChained).fully(false));
        eyes.close();

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByChainedScrollRootElement() throws InterruptedException {
        eyes.open(driver,"Android Test","TestAppiumByChainedScrollRootElement");

        ByChained byChained = new ByChained(
                new By[]{
                        AppiumBy.id("android:id/content"),
                        AppiumBy.id("com.applitools.app_androidx:id/scrollView"),
                        AppiumBy.id("recyclerView"),
                }
        );

        driver.findElement(AppiumBy.id("btn_recycler_view_in_scroll_view_activity")).click();
        Thread.sleep(TIMEOUT);

        eyes.check(Target.window().scrollRootElement(byChained).fully());
        eyes.close();

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByChainedFloatingRegion() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

        eyes.open(driver,"Android Test","TestAppiumByChainedFloatingRegion");

        ByChained byChained = new ByChained(
                new By[]{
                        AppiumBy.id("android:id/content"),
                        AppiumBy.id(target),
                }
        );

        eyes.check(Target.window().floating(byChained, 5, 10, 15, 20).fully(false));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        FloatingMatchSettings[] floatingRegions = info.getActualAppOutput()[0].getImageMatchSettings().getFloating();
        Assert.assertEquals(floatingRegions[0], new FloatingMatchSettings(96, 66, 200, 300, 5, 10, 15, 20));

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByChainedAccessibilityRegion() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

        eyes.open(driver,"Android Test","TestAppiumByChainedAccessibilityRegion");

        ByChained byChained = new ByChained(
                new By[]{
                        AppiumBy.id("android:id/content"),
                        AppiumBy.id(target),
                }
        );

        eyes.check(Target.window().accessibility(byChained, AccessibilityRegionType.RegularText).fully(false));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        AccessibilityRegionByRectangle[] accessibilityRegion = info.getActualAppOutput()[0].getImageMatchSettings().getAccessibility();
        Assert.assertEquals(accessibilityRegion[0], new AccessibilityRegionByRectangle(96, 66, 200, 300, AccessibilityRegionType.RegularText));

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

    @Test
    public void testAppiumByChainedCodedRegions() throws InterruptedException {
        driver.findElement(AppiumBy.id("btn_view_shadow_activity")).click();
        Thread.sleep(TIMEOUT);

        eyes.open(driver,"Android Test","TestAppiumByChainedCodedRegions");

        ByChained byChained = new ByChained(
                new By[]{
                        AppiumBy.id("android:id/content"),
                        AppiumBy.id(target),
                }
        );

        eyes.check(Target.window()
                .ignore(byChained)
                .layout(byChained)
                .content(byChained)
                .strict(byChained)
                .fully(true)
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

        driver.navigate().back();
        Thread.sleep(TIMEOUT);
    }

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
