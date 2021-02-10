package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class ScrollRootElementTest extends IOSTestSetup {

    @Test
    public void testScrollRootElementIOS() {
        // Start the test.
        eyes.open(driver, "IOS test application", "IOS scroll root element");

        WebElement showTableWithButtons = driver.findElement(MobileBy.AccessibilityId("Scroll view with nested table"));
        showTableWithButtons.click();

        eyes.check(Target.window().scrollRootElement(MobileBy.xpath("//XCUIElementTypeTable[1]")).fully().withName("Window Fullpage").timeout(0));

        eyes.check(Target.region(MobileBy.xpath("//XCUIElementTypeTable[1]")).scrollRootElement(MobileBy.xpath("//XCUIElementTypeTable[1]")).fully().withName("Region Fullpage").timeout(0));

        // End the test.
        eyes.close();
    }
}
