package com.applitools.eyes;

import com.applitools.eyes.config.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutProxyTest {

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
