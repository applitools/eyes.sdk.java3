package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.locators.VisualLocator;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class TestVisualLocators extends ReportingTestSuite {

    private RemoteWebDriver driver;

    public TestVisualLocators() {
        super.setGroupName("selenium");
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.FALSE};
    }

    @BeforeMethod
    public void beforeEach() {
        driver = SeleniumUtils.createChromeDriver();
    }

    @Test(dataProvider = "booleanDP")
    public void testVisualLocators(boolean useVisualGrid) {
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        runner.setLogHandler(new StdoutLogHandler());
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(new StdoutLogHandler());
        eyes.setProxy(new ProxySettings("http://localhost:8888"));
        driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
        try {
            eyes.initLocatorProvider(driver);
            eyes.open(driver, "Applitools Eyes SDK", "testVisualLocators");
            Map<String, List<Region>> result = eyes.locate(VisualLocator.name("letters"));
            eyes.closeAsync();
            Assert.assertEquals(result.size(), 1);
        } finally {
            driver.quit();
        }
    }
}
