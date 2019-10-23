package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class TestSpecialCharacters {
    @Test
    public void TestRenderSpecialCharacters()
    {
        VisualGridRunner runner = new VisualGridRunner(30);
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(TestUtils.initLogger());

        Configuration sconf = new Configuration();
        sconf.setTestName("Special Characters");
        sconf.setAppName("Special Characters Test");

        sconf.addBrowser(800, 600, BrowserType.CHROME);
        sconf.setBatch(TestDataProvider.batchInfo);

        eyes.setConfiguration(sconf);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        eyes.open(driver);
        driver.get("https://applitools.github.io/demo/TestPages/SpecialCharacters/index.html");
        eyes.check("Test Special Characters", Target.window().fully());
        driver.quit();
        eyes.close();
        TestResultsSummary allResults = runner.getAllTestResults();
    }
}
