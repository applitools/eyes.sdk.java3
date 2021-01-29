package com.applitools.eyes.appium.android;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.metadata.ImageMatchSettings;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AndroidAccessibilityTest extends AndroidTestSetup {

    @Test
    public void testAndroidAccessibility() throws Exception {
        eyes.setForceFullPageScreenshot(false);
        eyes.setMatchTimeout(1000);
        eyes.setSaveDebugScreenshots(false);

        Configuration configuration = eyes.getConfiguration();
        configuration.setAppName(getApplicationName());
        configuration.setTestName("Test accessibility");
        configuration.setAccessibilityValidation(new AccessibilitySettings(AccessibilityLevel.AA, AccessibilityGuidelinesVersion.WCAG_2_0));

        eyes.open(driver, configuration);
        driver.findElementById("btn_edit_text").click();
        eyes.check("Launcher screen", Target.window().accessibility(By.xpath("//*[@text='First Edit Text']"), AccessibilityRegionType.GraphicalObject));
        TestResults results = eyes.close(false);

        SessionAccessibilityStatus accessibilityStatus = results.getAccessibilityStatus();
        Assert.assertEquals(accessibilityStatus.getLevel(), AccessibilityLevel.AA);
        Assert.assertEquals(accessibilityStatus.getVersion(), AccessibilityGuidelinesVersion.WCAG_2_0);

        SessionResults sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        com.applitools.eyes.metadata.ImageMatchSettings defaultMatchSettings = sessionResults.getStartInfo().getDefaultMatchSettings();
        Assert.assertEquals(defaultMatchSettings.getAccessibilitySettings().getGuidelinesVersion(), AccessibilityGuidelinesVersion.WCAG_2_0);
        Assert.assertEquals(defaultMatchSettings.getAccessibilitySettings().getLevel(), AccessibilityLevel.AA);

        ImageMatchSettings matchSettings = sessionResults.getActualAppOutput()[0].getImageMatchSettings();
        List<AccessibilityRegionByRectangle> actual = Arrays.asList(matchSettings.getAccessibility());
        Assert.assertEquals(new HashSet<>(actual), new HashSet<>(Collections.singletonList(
                new AccessibilityRegionByRectangle(16, 72, 380, 45, AccessibilityRegionType.GraphicalObject)
        )));

        configuration.setAccessibilityValidation(null);
        eyes.open(driver, configuration);
        eyes.checkWindow("No accessibility");
        results = eyes.close(false);
        Assert.assertNull(results.getAccessibilityStatus());
    }
}
