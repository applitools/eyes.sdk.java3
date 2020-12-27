package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.locators.VisualLocator;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class TestVisualLocators extends ReportingTestSuite {

    public TestVisualLocators() {
        super.setGroupName("selenium");
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.TRUE, Boolean.FALSE};
    }

    @Test(dataProvider = "booleanDP")
    public void testVisualLocators(boolean useVisualGrid) {
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setSaveNewTests(false);
        RemoteWebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
        try {
            eyes.open(driver, "Applitools Eyes SDK", "testVisualLocators" + suffix, new RectangleSize(800, 600));
            eyes.check(Target.window().fully(false));
            Map<String, List<Region>> result = eyes.locate(VisualLocator.name("applitools_title"));
            eyes.setImageCut(new FixedCutProvider(19, 0, 3, 0));
            Map<String, List<Region>> resultCut = eyes.locate(VisualLocator.name("applitools_title"));
            eyes.setImageCut(null);
            eyes.setScaleRatio(0.5);
            Map<String, List<Region>> resultScale = eyes.locate(VisualLocator.name("applitools_title_scaled_down"));
            eyes.closeAsync();

            Assert.assertEquals(result.size(), 1);
            List<Region> regionList = result.get("applitools_title");
            Assert.assertEquals(regionList.size(), 1);
            Region region = regionList.get(0);
            Assert.assertEquals(region, new Region(3, 19, 158, 38));

            Assert.assertEquals(resultCut.size(), 1);
            regionList = resultCut.get("applitools_title");
            Assert.assertEquals(regionList.size(), 1);
            region = regionList.get(0);
            Assert.assertEquals(region, new Region(0, 0, 158, 38));

            Assert.assertEquals(resultScale.size(), 1);
            regionList = resultScale.get("applitools_title_scaled_down");
            Assert.assertEquals(regionList.size(), 1);
            region = regionList.get(0);
            Assert.assertEquals(region, new Region(2, 8, 77, 22));
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }
}
