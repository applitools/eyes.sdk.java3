package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;

public class SerialTest {

    public static void main(String[] args) {

        EyesRunner visualGridRunner = new VisualGridRunner(10);
        visualGridRunner.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));
        visualGridRunner.getLogger().log("enter");
        String testedUrl = "https://github.com/";
        Eyes eyes = new Eyes(visualGridRunner);
        BatchInfo batchInfo = new BatchInfo("Github in VG");
        eyes.setBatch(batchInfo);
        Logger logger = eyes.getLogger();
        visualGridRunner.getLogger().log("entering with url " + testedUrl);
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        webDriver.get(testedUrl);
        try {
            Configuration configuration = new Configuration();
            configuration.setAppName("Github");
            String environment = "My env name";
            configuration.addBrowser(800, 600, BrowserType.CHROME, environment);
//            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setConfiguration(configuration);
            eyes.open(webDriver, "Github", "First Test", new RectangleSize(800, 600));
            try {
                eyes.check(Target.window().withName("Step1 - " + testedUrl).sendDom(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.closeAsync();
            logger.log("end of `try` block for url " + testedUrl);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            logger.log("closing WebDriver for url " + testedUrl);
            webDriver.quit();
            logger.log("url " + testedUrl + " - done with browser.");
        }

        TestResultsSummary allTestResults = visualGridRunner.getAllTestResults();
        logger.log(allTestResults.toString());
    }
}
