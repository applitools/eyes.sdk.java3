package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.Image;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestIEyesVG extends TestIEyesBase {


    private VisualGridRunner runner;
    private Configuration renderingConfiguration;
    protected final BatchInfo batchInfo = new BatchInfo("Top Sites - Visual Grid");

    public TestIEyesVG(String fixtureName) {
        super(fixtureName);
        runner = new VisualGridRunner(40);
        runner.setLogHandler(logHandler);
        logger = runner.getLogger();
        logger.log("enter");
        runner.setServerUrl(SERVER_URL);
        runner.setApiKey(API_KEY);
    }

    public TestIEyesVG() {
        this("visual_grid");
    }

    protected Eyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(runner);
        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);
        Configuration renderingConfiguration = new Configuration(GetConfiguration());
        renderingConfiguration.setTestName("Top Sites - " + testedUrl);

        logger.log("created configurations for url " + testedUrl);
        eyes.setConfiguration(renderingConfiguration);
        eyes.open(webDriver);
        return eyes;
    }

    private Configuration GetConfiguration() {
        if (renderingConfiguration == null) {
            renderingConfiguration = new Configuration();
            renderingConfiguration.setAppName("Top Sites");
            renderingConfiguration.setBatch(batchInfo);
            renderingConfiguration.addBrowser(800, 600, BrowserType.CHROME);
            renderingConfiguration.addBrowser(700, 500, BrowserType.FIREFOX);
            renderingConfiguration.addBrowser(1200, 800, BrowserType.IE_10);
            renderingConfiguration.addBrowser(1200, 800, BrowserType.IE_11);
        }
        return renderingConfiguration;
    }

    void validateResults(Eyes eyes, TestResults results) {
    }

    private void validateRunnerResults() {
        Map<BrowserType, String> browserTypes = new HashMap<>();
        browserTypes.put(BrowserType.CHROME, "CHROME");
        browserTypes.put(BrowserType.FIREFOX, "FIREFOX");
        browserTypes.put(BrowserType.EDGE, "EDGE");
        browserTypes.put(BrowserType.IE_10, "IE 10.0");
        browserTypes.put(BrowserType.IE_11, "IE 11.0");


        List<RenderBrowserInfo> browsers = renderingConfiguration.getBrowsersInfo();
        TestResultsSummary resultsSummary = runner.getAllTestResults();

        logger.log(resultsSummary.toString());

        TestResultContainer[] testResultsContainer = resultsSummary.getAllResults();

        for (TestResultContainer testResultContainer : testResultsContainer) {
            TestResults testResults = testResultContainer.getTestResults();
            SessionResults sessionResults = null;
            try {
                sessionResults = TestUtils.getSessionResults(runner.getApiKey(), testResults);
            } catch (IOException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }

            ActualAppOutput[] actualAppOutputs = sessionResults.getActualAppOutput();
            Assert.assertEquals(2, actualAppOutputs.length);

            Image image1 = actualAppOutputs[0].getImage();
            Assert.assertTrue(image1.getHasDom());
            RectangleSize hostDisplaySize = testResults.getHostDisplaySize();
            Assert.assertEquals(hostDisplaySize.getWidth(), image1.getSize().getWidth());
            Assert.assertEquals(hostDisplaySize.getHeight(), image1.getSize().getHeight());

            Image image2 = actualAppOutputs[1].getImage();
            Assert.assertTrue(image2.getHasDom());

            AppEnvironment env = sessionResults.getEnv();
            Iterator<RenderBrowserInfo> iterator = browsers.iterator();
            RenderBrowserInfo browser = null;
            while (iterator.hasNext()) {
                RenderBrowserInfo item = iterator.next();
                String hostingAppInfo = env.getHostingAppInfo();
                if (hostingAppInfo == null || !hostingAppInfo.startsWith(browserTypes.get(item.getBrowserType())) &&
                        (env.getDisplaySize().getWidth() == item.getWidth()) && (env.getDisplaySize().getHeight() == item.getHeight())) {
                    browser = item;
                    iterator.remove();
                }
            }
            {
                Assert.assertNotNull(browser, "browser " + env.getHostingAppInfo() + " , " + env.getDisplaySize() + " was not found in list: ");
            }
            Assert.assertEquals(browsers.size(), 0);
        }
    }

    @AfterClass
    public void afterClass() {
        logger.verbose("calling renderingManager_.GetAllTestResults()");
        validateRunnerResults();
    }

}
