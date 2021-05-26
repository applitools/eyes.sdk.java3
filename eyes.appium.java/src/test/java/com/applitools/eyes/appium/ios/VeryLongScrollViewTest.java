package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class VeryLongScrollViewTest extends IOSTestSetup {

    @Test
    public void testVeryLongScrollView() {
        WebElement button = driver.findElement(MobileBy.AccessibilityId("Very long ScrollView"));
        button.click();

        eyes.open(driver, "IOS test application", "VeryLongScrollView");
        eyes.check(Target.window().fully(true).withName("Window Fullpage"));
        eyes.close();
    }
}
