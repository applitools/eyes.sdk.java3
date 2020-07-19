package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class TableViewTest extends IOSTestSetup {

    @Test
    public void testTableView() {
        eyes.setMatchTimeout(1000);

        // Start the test.
        eyes.open(driver, getApplicationName(), "XCUIElementTypeTable");

        WebElement showTable = driver.findElement(MobileBy.AccessibilityId("Table view"));
        showTable.click();

        // Check viewport.
        eyes.check(Target.window().fully(false).withName("Window Viewport"));
        // Full page screenshot.
        eyes.check(Target.window().fully(true).withName("Window Fullpage"));
        // Check table view's viewport.
        eyes.check(Target.region(MobileBy.xpath("//XCUIElementTypeTable[1]")).withName("Table Viewport"));
        // Check full content of table view.
        eyes.check(Target.region(MobileBy.xpath("//XCUIElementTypeTable[1]")).fully().withName("Table Fullpage"));

        // End the test.
        eyes.close();
    }
}
