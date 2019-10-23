package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class TestClassicRunner {
    @Test
    public void test() {
        ClassicRunner classicRunner = new ClassicRunner();
        VisualGridRunner VGRunner = new VisualGridRunner(10);

        eyesTest(classicRunner);
        eyesTest(VGRunner);

    }
    @Test
    public void testClassicRunnerThrowException(){
        WebDriver driver = SeleniumUtils.createChromeDriver();
        final EyesRunner runner = new ClassicRunner();
        Eyes eyes = new Eyes(runner);
        driver.get("http://applitools.github.io/demo/TestPages/VisualGridTestPage/index.html");

        eyes.open(driver, "Applitools Eyes Java SDK", "Classic Runner Test",
                new RectangleSize(1200, 800));

        eyes.check(Target.window().withName("Step 1"));

        System.out.println(eyes.close(false));

        Assert.assertThrows(Throwable.class, new Assert.ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                runner.getAllTestResults();
            }
        });
    }
    private void eyesTest(EyesRunner runner) {

        WebDriver driver = SeleniumUtils.createChromeDriver();

        // Initialize the VisualGridEyes SDK and set your private API key.
        Eyes eyes = new Eyes(runner);
        Eyes eyes2 = new Eyes(runner);
        eyes.setLogHandler(new FileLogger("runnerTest.log", false, true));
        eyes2.setLogHandler(new FileLogger("runnerTest.log", false, true));
//        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setServerUrl("https://eyes.applitools.com/");
        eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));
        eyes2.setProxy(new ProxySettings("http://127.0.0.1:8888"));

        // Switch sendDom flag on
        eyes.setSendDom(true);
        eyes2.setSendDom(false);
        eyes2.setStitchMode(StitchMode.CSS);
        eyes.setStitchMode(StitchMode.CSS);
        BatchInfo batchInfo = new BatchInfo("Runner Testing");
        batchInfo.setId("RCA_Batch_ID");
        eyes.setBatch(batchInfo);
        eyes2.setBatch(batchInfo);
//        try {


        // Navigate the browser to the "hello world!" web-site.
        driver.get("http://applitools.github.io/demo/TestPages/VisualGridTestPage/index.html");

        eyes.open(driver, "Applitools Eyes Java SDK", "Classic Runner Test",
                new RectangleSize(1200, 800));
        eyes2.open(driver, "Applitools Eyes Java SDK", "Classic Runner 2 Test",
                new RectangleSize(1200, 800));


        eyes.check(Target.window().fully().ignoreDisplacements(false).withName("Step 1"));
        eyes2.check(Target.window().fully().ignoreDisplacements(false).withName("Step 1"));


        eyes.closeAsync();

        eyes.open(driver, "Applitools Eyes Java SDK", "Classic Runner Test",
                new RectangleSize(1200, 800));

        eyes.check(Target.window().fully().ignoreDisplacements(false).withName("Step 2"));

        eyes.closeAsync();
        eyes2.close(true);

        driver.quit();

        TestResultsSummary allTestResults = runner.getAllTestResults();
        if (allTestResults.getAllResults().length != 3) {
            throw new Error();
        }
    }
}