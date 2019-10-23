package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class TestVisualGridRCA {
    private BatchInfo batch = new BatchInfo("Test Visual Grid RCA");

    @Test
    public void test_VG_RCA_Config() {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setBatch(batch);
        //eyes.setProxy(new ProxySettings("http://127.0.0.1",8888));
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
            eyes.open(driver, "Test Visual Grid", "Test RCA Config");
            eyes.setSendDom(true);
            eyes.check(Target.window());
            eyes.close();
            runner.getAllTestResults();
        } finally {
            driver.quit();
        }
    }

    @Test
    public void test_VG_RCA_Fluent() {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setBatch(batch);
        //eyes.setProxy(new ProxySettings("http://127.0.0.1",8888));
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {
            driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");

            driver.switchTo().frame("iframe");
            WebDriverWait wait = new WebDriverWait(driver,30);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#p2")));
            driver.switchTo().defaultContent();

            eyes.open(driver, "Test Visual Grid", "Test RCA Fluent");
            eyes.setSendDom(false);
            eyes.check(Target.window().sendDom(true));
            eyes.close();
            runner.getAllTestResults();
        } finally {
            driver.quit();
        }
    }
}
