package com.applitools.eyes.selenium.rendering;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestBadSelectors extends ReportingTestSuite {

    EyesRunner runner = null;

    public TestBadSelectors() {
        super.setGroupName("selenium");
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.TRUE, Boolean.FALSE};
    }

    @Test(dataProvider = "booleanDP")
    public void TestCheckRegionWithBadSelector(boolean useVisualGrid) {
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        final Eyes eyes = new Eyes(runner);
        driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage/");

        String suffix = useVisualGrid ? "_VG" : "";
        eyes.open(driver, "Applitools Eyes SDK", "TestCheckRegionWithBadSelector" + suffix, new RectangleSize(1200, 800));

        Assert.assertThrows(Throwable.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() {
                eyes.checkRegion(By.cssSelector("#element_that_does_not_exist"));
                eyes.closeAsync();
                runner.getAllTestResults();
            }
        });
        driver.quit();
    }
}
