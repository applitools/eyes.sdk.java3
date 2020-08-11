package com.applitools.eyes.selenium;

import com.applitools.eyes.selenium.fluent.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestDefaultRootElement extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestDefaultRootElement(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Fluent API", caps, mode);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/TestBodyGreaterThanHtml/";
    }

    @Test
    public void TestCheckElementFullyOnBottomAfterScroll() {
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        WebElement element = getDriver().findElement(By.cssSelector("html > body > div"));
        getEyes().check(Target.region(element).fully());
    }
}
