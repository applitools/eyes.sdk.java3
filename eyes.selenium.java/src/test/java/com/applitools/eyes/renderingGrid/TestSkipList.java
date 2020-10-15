package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class TestSkipList {

    @Test
    public void TestSkipList() {
        VisualGridRunner runner = new VisualGridRunner(30);
        Eyes eyes = new Eyes(runner);

//        eyes.setLogHandler(TestUtils.initLogger());
        eyes.setLogHandler(new StdoutLogHandler(true));

        Configuration conf = new Configuration();
        conf.setTestName("Skip List");
        conf.setAppName("Visual Grid Render Test");
        conf.setBatch(TestDataProvider.batchInfo);
        conf.setUseDom(true);
        conf.setSendDom(true);

        eyes.setConfiguration(conf);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();
        eyes.open(driver);
        driver.get("https://applitools.github.io/demo/DomSnapshot/test-iframe.html");
        eyes.check("Check1", Target.window());
        Set<String> expectedUrls = new HashSet<>();
        expectedUrls.add("https://applitools.github.io/demo/DomSnapshot/test.css");
        expectedUrls.add("https://applitools.github.io/demo/DomSnapshot/smurfs.jpg");
        expectedUrls.add("https://applitools.github.io/blabla");
        expectedUrls.add("https://applitools.github.io/demo/DomSnapshot/iframes/inner/smurfs.jpg");
        expectedUrls.add("https://applitools.github.io/demo/DomSnapshot/test.html");
        expectedUrls.add("https://applitools.github.io/demo/DomSnapshot/iframes/inner/test.html");
        expectedUrls.add("https://applitools.github.io/demo/DomSnapshot/iframes/frame.html");

        Assert.assertEquals(runner.getCachedBlobsUrls(), expectedUrls);
        eyes.check("Check2", Target.window());
        driver.quit();
        eyes.close();

        runner.getAllTestResults();
    }
}
