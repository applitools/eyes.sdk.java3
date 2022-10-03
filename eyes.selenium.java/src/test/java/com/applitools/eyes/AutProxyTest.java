package com.applitools.eyes;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutProxyTest {

    @Test
    public void shouldBeNullWhenNotAssignedInRunnerOptions() {
        RunnerOptions runnerOptions = new RunnerOptions();
        Assert.assertNull(runnerOptions.getAutProxyDomains());
        Assert.assertNull(runnerOptions.getAutProxy());
        Assert.assertNull(runnerOptions.getAutProxyMode());

        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        runnerOptions = new RunnerOptions().autProxy(proxySettings);
        Assert.assertNull(runnerOptions.getAutProxyDomains());
        Assert.assertNull(runnerOptions.getAutProxyMode());
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

        Assert.assertEquals(configuration.getAutProxy(), autProxySettings);
    }
}
