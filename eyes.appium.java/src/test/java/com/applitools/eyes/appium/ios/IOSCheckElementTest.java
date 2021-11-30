package com.applitools.eyes.appium.ios;

import com.applitools.eyes.appium.Target;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.Duration;

public class IOSCheckElementTest extends IOSTestSetup {

    @Test
    public void testIOSCheckElement() {
        eyes.setSaveDebugScreenshots(false);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        eyes.open(driver, getApplicationName(), "Check element test");

        WebElement showScrollView = driver.findElement(AppiumBy.accessibilityId("Scroll view"));
        showScrollView.click();

        String xpath = "//XCUIElementTypeScrollView[1]";
        eyes.check(Target.region(By.xpath(xpath)).fully().statusBarExists());

        eyes.close();
    }
}
