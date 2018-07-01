package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class OrbisTest extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestsDataProvider.class)
    public OrbisTest(Capabilities caps, String platform) {
        super.caps = caps;
        super.platform = platform;
        super.forceFPS = false;

        testSuitName = "Test Orbis";
        testedPageUrl = "https://www.orbis.com/jp/institutional/about-us/press-room";
        testedPageSize = new RectangleSize(1266, 800);
    }

    @Test
    public void TestOrbis() {
        eyes.check("Orbis Full Window", Target.window().fully());
    }
}
