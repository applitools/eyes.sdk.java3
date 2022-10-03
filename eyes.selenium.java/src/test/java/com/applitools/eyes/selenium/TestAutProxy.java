package com.applitools.eyes.selenium;

import com.applitools.eyes.AutProxyMode;
import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;

public class TestAutProxy {

    private WebDriver driver;

    @BeforeTest
    public void setup() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        driver = SeleniumUtils.createChromeDriver();
    }

    @AfterTest
    public void teardown() {
        if (driver != null)
            driver.quit();
    }

    private void startProxyDocker() throws IOException, InterruptedException {
        Process stopDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker run -d --name='tinyproxy' -p 8080:8888 dannydirect/tinyproxy:latest ANY"});
        stopDocker.waitFor();
    }

    private void stopAllDockers() throws IOException, InterruptedException {
        Process stopDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker stop $(docker ps -a -q)"});
        stopDocker.waitFor();
        Process removeDocker = Runtime.getRuntime().exec(new String[]{"bash","-c","docker rm $(docker ps -a -q)"});
        removeDocker.waitFor();
    }

    @Test
    public void shouldWorkThenFailWithAutProxy() throws IOException, InterruptedException {
        networkFailWithProxy();
        networkAndCheckPassWithAutProxy();
        CheckFailWithoutAutProxy();
    }

    private void networkFailWithProxy() throws IOException, InterruptedException {
        stopAllDockers();
        WebDriver driver = SeleniumUtils.createChromeDriver();
        boolean isOpenFailed = false;
        try {
            VisualGridRunner visualGridRunner = new VisualGridRunner(new RunnerOptions());
            Eyes eyes = new Eyes(visualGridRunner);

            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8080));
            eyes.open(driver, "ProxyTest", "proxy test");
        } catch (Exception e){
            isOpenFailed = true;
        } finally {
            driver.quit();
        }
        Assert.assertTrue(isOpenFailed);
    }

    private void networkAndCheckPassWithAutProxy() throws IOException, InterruptedException {
        stopAllDockers();
        startProxyDocker();
        WebDriver driver = SeleniumUtils.createChromeDriver();
        AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080),
                new String[]{"applitools.github.io", "github.com"}, AutProxyMode.BLOCK);
        try {

            VisualGridRunner visualGridRunner = new VisualGridRunner(new RunnerOptions().autProxy(autProxySettings));
            Eyes eyes = new Eyes(visualGridRunner);

            driver.get("https://applitools.github.io/demo/TestPages/PaddedBody/region-padding.html");

            eyes.open(driver, "AutProxyTest", "aut proxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.check(Target.window());
            eyes.close();
        } finally {
            driver.quit();
        }
    }

    private void CheckFailWithoutAutProxy() throws IOException, InterruptedException {
        stopAllDockers();
        WebDriver driver = SeleniumUtils.createChromeDriver();
        boolean isFailed = false;
        try {

            VisualGridRunner visualGridRunner = new VisualGridRunner(new RunnerOptions());
            Eyes eyes = new Eyes(visualGridRunner);

            driver.get("https://applitools.github.io/demo/TestPages/PaddedBody/region-padding.html");

            eyes.open(driver, "AutProxyTest", "aut proxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.check(Target.window());
            eyes.close();
        } catch (DiffsFoundException e) {
            System.out.println(e.getMessage());
            isFailed = true;
        }
        finally {
            driver.quit();
        }

        Assert.assertTrue(isFailed);
    }
}
