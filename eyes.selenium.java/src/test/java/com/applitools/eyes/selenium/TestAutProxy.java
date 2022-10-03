package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestAutProxy {

    @BeforeTest
    public void setup() {
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if(chromeDriverPath == null) throw new EyesException("CHROME_DRIVER_PATH missing");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
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
    public void shouldWorkThenFailWithAutProxy() {
        networkAndCheckPassWithAutProxy();
    }

    private void networkAndCheckPassWithAutProxy() {
        WebDriver driver = SeleniumUtils.createChromeDriver();
        try {

            VisualGridRunner visualGridRunner = new VisualGridRunner(new RunnerOptions().autProxy(new ProxySettings("http://127.0.0.1", 9999)));
            Eyes eyes = new Eyes(visualGridRunner);
            eyes.setConfiguration(eyes.getConfiguration().setDisableBrowserFetching(true));
            driver.get("https://applitools.com");

            eyes.open(driver, "AutProxyTest", "aut proxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.check(Target.window());
            eyes.close();
        } finally {
            driver.quit();
        }
    }
}
