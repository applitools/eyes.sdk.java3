package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.IOSScrollPositionProvider;
import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CleanCachedDataTest extends IOSTestSetup {

    @Test
    public void testCollectionView() {
        WebElement showTableWithButtons = driver.findElement(AppiumBy.accessibilityId("Collection view"));
        showTableWithButtons.click();

        eyes.open(driver, getApplicationName(), "CleanCachedDataTest");

        eyes.check(Target.window().fully().withName("Fullpage").timeout(0));

        IOSScrollPositionProvider positionProvider = (IOSScrollPositionProvider) eyes.getPositionProvider();
        Assert.assertNull(positionProvider.getFirstVisibleChild());

        eyes.close();
    }
}
