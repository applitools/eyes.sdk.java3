package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BasicDemo extends ReportingTestSuite {
    private static BatchInfo batch;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler();

    public BasicDemo(){
        super.setGroupName("selenium");
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.TRUE, Boolean.FALSE};
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
        driver = SeleniumUtils.createChromeDriver();
    }

    @Test(dataProvider = "booleanDP")
    public void basicDemo(boolean useVisualGrid) {
        super.addSuiteArg("isVisualGrid", useVisualGrid);
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logger);
        eyes.setBatch(batch);
        //eyes.setProxy(new ProxySettings("http://localhost:8888"));
        try {
            eyes.open(driver, "Demo App", "BasicDemo" + suffix, new RectangleSize(800, 800));
            driver.get("https://applitools.github.io/demo/TestPages/FramesTestPage/");
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