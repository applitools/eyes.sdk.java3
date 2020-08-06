package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.config.Feature;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class BasicDemo extends ReportingTestSuite {
    private static BatchInfo batch;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler(false);

    public BasicDemo(){
        super.setGroupName("selenium");
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.TRUE};
    }

    @BeforeClass
    public static void beforeAll() {
        if (TestUtils.runOnCI && System.getenv("TRAVIS") != null) {
            System.setProperty("webdriver.chrome.driver", "/home/travis/build/chromedriver"); // for travis build.
        }

        batch = new BatchInfo("Basic Sanity");
    }

    @BeforeMethod
    public void beforeEach() {
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "iPhone X");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", mobileEmulation);
        driver = SeleniumUtils.createChromeDriver(options);
    }

    @Test(dataProvider = "booleanDP")
    public void basicDemo(boolean useVisualGrid) throws InterruptedException {
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logger);
        eyes.setBatch(batch);
        Configuration conf = eyes.getConfiguration();
        conf.addDeviceEmulation(DeviceName.iPhone_X,ScreenOrientation.PORTRAIT);
        conf.addDeviceEmulation(DeviceName.Galaxy_S5, ScreenOrientation.LANDSCAPE);
        conf.activateFeatures(Feature.OPTIMIZE_TEST);
        eyes.setConfiguration(conf);
        eyes.setProxy(new ProxySettings("http://localhost:8888"));
        // Navigate the browser to the "ACME" demo app.
        driver.get("https://www.agilent.com/search/?Ntt=db-1");
        Thread.sleep(10000);
        try {
            eyes.open(driver, "Demo App", "agilent" + suffix);


            // To see visual bugs after the first run, use the commented line below instead.
            //driver.get("https://demo.applitools.com/index_v2.html");

            eyes.checkWindow();
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            TestResultsSummary allTestResults = runner.getAllTestResults();
            System.out.println(allTestResults);
        }
    }
}
