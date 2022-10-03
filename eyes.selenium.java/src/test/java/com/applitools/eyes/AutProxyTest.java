package com.applitools.eyes;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutProxyTest {


    @Test
    public void shouldNotBeNullWhenSetInVisualGridRunnerProxy() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        VisualGridRunner visualGridRunner = new VisualGridRunner();
        visualGridRunner.setProxy(proxySettings);

        Assert.assertNotNull(visualGridRunner.getProxy());
        Assert.assertNotNull(visualGridRunner.getAutProxy());

        Eyes eyes = new Eyes(visualGridRunner);

        Assert.assertNotNull(eyes.getConfiguration().getAutProxy());
        Assert.assertNotNull(eyes.getConfiguration().getProxy());
    }

    @Test
    public void shouldBeNullWhenNotSet() {
        VisualGridRunner visualGridRunner = new VisualGridRunner();

        Assert.assertNull(visualGridRunner.getProxy());
        Assert.assertNull(visualGridRunner.getAutProxy());

        Eyes eyes = new Eyes(visualGridRunner);

        Assert.assertNull(eyes.getConfiguration().getAutProxy());
        Assert.assertNull(eyes.getConfiguration().getProxy());
    }

    @Test
    public void shouldBeNullWhenNotAssignedInRunnerOptions() {
        RunnerOptions runnerOptions = new RunnerOptions();
        Assert.assertNull(runnerOptions.getAutProxy());
        Assert.assertNull(runnerOptions.getProxy());

        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        runnerOptions = new RunnerOptions().autProxy(proxySettings);
        Assert.assertNull(runnerOptions.getProxy());
        Assert.assertNull(runnerOptions.getAutProxy().getDomains());
        Assert.assertNull(runnerOptions.getAutProxy().getAutProxyMode());

        runnerOptions = new RunnerOptions().proxy(proxySettings);
        Assert.assertNotNull(runnerOptions.getProxy());
        Assert.assertNull(runnerOptions.getAutProxy());
    }

    @Test
    public void testVisualGridRunnerAutProxy() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        VisualGridRunner runner = new VisualGridRunner();
        runner.setProxy(proxySettings);

        AutProxySettings autProxy = runner.getAutProxy();
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getProxy(), proxySettings);

        runner = new VisualGridRunner("autProxy");
        runner.setProxy(proxySettings);
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getProxy(), proxySettings);

        runner = new VisualGridRunner(5);
        runner.setProxy(proxySettings);
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getProxy(), proxySettings);

        runner = new VisualGridRunner(5, "autProxy");
        runner.setProxy(proxySettings);
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getProxy(), proxySettings);
    }

    @Test
    public void testRunnerOptionsAutProxy() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        RunnerOptions options = new RunnerOptions().autProxy(proxySettings);
        VisualGridRunner runner = new VisualGridRunner(options);

        AutProxySettings autProxy = options.getAutProxy();
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getProxy(), proxySettings);

        autProxy = runner.getAutProxy();
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getProxy(), proxySettings);
    }

    @Test
    public void testRunnerOptionsutProxyWithDomains() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        RunnerOptions options = new RunnerOptions().autProxy(proxySettings, domains);
        VisualGridRunner runner = new VisualGridRunner(options);

        AutProxySettings autProxy = options.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), AutProxyMode.ALLOW);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getProxy(), proxySettings);

        autProxy = runner.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), AutProxyMode.ALLOW);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getProxy(), proxySettings);
    }

    @Test
    public void testRunnerOptionsAutProxyWithDomainsAndAutModeAllow() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.ALLOW;
        RunnerOptions options = new RunnerOptions().autProxy(proxySettings, domains, proxyMode);
        VisualGridRunner runner = new VisualGridRunner(options);

        AutProxySettings autProxy = options.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getProxy(), proxySettings);

        autProxy = runner.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getProxy(), proxySettings);
    }

    @Test
    public void testRunnerOptionsAutProxyWithDomainsAndAutModeBlock() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.BLOCK;
        RunnerOptions options = new RunnerOptions().autProxy(proxySettings, domains, proxyMode);
        VisualGridRunner runner = new VisualGridRunner(options);

        AutProxySettings autProxy = options.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getProxy(), proxySettings);

        autProxy = runner.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getProxy(), proxySettings);
    }

    @Test
    public void testAutProxySettingsWithProxySettings() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertNull(autProxySettings.getDomains());
        Assert.assertNull(autProxySettings.getAutProxyMode());
    }

    @Test
    public void testAutProxySettingsWithProxySettingsAndDomains() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), AutProxyMode.ALLOW);
    }

    @Test
    public void testAutProxySettingsWithProxySettingsWithDomainsAndAutModeAllow() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.ALLOW;
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains, proxyMode);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), proxyMode);
    }

    @Test
    public void testAutProxySettingsWithProxySettingsWithDomainsAndAutModeBlock() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.BLOCK;
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains, proxyMode);

        Assert.assertEquals(autProxySettings.getProxy(), proxySettings);
        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), proxyMode);
    }

    @Test
    public void testAutProxyFromConfiguration() {
        Configuration configuration = new Configuration();
        AutProxySettings autProxySettings = new AutProxySettings(new ProxySettings("http://127.0.0.1", 8080));

        configuration.setAutProxy(autProxySettings);
        Assert.assertNull(configuration.getProxy());
        Assert.assertEquals(configuration.getAutProxy(), autProxySettings);

        configuration = new Configuration();
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);

        configuration.setProxy(proxySettings);
        Assert.assertNull(configuration.getAutProxy());
        Assert.assertEquals(configuration.getProxy(), proxySettings);
    }

    @Test
    public void shouldAssignConfigurationWhenSetInVisualGridRunner() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        VisualGridRunner visualGridRunner = new VisualGridRunner();
        visualGridRunner.setProxy(proxySettings); // should set both proxy and autProxy

        Assert.assertEquals(visualGridRunner.getProxy(), proxySettings);
        Assert.assertNotNull(visualGridRunner.getAutProxy());

        Eyes eyes = new Eyes(visualGridRunner);

        Configuration configuration = eyes.getConfiguration();
        ProxySettings proxySettings2 = new ProxySettings("http://127.0.0.2", 8080);
        AutProxySettings autProxySettings = configuration.getAutProxy();
        AutProxySettings autProxySettings2 = new AutProxySettings(proxySettings2);

        configuration.setAutProxy(autProxySettings2); // should change to second set autProxy
        eyes.setConfiguration(configuration);

        Assert.assertEquals(eyes.getConfiguration().getAutProxy(), autProxySettings2);
        Assert.assertNotEquals(autProxySettings, autProxySettings2);
    }
}
