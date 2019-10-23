package com.applitools.eyes.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestDuplicates extends TestSetup {
    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestDuplicates(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Duplicates", caps, mode);
        testedPageUrl = "https://applitools.github.io/demo/TestPages/VisualGridTestPage/duplicates.html";
    }

    @Test
    public void TestDuplicatedIFrames() {
        WebDriver driver = getDriver();
        driver.switchTo().frame(2);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#p2")));
        driver.switchTo().defaultContent();
        getEyes().checkWindow("Duplicated Iframes");
    }
}
