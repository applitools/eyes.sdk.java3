package com.applitools.eyes.selenium;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.utils.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

@Listeners(TestListener.class)
public class ProxyTest {

    @Test
    public void checkNetworkThroughProxy() throws IOException, InterruptedException {
        checkNetworkPassThroughProxy();
        checkNetworkFailIfNoProxy();
    }

    private void checkNetworkFailIfNoProxy() throws IOException, InterruptedException{
        stopAllDockers();
        WebDriver driver2 = SeleniumUtils.createChromeDriver();
        boolean isOpenFailed = false;
        try {
            Eyes eyes = new Eyes();
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8080));
            eyes.open(driver2, "ProxyTest", "proxy test");
        } catch (EyesException e){
            isOpenFailed = true;
        } finally {
            driver2.quit();
        }
        Assert.assertTrue(isOpenFailed);
    }

    private void checkNetworkPassThroughProxy() throws IOException, InterruptedException{
        stopAllDockers();
        startProxyDocker();
        WebDriver driver1 = SeleniumUtils.createChromeDriver();
        try {
            Eyes eyes = new Eyes();
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8080));
            eyes.open(driver1, "ProxyTest", "proxy test");
            Assert.assertTrue(eyes.getIsOpen());
            eyes.close();
        } finally {
            driver1.quit();
        }
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

}
