package com.applitools.eyes;

import com.applitools.eyes.fluent.BatchClose;
import com.applitools.eyes.fluent.EnabledBatchClose;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.universal.dto.CloseBatchSettingsDto;
import com.applitools.eyes.universal.mapper.SettingsMapper;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCloseBatch extends ReportingTestSuite {

    @BeforeClass
    public void setup() {
        super.setGroupName("selenium");
    }

    private WebDriver driver;
    private Eyes eyes;
    private BatchInfo batch;

    @BeforeTest(alwaysRun = true)
    public void before() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        driver = SeleniumUtils.createChromeDriver(new ChromeOptions().setHeadless(true));

        batch = new BatchInfo("closeBatch");
    }

    @AfterTest(alwaysRun = true)
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void testCloseBatchClassicRunner() {

        eyes = new Eyes();
        eyes.setBatch(batch);
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY_TEST_EYES"));
        eyes.setServerUrl("https://testeyes.applitools.com");

        eyes.open(driver, "test close batch", "test close batch");
        eyes.check(Target.window().fully(false));
        TestResults results = eyes.close(false);
        com.applitools.eyes.metadata.BatchInfo batchInfo = getBatchInfo(results);

        BatchClose bc = new BatchClose();
        bc.setUrl("https://testeyes.applitools.com");
        bc.setApiKey(System.getenv("APPLITOOLS_API_KEY_TEST_EYES"));
        EnabledBatchClose close = bc.setBatchId(Arrays.asList(batch.getId()));
        Assert.assertFalse(batchInfo.getIsCompleted());
        close.close();
        AssertBatchInfoWithRetry(results);
    }

    private void AssertBatchInfoWithRetry(TestResults results) {
        int retries = 10;
        while (retries > 0) {
            try {
                System.out.println("BatchInfo retry #" + retries);
                com.applitools.eyes.metadata.BatchInfo batchInfo = getBatchInfo(results);
                Assert.assertTrue(batchInfo.getIsCompleted());
                break;
            } catch (AssertionError | Exception e) {
                retries--;
                if (retries == 0) {
                    e.printStackTrace();
                    throw e;
                }
                try { Thread.sleep(2000); }
                catch (InterruptedException ex) { ex.printStackTrace(); }
            }
        }
    }

    // using bc.setBatchId() will spawn a universal server now
    // can test manually with below
//    @Test(priority = 2)
//    public void testCloseBatchWithoutEyes() {
//        BatchClose bc = new BatchClose();
//        bc.setUrl("https://testeyes.applitools.com");
//        bc.setApiKey(System.getenv("APPLITOOLS_API_KEY_TEST_EYES"));
//        EnabledBatchClose close = bc.setBatchId(Arrays.asList("0048356c-d2c0-4063-92fe-eec2accbe348"));
//        close.close();
//    }

    private com.applitools.eyes.metadata.BatchInfo getBatchInfo(TestResults results) {
        com.applitools.eyes.metadata.BatchInfo batchInfo = null;
        try {
            batchInfo = TestUtils.getBatchResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return batchInfo;
    }

    @Test
    public void testCloseBatchMapping() {
        ProxySettings proxySettings = new ProxySettings("uri");
        String serverUrl = "server-url";
        String apiKey = "api-key";
        List<String> batchIds = new ArrayList<String>(Arrays.asList("batch-id1", "batch-id2")) {};
        List<CloseBatchSettingsDto> dto = SettingsMapper.toCloseBatchSettingsDto(batchIds, apiKey, serverUrl, proxySettings);

        Assert.assertEquals(dto.get(0).getBatchId(), "batch-id1");
        Assert.assertEquals(dto.get(1).getBatchId(), "batch-id2");

        Assert.assertEquals(dto.get(0).getServerUrl(), "server-url");
        Assert.assertEquals(dto.get(1).getServerUrl(), "server-url");

        Assert.assertEquals(dto.get(0).getApiKey(), "api-key");
        Assert.assertEquals(dto.get(1).getApiKey(), "api-key");

        Assert.assertEquals(dto.get(0).getProxy().getUrl(), "uri");
        Assert.assertEquals(dto.get(1).getProxy().getUrl(), "uri");
    }
}
