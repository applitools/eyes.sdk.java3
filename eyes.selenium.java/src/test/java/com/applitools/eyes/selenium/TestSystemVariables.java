package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Logger;
import com.applitools.eyes.ServerConnector;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestSystemVariables extends EnivironmentModifier {

    private final Logger logger = new Logger();

    @BeforeClass
    public void ResetEnvVars() {
        resetAllEnvVars();
    }

    @Test
    public void testApiKeySystemVariables() {
        setEnvironmentVariable("APPLITOOLS_API_KEY", "ApiKeyTest1234");
        ServerConnector serverConnector = new ServerConnector(logger);
        Assert.assertEquals("ApiKeyTest1234", serverConnector.getApiKey());
        Assert.assertEquals("https://eyesapi.applitools.com", serverConnector.getServerUrl().toString());
        setEnvironmentVariable("bamboo_APPLITOOLS_API_KEY", "bambooApiKeyTest1234");
        Assert.assertEquals("ApiKeyTest1234", serverConnector.getApiKey());
        setEnvironmentVariable("APPLITOOLS_API_KEY", null);
        Assert.assertEquals("bambooApiKeyTest1234", serverConnector.getApiKey());
    }

    @Test
    public void testServerUrlSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_SERVER_URL", "https://some.testurl.com/");
        ServerConnector serverConnector = new ServerConnector(logger);
        Assert.assertEquals("https://some.testurl.com/", serverConnector.getServerUrl().toString());

        setEnvironmentVariable("bamboo_APPLITOOLS_SERVER_URL", "https://bamboo.testurl.com/");
        serverConnector = new ServerConnector(logger);
        Assert.assertEquals("https://some.testurl.com/", serverConnector.getServerUrl().toString());

        setEnvironmentVariable("APPLITOOLS_SERVER_URL", null);
        serverConnector = new ServerConnector(logger);
        Assert.assertEquals("https://bamboo.testurl.com/", serverConnector.getServerUrl().toString());
    }

    @Test
    public void testDontCloseBatchesSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_DONT_CLOSE_BATCHES", "true");
        ServerConnector serverConnector = new ServerConnector(logger);
        Assert.assertEquals(true, serverConnector.getDontCloseBatches());

        setEnvironmentVariable("bamboo_APPLITOOLS_DONT_CLOSE_BATCHES", "false");
        serverConnector = new ServerConnector(logger);
        Assert.assertEquals(true, serverConnector.getDontCloseBatches());

        setEnvironmentVariable("APPLITOOLS_DONT_CLOSE_BATCHES", null);
        serverConnector = new ServerConnector(logger);
        Assert.assertEquals(false, serverConnector.getDontCloseBatches());

        setEnvironmentVariable("bamboo_APPLITOOLS_DONT_CLOSE_BATCHES", "true");
        serverConnector = new ServerConnector(logger);
        Assert.assertEquals(true, serverConnector.getDontCloseBatches());
    }

    @Test
    public void testBatchIdSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_BATCH_ID", "testBatchId");
        BatchInfo batchInfo = new BatchInfo();
        Assert.assertEquals("testBatchId", batchInfo.getId());

        setEnvironmentVariable("bamboo_APPLITOOLS_BATCH_ID", "bambooTestBatchId");
        batchInfo = new BatchInfo();
        Assert.assertEquals("testBatchId", batchInfo.getId());

        setEnvironmentVariable("APPLITOOLS_BATCH_ID", null);
        batchInfo = new BatchInfo();
        Assert.assertEquals("bambooTestBatchId", batchInfo.getId());
    }

    @Test
    public void testBatchNameSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_BATCH_NAME", "testBatchName");
        BatchInfo batchInfo = new BatchInfo();
        Assert.assertEquals("testBatchName", batchInfo.getName());

        setEnvironmentVariable("bamboo_APPLITOOLS_BATCH_NAME", "bambooTestBatchName");
        batchInfo = new BatchInfo();
        Assert.assertEquals("testBatchName", batchInfo.getName());

        setEnvironmentVariable("APPLITOOLS_BATCH_NAME", null);
        batchInfo = new BatchInfo();
        Assert.assertEquals("bambooTestBatchName", batchInfo.getName());
    }

    @Test
    public void testBatchSequenceNameSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_BATCH_SEQUENCE", "testBatchSequence");
        BatchInfo batchInfo = new BatchInfo();
        Assert.assertEquals("testBatchSequence", batchInfo.getSequenceName());

        setEnvironmentVariable("bamboo_APPLITOOLS_BATCH_SEQUENCE", "bambooTestBatchSequence");
        batchInfo = new BatchInfo();
        Assert.assertEquals("testBatchSequence", batchInfo.getSequenceName());

        setEnvironmentVariable("APPLITOOLS_BATCH_SEQUENCE", null);
        batchInfo = new BatchInfo();
        Assert.assertEquals("bambooTestBatchSequence", batchInfo.getSequenceName());
    }

    @Test
    public void testBatchNotifySystemVariables() {
        setEnvironmentVariable("APPLITOOLS_BATCH_NOTIFY", "true");
        BatchInfo batchInfo = new BatchInfo();
        Assert.assertEquals(true, batchInfo.isNotifyOnCompletion());

        setEnvironmentVariable("bamboo_APPLITOOLS_BATCH_NOTIFY", "false");
        batchInfo = new BatchInfo();
        Assert.assertEquals(true, batchInfo.isNotifyOnCompletion());

        setEnvironmentVariable("APPLITOOLS_BATCH_NOTIFY", null);
        batchInfo = new BatchInfo();
        Assert.assertEquals(false, batchInfo.isNotifyOnCompletion());

        setEnvironmentVariable("bamboo_APPLITOOLS_BATCH_NOTIFY", "true");
        batchInfo = new BatchInfo();
        Assert.assertEquals(true, batchInfo.isNotifyOnCompletion());
    }

    @Test
    public void testBranchSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_BRANCH", "testBranchName");
        Configuration config = new Configuration();
        Assert.assertEquals("testBranchName", config.getBranchName());

        setEnvironmentVariable("bamboo_APPLITOOLS_BRANCH", "bambooTestBranchName");
        config = new Configuration();
        Assert.assertEquals("testBranchName", config.getBranchName());

        setEnvironmentVariable("APPLITOOLS_BRANCH", null);
        config = new Configuration();
        Assert.assertEquals("bambooTestBranchName", config.getBranchName());
    }

    @Test
    public void testParentBranchSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_PARENT_BRANCH", "testParentBranchName");
        Configuration config = new Configuration();
        Assert.assertEquals("testParentBranchName", config.getParentBranchName());

        setEnvironmentVariable("bamboo_APPLITOOLS_PARENT_BRANCH", "bambooParentTestBranchName");
        config = new Configuration();
        Assert.assertEquals("testParentBranchName", config.getParentBranchName());

        setEnvironmentVariable("APPLITOOLS_PARENT_BRANCH", null);
        config = new Configuration();
        Assert.assertEquals("bambooParentTestBranchName", config.getParentBranchName());
    }

    @Test
    public void testBaselineBranchSystemVariables() {
        setEnvironmentVariable("APPLITOOLS_BASELINE_BRANCH", "testBaselineBranchName");
        Configuration config = new Configuration();
        Assert.assertEquals("testBaselineBranchName", config.getBaselineBranchName());

        setEnvironmentVariable("bamboo_APPLITOOLS_BASELINE_BRANCH", "bambooBaselineTestBranchName");
        config = new Configuration();
        Assert.assertEquals("testBaselineBranchName", config.getBaselineBranchName());

        setEnvironmentVariable("APPLITOOLS_BASELINE_BRANCH", null);
        config = new Configuration();
        Assert.assertEquals("bambooBaselineTestBranchName", config.getBaselineBranchName());
    }

    private void resetAllEnvVars() {
        setEnvVar("APPLITOOLS_API_KEY", null);
        setEnvVar("APPLITOOLS_SERVER_URL", null);
        setEnvVar("APPLITOOLS_BATCH_ID", null);
        setEnvVar("APPLITOOLS_BATCH_NAME", null);
        setEnvVar("APPLITOOLS_BATCH_SEQUENCE", null);
        setEnvVar("APPLITOOLS_BATCH_NOTIFY", null);
        setEnvVar("APPLITOOLS_BRANCH", null);
        setEnvVar("APPLITOOLS_PARENT_BRANCH", null);
        setEnvVar("APPLITOOLS_BASELINE_BRANCH", null);
        setEnvVar("APPLITOOLS_DONT_CLOSE_BATCHES", null);
    }

    private void setEnvVar(String envVarName, String envVarValue) {
        setEnvironmentVariable(envVarName, envVarValue);
        setEnvironmentVariable("bamboo_" + envVarName, envVarValue);
    }
}
