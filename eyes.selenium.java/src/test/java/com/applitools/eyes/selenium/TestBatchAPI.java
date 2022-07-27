package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.utils.CommunicationUtils;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public final class TestBatchAPI extends ReportingTestSuite {

    public TestBatchAPI() {
        super.setGroupName("selenium");
    }

//    @Test
//    public void testCloseBatch() throws Exception {
//        ClassicRunner runner = new ClassicRunner();
//        Eyes eyes = new Eyes(runner);
//        eyes.setLogHandler(new StdoutLogHandler());
//        BatchInfo batchInfo = new BatchInfo("Runner Testing");
//        batchInfo.setId(UUID.randomUUID().toString());
//        eyes.setBatch(batchInfo);
//
//        WebDriver driver = SeleniumUtils.createChromeDriver();
//        try {
//            driver.get("https://applitools.com/helloworld");
//
//            eyes.open(driver, "Applitools Eyes Java SDK", "Test Close Batch", new RectangleSize(1200, 800));
//
//            BatchInfo batchBeforeDelete = CommunicationUtils.getBatch(eyes.getLogger(), batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());
//
//            Assert.assertFalse(batchBeforeDelete.isCompleted());
//
//            eyes.closeAsync();
//        } finally {
//            driver.quit();
//            eyes.abortAsync();
//            runner.getAllTestResults(false);
//            eyes.getServerConnector().closeBatch(batchInfo.getId());
//        }
//
//        // Getting 200 for the request is enough for the test
//        CommunicationUtils.getBatch(eyes.getLogger(), batchInfo.getId(), eyes.getServerUrl().toString(), eyes.getApiKey());
//    }

    @Test
    public void testDontCloseBatches() throws Exception {
        WebDriver driver = SeleniumUtils.createChromeDriver();

        try {
            ClassicRunner runner1 = new ClassicRunner();
            runner1.setDontCloseBatches(true);

            BatchInfo batchInfo = new BatchInfo("DontCloseBatch Testing");
            String batchId = UUID.randomUUID().toString();
            batchInfo.setId(batchId);

            // Run a test using Runner1 (this should NOT automatically close the batch on getAllTestsResults)
            Eyes eyes1 = new Eyes(runner1);
            eyes1.setBatch(batchInfo);
            eyes1.setApiKey(System.getenv("APPLITOOLS_API_KEY"));  // Required so that an eyes.getApiKey() later
                                                                         // will not result in "null"

            try {
                driver.get("https://applitools.com/helloworld");

                eyes1.open(driver, "Applitools Eyes Java SDK", "Test Dont Close Batch1");
                eyes1.closeAsync();
            } finally {
                eyes1.abortAsync();
                System.out.println(runner1.getAllTestResults(false));
            }

            BatchInfo batchFromRunner1 = CommunicationUtils.getBatch(eyes1.getLogger(), batchInfo.getId(), eyes1.getServerUrl().toString(), eyes1.getApiKey());
            Assert.assertFalse(batchFromRunner1.isCompleted());


            // Run a test using Runner2 (this SHOULD automatically close the batch)
            ClassicRunner runner2 = new ClassicRunner();
            runner2.setDontCloseBatches(true);
            Eyes eyes2 = new Eyes(runner2);
            eyes2.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
            BatchInfo batchInfo2 = new BatchInfo("DontCloseBatch Testing2");
            batchInfo2.setId(batchId);
            eyes2.setBatch(batchInfo2);

            try {
                driver.get("https://applitools.com/helloworld");

                eyes2.open(driver, "Applitools Eyes Java SDK", "Test Dont Close Batch1");
                eyes2.closeAsync();
            } finally {
                eyes2.abortAsync();
                runner2.getAllTestResults(false);
            }

            BatchInfo batchFromRunner2 = CommunicationUtils.getBatch(eyes2.getLogger(), batchInfo2.getId(), eyes2.getServerUrl().toString(), eyes2.getApiKey());
            Assert.assertTrue(batchFromRunner2.isCompleted() || batchFromRunner2.getApplyIsCompleted());
            Assert.assertEquals(batchFromRunner1.getId(), batchFromRunner2.getId());

        } finally {
            driver.quit();
        }
    }

    @Test
    public void testCloseBatches() throws Exception {
        WebDriver driver = SeleniumUtils.createChromeDriver();

        try {
            ClassicRunner runner1 = new ClassicRunner();

            BatchInfo batchInfo = new BatchInfo("DontCloseBatch Testing");
            String batchId = UUID.randomUUID().toString();
            batchInfo.setId(batchId);

            // Run a test using Runner1 (this should NOT automatically close the batch on getAllTestsResults)
            Eyes eyes1 = new Eyes(runner1);
            eyes1.setBatch(batchInfo);
            eyes1.setApiKey(System.getenv("APPLITOOLS_API_KEY"));  // Required so that an eyes.getApiKey() later
            // will not result in "null"

            try {
                driver.get("https://applitools.com/helloworld");

                eyes1.open(driver, "Applitools Eyes Java SDK", "Test Dont Close Batch1");
                eyes1.closeAsync();
            } finally {
                eyes1.abortAsync();
                System.out.println(runner1.getAllTestResults(false));
            }

            BatchInfo batchFromRunner1 = CommunicationUtils.getBatch(eyes1.getLogger(), batchInfo.getId(), eyes1.getServerUrl().toString(), eyes1.getApiKey());
            Assert.assertFalse(batchFromRunner1.isCompleted());


            // Run a test using Runner2 (this SHOULD automatically close the batch)
            ClassicRunner runner2 = new ClassicRunner();
            Eyes eyes2 = new Eyes(runner2);
            eyes2.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
            BatchInfo batchInfo2 = new BatchInfo("DontCloseBatch Testing2");
            batchInfo2.setId(batchId);
            eyes2.setBatch(batchInfo2);

            try {
                driver.get("https://applitools.com/helloworld");

                eyes2.open(driver, "Applitools Eyes Java SDK", "Test Dont Close Batch1");
                eyes2.closeAsync();
            } finally {
                eyes2.abortAsync();
                runner2.getAllTestResults(false);
            }

            BatchInfo batchFromRunner2 = CommunicationUtils.getBatch(eyes2.getLogger(), batchInfo2.getId(), eyes2.getServerUrl().toString(), eyes2.getApiKey());
            Assert.assertTrue(batchFromRunner1.isCompleted() || batchFromRunner1.getApplyIsCompleted());
            Assert.assertTrue(batchFromRunner2.isCompleted() || batchFromRunner2.getApplyIsCompleted());
            Assert.assertNotEquals(batchFromRunner1.getId(), batchFromRunner2.getId());

        } finally {
            driver.quit();
        }
    }
}
