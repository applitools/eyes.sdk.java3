package com.applitools.eyes.e2e;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.playwright.fluent.Target;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.utils.PlaywrightTestSetup;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestLocators extends PlaywrightTestSetup {

    @BeforeClass
    public void setup() {
        initEyes(false, "CSS");
        page = initDriver(true);
        page.navigate("https://applitools.github.io/demo/TestPages/FramesTestPage/");
    }

    @Test
    public void testRegionWithLocator() {
        eyes.open(page, "Eyes Playwright Java", "TestRegionWithLocator", new RectangleSize(1400, 700));
        eyes.check(Target.region(page.locator("[name=\"frame1\"]")));
        eyes.close(true);
    }

    @Test
    public void testCodedRegionsWithLocator() {
        eyes.open(page, "Eyes Playwright Java", "TestCodedRegionsWithLocator", new RectangleSize(1400, 700));
        eyes.check(Target.window()
                .ignore(page.locator("[name=\"frame1\"]"))
                .layout(page.locator("[name=\"frame1\"]"))
                .ignoreColors(page.locator("[name=\"frame1\"]"))
                .strict(page.locator("[name=\"frame1\"]"))
        );
        TestResults results = eyes.close(false);
        SessionResults testInfo = getTestInfo(results);

        Region ignoreRegion = testInfo.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = testInfo.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = testInfo.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = testInfo.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(58, 506, 504, 404), "ignore");
        Assert.assertEquals(layoutRegion, new Region(58, 506, 504, 404), "layout");
        Assert.assertEquals(contentRegion, new Region(58, 506, 504, 404), "content");
        Assert.assertEquals(strictRegion, new Region(58, 506, 504, 404), "strict");
    }

    @Test
    public void testAccessibilityRegionWithLocator() {
        eyes.open(page, "Eyes Playwright Java", "TestAccessibilityRegionWithLocator", new RectangleSize(1400, 700));
        eyes.check(Target.window().accessibility(page.locator("[name=\"frame1\"]"), AccessibilityRegionType.RegularText));
        TestResults results = eyes.close(false);
        SessionResults testInfo = getTestInfo(results);

        AccessibilityRegionByRectangle accessibilityRegion = testInfo.getActualAppOutput()[0].getImageMatchSettings().getAccessibility()[0];

        Assert.assertEquals(accessibilityRegion, new AccessibilityRegionByRectangle(58, 506, 504, 404, AccessibilityRegionType.RegularText), "accessibility");
    }

    @Test
    public void testFrameLocator() {
        eyes.open(page, "Eyes Playwright Java", "TestFrameLocator", new RectangleSize(1400, 700));
        eyes.check(Target.frame(page.locator("[name=\"frame1\"]")));
        eyes.close(true);
    }

    @Test
    public void testScrollRootElementWithLocator() {
        page.navigate("https://applitools.github.io/demo/TestPages/SimpleTestPage/scrollablebody.html");
        eyes.setStitchMode(StitchMode.SCROLL);

        eyes.open(page, "Eyes Playwright Java", "TestScrollRootElementWithLocator", new RectangleSize(1400, 700));
        eyes.check(Target.window().scrollRootElement(page.locator("html")).fully().withName("viewport"));
        eyes.check(Target.window().scrollRootElement(page.locator("body")).fully().withName("full-page"));
        eyes.close(true);
    }
}
