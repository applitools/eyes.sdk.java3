package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class TestPageWithHeader extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestPageWithHeader(Capabilities options, String mode) {
        super("Eyes Selenium SDK - Page With Header", options, mode);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/PageWithHeader/index.html";
    }

    @Test
    public void TestCheckPageWithHeader_Window() {
        getEyes().check("Page with header", Target.window().fully(false));
    }

    @Test
    public void TestCheckPageWithHeader_Window_Fully() {
        getEyes().check("Page with header - fully", Target.window().fully(true));
    }

    @Test
    public void TestCheckPageWithHeader_Region() {
        getEyes().check("Page with header", Target.region(By.cssSelector("div.page")).fully(false));
    }

    @Test
    public void TestCheckPageWithHeader_Region_Fully() {
        getEyes().check("Page with header - fully", Target.region(By.cssSelector("div.page")).fully(true));
    }

}
