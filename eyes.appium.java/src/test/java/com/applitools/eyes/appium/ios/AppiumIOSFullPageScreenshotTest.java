package com.applitools.eyes.appium.ios;

import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class AppiumIOSFullPageScreenshotTest extends IOSTestSetup {

    @Test
    public void testAppiumIOSFullPageScreenshot() {
        eyes.setScrollToRegion(false);
        eyes.setMatchTimeout(1000);
        eyes.setStitchOverlap(44);

        // Start the test.
        eyes.open(driver, getApplicationName(), "Appium Native iOS with Full page screenshot");
        WebElement showTable = driver.findElement(MobileBy.AccessibilityId("Table view"));
        eyes.checkRegion(showTable);
        showTable.click();
        eyes.checkWindow("Big Table");

        // End the test.
        eyes.close();
    }

    @Override
    protected void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("useNewWDA", false);
    }
}
