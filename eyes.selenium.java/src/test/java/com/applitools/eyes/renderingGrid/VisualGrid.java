package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class VisualGrid {

    private VisualGridRunner VisualGrid = new VisualGridRunner(10);

    private Eyes eyes = new Eyes(VisualGrid);

    private WebDriver driver;


    @BeforeClass
    protected void init(){

        try {
            TestUtils.setFinalStatic(VisualGridEyes.class, "DOM_EXTRACTION_TIMEOUT", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        eyes.setLogHandler(TestUtils.initLogger("VisualGrid"));
        eyes.setBranchName("Child Branch");
        //eyes.setProxy(new ProxySettings("https://127.0.0.1", 8888));
        VisualGrid.setLogger(eyes.getLogger());
        Configuration configuration = eyes.getConfiguration();
        configuration.setBatch(new BatchInfo("example vg"));
    }

    @BeforeMethod
    public void setUp(){
        driver = SeleniumUtils.createChromeDriver();
        Configuration configuration = eyes.getConfiguration();
        configuration.addBrowser(700, 500, BrowserType.CHROME);
        configuration.addBrowser(800, 600, BrowserType.IE_10);
        configuration.addBrowser(900, 700, BrowserType.IE_11);
        configuration.addBrowser(1200, 800, BrowserType.FIREFOX);
        configuration.addBrowser(1600, 1200, BrowserType.EDGE);
        //configuration.setProxy(new ProxySettings("http://127.0.0.1", 8888));
        eyes.setConfiguration(configuration);

    }

    @Test
    public void HelloWorld() {

        try {
            driver.get("https://applitools.com/helloworld");

            eyes.open(driver, "app", "test", new RectangleSize(600, 600));
            eyes.check("first check", Target.window().withName("Step 1 A"));
            eyes.check("second check", Target.window().fully(false).withName("Step 1 B"));
            driver.findElement(By.tagName("button")).click();
            eyes.check("third check", Target.window().withName("Step 2 A"));
            eyes.check("forth check", Target.window().fully(false).withName("Step 2 B"));

            eyes.check(Target.window());

            eyes.close();


            TestResultsSummary allTestResults = VisualGrid.getAllTestResults();
            System.out.println("Results: " + allTestResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Github(){


        driver.get("https://github.com/");
        eyes.open(driver, "app", "test", new RectangleSize(600, 600));

        eyes.check(Target.window().fully());

        eyes.close();

//        TestResultsSummary allTestResults = VisualGrid.getAllTestResults();
//        System.out.println("Results: " + allTestResults);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @AfterClass
    public void closeRunner() {
        TestResultsSummary allTestResults = this.VisualGrid.getAllTestResults();
        String message = allTestResults.toString();
        eyes.getLogger().verbose(message);
    }
}