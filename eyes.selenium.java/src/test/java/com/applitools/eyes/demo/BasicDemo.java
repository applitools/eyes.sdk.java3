package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.AsyncClassicRunner;
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

    @DataProvider(name = "runnerDP")
    public Object[] dp() {
        return new Object[]{"classic", "async", "vg"};
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

    @Test(dataProvider = "runnerDP")
    public void basicDemo(String runnerType) {
        super.addSuiteArg("runner", runnerType);
        EyesRunner runner;
        switch (runnerType) {
            case "classic":
                runner = new ClassicRunner();
                break;
            case "async":
                runner = new AsyncClassicRunner();
                break;
            case "vg":
                runner = new VisualGridRunner(10);
                break;
            default:
                throw new IllegalStateException(String.format("Unsupported runner %s", runnerType));
        }

        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logger);
        eyes.setBatch(batch);
        eyes.setSaveNewTests(false);
        //eyes.setProxy(new ProxySettings("http://localhost:8888"));
        try {
            eyes.open(driver, "Demo App", "BasicDemo_" + runnerType, new RectangleSize(800, 800));
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