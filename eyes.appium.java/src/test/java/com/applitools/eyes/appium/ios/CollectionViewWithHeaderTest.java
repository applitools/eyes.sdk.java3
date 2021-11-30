package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class CollectionViewWithHeaderTest extends IOSTestSetup {

    @Test
    public void testCollectionViewWithHeader() {
        WebElement showTableWithButtons = driver.findElement(AppiumBy.accessibilityId("Another tab bar - insuarance"));
        showTableWithButtons.click();

        eyes.open(driver, getApplicationName(), "CollectionView with header");

        eyes.check(Target.window().fully().withName("Fullpage").timeout(0));

        eyes.close();
    }
}
