package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.locators.OcrRegion;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class BasicDemo extends ReportingTestSuite {
    private static BatchInfo batch;
    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler();

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
            eyes.open(driver, "Demo App", "ocr" + suffix, new RectangleSize(700, 460));
            driver.get("https://applitools.github.io/demo/TestPages/OCRPage");
            final WebElement element = driver.findElement(By.cssSelector("body > h3"));
            List<String> list = eyes.extractText(new OcrRegion(new Region(8, 21, 400, 37)).hint("Header1: Hello world!"), new OcrRegion(element), new OcrRegion(By.cssSelector("body > ol > li:nth-child(1)")));
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            TestResultsSummary allTestResults = runner.getAllTestResults(false);
            System.out.println(allTestResults);
        }
    }
}