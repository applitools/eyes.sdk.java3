package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Calendar;

public class TestConfigurationSentCorrectlyToServer extends EnvironmentModifier {

    @DataProvider(name = "dp")
    public static Object[][] dp() {
        return new Object[][]{
                {false, "Test Sequence", "Test Sequence Name Env Var"},
                {false, "Test Sequence", null},
                {true, "Test Sequence", null},
                {false, null, "Test Sequence Name Env Var"},
                {false, null, null},
                {true, "Test Sequence", "Test Sequence Name Env Var"},
                {true, null, "Test Sequence Name Env Var"},
                {true, null, null}
        };
    }

    @Test(dataProvider = "dp")
    public void TestEyesConfiguration(boolean useVisualGrid, String sequenceName, String sequenceNameEnvVar) {
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        super.addSuiteArg("sequenceName", sequenceName);
        super.addSuiteArg("sequenceNameEnvVar", sequenceNameEnvVar);
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        Eyes eyes = new Eyes(runner);

        WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");

        String originalBatchSequence = System.getenv("APPLITOOLS_BATCH_SEQUENCE");
        setEnvironmentVariable("APPLITOOLS_BATCH_SEQUENCE", sequenceNameEnvVar);

        String effectiveSequenceName = sequenceName != null ? sequenceName : sequenceNameEnvVar;

        BatchInfo batchInfo = new BatchInfo(TestDataProvider.batchInfo.getName() + "_" + effectiveSequenceName, Calendar.getInstance());
        batchInfo.setId(TestDataProvider.batchInfo.getId() + "_" + effectiveSequenceName);

        if (sequenceName != null) {
            batchInfo.setSequenceName(sequenceName);
        }

        if (sequenceNameEnvVar != null) {
            try {
                setEnvironmentVariable("APPLITOOLS_BATCH_SEQUENCE", originalBatchSequence);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Assert.assertEquals(effectiveSequenceName, batchInfo.getSequenceName());

            Configuration conf = new Configuration();
            String testName = "Test - " + (useVisualGrid ? "Visual Grid" : "Selenium");
            conf.setAppName("app").setTestName(testName)
                    //.setBaselineBranchName("baseline branch")
                    //.setBaselineEnvName("baseline env")
                    .setEnvironmentName("env name")
                    .setViewportSize(new RectangleSize(800, 600))
                    .setBatch(batchInfo);

            eyes.setConfiguration(conf);
            eyes.setLogHandler(new StdoutLogHandler());
            eyes.open(driver);

            eyes.setMatchLevel(MatchLevel.LAYOUT);
            eyes.check(Target.window());

            eyes.setMatchLevel(MatchLevel.CONTENT);
            eyes.check(Target.window());
        } finally {
            driver.quit();
        }

        TestResults results = eyes.close(false);
        SessionResults sessionResults = null;
        try {
            sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(sessionResults);

        Assert.assertEquals(batchInfo.getSequenceName(), sessionResults.getStartInfo().getBatchInfo().getSequenceName());

        Assert.assertNotNull(sessionResults.getActualAppOutput());
        Assert.assertEquals(2, sessionResults.getActualAppOutput().length);
        Assert.assertEquals(MatchLevel.LAYOUT2, sessionResults.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel());
        Assert.assertEquals(MatchLevel.CONTENT, sessionResults.getActualAppOutput()[1].getImageMatchSettings().getMatchLevel());

        runner.getAllTestResults(false);
        eyes.abortIfNotClosed();
    }

    @Test
    public void testSetEnv() {
        String mockKey = "mockKey";
        String mockValue = "mockValue";
        String envVar = System.getenv(mockKey);
        Assert.assertNull(envVar);
        printEnv();
        setEnvironmentVariable(mockKey, mockValue);
        System.out.println("+++++++++++++++++++++++++++++");
        printEnv();
        String newEnvVar = System.getenv(mockKey);

        Assert.assertEquals(newEnvVar, mockValue);

    }

}
