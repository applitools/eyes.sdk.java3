package com.applitools.eyes.selenium;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.utils.GeneralUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GeneralUtils.class)
public class TestProxy extends ReportingTestSuite {

    public TestProxy() {
        super.setGroupName("selenium");
    }

    @Test
    public void testNetworkThroughProxy() throws IOException, InterruptedException {
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
        } catch (Exception e){
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
            Assert.assertTrue(eyes.isOpen());
            eyes.close();
        } finally {
            driver1.quit();
        }
    }

    @Test
    public void testProxyNoUrl() {
        PowerMockito.spy(GeneralUtils.class);
        when(GeneralUtils.getEnvString(AbstractProxySettings.PROXY_ENV_VAR_NAME)).thenReturn("http://localhost:8888");
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.setProxy(new ProxySettings());
        Assert.assertEquals(serverConnector.getProxy().getUri(), "http://localhost:8888");
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
