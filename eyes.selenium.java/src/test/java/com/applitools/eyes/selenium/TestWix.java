package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestWix extends TestSetup {
    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestWix(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Test Wix", caps, mode);
        testedPageUrl = "https://eventstest.wixsite.com/events-page-e2e/events/ba837913-7dad-41b9-b530-6c2cbfc4c265";
    }

    @Test
    public void TestMap() {
        getEyes().check("map", Target.frame("TPAMultiSection_j5ocg4p8iframe").region(By.className("_2V52V")));
    }

}
