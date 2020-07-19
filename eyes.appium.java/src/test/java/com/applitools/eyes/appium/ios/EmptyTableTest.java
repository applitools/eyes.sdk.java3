package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class EmptyTableTest extends IOSTestSetup {

    @Test
    public void testEmptyTable() {
        eyes.setMatchTimeout(1000);

        // Start the test.
        eyes.open(driver, getApplicationName(), "Empty XCUIElementTypeTable");

        WebElement showTable = driver.findElement(MobileBy.AccessibilityId("Empty table view"));
        showTable.click();

        eyes.check(Target.window().fully().withName("Fullpage"));

        // End the test.
        eyes.close();
    }
}
