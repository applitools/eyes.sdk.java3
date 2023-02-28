package com.applitools.eyes;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AutProxyTest extends ReportingTestSuite {

    @BeforeClass
    public void setup() {
        super.setGroupName("selenium");
    }

    @Test
    public void shouldBeNullWhenProxySetInVisualGridRunnerProxy() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        VisualGridRunner visualGridRunner = new VisualGridRunner();
        visualGridRunner.setProxy(proxySettings);

        Assert.assertNotNull(visualGridRunner.getProxy());
        Assert.assertNull(visualGridRunner.getAutProxy());

        Eyes eyes = new Eyes(visualGridRunner);

        Assert.assertNotNull(eyes.getConfiguration().getProxy());
        Assert.assertNull(eyes.getConfiguration().getAutProxy());
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
        Assert.assertNotNull(runnerOptions.getAutProxy());
        Assert.assertEquals(runnerOptions.getAutProxy().getUri(), proxySettings.getUri());
        Assert.assertEquals(runnerOptions.getAutProxy().getUsername(), proxySettings.getUsername());
        Assert.assertEquals(runnerOptions.getAutProxy().getPassword(), proxySettings.getPassword());
        Assert.assertEquals(runnerOptions.getAutProxy().getPort(), proxySettings.getPort());

        runnerOptions = new RunnerOptions().proxy(proxySettings);
        Assert.assertNotNull(runnerOptions.getProxy());
        Assert.assertNull(runnerOptions.getAutProxy());
    }

    @Test
    public void testRunnerOptionsAutProxy() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        RunnerOptions options = new RunnerOptions().autProxy(proxySettings);
        VisualGridRunner runner = new VisualGridRunner(options);

        AutProxySettings autProxy = options.getAutProxy();
        Assert.assertNotNull(autProxy);
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());

        autProxy = runner.getAutProxy();
        Assert.assertNotNull(autProxy);
        Assert.assertNull(autProxy.getAutProxyMode());
        Assert.assertNull(autProxy.getDomains());
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());
    }

    @Test
    public void testRunnerOptionsutProxyWithDomains() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        RunnerOptions options = new RunnerOptions().autProxy(proxySettings, domains);
        VisualGridRunner runner = new VisualGridRunner(options);

        AutProxySettings autProxy = options.getAutProxy();
        Assert.assertNotNull(autProxy);
        Assert.assertEquals(autProxy.getAutProxyMode(), AutProxyMode.ALLOW);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());

        autProxy = runner.getAutProxy();
        Assert.assertNotNull(autProxy);
        Assert.assertEquals(autProxy.getAutProxyMode(), AutProxyMode.ALLOW);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());
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
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());

        autProxy = runner.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());
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
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());

        autProxy = runner.getAutProxy();
        Assert.assertEquals(autProxy.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxy.getDomains(), domains);
        Assert.assertEquals(autProxy.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxy.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxy.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxy.getPort(), proxySettings.getPort());
    }

    @Test
    public void testAutProxySettingsWithProxySettings() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings);

        Assert.assertNull(autProxySettings.getDomains());
        Assert.assertNull(autProxySettings.getAutProxyMode());
        Assert.assertEquals(autProxySettings.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxySettings.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxySettings.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxySettings.getPort(), proxySettings.getPort());
    }

    @Test
    public void testAutProxySettingsWithProxySettingsAndDomains() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains);

        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), AutProxyMode.ALLOW);
        Assert.assertEquals(autProxySettings.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxySettings.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxySettings.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxySettings.getPort(), proxySettings.getPort());
    }

    @Test
    public void testAutProxySettingsWithProxySettingsWithDomainsAndAutModeAllow() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.ALLOW;
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains, proxyMode);

        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxySettings.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxySettings.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxySettings.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxySettings.getPort(), proxySettings.getPort());
    }

    @Test
    public void testAutProxySettingsWithProxySettingsWithDomainsAndAutModeBlock() {
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        String[] domains = new String[]{"a.com", "b.com"};
        AutProxyMode proxyMode = AutProxyMode.BLOCK;
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings, domains, proxyMode);

        Assert.assertEquals(autProxySettings.getDomains(), domains);
        Assert.assertEquals(autProxySettings.getAutProxyMode(), proxyMode);
        Assert.assertEquals(autProxySettings.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxySettings.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxySettings.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxySettings.getPort(), proxySettings.getPort());
    }

    @Test
    public void testAutProxyFromConfiguration() {
        Configuration configuration = new Configuration();
        ProxySettings proxySettings = new ProxySettings("http://127.0.0.1", 8080);
        AutProxySettings autProxySettings = new AutProxySettings(proxySettings);

        configuration.setAutProxy(autProxySettings);
        Assert.assertNull(configuration.getProxy());
        Assert.assertEquals(configuration.getAutProxy(), autProxySettings);
        Assert.assertEquals(autProxySettings.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxySettings.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxySettings.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxySettings.getPort(), proxySettings.getPort());

        configuration = new Configuration();
        proxySettings = new ProxySettings("http://127.0.0.1", 8080);

        configuration.setProxy(proxySettings);
        Assert.assertNull(configuration.getAutProxy());
        Assert.assertEquals(configuration.getProxy(), proxySettings);
        Assert.assertEquals(autProxySettings.getUri(), proxySettings.getUri());
        Assert.assertEquals(autProxySettings.getUsername(), proxySettings.getUsername());
        Assert.assertEquals(autProxySettings.getPassword(), proxySettings.getPassword());
        Assert.assertEquals(autProxySettings.getPort(), proxySettings.getPort());
    }
}
