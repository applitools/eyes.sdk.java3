package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.metadata.ImageMatchSettings;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TestAccessibility extends ReportingTestSuite {
    public TestAccessibility(){
        super.setGroupName("selenium");
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.TRUE, Boolean.FALSE};
    }

    @Test(dataProvider = "booleanDP")
    public void testAccessibility(boolean useVisualGrid) throws IOException {
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        runner.setLogHandler(new StdoutLogHandler());
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        AccessibilitySettings settings = new AccessibilitySettings(AccessibilityLevel.AA, AccessibilityGuidelinesVersion.WCAG_2_0);
        Configuration configuration = eyes.getConfiguration();
        configuration.setAccessibilityValidation(settings);
        eyes.setConfiguration(configuration);
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
            eyes.open(driver, "Applitools Eyes SDK", "TestAccessibility_Sanity" + suffix, new RectangleSize(700, 460));
            eyes.check("Sanity", Target.window().accessibility(By.className("ignore"), AccessibilityRegionType.LargeText));
            eyes.closeAsync();
            configuration.setAccessibilityValidation(null);
            eyes.setConfiguration(configuration);
            eyes.open(driver, "Applitools Eyes SDK", "TestAccessibility_No_Accessibility" + suffix, new RectangleSize(1200, 800));
            eyes.checkWindow("No accessibility");
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortAsync();
            TestResultsSummary allTestResults = runner.getAllTestResults();

            Assert.assertEquals(2, allTestResults.getAllResults().length);
            TestResults resultSanity = allTestResults.getAllResults()[0].getTestResults();
            TestResults resultNoAccessibility = allTestResults.getAllResults()[1].getTestResults();
            // Visual grid runner doesn't guarantee the order of the results
            if (allTestResults.getAllResults()[1].getTestResults().getName().startsWith("TestAccessibility_Sanity")) {
                TestResults temp = resultSanity;
                resultSanity = resultNoAccessibility;
                resultNoAccessibility = temp;
            }

            // Testing the accessibility status returned in the results
            SessionAccessibilityStatus accessibilityStatus = resultSanity.getAccessibilityStatus();
            Assert.assertEquals(accessibilityStatus.getLevel(), AccessibilityLevel.AA);
            Assert.assertEquals(accessibilityStatus.getVersion(), AccessibilityGuidelinesVersion.WCAG_2_0);
            Assert.assertNull(resultNoAccessibility.getAccessibilityStatus());

            // Testing the accessibility settings sent in the start info
            SessionResults sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), resultSanity);
            ImageMatchSettings defaultMatchSettings = sessionResults.getStartInfo().getDefaultMatchSettings();
            Assert.assertEquals(defaultMatchSettings.getAccessibilitySettings().getGuidelinesVersion(), AccessibilityGuidelinesVersion.WCAG_2_0);
            Assert.assertEquals(defaultMatchSettings.getAccessibilitySettings().getLevel(), AccessibilityLevel.AA);

            // Testing the accessibility regions sent in the session
            ImageMatchSettings matchSettings = sessionResults.getActualAppOutput()[0].getImageMatchSettings();
            List<AccessibilityRegionByRectangle> actual = Arrays.asList(matchSettings.getAccessibility());
            Assert.assertEquals(new HashSet<>(actual), new HashSet<>(Arrays.asList(
                    new AccessibilityRegionByRectangle(122, 933, 456, 306, AccessibilityRegionType.LargeText),
                    new AccessibilityRegionByRectangle(8, 1277, 690, 206, AccessibilityRegionType.LargeText),
                    new AccessibilityRegionByRectangle(10, 286, 800, 500, AccessibilityRegionType.LargeText)
            )));
        }
    }
}
