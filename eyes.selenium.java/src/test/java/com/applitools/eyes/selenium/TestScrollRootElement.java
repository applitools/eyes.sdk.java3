package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestScrollRootElement extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestScrollRootElement(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Scroll Root Element", caps, mode);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/SimpleTestPage/scrollablebody.html";
    }

    @Test
    public void TestCheckWindow_Body() {
        getEyes().check("Body (" + stitchMode + " stitching)", Target.window().scrollRootElement(By.tagName("body")).fully());
    }

    @Test
    public void TestCheckWindow_Html() {
        getEyes().check("Html (" + stitchMode + "stitching)", Target.window().scrollRootElement(By.tagName("html")).fully());
    }
}
