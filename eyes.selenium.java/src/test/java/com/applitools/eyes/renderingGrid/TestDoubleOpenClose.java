package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.TestListener;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class TestDoubleOpenClose {

    EyesRunner runner = null;
    @DataProvider(name="booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.TRUE, Boolean.FALSE};
    }

    @Test(dataProvider = "booleanDP")
    public void TestDoubleOpenCheckClose(boolean useVisualGrid)
    {
        runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        final Eyes eyes = new Eyes(runner);
        driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage/");

        String suffix = useVisualGrid ? "_VG" : "";

        eyes.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckClose" + suffix, new RectangleSize(1200, 800));
        eyes.check(Target.window().fully().ignoreDisplacements(false).withName("Step 1"));
        eyes.close(false);

        eyes.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckClose" + suffix, new RectangleSize(1200, 800));
        eyes.check(Target.window().fully().ignoreDisplacements(false).withName("Step 2"));
        eyes.close(false);

        driver.quit();

        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(2, allTestResults.getAllResults().length);
    }

    @Test(dataProvider = "booleanDP")
    public void TestDoubleOpenCheckCloseAsync(boolean useVisualGrid)
    {
        runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        final Eyes eyes = new Eyes(runner);
        driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage/");

        String suffix = useVisualGrid ? "_VG" : "";

        eyes.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckCloseAsync" + suffix, new RectangleSize(1200, 800));
        eyes.check(Target.window().fully().ignoreDisplacements(false).withName("Step 1"));
        eyes.closeAsync();

        eyes.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckCloseAsync" + suffix, new RectangleSize(1200, 800));
        eyes.check(Target.window().fully().ignoreDisplacements(false).withName("Step 2"));
        eyes.closeAsync();

        driver.quit();

        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(2, allTestResults.getAllResults().length);
    }

    @Test(dataProvider = "booleanDP")
    public void TestDoubleOpenCheckCloseWithDifferentInstances(boolean useVisualGrid)
    {
        runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage/");

        String suffix = useVisualGrid ? "_VG" : "";

        EyesRunner runner = useVisualGrid ? (EyesRunner)new VisualGridRunner(10) : new ClassicRunner();

        Eyes eyes1 = new Eyes(runner);
        eyes1.setBatch(TestDataProvider.batchInfo);
        eyes1.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckCloseWithDifferentInstances" + suffix, new RectangleSize(1200, 800));
        eyes1.check(Target.window().fully().ignoreDisplacements(false).withName("Step 1"));
        eyes1.close(false);

        Eyes eyes2 = new Eyes(runner);
        eyes2.setBatch(TestDataProvider.batchInfo);
        eyes2.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckCloseWithDifferentInstances" + suffix, new RectangleSize(1200, 800));
        eyes2.check(Target.window().fully().ignoreDisplacements(false).withName("Step 2"));
        eyes2.close(false);

        driver.quit();

        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(2, allTestResults.getAllResults().length);
    }

    @Test(dataProvider = "booleanDP")
    public void TestDoubleOpenCheckCloseAsyncWithDifferentInstances(boolean useVisualGrid)
    {
        runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage/");

        String suffix = useVisualGrid ? "_VG" : "";


        Eyes eyes1 = new Eyes(runner);
        eyes1.setBatch(TestDataProvider.batchInfo);
        eyes1.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckCloseAsyncWithDifferentInstances" + suffix, new RectangleSize(1200, 800));
        eyes1.check(Target.window().fully().ignoreDisplacements(false).withName("Step 1"));
        eyes1.closeAsync();

        Eyes eyes2 = new Eyes(runner);
        eyes2.setBatch(TestDataProvider.batchInfo);
        eyes2.open(driver, "Applitools Eyes SDK", "TestDoubleOpenCheckCloseAsyncWithDifferentInstances" + suffix, new RectangleSize(1200, 800));
        eyes2.check(Target.window().fully().ignoreDisplacements(false).withName("Step 2"));
        eyes2.closeAsync();

        driver.quit();

        TestResultsSummary allTestResults = runner.getAllTestResults(false);
        Assert.assertEquals(2, allTestResults.getAllResults().length);
    }

    @Test(dataProvider = "booleanDP")
    public void TestDoubleCheckDontGetAllResults(boolean useVisualGrid)
    {
        runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        final WebDriver driver = SeleniumUtils.createChromeDriver();
        driver.get("https://applitools.com/helloworld");

        String suffix = useVisualGrid ? "_VG" : "";

        Eyes eyes1 = new Eyes(runner);
        eyes1.setBatch(TestDataProvider.batchInfo);
        eyes1.open(driver, "Applitools Eyes SDK", "TestDoubleCheckDontGetAllResults" + suffix, new RectangleSize(1200, 800));
        eyes1.check(Target.window().withName("Step 1"));
        eyes1.check(Target.window().withName("Step 2"));
        eyes1.close(false);

        driver.quit();
    }
}
