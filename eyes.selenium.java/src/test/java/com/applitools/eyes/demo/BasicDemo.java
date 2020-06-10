package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BasicDemo extends ReportingTestSuite {
    private static BatchInfo batch;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler(false);

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
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logger);
        eyes.setBatch(batch);
        eyes.setProxy(new ProxySettings("http://localhost:8888"));
        try
        {
            eyes.setStitchMode(StitchMode.CSS);
            eyes.open(driver, "Netcentric 33594 app", "Netcentric 33594 Test" + suffix, new RectangleSize(1000, 900));
            driver.get("https://www.queenslandminingexpo.com.au/en-gb.html");
            JavascriptExecutor js = (JavascriptExecutor)driver;
            js.executeScript("window.scrollTo(0,document.body.scrollHeight)");
            Thread.sleep(5000);
            eyes.check(Target.window().fully());
            eyes.closeAsync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally
        {
            driver.quit();
            eyes.abortAsync();
            runner.getAllTestResults();
        }
    }
}
