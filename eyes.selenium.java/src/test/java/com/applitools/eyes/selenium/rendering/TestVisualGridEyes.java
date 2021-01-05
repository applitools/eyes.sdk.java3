package com.applitools.eyes.selenium.rendering;

import com.applitools.connectivity.MockServerConnector;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.config.ConfigurationProvider;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.visualgrid.model.DeviceSize;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.mockito.ArgumentMatchers;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

public class TestVisualGridEyes {

    @Test
    public void testSetConfiguration() {
        String expectedApiKey = "expectedApiKey";
        String defaultApiKey = GeneralUtils.getEnvString("APPLITOOLS_API_KEY");
        String expectedServerUrl = "http://expectedServerUrl";
        String defaultServerUrl = GeneralUtils.getServerUrl().toString();
        ProxySettings expectedProxy = new ProxySettings("http://expectedProxy:8888");

        final Configuration configuration = new Configuration();
        ConfigurationProvider configurationProvider = new ConfigurationProvider() {
            @Override
            public Configuration get() {
                return configuration;
            }
        };

        RemoteWebDriver driver = mock(RemoteWebDriver.class);
        when(driver.getSessionId()).thenReturn(mock(SessionId.class));

        RunnerOptions runnerOptions = new RunnerOptions().apiKey(expectedApiKey).serverUrl(expectedServerUrl).proxy(expectedProxy);
        VisualGridRunner runner = new VisualGridRunner(runnerOptions);
        VisualGridEyes eyes = new VisualGridEyes(runner, configurationProvider);
        Assert.assertEquals(runner.getApiKey(), expectedApiKey);
        Assert.assertEquals(eyes.getApiKey(), expectedApiKey);
        Assert.assertEquals(runner.getServerUrl(), expectedServerUrl);
        Assert.assertEquals(eyes.getServerUrl().toString(), expectedServerUrl);
        Assert.assertEquals(runner.getProxy(), expectedProxy);
        Assert.assertEquals(eyes.getProxy(), expectedProxy);

        runner = new VisualGridRunner();
        eyes = spy(new VisualGridEyes(runner, configurationProvider));
        doNothing().when(eyes).setViewportSize(ArgumentMatchers.<EyesSeleniumDriver>any());
        eyes.setServerConnector(new MockServerConnector());
        eyes.setApiKey(expectedApiKey);
        eyes.setServerUrl(expectedServerUrl);
        eyes.proxy(expectedProxy);
        eyes.open(driver, "app", "test", new RectangleSize(800, 800));
        Assert.assertEquals(runner.getApiKey(), expectedApiKey);
        Assert.assertEquals(eyes.getApiKey(), expectedApiKey);
        Assert.assertEquals(eyes.testList.values().iterator().next().getApiKey(), expectedApiKey);
        Assert.assertEquals(runner.getServerUrl(), expectedServerUrl);
        Assert.assertEquals(eyes.getServerUrl().toString(), expectedServerUrl);
        Assert.assertEquals(eyes.testList.values().iterator().next().getServerUrl().toString(), expectedServerUrl);
        Assert.assertEquals(runner.getProxy(), expectedProxy);
        Assert.assertEquals(eyes.getProxy(), expectedProxy);
        Assert.assertEquals(eyes.testList.values().iterator().next().getProxy(), expectedProxy);

        final Configuration configuration2 = new Configuration();
        ConfigurationProvider configurationProvider2 = new ConfigurationProvider() {
            @Override
            public Configuration get() {
                return configuration2;
            }
        };
        runner = new VisualGridRunner();
        eyes = spy(new VisualGridEyes(runner, configurationProvider2));
        doNothing().when(eyes).setViewportSize(ArgumentMatchers.<EyesSeleniumDriver>any());
        eyes.setServerConnector(new MockServerConnector());
        eyes.open(driver, "app", "test", new RectangleSize(800, 800));
        Assert.assertEquals(runner.getApiKey(), defaultApiKey);
        Assert.assertEquals(eyes.getApiKey(), defaultApiKey);
        Assert.assertEquals(eyes.testList.values().iterator().next().getApiKey(), defaultApiKey);
        Assert.assertEquals(runner.getServerUrl(), defaultServerUrl);
        Assert.assertEquals(eyes.getServerUrl().toString(), defaultServerUrl);
        Assert.assertEquals(eyes.testList.values().iterator().next().getServerUrl().toString(), defaultServerUrl);

        configuration2.setApiKey(expectedApiKey);
        configuration2.setServerUrl(expectedServerUrl);
        configuration2.setProxy(expectedProxy);
        configuration2.addBrowser(new IosDeviceInfo(IosDeviceName.iPad_7));
        runner = new VisualGridRunner();
        eyes = spy(new VisualGridEyes(runner, configurationProvider2));
        doNothing().when(eyes).setViewportSize(ArgumentMatchers.<EyesSeleniumDriver>any());
        final AtomicReference<String> actualApiKey = new AtomicReference<>();
        eyes.setServerConnector(new MockServerConnector() {
            @Override
            public Map<String, DeviceSize> getDevicesSizes(String path) {
                actualApiKey.set(getApiKey());
                return super.getDevicesSizes(path);
            }
        });

        eyes.open(driver, "app", "test", new RectangleSize(800, 800));
        Assert.assertEquals(runner.getApiKey(), expectedApiKey);
        Assert.assertEquals(eyes.getApiKey(), expectedApiKey);
        Assert.assertEquals(eyes.testList.values().iterator().next().getApiKey(), expectedApiKey);
        Assert.assertEquals(runner.getServerUrl(), expectedServerUrl);
        Assert.assertEquals(eyes.getServerUrl().toString(), expectedServerUrl);
        Assert.assertEquals(eyes.testList.values().iterator().next().getServerUrl().toString(), expectedServerUrl);
        Assert.assertEquals(runner.getProxy(), expectedProxy);
        Assert.assertEquals(eyes.getProxy(), expectedProxy);
        Assert.assertEquals(eyes.testList.values().iterator().next().getProxy(), expectedProxy);
        Assert.assertEquals(actualApiKey.get(), expectedApiKey);
    }
}
