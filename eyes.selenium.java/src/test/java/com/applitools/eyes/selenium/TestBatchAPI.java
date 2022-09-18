package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.utils.CommunicationUtils;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

public final class TestBatchAPI extends ReportingTestSuite {

    private static WebDriver driver;

    public TestBatchAPI() {
        super.setGroupName("selenium");
    }

    @BeforeClass
    public static void classSetup() {
//        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
//        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-gpu"); // applicable to windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model
        driver = new ChromeDriver(options);
        driver.get("https://applitools.com/helloworld");
    }

    @AfterClass
    public static void classTearDown() {
        driver.quit();
    }

    @Test
    public void testDontCloseBatches() {
        String batchId = UUID.randomUUID().toString();

        TestResultsSummary summary;

        // First test, batch should not be closed.
        BatchInfo batchInfo1 = new BatchInfo("DontCloseBatch Tests");
        batchInfo1.setId(batchId);
        summary = runSingleTest(driver, batchInfo1, "DontCloseBatches Test1", true);
        Assert.assertEquals(summary.size(), 1);
        String batchId1 = summary.getAllResults()[0].getTestResults().getBatchId();

        // Second test, batch should not be closed (default).
        BatchInfo batchInfo2 = new BatchInfo("If you see this - testDontCloseBatches failed");
        batchInfo2.setId(batchId);
        summary = runSingleTest(driver, batchInfo2, "DontCloseBatches Test2", null);
        Assert.assertEquals(summary.size(), 1);
        String batchId2 = summary.getAllResults()[0].getTestResults().getBatchId();

        Assert.assertEquals(batchId1, batchId2, "First batch was closed!");

    }

    @Test
    public void testCloseBatches() {
        String batchId = UUID.randomUUID().toString();

        TestResultsSummary summary;

        // First test, batch should not be closed.
        BatchInfo batchInfo1 = new BatchInfo("CloseBatch - Batch1/2");
        batchInfo1.setId(batchId);
        summary = runSingleTest(driver, batchInfo1, "DontCloseBatches Test1", false);
        Assert.assertEquals(summary.size(), 1);
        String batchId1 = summary.getAllResults()[0].getTestResults().getBatchId();

        // Second test, batch should not be closed (default).
        BatchInfo batchInfo2 = new BatchInfo("CloseBatch - Batch2/2");
        batchInfo2.setId(batchId);
        summary = runSingleTest(driver, batchInfo2, "DontCloseBatches Test2", null);
        Assert.assertEquals(summary.size(), 1);
        String batchId2 = summary.getAllResults()[0].getTestResults().getBatchId();

        Assert.assertNotEquals(batchId1, batchId2, "First batch was not closed!");
    }

    public static TestResultsSummary runSingleTest(WebDriver driver, BatchInfo batchInfo, String testName,
                                                   Boolean dontCloseBatches) {
        ClassicRunner runner = new ClassicRunner();

        if (dontCloseBatches != null) {
            runner.setDontCloseBatches(dontCloseBatches);
        }

        Eyes eyes1 = new Eyes(runner);
        eyes1.setBatch(batchInfo);

        eyes1.open(driver, "Applitools Eyes Java SDK", testName);
        eyes1.closeAsync();

        return runner.getAllTestResults(false);

    }
}
