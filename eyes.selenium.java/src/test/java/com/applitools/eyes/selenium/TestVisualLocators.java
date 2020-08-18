package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.FixedCutProvider;
import com.applitools.eyes.Region;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.locators.VisualLocator;
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
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        runner.setLogHandler(new StdoutLogHandler());
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        RemoteWebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
        try {
            eyes.open(driver, "Applitools Eyes SDK", "testVisualLocators" + suffix);
            Map<String, List<Region>> result = eyes.locate(VisualLocator.name("applitools_title"));
            eyes.setImageCut(new FixedCutProvider(11, 0, 2, 0));
            Map<String, List<Region>> resultCut = eyes.locate(VisualLocator.name("applitools_title"));
            eyes.closeAsync();

            Assert.assertEquals(result.size(), 1);
            List<Region> regionList = result.get("applitools_title");
            Assert.assertEquals(regionList.size(), 1);
            Region region = regionList.get(0);
            Assert.assertEquals(region, new Region(2, 11, 173, 58));

            Assert.assertEquals(resultCut.size(), 1);
            regionList = resultCut.get("applitools_title");
            Assert.assertEquals(regionList.size(), 1);
            region = regionList.get(0);
            Assert.assertEquals(region, new Region(0, 0, 173, 58));
        } finally {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }
}
