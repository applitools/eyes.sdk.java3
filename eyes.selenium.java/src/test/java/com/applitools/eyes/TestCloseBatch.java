package com.applitools.eyes;

import com.applitools.eyes.fluent.BatchClose;
import com.applitools.eyes.fluent.EnabledBatchClose;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.universal.dto.CloseBatchSettingsDto;
import com.applitools.eyes.universal.mapper.SettingsMapper;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCloseBatch {

    private WebDriver driver;
    private Eyes eyes;
    private BatchInfo batch;

    @BeforeTest(alwaysRun = true)
    public void before() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions().setHeadless(true);
        options.addArguments("--disable-gpu"); // applicable to windows os only
        options.addArguments("--no-sandbox"); // Bypass OS security model
        driver = new ChromeDriver(options);

        batch = new BatchInfo("closeBatch");
    }

    @AfterTest(alwaysRun = true)
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void testCloseBatchClassicRunner() throws InterruptedException {

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
        Thread.sleep(3000);
        batchInfo = getBatchInfo(results);
        Assert.assertTrue(batchInfo.getIsCompleted());
    }

    // using bc.setBatchId() will spawn a universal server now
    // can test manually with below
//    @Test(priority = 2)
//    public void testCloseBatchWithoutEyes() {
//        BatchClose bc = new BatchClose();
//        bc.setUrl("https://testeyes.applitools.com");
//        bc.setApiKey("APPLTOOLS_TEST_EYES_API_KEY");
//        EnabledBatchClose close = bc.setBatchId(Arrays.asList("a0a86603-f36c-4590-b557-c52731c37bd8"));
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
