package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class CollectionViewTest extends IOSTestSetup {

    @Test
    public void testCollectionView() {
        WebElement showTableWithButtons = driver.findElement(MobileBy.AccessibilityId("Collection view"));
        showTableWithButtons.click();

        eyes.open(driver, getApplicationName(), "XCUIElementTypeCollectionView");

        eyes.check(Target.window().fully().withName("Fullpage").timeout(0));

        eyes.close();
    }
}
