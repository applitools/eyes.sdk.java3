package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestSimpleCases extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestSimpleCases(Capabilities options, String mode) {
        super("Eyes Selenium SDK - Simple Test Cases", options, mode);
        testedPageSize = new RectangleSize(1024, 600);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/SimpleTestPage/";
    }

    @Test
    public void TestCheckDivOverflowingThePage() {
        getEyes().check("overflowing div", Target.region(By.id("overflowing-div")).fully());
    }
}
