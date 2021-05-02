package com.applitools.eyes.selenium;

import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public final class TestScrollRootElementInCenter extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestScrollRootElementInCenter(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Scroll Root Element", caps, mode);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/PageWithScrollableArea/index.html";
    }

    @Test
    public void TestCheckScrollRootElement() {
        getEyes().check("Scrollable area",
                Target.window().scrollRootElement(By.cssSelector("div.wrapper"))
                        .fully().layout(By.cssSelector("body > section > main > div > article > h1")));

        setExpectedLayoutRegions(new Region(29, 52, 448, 24));
    }
}

