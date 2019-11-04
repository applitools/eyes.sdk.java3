package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;
import org.testng.annotations.*;

public class Test_VG_USA_Today {
    private VisualGridRunner renderingManager;

    private String defaultServerUrl = "https://eyes.applitools.com";

    @BeforeClass
    public void beforeClass() {
        renderingManager = new VisualGridRunner(10);
        renderingManager.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));
        FileLogger logHandler = new FileLogger("Oran.log", false, true);
        renderingManager.setLogHandler(logHandler);
        renderingManager.getLogger().log("enter");
    }

    @DataProvider(name = "Pages",parallel=true)
    public Object[][] ProductPage_urls() {
        BatchInfo batch = new BatchInfo("Demo - RCA");
        return new Object[][]{
//                {"https://shop.nordstrom.com/s/nordstrom-at-home-ribbed-velour-bath-rug/3682353?origin=category-personalizedsort&breadcrumb=Home%2FHome%20%26%20Gifts%2FHome%2FBath&color=blue%20chambray",batch},
//                {"https://shop.nordstrom.com/s/levtex-fairbourne-duvet-cover/5066777?origin=category-personalizedsort&breadcrumb=Home%2FHome%20%26%20Gifts%2FHome&color=multi",batch},
//                {"https://shop.nordstrom.com/s/david-yurman-cable-classics-bracelet-with-18k-gold-4mm/3625630?origin=category-personalizedsort&color=two%20tone",batch},
//                {"https://shop.nordstrom.com/s/comme-des-garcons-play-x-converse-chuck-taylor-hidden-heart-low-top-sneaker-women/4650436?origin=category-personalizedsort&color=beige",batch},
                {"https://www.usatoday.com/",batch}};
    }

    @Test(dataProvider = "Pages")
    public void test_productPage(String testedUrl, BatchInfo batch) {

        renderingManager.getLogger().log("entering with url " + testedUrl);

        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(batch);
        eyes.setMatchLevel(MatchLevel.STRICT);
        eyes.setServerUrl(defaultServerUrl);

        eyes.getLogger().log("creating WebDriver: " + testedUrl);
        WebDriver webDriver = SeleniumUtils.createChromeDriver();
        eyes.getLogger().log("navigating to " + testedUrl);
        webDriver.get(testedUrl);

        try {
            // Close popup
            WebElement close_button = webDriver.findElement(By.cssSelector("#root>div>div.Z1igStJ.oLDjG.Z2okeoT>div:nth-child(5)>div>div._1dF7W9>div.Z1jcKDo>div>div:nth-child(2)>div>div>div>a"));
            close_button.click();
        } catch(Exception e){}

        try {
            Configuration configuration = new Configuration();
            configuration.setTestName("USAToday - RCA demo");
            configuration.setAppName("USA Today website");

            configuration.addBrowser(1200, 800, BrowserType.CHROME);

            eyes.getLogger().log("created configurations for url " + testedUrl);

            eyes.setConfiguration(configuration);
            eyes.open(webDriver);

            eyes.getLogger().log("running check for url " + testedUrl);


            try {
                eyes.check(Target.window().withName("Step 1").sendDom(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling VisualGridEyes.close() for url " + testedUrl);

            eyes.getLogger().log("end of `try` block for url " + testedUrl);
            eyes.close(false);

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            eyes.getLogger().log("closing WebDriver for url " + testedUrl);
            webDriver.quit();
        }
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        renderingManager.getLogger().log("enter");
    }

    @AfterClass
    public void afterClass(ITestContext testContext){
        TestResultsSummary allTestResults = renderingManager.getAllTestResults();
        renderingManager.getLogger().log(allTestResults.toString());
    }

}
